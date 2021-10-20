package be.reaktika.weatherstation.action;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.akkaserverless.javasdk.action.ActionCreationContext;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class WeatherStationToTopicServiceAction extends AbstractWeatherStationToTopicServiceAction {

  public WeatherStationToTopicServiceAction(ActionCreationContext creationContext) {}

  /** Handler for "PublishStationRegistered". */
  @Override
  public Effect<WeatherStationToTopic.WeatherStationData> publishStationRegistered(WeatherStationDomain.StationRegistered stationRegistered) {
    throw new RuntimeException("The command handler for `PublishStationRegistered` is not implemented, yet");
  }
  /** Handler for "PublishTemperatureRegistered". */
  @Override
  public Effect<WeatherStationToTopic.WeatherStationData> publishTemperatureRegistered(WeatherStationDomain.TemperaturesCelciusAdded temperaturesCelciusAdded) {
    throw new RuntimeException("The command handler for `PublishTemperatureRegistered` is not implemented, yet");
  }
  /** Handler for "PublishWindspeedRegistered". */
  @Override
  public Effect<WeatherStationToTopic.WeatherStationData> publishWindspeedRegistered(WeatherStationDomain.WindspeedsAdded windspeedsAdded) {
    throw new RuntimeException("The command handler for `PublishWindspeedRegistered` is not implemented, yet");
  }
}
