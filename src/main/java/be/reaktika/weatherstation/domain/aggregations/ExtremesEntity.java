package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.action.WeatherStationToTopic.WeatherStationData;
import be.reaktika.weatherstation.action.WeatherStationToTopic.WeatherStationTemperatures;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.AddToAggregationCommand;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.AggregationType;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation.TemperatureMeasurement;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesAggregation.TemperatureRecord;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesAggregation.WeatherStationExtremesState;
import com.akkaserverless.javasdk.valueentity.ValueEntityContext;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Collectors;
@Deprecated
public class ExtremesEntity {

    private final static Logger logger = LoggerFactory.getLogger(ExtremesEntity.class);
    private final AggregationType type;


    public ExtremesEntity(ValueEntityContext context) {
        this.type = AggregationType.valueOf(context.entityId());
    }

/*
    private Effect<Empty> aggregateTemperature(WeatherStationData weatherdata, WeatherStationExtremesState currentExtremes) {
        WeatherStationExtremesState.Builder newExtremesBuilder = WeatherStationExtremesState.newBuilder(currentExtremes);

        var sorted = weatherdata.getTemperaturesList()
                .stream()
                .sorted(Comparator.comparingDouble(WeatherStationTemperatures::getTemperatureCelcius))
                .collect(Collectors.toList());
        WeatherStationTemperatures highestEvent = sorted.get(sorted.size()-1);
        WeatherStationTemperatures lowestEvent = sorted.get(0);

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
        return effects().updateState(newExtremes).thenReply(Empty.getDefaultInstance());
    }


    @Override
    public Effect<Empty> registerData(WeatherStationExtremesState currentState, AddToAggregationCommand command) {
        if (!command.getWeatherdata().getTemperaturesList().isEmpty()){
            logger.info("registering Temperature " + command.getWeatherdata().getTemperaturesList());
            return aggregateTemperature(command.getWeatherdata(), currentState);
        } else if (!command.getWeatherdata().getWindspeedsList().isEmpty()){
            logger.info("registering windspeed " + command.getWeatherdata().getWindspeedsList());
            //TODO: implement this
            return effects().reply(Empty.getDefaultInstance());
        }else {
            logger.info("registering station: nothing to aggregate");
            return effects().reply(Empty.getDefaultInstance());
        }
    }

    @Override
    public WeatherStationExtremesState emptyState() {
        return WeatherStationExtremesState.getDefaultInstance();
    }

 */
}
