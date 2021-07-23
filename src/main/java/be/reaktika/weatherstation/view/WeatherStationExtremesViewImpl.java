package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesAggregation.*;
import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@View
public class WeatherStationExtremesViewImpl {

    private final static Logger logger = LoggerFactory.getLogger(WeatherStationExtremesViewImpl.class);

    @UpdateHandler
    public WeatherStationExtremes updateAggregations(WeatherStationExtremes update, Optional<WeatherStationExtremes> state){
        logger.info("updating extremes view ");
        return update;
    }
}
