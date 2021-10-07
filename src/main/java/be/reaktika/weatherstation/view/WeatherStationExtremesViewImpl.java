package be.reaktika.weatherstation.view;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class WeatherStationExtremesViewImpl {

    private final static Logger logger = LoggerFactory.getLogger(WeatherStationExtremesViewImpl.class);


    public WeatherStationExtremes updateAggregations(WeatherStationExtremes update, Optional<WeatherStationExtremes> state){
        logger.info("updating extremes view ");
        return update;
    }
}
