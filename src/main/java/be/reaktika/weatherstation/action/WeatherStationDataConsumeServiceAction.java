package be.reaktika.weatherstation.action;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation;
import com.google.protobuf.Empty;
import kalix.javasdk.SideEffect;
import kalix.javasdk.action.ActionCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class WeatherStationDataConsumeServiceAction extends AbstractWeatherStationDataConsumeServiceAction {

  private final Logger logger = LoggerFactory.getLogger(WeatherStationDataConsumeServiceAction.class);

  public WeatherStationDataConsumeServiceAction(ActionCreationContext ctx) {
        logger.info("created WeatherStationDataConsumeServiceAction");
  }

  /** Handler for "DispatchWeatherStationData". */
  @Override
  public Effect<Empty> dispatchWeatherStationData(WeatherStationToTopic.WeatherStationData data) {
    logger.info("dispatching data to aggregators");
    var extremesCommand =  WeatherStationAggregation.AddToAggregationCommand.newBuilder()
            .setType(WeatherStationAggregation.AggregationType.EXTREMES)
            .setWeatherdata(data);
    var countryCommand = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
            .setType(WeatherStationAggregation.AggregationType.COUNTRY)
            .setWeatherdata(data);

    var extremesCall = components().weatherStationExtremes().registerData(extremesCommand.build());
    var geocodingCall = components().geoCoding().registerData(countryCommand.build());

    return effects().reply(Empty.getDefaultInstance())
            .addSideEffect(SideEffect.of(extremesCall))
            .addSideEffect(SideEffect.of(geocodingCall));

  }
}
