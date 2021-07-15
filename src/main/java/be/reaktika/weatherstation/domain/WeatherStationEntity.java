package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.domain.WeatherStationDomain.*;
import com.akkaserverless.javasdk.*;
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

    private final ServiceCallRef<WeatherStationAggregations.RecordTemperatureCommand> temperatureAggregations;
    private final ServiceCallRef<WeatherStationAggregations.RecordWindspeedCommand> windspeedAggregations;

    public WeatherStationEntity(@EntityId String entityId, Context ctx) {
        this.entityId = entityId;
        this.temperatureAggregations = ctx.serviceCallFactory()
                .lookup("be.reaktika.weatherstation.domain.WeatherStationAggregationService",
                        "RegisterTemperature",
                        WeatherStationAggregations.RecordTemperatureCommand.class);
        this.windspeedAggregations = ctx.serviceCallFactory()
                .lookup("be.reaktika.weatherstation.domain.WeatherStationAggregationService",
                        "RegisterWindspeed",
                        WeatherStationAggregations.RecordWindspeedCommand.class);
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
    protected Empty registerStation(StationRegistrationCommand command, CommandContext ctx) {
        logger.info("registering station " + command);
        var event = StationRegistered.newBuilder()
                .setStationName(command.getStationName())
                .setStationId(command.getStationId())
                .setLongitude(command.getLongitude())
                .setLatitude(command.getLatitude()).build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
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

        logger.info("converting command to aggregation command");
        var aggregationCommandBuilder = WeatherStationAggregations.RecordTemperatureCommand.newBuilder()
                .setType(WeatherStationAggregations.AggregationType.EXTREMES);
        command.getTempMeasurementsList().stream().map(m ->
                WeatherStationAggregations.TemperatureMeasurement.newBuilder()
                        .setStationId(command.getStationId())
                        .setMeasuredTemperature(m.getTemperatureCelcius())
                        .setMeasurementTime(m.getMeasurementTime()))
                .forEach(aggregationCommandBuilder::addMeasurements);
        logger.info("emitting event");
        ctx.emit(eventBuilder.build());
        logger.info("triggering effect");
        return Reply.message(Empty.getDefaultInstance())
                .addEffects(Effect.of(temperatureAggregations.createCall(aggregationCommandBuilder.build())));

    }

    @CommandHandler
    protected Reply<Empty> publishWindspeedReport(StationWindspeedCommand command, CommandContext ctx) {
        logger.info("publishing windspeed " + command);
        var eventBuilder = WindspeedsAdded.newBuilder().setStationId(command.getStationId());
        command.getWindspeedMeasurementsList().forEach(m -> eventBuilder.addWindspeed(Windspeed.newBuilder()
                            .setMeasurementTime(m.getMeasurementTime())
                            .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        logger.info("converting command to aggregation command");
        var aggregationCommandBuilder = WeatherStationAggregations.RecordWindspeedCommand.newBuilder()
                .setType(WeatherStationAggregations.AggregationType.EXTREMES);
        command.getWindspeedMeasurementsList().stream().map(m ->
                WeatherStationAggregations.WinspeedMeasurement.newBuilder()
                        .setStationId(command.getStationId())
                        .setMeasuredWindspeed(m.getWindspeedMPerS())
                        .setMeasurementTime(m.getMeasurementTime()))
                        .forEach(aggregationCommandBuilder::addMeasurements);
        logger.info("emitting event");
        ctx.emit(eventBuilder.build());

        return Reply.message(Empty.getDefaultInstance())
                .addEffects(Effect.of(windspeedAggregations.createCall(aggregationCommandBuilder.build())));

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