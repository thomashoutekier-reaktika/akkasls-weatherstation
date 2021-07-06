package be.reaktika.domain;

import be.reaktika.WeatherStationPublishApi;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An event sourced entity. */
@EventSourcedEntity(entityType = "weatherstation")
public class WeatherStationImpl extends WeatherStationInterface {
    @SuppressWarnings("unused")
    private final String entityId;
    private String name = "unknown";
    private final Logger logger = LoggerFactory.getLogger(WeatherStationImpl.class);
    private double temp_avg_overall = 0;
    private double windspeed_avg_overall = 0;
    private double latitude = 0;
    private double longitude = 0;

    public WeatherStationImpl(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @Override
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

    @Override
    public void handleSnapshot(WeatherStationDomain.WeatherStationState snapshot) {
        this.temp_avg_overall = snapshot.getAverageTempCelciusOverall();
        this.windspeed_avg_overall = snapshot.getAverageWindspeedOverall();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
    }

    @Override
    protected Empty registerStation(WeatherStationPublishApi.StationRegistrationCommand command, CommandContext ctx) {
        logger.info("registering station " + command);
        var event = WeatherStationDomain.StationRegistered.newBuilder()
                .setStationName(command.getStationName())
                .setStationId(command.getStationId())
                .setLongitude(command.getLongitude())
                .setLatitude(command.getLatitude()).build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @Override
    protected Empty publishTemperatureReport(WeatherStationPublishApi.StationTemperatureCommand command, CommandContext ctx) {
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

    @Override
    protected Empty publishWindspeedReport(WeatherStationPublishApi.StationWindspeedCommand command, CommandContext ctx) {
        throw ctx.fail("The command handler for `PublishWindspeedReport` is not implemented, yet");
    }

    @Override
    public void stationRegistered(WeatherStationDomain.StationRegistered event) {
        this.name = event.getStationName();
        this.latitude = event.getLatitude();
        this.longitude = event.getLongitude();
        logger.info("station registered");
    }

    @Override
    public void temperaturesCelciusAdded(WeatherStationDomain.TemperaturesCelciusAdded event) {
        logger.info("temperatures added " + event);
        //todo: calculate averages
    }

    @Override
    public void windspeedsAdded(WeatherStationDomain.WindspeedsAdded event) {
        throw new RuntimeException("The event handler for `WindspeedsAdded` is not implemented, yet");
    }

    @Override
    public WeatherStationPublishApi.StationState getState(WeatherStationPublishApi.GetStationStateCommand command, CommandContext ctx) {
        logger.info("getting state from " + ctx.entityId());
        return WeatherStationPublishApi.StationState.newBuilder()
                .setAverageTempCelciusOverall(this.temp_avg_overall)
                .setAverageWindspeedOverall(this.windspeed_avg_overall)
                .setLatitude(this.latitude)
                .setLongitude(this.longitude)
                .setStationName(this.name)
                .setStationId(this.entityId)
                .build();
    }
}