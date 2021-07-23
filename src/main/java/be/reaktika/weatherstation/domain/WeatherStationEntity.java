package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.domain.WeatherStationDomain.*;
import com.akkaserverless.javasdk.Context;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "weatherstation")
public class WeatherStationEntity {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(WeatherStationEntity.class);
    private final String entityId;
    private String name = "unknown";
    private double latitude = 0;
    private double longitude = 0;

    public static final double MAX_LAT_ABS = 85.;
    public static final double MAX_LON_ABS = 180.;


    public WeatherStationEntity(@EntityId String entityId, Context ctx) {
        this.entityId = entityId;
    }

    @Snapshot
    public WeatherStationDomain.WeatherStationState snapshot() {
        return WeatherStationState
                .newBuilder()
                .setStationId(this.entityId)
                .setStationName(this.name)
                .setLatitude(this.latitude)
                .setLongitude(this.longitude)
                .build();
    }

    @SnapshotHandler
    public void handleSnapshot(WeatherStationState snapshot) {
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
    }

    @CommandHandler
    protected Reply<Empty> registerStation(StationRegistrationCommand command, CommandContext ctx) {
        logger.info("registering station " + command);
        if (Math.abs(command.getLatitude()) > MAX_LAT_ABS || Math.abs(command.getLongitude()) > MAX_LON_ABS){
            throw ctx.fail(String.format("latitude or longitude are invalid: %f, %f",command.getLongitude(), command.getLongitude()));
        }
        var event = StationRegistered.newBuilder()
                .setStationName(command.getStationName())
                .setStationId(command.getStationId())
                .setLongitude(command.getLongitude())
                .setLatitude(command.getLatitude()).build();

        logger.info("registering station per country");

        ctx.emit(event);
        return Reply.message(Empty.getDefaultInstance());
    }

    @CommandHandler
    protected Reply<Empty> publishTemperatureReport(StationTemperatureCommand command, CommandContext ctx) {
        logger.info("publishing temperature " + command);
        var eventBuilder = TemperaturesCelciusAdded.newBuilder()
                .setStationId(command.getStationId());
        command.getTempMeasurementsList().forEach(t -> eventBuilder.addTemperature(Temperature.newBuilder()
                                .setMeasurementTime(t.getMeasurementTime())
                                .setTemperatureCelcius(t.getTemperatureCelcius())
                .build()));

        logger.info("emitting event");
        ctx.emit(eventBuilder.build());

        return Reply.message(Empty.getDefaultInstance());

    }

    @CommandHandler
    protected Reply<Empty> publishWindspeedReport(StationWindspeedCommand command, CommandContext ctx) {
        logger.info("publishing windspeed " + command);
        var eventBuilder = WindspeedsAdded.newBuilder().setStationId(command.getStationId());
        command.getWindspeedMeasurementsList().forEach(m -> eventBuilder.addWindspeed(Windspeed.newBuilder()
                            .setMeasurementTime(m.getMeasurementTime())
                            .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        logger.info("emitting event");
        ctx.emit(eventBuilder.build());


        return Reply.message(Empty.getDefaultInstance());

    }

    @EventHandler
    public void stationRegistered(StationRegistered event) {
        this.name = event.getStationName();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        logger.info("station registered");
    }

    @EventHandler
    public void temperaturesCelciusAdded(TemperaturesCelciusAdded event) {
        logger.info("temperatures added " + event);

    }

    @EventHandler
    public void windspeedsAdded(WindspeedsAdded event) {
        logger.info("windspeeds added " + event);

    }

}