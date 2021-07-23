package be.reaktika.weatherstation.actions;

import be.reaktika.weatherstation.domain.geocoding.WeatherstationGeocoding;
import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * publishes measurements per country to a topic
 */
@Action
public class GeoCodingPublishAction {

    private static final Logger logger = LoggerFactory.getLogger(GeoCodingPublishAction.class);

    @Handler
    public WeatherstationGeocoding.CountryMeasurements publishMeasurements(WeatherstationGeocoding.CountryMeasurements measurements){
        logger.info("publishing measurements " + measurements);
        return measurements;
    }

}
