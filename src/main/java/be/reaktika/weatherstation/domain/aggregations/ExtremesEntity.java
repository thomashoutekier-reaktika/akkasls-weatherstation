package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.domain.WeatherStationExtremesAggregation.*;
import be.reaktika.weatherstation.domain.WeatherStationPublish;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.CommandHandler;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Collectors;

@ValueEntity(entityType = "weatherstationextremes")
public class ExtremesEntity {

    private final static Logger logger = LoggerFactory.getLogger(ExtremesEntity.class);
    private final AggregationType type;


    public ExtremesEntity(@EntityId String type) {
        this.type = AggregationType.valueOf(type);
    }


    @CommandHandler
    public Reply<Empty> registerData(AddToAggregationCommand command, CommandContext<Aggregations> ctx) {
        if (!command.getWeatherdata().getTemperaturesList().isEmpty()){
            logger.info("registering Temperature " + command.getWeatherdata().getTemperaturesList());
            return aggregateTemperature(command.getWeatherdata(), ctx);
        } else if (!command.getWeatherdata().getWindspeedsList().isEmpty()){
            logger.info("registering windspeed " + command.getWeatherdata().getWindspeedsList());

            return Reply.message(Empty.getDefaultInstance());
        }else {
            logger.info("registering station: nothing to aggregate");
            return Reply.message(Empty.getDefaultInstance());
        }


    }

    private Reply<Empty> aggregateTemperature(WeatherStationPublish.WeatherStationData weatherdata, CommandContext<Aggregations> ctx) {
        WeatherStationExtremes currentExtremes = ctx.getState().map(Aggregations::getExtremes).orElse(WeatherStationExtremes.getDefaultInstance());
        WeatherStationExtremes.Builder newExtremesBuilder = WeatherStationExtremes.newBuilder(currentExtremes);

        var sorted = weatherdata.getTemperaturesList()
                .stream()
                .sorted(Comparator.comparingDouble(WeatherStationPublish.WeatherStationTemperatures::getTemperatureCelcius))
                .collect(Collectors.toList());
        WeatherStationPublish.WeatherStationTemperatures highestEvent = sorted.get(sorted.size()-1);
        WeatherStationPublish.WeatherStationTemperatures lowestEvent = sorted.get(0);

        TemperatureMeasurement highestInEvent = TemperatureMeasurement.newBuilder()
                .setMeasuredTemperature(highestEvent.getTemperatureCelcius())
                .setMeasurementTime(highestEvent.getMeasurementTime())
                .setStationId(weatherdata.getStationId())
                .build();

        TemperatureMeasurement lowestInEvent = TemperatureMeasurement.newBuilder()
                .setMeasuredTemperature(lowestEvent.getTemperatureCelcius())
                .setMeasurementTime(lowestEvent.getMeasurementTime())
                .setStationId(weatherdata.getStationId())
                .build();

        TemperatureRecord previousMaxRecord = currentExtremes.hasMaxTemperature() ? currentExtremes.getMaxTemperature() : TemperatureRecord.newBuilder().setCurrent(highestInEvent).build();
        TemperatureRecord previousMinRecord = currentExtremes.hasMinTemperature() ? currentExtremes.getMinTemperature() : TemperatureRecord.newBuilder().setCurrent(lowestInEvent).build();

        //initialize the state for the first event
        if (!currentExtremes.hasMaxTemperature()) {
            newExtremesBuilder.setMaxTemperature(previousMaxRecord);
        }
        if (!currentExtremes.hasMinTemperature()){
            newExtremesBuilder.setMinTemperature(previousMinRecord);
        }

        Aggregations.Builder stateBuilder;
        if (ctx.getState().isPresent()){
            stateBuilder = Aggregations.newBuilder(ctx.getState().get());
        }else {
            stateBuilder = Aggregations.newBuilder()
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


}
