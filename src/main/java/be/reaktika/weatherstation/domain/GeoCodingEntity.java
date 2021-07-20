package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.ports.geocoding.WeatherstationGeocoding;
import com.akkaserverless.javasdk.*;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.CommandHandler;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import com.google.protobuf.Empty;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * finds the country for a registered weatherstation.
 * Converts measurements (per station_id) to CountryMeasurements (per country)
 */
@ValueEntity(entityType = "geocodingentity")
public class GeoCodingEntity {

    private static final Logger logger = LoggerFactory.getLogger(GeoCodingEntity.class);
    private final WeatherStationAggregations.AggregationType type;
    private final String api_key;
    private final JOpenCageGeocoder geocoder;

    private final ServiceCallRef<WeatherstationGeocoding.CountryMeasurements> measurementsPublisher;

    public GeoCodingEntity(@EntityId String type, Context ctx){
        Config config = ConfigFactory.load();
        this.type = WeatherStationAggregations.AggregationType.valueOf(type);
        logger.info("creating GeoCodingEntity with env " + System.getenv("OPENCAGE_API_KEY"));
        this.api_key = config.getString("reactiveweather.geocode.opencage.apikey");
        logger.info("key " + api_key);
        this.geocoder = new JOpenCageGeocoder(api_key);
        measurementsPublisher = ctx.serviceCallFactory()
                .lookup("be.reaktika.weatherstation.ports.geocoding.publishing.GeoCodingPublishService",
                        "PublishMeasurements",
                        WeatherstationGeocoding.CountryMeasurements.class);
    }


    @CommandHandler
    public Reply<Empty> stationRegistered(WeatherstationGeocoding.RegisterStationPerCountryCommand event, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("station registered: reverse geocoding the location to find the country");

        var state = ctx.getState().orElse(WeatherstationGeocoding.GeoCodingState.getDefaultInstance());
        var builder = WeatherstationGeocoding.GeoCodingState.newBuilder(state);

        JOpenCageReverseRequest request = new JOpenCageReverseRequest(event.getStation().getLatitude(), event.getStation().getLongitude());
        JOpenCageResponse response = geocoder.reverse(request);
        var country = response.getFirstComponents().getCountryCode();
        logger.info("registered " + event.getStation() + " in country " + country);
        builder.putStationIdToCountry(event.getStation().getStationId(), country);

        ctx.updateState(builder.build());

        var toPublish = WeatherstationGeocoding.CountryMeasurements
                .newBuilder().setCountry(country);

        return Reply.message(Empty.getDefaultInstance()).addEffects(Effect.of(measurementsPublisher.createCall(toPublish.build())));
    }


    @CommandHandler
    public Reply<Empty> processTemperatureAdded(WeatherstationGeocoding.RegisterTemperaturesPerCountryCommand event, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("temperature added for " + event + " with state " + ctx.getState());
        var state = ctx.getState().orElse(WeatherstationGeocoding.GeoCodingState.getDefaultInstance());
        var country = state.getStationIdToCountryOrDefault(event.getStationId(),"none");
        logger.info("country for " + event.getStationId() + " is " + country);
        var builder = WeatherstationGeocoding.CountryMeasurements.newBuilder();
        builder.setCountry(country);
        if (!country.equals("none")) {
            event.getTempMeasurementsList().forEach(t -> {
                builder.addTemperatures(WeatherStationAggregations.TemperatureMeasurement.newBuilder()
                        .setStationId(event.getStationId())
                        .setMeasuredTemperature(t.getTemperatureCelcius())
                        .setMeasurementTime(t.getMeasurementTime()));
            });
            return Reply.forward(measurementsPublisher.createCall(builder.build()));
        } else {
            logger.warn("no country found for station " + event.getStationId());
            return Reply.message(Empty.getDefaultInstance());
        }


    }

    @CommandHandler
    public Reply<Empty> processWindspeedAdded(WeatherstationGeocoding.RegisterWindspeedsPerCountryCommand event, CommandContext<WeatherstationGeocoding.GeoCodingState> ctx){
        logger.info("windspeeds added for " + event + " with state " + ctx.getState());
        return Reply.forward(measurementsPublisher.createCall(WeatherstationGeocoding.CountryMeasurements.getDefaultInstance()));
    }

}
