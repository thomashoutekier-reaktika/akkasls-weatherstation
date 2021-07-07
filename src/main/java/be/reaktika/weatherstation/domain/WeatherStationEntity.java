package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.domain.WeatherStationDomain.*;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "weatherstation")
public class WeatherStationEntity {
    @SuppressWarnings("unused")
    private final String entityId;
    private String name = "unknown";
    private final Logger logger = LoggerFactory.getLogger(WeatherStationEntity.class);
    private double temp_avg_overall = 0;
    private double windspeed_avg_overall = 0;
    private long numberOfWindMeasurements = 0;
    private long numberOfTempMeasurements = 0;
    private double latitude = 0;
    private double longitude = 0;

    public WeatherStationEntity(@EntityId String entityId) {
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
                .setAverageTempCelciusOverall(this.temp_avg_overall)
                .setAverageWindspeedOverall(this.windspeed_avg_overall)
                .build();
    }

    @SnapshotHandler
    public void handleSnapshot(WeatherStationState snapshot) {
        this.temp_avg_overall = snapshot.getAverageTempCelciusOverall();
        this.windspeed_avg_overall = snapshot.getAverageWindspeedOverall();
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
    protected Empty publishTemperatureReport(StationTemperatureCommand command, CommandContext ctx) {
        logger.info("publishing temperature " + command);
        var eventBuilder = TemperaturesCelciusAdded.newBuilder()
                .setStationId(command.getStationId());
        command.getTempMeasurementsList().forEach(t -> eventBuilder.addTemperature(Temperature.newBuilder()
                                .setMeasurementTime(t.getMeasurementTime())
                                .setTemperatureCelcius(t.getTemperatureCelcius())
                .build()));
        ctx.emit(eventBuilder.build());
        return Empty.getDefaultInstance();

    }

    @CommandHandler
    protected Empty publishWindspeedReport(StationWindspeedCommand command, CommandContext ctx) {
        logger.info("publishing windspeed " + command);
        var eventBuilder = WindspeedsAdded.newBuilder().setStationId(command.getStationId());
        command.getWindspeedMeasurementsList().forEach(m -> eventBuilder.addWindspeed(Windspeed.newBuilder()
                            .setMeasurementTime(m.getMeasurementTime())
                            .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        ctx.emit(eventBuilder.build());
        return Empty.getDefaultInstance();
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
        var eventAvg = event.getTemperatureList().stream().mapToDouble(Temperature::getTemperatureCelcius).average().orElse(this.temp_avg_overall);
        this.numberOfTempMeasurements+=1;
        this.temp_avg_overall = this.temp_avg_overall + (eventAvg - this.temp_avg_overall)/numberOfTempMeasurements;
    }

    @EventHandler
    public void windspeedsAdded(WindspeedsAdded event) {
        logger.info("windspeeds added " + event);
        var eventAvg = event.getWindspeedList().stream().mapToDouble(Windspeed::getWindspeedMPerS).average().orElse(this.windspeed_avg_overall);
        this.numberOfWindMeasurements+=1;
        this.windspeed_avg_overall = this.windspeed_avg_overall + (eventAvg - this.windspeed_avg_overall)/numberOfWindMeasurements;
    }

    @CommandHandler
    public WeatherStationDomain.StationState getState(GetStationStateCommand command, CommandContext ctx) {
        logger.info("getting state from " + ctx.entityId());
        return StationState.newBuilder()
                .setAverageTempCelciusOverall(this.temp_avg_overall)
                .setAverageWindspeedOverall(this.windspeed_avg_overall)
                .setLatitude(this.latitude)
                .setLongitude(this.longitude)
                .setStationName(this.name)
                .setStationId(this.entityId)
                .build();
    }
}