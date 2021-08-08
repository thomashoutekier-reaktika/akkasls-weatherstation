package be.reaktika.weatherstation.actions;

import be.reaktika.weatherstation.domain.WeatherStationPublish;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.AddToAggregationCommand;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.AggregationType;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesAggregation;
import com.akkaserverless.javasdk.Context;
import com.akkaserverless.javasdk.Effect;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.action.Action;
import com.akkaserverless.javasdk.action.Handler;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Action
public class WeatherStationDataConsumeAction {
    private final Logger logger = LoggerFactory.getLogger(WeatherStationDataConsumeAction.class);


    private final ServiceCallRef<AddToAggregationCommand> extremesAggregator;
    private final ServiceCallRef<AddToAggregationCommand> geoCodingAggregator;

    public WeatherStationDataConsumeAction(Context ctx){
        this.extremesAggregator = ctx.serviceCallFactory().lookup("be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesEntityService","RegisterData", AddToAggregationCommand.class);
        this.geoCodingAggregator = ctx.serviceCallFactory().lookup("be.reaktika.weatherstation.domain.geocoding.GeoCodingEntityService","RegisterData", AddToAggregationCommand.class);
        logger.info("created WeatherStationDataConsumeAction");
    }

    @Handler
    public Reply<Empty> dispatchWeatherStationData(WeatherStationPublish.WeatherStationData data) {
        logger.info("dispatching data to aggregators");
        var extremesCommand =  AddToAggregationCommand.newBuilder()
                .setType(AggregationType.EXTREMES)
                .setWeatherdata(data);
        var countryCommand = AddToAggregationCommand.newBuilder()
                .setType(AggregationType.COUNTRY)
                .setWeatherdata(data);
        return Reply.message(Empty.getDefaultInstance())
                .addEffects(Effect.of(extremesAggregator.createCall(extremesCommand.build())))
                .addEffects(Effect.of(geoCodingAggregator.createCall(countryCommand.build())));
    }
}
