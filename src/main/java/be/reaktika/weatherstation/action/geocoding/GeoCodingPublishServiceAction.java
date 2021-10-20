package be.reaktika.weatherstation.action.geocoding;

import be.reaktika.weatherstation.domain.geocoding.WeatherstationGeocoding;
import com.akkaserverless.javasdk.action.ActionCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

/** An action. */
public class GeoCodingPublishServiceAction extends AbstractGeoCodingPublishServiceAction {

  private static final Logger logger = LoggerFactory.getLogger(GeoCodingPublishServiceAction.class);

  public GeoCodingPublishServiceAction(ActionCreationContext creationContext) {}

  /** Handler for "PublishMeasurements". */
  @Override
  public Effect<WeatherstationGeocoding.CountryMeasurements> publishMeasurements(WeatherstationGeocoding.CountryMeasurements measurements) {
    logger.info("publishing measurements " + measurements);
    return effects().reply(measurements);
  }
}
