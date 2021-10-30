package be.reaktika.weatherstation.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class WeatherStationDataPublishAction {

    private final Logger logger = LoggerFactory.getLogger(WeatherStationDataPublishAction.class);
/*
    @Handler
    public WeatherStationData publishStationRegistered(WeatherStationDomain.StationRegistered event){
        logger.info("publishing registered station to topic");
        var data =  WeatherStationData.newBuilder()
                .setStationId(event.getStationId())
                .setLatitude(event.getLatitude())
                .setLongitude(event.getLongitude())
                .setStationName(event.getStationName())
                .build();
        logger.info("publishing " + data);
        return data;
    }


    @Handler
    public WeatherStationData publishTemperatureRegistered(WeatherStationDomain.TemperaturesCelciusAdded event){
        logger.info("publishing registered temperatures to topic");
        var builder = WeatherStationData.newBuilder()
                .setStationId(event.getStationId());
        event.getTemperatureList().forEach(t -> {
            var tempBuilder = WeatherStationTemperatures
                    .newBuilder()
                    .setTemperatureCelcius(t.getTemperatureCelcius())
                    .setMeasurementTime(t.getMeasurementTime());
            builder.addTemperatures(tempBuilder.build());
        });
        var data = builder.build();
        logger.info("publishing " + data);
        return data;
    }

    @Handler
    public WeatherStationData publishWindspeedRegistered(WeatherStationDomain.WindspeedsAdded event){
        logger.info("publishing registered windspeeds to topic");
        var builder = WeatherStationData.newBuilder()
                .setStationId(event.getStationId());
        event.getWindspeedList().forEach(w -> {
            var windspeedBuilder = WeatherStationWindspeeds
                    .newBuilder()
                    .setWindspeedMPerS(w.getWindspeedMPerS())
                    .setMeasurementTime(w.getMeasurementTime());
            builder.addWindspeeds(windspeedBuilder.build());
        });
        var data = builder.build();
        logger.info("publishing " + data);
        return data;
    }


 */
}
