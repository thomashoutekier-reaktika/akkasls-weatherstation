package be.reaktika.weatherstation.action;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import kalix.javasdk.SideEffect;
import kalix.javasdk.action.Action;
import kalix.javasdk.action.ActionCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Kalix tooling.
// This is the implementation for the Action Service described in your action/weatherstation_consume.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class WeatherStationDataConsumeServiceAction extends AbstractWeatherStationDataConsumeServiceAction {

  private final Logger logger = LoggerFactory.getLogger(WeatherStationDataConsumeServiceAction.class);

  public WeatherStationDataConsumeServiceAction(ActionCreationContext creationContext) {}

  @Override
  public Action.Effect<Empty> dispatchWeatherStationData(WeatherStationToTopic.WeatherStationData data) {
    logger.info("dispatching data to aggregators");

    var countryCommand = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
            .setType(WeatherStationAggregation.AggregationType.COUNTRY)
            .setWeatherdata(data);

    var geocodingCall = components().geoCoding().registerData(data);

    return effects().reply(Empty.getDefaultInstance())
            .addSideEffect(SideEffect.of(geocodingCall));
  }

  @Override
  public Effect<Empty> ignoreOtherEvents(Any any) {
    logger.info("ignoring data " + any);
    return effects().reply(Empty.getDefaultInstance());
  }
}
