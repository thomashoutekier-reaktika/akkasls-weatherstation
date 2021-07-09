package be.reaktika.weatherstation.actions.api;

import be.reaktika.weatherstation.api.WeatherStationApi.StationRegistrationRequest;
import be.reaktika.weatherstation.api.WeatherStationApi.StationTemperaturePublishRequest;
import be.reaktika.weatherstation.api.WeatherStationApi.StationWindspeedPublishRequest;
import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.ActionContext;
import com.akkaserverless.javasdk.action.Handler;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Action
public class WeatherStationPublishApiImpl {

    private static final String WEATHERSTATION_ENTITY_SERVICE_NAME = "be.reaktika.weatherstation.domain.WeatherStationEntityService";
    private final Logger logger = LoggerFactory.getLogger(WeatherStationPublishApiImpl.class);

    @Handler
    public Reply<Empty> registerStation(StationRegistrationRequest request, ActionContext ctx) {
        logger.info("registering station " + request);
        ServiceCallRef<WeatherStationDomain.StationRegistrationCommand> registerStationCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"RegisterStation", WeatherStationDomain.StationRegistrationCommand.class);
        var command = WeatherStationDomain.StationRegistrationCommand.newBuilder()
                .setStationName(request.getStationName())
                .setStationId(request.getStationId())
                .setLongitude(request.getLongitude())
                .setLatitude(request.getLatitude()).build();
        return Reply.forward(registerStationCall.createCall(command));
    }

    @Handler
    public Reply<Empty> publishTemperatureReport(StationTemperaturePublishRequest request, ActionContext ctx) {
        logger.info("publishing temperature " + request);
        ServiceCallRef<WeatherStationDomain.StationTemperatureCommand> publishTempCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishTemperatureReport", WeatherStationDomain.StationTemperatureCommand.class);
        var commandBuilder = WeatherStationDomain.StationTemperatureCommand.newBuilder()
                .setStationId(request.getStationId());
        request.getTempMeasurementsList().forEach(t -> commandBuilder.addTempMeasurements(WeatherStationDomain.TemperatureMeasurements.newBuilder()
                .setMeasurementTime(t.getMeasurementTime())
                .setTemperatureCelcius(t.getTemperatureCelcius())
                .build()));

        return Reply.forward(publishTempCall.createCall(commandBuilder.build()));

    }

    @Handler
    public Reply<Empty> publishWindspeedReport(StationWindspeedPublishRequest request, ActionContext ctx) {
        logger.info("publishing windspeed " + request);
        ServiceCallRef<WeatherStationDomain.StationWindspeedCommand> publishWindCall = ctx.serviceCallFactory()
                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"PublishWindspeedReport", WeatherStationDomain.StationWindspeedCommand.class);
        var commandBuilder = WeatherStationDomain.StationWindspeedCommand.newBuilder().setStationId(request.getStationId());
        request.getWindspeedMeasurementsList().forEach(m -> commandBuilder.addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
                .setMeasurementTime(m.getMeasurementTime())
                .setWindspeedMPerS(m.getWindspeedMPerS())
                .build()));

        return Reply.forward(publishWindCall.createCall(commandBuilder.build()));
    }
/*
    @Handler
    public Reply<StationStateResponse> getDomainState(GetStationStateRequest request, ActionContext ctx) {
        logger.info("getting state from " + request.getStationId() );
        ServiceCallRef<WeatherStationDomain.GetStationStateCommand> getStateCall = ctx.serviceCallFactory()
                                .lookup(WEATHERSTATION_ENTITY_SERVICE_NAME,"GetState", WeatherStationDomain.GetStationStateCommand.class);
        ServiceCall domainRequest = getStateCall.createCall(WeatherStationDomain.GetStationStateCommand.newBuilder().setStationId(request.getStationId()).build());

        //forward to the converter by means of the message-type
        logger.info("forwarding getState request to entity");

        return Reply.forward(domainRequest);

    }

    @Handler
    public Reply<StationStateResponse> convertDomainStateToResponse(WeatherStationDomain.StationState domainState) {
        logger.info("converting domain data to response");
        return Reply.message(StationStateResponse.newBuilder()
                .setStationId(domainState.getStationId())
                .setStationName(domainState.getStationName())
                .setLatitude(domainState.getLatitude())
                .setLongitude(domainState.getLongitude())
                .setAverageTempCelciusOverall(domainState.getAverageTempCelciusOverall())
                .setAverageWindspeedOverall(domainState.getAverageWindspeedOverall())
                .build());
    }

 */

}
