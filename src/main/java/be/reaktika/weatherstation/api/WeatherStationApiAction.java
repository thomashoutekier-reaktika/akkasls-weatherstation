package be.reaktika.weatherstation.api;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.action.ActionCreationContext;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class WeatherStationApiAction extends AbstractWeatherStationApiAction {

  private static final String WEATHERSTATION_ENTITY_SERVICE_NAME = "be.reaktika.weatherstation.domain.WeatherStationEntityService";
  private final Logger logger = LoggerFactory.getLogger(WeatherStationApiAction.class);
  private final ServiceCallRef<WeatherStationDomain.StationRegistrationCommand> stationRegistationService;
  private final ServiceCallRef<WeatherStationDomain.StationTemperatureCommand> temperatureRegistationService;
  private final ServiceCallRef<WeatherStationDomain.StationWindspeedCommand> windspeedRegistationService;

  public WeatherStationApiAction(ActionCreationContext ctx) {
    this.stationRegistationService = ctx.serviceCallFactory().lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"RegisterStation", WeatherStationDomain.StationRegistrationCommand.class);
    this.temperatureRegistationService = ctx.serviceCallFactory().lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishTemperatureReport", WeatherStationDomain.StationTemperatureCommand.class);
    this.windspeedRegistationService = ctx.serviceCallFactory().lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishWindspeedReport", WeatherStationDomain.StationWindspeedCommand.class);
  }

  @Override
  public Effect<Empty> registerStation(WeatherStationService.StationRegistrationRequest request) {
    logger.info("Registering station " + request);
    var command = WeatherStationDomain.StationRegistrationCommand.newBuilder()
            .setStationName(request.getStationName())
            .setStationId(request.getStationId())
            .setLongitude(request.getLongitude())
            .setLatitude(request.getLatitude()).build();
    return effects().forward(stationRegistationService.createCall(command));
  }

  @Override
  public Effect<Empty> publishTemperatureReport(WeatherStationService.StationTemperaturePublishRequest request) {
    logger.info("publishing temperature " + request);
    var commandBuilder = WeatherStationDomain.StationTemperatureCommand.newBuilder()
            .setStationId(request.getStationId());
    request.getTempMeasurementsList().forEach(t -> commandBuilder.addTempMeasurements(WeatherStationDomain.TemperatureMeasurements.newBuilder()
            .setMeasurementTime(t.getMeasurementTime())
            .setTemperatureCelcius(t.getTemperatureCelcius())
            .build()));

    return effects().forward(temperatureRegistationService.createCall(commandBuilder.build()));
  }

  @Override
  public Effect<Empty> publishWindspeedReport(WeatherStationService.StationWindspeedPublishRequest request) {
    logger.info("publishing windspeed " + request);
    var commandBuilder = WeatherStationDomain.StationWindspeedCommand.newBuilder().setStationId(request.getStationId());
    request.getWindspeedMeasurementsList().forEach(m -> commandBuilder.addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
            .setMeasurementTime(m.getMeasurementTime())
            .setWindspeedMPerS(m.getWindspeedMPerS())
            .build()));

    return effects().forward(windspeedRegistationService.createCall(commandBuilder.build()));
  }
/*
    @Handler
    public Reply<Empty> registerStation(StationRegistrationRequest request, ActionContext ctx) {
        logger.info("registering station " + request);
        ServiceCallRef<WeatherStationDomain.StationRegistrationCommand> registerStationCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"RegisterStation", WeatherStationDomain.StationRegistrationCommand.class);
        var command = WeatherStationDomain.StationRegistrationCommand.newBuilder()
                .setStationName(request.getStationName())
                .setStationId(request.getStationId())
                .setLongitude(request.getLongitude())
                .setLatitude(request.getLatitude()).build();
        return Reply.forward(registerStationCall.createCall(command));
    }

    @Handler
    public Reply<Empty> publishTemperatureReport(StationTemperaturePublishRequest request, ActionContext ctx) {
        logger.info("publishing temperature " + request);
        ServiceCallRef<WeatherStationDomain.StationTemperatureCommand> publishTempCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishTemperatureReport", WeatherStationDomain.StationTemperatureCommand.class);
        var commandBuilder = WeatherStationDomain.StationTemperatureCommand.newBuilder()
                .setStationId(request.getStationId());
        request.getTempMeasurementsList().forEach(t -> commandBuilder.addTempMeasurements(WeatherStationDomain.TemperatureMeasurements.newBuilder()
                .setMeasurementTime(t.getMeasurementTime())
                .setTemperatureCelcius(t.getTemperatureCelcius())
                .build()));

        return Reply.forward(publishTempCall.createCall(commandBuilder.build()));

    }

    @Handler
    public Reply<Empty> publishWindspeedReport(StationWindspeedPublishRequest request, ActionContext ctx) {
        logger.info("publishing windspeed " + request);
        ServiceCallRef<WeatherStationDomain.StationWindspeedCommand> publishWindCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishWindspeedReport", WeatherStationDomain.StationWindspeedCommand.class);
        var commandBuilder = WeatherStationDomain.StationWindspeedCommand.newBuilder().setStationId(request.getStationId());
        request.getWindspeedMeasurementsList().forEach(m -> commandBuilder.addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
                .setMeasurementTime(m.getMeasurementTime())
                .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        return Reply.forward(publishWindCall.createCall(commandBuilder.build()));
    }


 */
}
