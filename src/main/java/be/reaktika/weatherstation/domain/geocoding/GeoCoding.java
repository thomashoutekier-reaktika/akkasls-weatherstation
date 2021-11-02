/* This code was generated by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */
package be.reaktika.weatherstation.domain.geocoding;

import be.reaktika.weatherstation.action.WeatherStationToTopic.WeatherStationData;
import be.reaktika.weatherstation.action.geocoding.GeoCodingPublishService;
import be.reaktika.weatherstation.domain.aggregations.GeoCodingService;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.SideEffect;
import com.akkaserverless.javasdk.impl.GrpcClients;
import com.akkaserverless.javasdk.valueentity.ValueEntityContext;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A value entity. */
public class GeoCoding extends AbstractGeoCoding {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(GeoCoding.class);
  private final String entityId;
  //private final ServiceCallRef<GeoCodingModel.CountryMeasurements> geocodingPublisher;
  private final GeoCodingPublishService geoCodingPublishService;


  public GeoCoding(ValueEntityContext context) {
    this.entityId = context.entityId();
    var system = context.materializer().system();
    geoCodingPublishService = new GrpcClients(system.systemImpl()).getGrpcClient(GeoCodingPublishService.class,
            "localhost",9000);
    /*
    cfr. https://discuss.lightbend.com/t/publishing-to-a-topic-from-an-action-fails-silently/8965/1
    geocodingPublisher = context.serviceCallFactory()
            .lookup("be.reaktika.weatherstation.action.geocoding.GeoCodingPublishService",
                    "PublishMeasurements",
                    GeoCodingModel.CountryMeasurements.class);

     */
  }

  @Override
  public GeoCodingModel.GeoCodingState emptyState() {
    return GeoCodingModel.GeoCodingState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> registerData(GeoCodingModel.GeoCodingState currentState, WeatherStationAggregation.AddToAggregationCommand command) {
    logger.info("registering weatherstation data " + command);
    if (command.getWeatherdata().getTemperaturesList().isEmpty() && command.getWeatherdata().getWindspeedsList().isEmpty()){
      return stationRegistered(command.getWeatherdata(), currentState);
    }
    if (!command.getWeatherdata().getTemperaturesList().isEmpty()){
      return processTemperatureAdded(command.getWeatherdata(), currentState);
    }
    return processWindspeedAdded(command.getWeatherdata(), currentState);
  }

  private Effect<Empty> stationRegistered(WeatherStationData data, GeoCodingModel.GeoCodingState currentState){
    logger.info("station registered: reverse geocoding the location to find the country");
    var country = GeoCodingService.getInstance().getCountryCode(data.getLatitude(), data.getLongitude());

    logger.info("registered " + data.getStationId() + " in country " + country);
    if(country.isPresent()) {
      var c = country.get();
      logger.info("updating state: " + data.getStationId() + "-> " + c);
      var stateBuilder = GeoCodingModel.GeoCodingState.newBuilder(currentState);
      stateBuilder.putStationIdToCountry(data.getStationId(), c);
      var toPublish = GeoCodingModel.CountryMeasurements
              .newBuilder().setCountry(c);
      logger.info("publishing " + toPublish + " to topic");
      geoCodingPublishService.publishMeasurements(toPublish.build()).toCompletableFuture().thenAccept(d -> {
        logger.info("done publishing CountryMeasurements to topic");
      });
      logger.info("updating state");
      return effects()
              .updateState(stateBuilder.build())
              .thenReply(Empty.getDefaultInstance());
              //.addSideEffects(SideEffect.of(geocodingPublisher.createCall(toPublish.build())));
    }else {
      logger.info("empty reply");
      return effects().reply(Empty.getDefaultInstance());
    }

  }

  private Effect<Empty> processTemperatureAdded(WeatherStationData data, GeoCodingModel.GeoCodingState currentState){
    logger.info("temperature added for " + data + " with state " + currentState);
    var country = currentState.getStationIdToCountryOrDefault(data.getStationId(),"NONE");
    logger.info("country for " + data.getStationId() + " is " + country);
    var builder = GeoCodingModel.CountryMeasurements.newBuilder();
    builder.setCountry(country);
    if (!country.equals("NONE")) {
      data.getTemperaturesList().forEach(t -> {
        builder.addTemperatures(WeatherStationAggregation.TemperatureMeasurement.newBuilder()
                .setStationId(data.getStationId())
                .setMeasuredTemperature(t.getTemperatureCelcius())
                .setMeasurementTime(t.getMeasurementTime()));
      });
      logger.info("forwarding temperatures for country " + country);
      logger.info("publishing to topic");
      geoCodingPublishService.publishMeasurements(builder.build()).toCompletableFuture().thenAccept(d -> {
        logger.info("DONE");
      });
      return effects()
              .reply(Empty.getDefaultInstance());
              //.addSideEffects(SideEffect.of(geocodingPublisher.createCall(builder.build())));
    } else {
      logger.warn("no country found for station " + data.getStationId());
      return effects().reply(Empty.getDefaultInstance());
    }
  }

  private Effect<Empty> processWindspeedAdded(WeatherStationData data, GeoCodingModel.GeoCodingState currentState){
    logger.info("windspeeds added for " + data + " with state " + currentState);
    //TODO: implement this
    logger.warn("Not yet implemented");
    return effects()
            .reply(Empty.getDefaultInstance());
            //.addSideEffects(SideEffect.of(geocodingPublisher.createCall(GeoCodingModel.CountryMeasurements.getDefaultInstance())));
  }

}
