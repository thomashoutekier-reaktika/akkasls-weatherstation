package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationExtremesAggregation.*;
import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@View
public class WeatherStationAggregationsView {

    private final static Logger logger = LoggerFactory.getLogger(WeatherStationAggregationsView.class);

    @UpdateHandler
    public Aggregations updateAggregations(Aggregations update, Optional<Aggregations> state){
        logger.info("updating aggregationview ");
        return update;
    }
}
