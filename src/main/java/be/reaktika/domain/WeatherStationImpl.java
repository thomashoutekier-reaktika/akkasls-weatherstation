package be.reaktika.domain;

import be.reaktika.WeatherStationPublishApi;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "weatherstation")
public class WeatherStationImpl {
    @SuppressWarnings("unused")
    private final String entityId;
    private String name = "unknown";
    private final Logger logger = LoggerFactory.getLogger(WeatherStationImpl.class);
    private double temp_avg_overall = 0;
    private double windspeed_avg_overall = 0;
    private long numberOfWindMeasurements = 0;
    private long numberOfTempMeasurements = 0;
    private double latitude = 0;
    private double longitude = 0;

    public WeatherStationImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @Snapshot
    public WeatherStationDomain.WeatherStationState snapshot() {
        return WeatherStationDomain.WeatherStationState
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
    public void handleSnapshot(WeatherStationDomain.WeatherStationState snapshot) {
        this.temp_avg_overall = snapshot.getAverageTempCelciusOverall();
        this.windspeed_avg_overall = snapshot.getAverageWindspeedOverall();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
    }

    @CommandHandler
    protected Empty registerStation(WeatherStationDomain.StationRegistrationCommand command, CommandContext ctx) {
        logger.info("registering station " + command);
        var event = WeatherStationDomain.StationRegistered.newBuilder()
                .setStationName(command.getStationName())
                .setStationId(command.getStationId())
                .setLongitude(command.getLongitude())
                .setLatitude(command.getLatitude()).build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @CommandHandler
    protected Empty publishTemperatureReport(WeatherStationDomain.StationTemperatureCommand command, CommandContext ctx) {
        logger.info("publishing temperature " + command);
        var eventBuilder = WeatherStationDomain.TemperaturesCelciusAdded.newBuilder()
                .setStationId(command.getStationId());
        command.getTempMeasurementsList().forEach(t -> eventBuilder.addTemperature(WeatherStationDomain.Temperature.newBuilder()
                                .setMeasurementTime(t.getMeasurementTime())
                                .setTemperatureCelcius(t.getTemperatureCelcius())
                .build()));
        ctx.emit(eventBuilder.build());
        return Empty.getDefaultInstance();

    }

    @CommandHandler
    protected Empty publishWindspeedReport(WeatherStationDomain.StationWindspeedCommand command, CommandContext ctx) {
        logger.info("publishing windspeed " + command);
        var eventBuilder = WeatherStationDomain.WindspeedsAdded.newBuilder().setStationId(command.getStationId());
        command.getWindspeedMeasurementsList().forEach(m -> eventBuilder.addWindspeed(WeatherStationDomain.Windspeed.newBuilder()
                            .setMeasurementTime(m.getMeasurementTime())
                            .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        ctx.emit(eventBuilder.build());
        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void stationRegistered(WeatherStationDomain.StationRegistered event) {
        this.name = event.getStationName();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        logger.info("station registered");
    }

    @EventHandler
    public void temperaturesCelciusAdded(WeatherStationDomain.TemperaturesCelciusAdded event) {
        logger.info("temperatures added " + event);
        var eventAvg = event.getTemperatureList().stream().mapToDouble(WeatherStationDomain.Temperature::getTemperatureCelcius).average().orElse(this.temp_avg_overall);
        this.numberOfTempMeasurements+=1;
        this.temp_avg_overall = this.temp_avg_overall + (eventAvg - this.temp_avg_overall)/numberOfTempMeasurements;
    }

    @EventHandler
    public void windspeedsAdded(WeatherStationDomain.WindspeedsAdded event) {
        logger.info("windspeeds added " + event);
        var eventAvg = event.getWindspeedList().stream().mapToDouble(WeatherStationDomain.Windspeed::getWindspeedMPerS).average().orElse(this.windspeed_avg_overall);
        this.numberOfWindMeasurements+=1;
        this.windspeed_avg_overall = this.windspeed_avg_overall + (eventAvg - this.windspeed_avg_overall)/numberOfWindMeasurements;
    }

    @CommandHandler
    public WeatherStationDomain.StationState getState(WeatherStationDomain.GetStationStateCommand command, CommandContext ctx) {
        logger.info("getting state from " + ctx.entityId());
        return WeatherStationDomain.StationState.newBuilder()
                .setAverageTempCelciusOverall(this.temp_avg_overall)
                .setAverageWindspeedOverall(this.windspeed_avg_overall)
                .setLatitude(this.latitude)
                .setLongitude(this.longitude)
                .setStationName(this.name)
                .setStationId(this.entityId)
                .build();
    }
}