package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.AggregationType;

import be.reaktika.weatherstation.domain.geocoding.WeatherstationGeocoding;
import com.akkaserverless.javasdk.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * finds the country for a registered weatherstation.
 * Converts measurements (per station_id) to CountryMeasurements (per country)
 */

public class GeoCodingEntity {

    private static final Logger logger = LoggerFactory.getLogger(GeoCodingEntity.class);
    private final AggregationType type;
    private final String api_key;

    private final ServiceCallRef<WeatherstationGeocoding.CountryMeasurements> measurementsPublisher;

    public GeoCodingEntity(String type, Context ctx){
        Config config = ConfigFactory.load();
        this.type = AggregationType.valueOf(type);
        logger.info("creating GeoCodingEntity with env " + System.getenv("OPENCAGE_API_KEY"));
        this.api_key = config.getString("reactiveweather.geocode.opencage.apikey");
        logger.info("key " + api_key);
        measurementsPublisher = ctx.serviceCallFactory()
                .lookup("be.reaktika.weatherstation.domain.geocoding.publishing.GeoCodingPublishService",
                        "PublishMeasurements",
                        WeatherstationGeocoding.CountryMeasurements.class);
    }


/*

    public Reply<Empty> registerData(AddToAggregationCommand command, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx) {
        if (command.getWeatherdata().getTemperaturesList().isEmpty() && command.getWeatherdata().getWindspeedsList().isEmpty()){
            return stationRegistered(command.getWeatherdata(), ctx);
        }
        if (!command.getWeatherdata().getTemperaturesList().isEmpty()){
            return processTemperatureAdded(command.getWeatherdata(), ctx);
        }
        return processWindspeedAdded(command.getWeatherdata(), ctx);

    }



    private Reply<Empty> stationRegistered(WeatherStationPublish.WeatherStationData data, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("station registered: reverse geocoding the location to find the country");

        var reply = Reply.message(Empty.getDefaultInstance());
        var country = GeoCodingService.getInstance().getCountryCode(data.getLatitude(), data.getLongitude());

        logger.info("registered " + data.getStationId() + " in country " + country);
        country.ifPresent(c -> {
            logger.info("updating state: " + data.getStationId() + "-> " + c);
            var currentState = ctx.getState().orElse(WeatherstationGeocoding.GeoCodingState.getDefaultInstance());
            var stateBuilder = WeatherstationGeocoding.GeoCodingState.newBuilder(currentState);
            stateBuilder.putStationIdToCountry(data.getStationId(), c);
            ctx.updateState(stateBuilder.build());

            var toPublish = WeatherstationGeocoding.CountryMeasurements
                .newBuilder().setCountry(c);
            reply.addEffects(Effect.of(measurementsPublisher.createCall(toPublish.build())));
        });


        return reply;
    }





    private Reply<Empty> processTemperatureAdded(WeatherStationPublish.WeatherStationData data, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("temperature added for " + data + " with state " + ctx.getState());
        var state = ctx.getState().orElse(WeatherstationGeocoding.GeoCodingState.getDefaultInstance());
        var country = state.getStationIdToCountryOrDefault(data.getStationId(),"NONE");
        logger.info("country for " + data.getStationId() + " is " + country);
        var builder = WeatherstationGeocoding.CountryMeasurements.newBuilder();
        builder.setCountry(country);
        if (!country.equals("NONE")) {
            data.getTemperaturesList().forEach(t -> {
                builder.addTemperatures(TemperatureMeasurement.newBuilder()
                        .setStationId(data.getStationId())
                        .setMeasuredTemperature(t.getTemperatureCelcius())
                        .setMeasurementTime(t.getMeasurementTime()));
            });
            logger.info("forwarding temperatures for country " + country);
            return Reply.forward(measurementsPublisher.createCall(builder.build()));
        } else {
            logger.warn("no country found for station " + data.getStationId());
            return Reply.message(Empty.getDefaultInstance());
        }


    }


    private Reply<Empty> processWindspeedAdded(WeatherStationPublish.WeatherStationData data, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("windspeeds added for " + data + " with state " + ctx.getState());
        return Reply.forward(measurementsPublisher.createCall(WeatherstationGeocoding.CountryMeasurements.getDefaultInstance()));
    }


 */
}
