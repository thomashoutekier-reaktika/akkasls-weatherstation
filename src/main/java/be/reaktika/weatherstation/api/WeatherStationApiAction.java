package be.reaktika.weatherstation.api;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.google.protobuf.Empty;
import kalix.javasdk.DeferredCall;
import kalix.javasdk.action.ActionCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class WeatherStationApiAction extends AbstractWeatherStationApiAction {

  private final Logger logger = LoggerFactory.getLogger(WeatherStationApiAction.class);

  public WeatherStationApiAction(ActionCreationContext ctx) {
  }

  @Override
  public Effect<Empty> registerStation(WeatherStationService.StationRegistrationRequest request) {
    logger.info("Registering station " + request);
    var command = WeatherStationDomain.StationRegistrationCommand.newBuilder()
            .setStationName(request.getStationName())
            .setStationId(request.getStationId())
            .setLongitude(request.getLongitude())
            .setLatitude(request.getLatitude()).build();
    DeferredCall<WeatherStationDomain.StationRegistrationCommand,Empty> call = components().weatherStation().registerStation(command);
    return effects().forward(call);
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

    return effects().forward(components().weatherStation().publishTemperatureReport(commandBuilder.build()));
  }

  @Override
  public Effect<Empty> publishWindspeedReport(WeatherStationService.StationWindspeedPublishRequest request) {
    logger.info("publishing windspeed " + request);
    var commandBuilder = WeatherStationDomain.StationWindspeedCommand.newBuilder().setStationId(request.getStationId());
    request.getWindspeedMeasurementsList().forEach(m -> commandBuilder.addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
            .setMeasurementTime(m.getMeasurementTime())
            .setWindspeedMPerS(m.getWindspeedMPerS())
            .build()));

    return effects().forward(components().weatherStation().publishWindspeedReport(commandBuilder.build()));
  }

}
