package be.reaktika.weatherstation.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.CommandHandler;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import be.reaktika.weatherstation.domain.WeatherStationAggregations.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Collectors;

@ValueEntity(entityType = "aggregations")
public class AggregationsEntity {

    private final static Logger logger = LoggerFactory.getLogger(AggregationsEntity.class);
    private final AggregationType type;


    public AggregationsEntity(@EntityId String type) {
        this.type = AggregationType.valueOf(type);
    }


    @CommandHandler
    public Reply<Empty> registerTemperature(RecordTemperatureCommand command, CommandContext<WeatherStationAggregations.Aggregations> ctx) {
        logger.info("registering Temperature " + command);
        var sorted = command.getMeasurementsList()
                .stream()
                .sorted(Comparator.comparingDouble(TemperatureMeasurement::getMeasuredTemperature))
                .collect(Collectors.toList());
        TemperatureMeasurement highestInEvent = sorted.get(sorted.size()-1);
        TemperatureMeasurement lowestInEvent = sorted.get(0);

        WeatherStationExtremes currentExtremes = ctx.getState().map(WeatherStationAggregations.Aggregations::getExtremes).orElse(WeatherStationExtremes.getDefaultInstance());
        WeatherStationExtremes.Builder newExtremesBuilder = WeatherStationExtremes.newBuilder(currentExtremes);

        TemperatureRecord previousMaxRecord = currentExtremes.hasMaxTemperature() ? currentExtremes.getMaxTemperature() : TemperatureRecord.newBuilder().setCurrent(highestInEvent).build();
        TemperatureRecord previousMinRecord = currentExtremes.hasMinTemperature() ? currentExtremes.getMinTemperature() : TemperatureRecord.newBuilder().setCurrent(lowestInEvent).build();

        //initialze the state for the first event
        if (!currentExtremes.hasMaxTemperature()) {
            newExtremesBuilder.setMaxTemperature(previousMaxRecord);
        }
        if (!currentExtremes.hasMinTemperature()){
            newExtremesBuilder.setMinTemperature(previousMinRecord);
        }

        WeatherStationAggregations.Aggregations.Builder stateBuilder;
        if (ctx.getState().isPresent()){
            stateBuilder = WeatherStationAggregations.Aggregations.newBuilder(ctx.getState().get());
        }else {
            stateBuilder = WeatherStationAggregations.Aggregations.newBuilder()
                    .setExtremes(WeatherStationExtremes.newBuilder().setMaxTemperature(TemperatureRecord.newBuilder().setCurrent(highestInEvent))
                                                                     .setMinTemperature(TemperatureRecord.newBuilder().setCurrent(lowestInEvent)));
        }
        if(highestInEvent.getMeasuredTemperature() > previousMaxRecord.getCurrent().getMeasuredTemperature()){
            logger.info("high temperature record is broken");
            newExtremesBuilder.setMaxTemperature(TemperatureRecord.newBuilder()
                    .setCurrent(highestInEvent)
                    .setPreviousRecord(previousMinRecord.getCurrent()));
        }
        if(lowestInEvent.getMeasuredTemperature() < previousMinRecord.getCurrent().getMeasuredTemperature()){
            logger.info("low temperature record is broken");
            newExtremesBuilder.setMinTemperature(TemperatureRecord.newBuilder()
                    .setCurrent(lowestInEvent)
                    .setPreviousRecord(previousMinRecord.getCurrent()));
        }
        var newExtremes = newExtremesBuilder.build();
        var newState = stateBuilder.setExtremes(newExtremes).build();
        ctx.updateState(newState);

        return Reply.message(Empty.getDefaultInstance());
    }


    @CommandHandler
    public Reply<Empty> registerWindspeed(RecordWindspeedCommand command, CommandContext<WeatherStationAggregations.Aggregations> ctx) {
        logger.info("registering Windspeed " + command);
        return Reply.message(Empty.getDefaultInstance());
    }
}
