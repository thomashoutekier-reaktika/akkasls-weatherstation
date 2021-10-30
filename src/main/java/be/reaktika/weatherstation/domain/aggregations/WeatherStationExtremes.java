/* This code was generated by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */
package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.action.WeatherStationToTopic.WeatherStationData;
import be.reaktika.weatherstation.action.WeatherStationToTopic.WeatherStationTemperatures;
import com.akkaserverless.javasdk.valueentity.ValueEntityContext;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.stream.Collectors;

/** A value entity. */
public class WeatherStationExtremes extends AbstractWeatherStationExtremes {
  @SuppressWarnings("unused")
  private final String entityId;
  private final static Logger logger = LoggerFactory.getLogger(WeatherStationExtremes.class);

  public WeatherStationExtremes(ValueEntityContext context) {
    this.entityId = context.entityId();
  }

  @Override
  public WeatherStationExtremesAggregation.WeatherStationExtremesState emptyState() {
    return WeatherStationExtremesAggregation.WeatherStationExtremesState.getDefaultInstance();
  }

  @Override
  public Effect<Empty> registerData(WeatherStationExtremesAggregation.WeatherStationExtremesState currentState, WeatherStationAggregation.AddToAggregationCommand command) {
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

  private Effect<Empty> aggregateTemperature(WeatherStationData weatherdata, WeatherStationExtremesAggregation.WeatherStationExtremesState currentExtremes) {
    WeatherStationExtremesAggregation.WeatherStationExtremesState.Builder newExtremesBuilder = WeatherStationExtremesAggregation.WeatherStationExtremesState.newBuilder(currentExtremes);

    var sorted = weatherdata.getTemperaturesList()
            .stream()
            .sorted(Comparator.comparingDouble(WeatherStationTemperatures::getTemperatureCelcius))
            .collect(Collectors.toList());
    WeatherStationTemperatures highestEvent = sorted.get(sorted.size()-1);
    WeatherStationTemperatures lowestEvent = sorted.get(0);

    WeatherStationAggregation.TemperatureMeasurement highestInEvent = WeatherStationAggregation.TemperatureMeasurement.newBuilder()
            .setMeasuredTemperature(highestEvent.getTemperatureCelcius())
            .setMeasurementTime(highestEvent.getMeasurementTime())
            .setStationId(weatherdata.getStationId())
            .build();

    WeatherStationAggregation.TemperatureMeasurement lowestInEvent = WeatherStationAggregation.TemperatureMeasurement.newBuilder()
            .setMeasuredTemperature(lowestEvent.getTemperatureCelcius())
            .setMeasurementTime(lowestEvent.getMeasurementTime())
            .setStationId(weatherdata.getStationId())
            .build();

    WeatherStationExtremesAggregation.TemperatureRecord previousMaxRecord = currentExtremes.hasMaxTemperature() ? currentExtremes.getMaxTemperature() : WeatherStationExtremesAggregation.TemperatureRecord.newBuilder().setCurrent(highestInEvent).build();
    WeatherStationExtremesAggregation.TemperatureRecord previousMinRecord = currentExtremes.hasMinTemperature() ? currentExtremes.getMinTemperature() : WeatherStationExtremesAggregation.TemperatureRecord.newBuilder().setCurrent(lowestInEvent).build();

    //initialize the state for the first event
    if (!currentExtremes.hasMaxTemperature()) {
      newExtremesBuilder.setMaxTemperature(previousMaxRecord);
    }
    if (!currentExtremes.hasMinTemperature()){
      newExtremesBuilder.setMinTemperature(previousMinRecord);
    }


    if(highestInEvent.getMeasuredTemperature() > previousMaxRecord.getCurrent().getMeasuredTemperature()){
      logger.info("high temperature record is broken");
      newExtremesBuilder.setMaxTemperature(WeatherStationExtremesAggregation.TemperatureRecord.newBuilder()
              .setCurrent(highestInEvent)
              .setPreviousRecord(previousMaxRecord.getCurrent()));
    }
    if(lowestInEvent.getMeasuredTemperature() < previousMinRecord.getCurrent().getMeasuredTemperature()){
      logger.info("low temperature record is broken");
      newExtremesBuilder.setMinTemperature(WeatherStationExtremesAggregation.TemperatureRecord.newBuilder()
              .setCurrent(lowestInEvent)
              .setPreviousRecord(previousMinRecord.getCurrent()));
    }
    var newExtremes = newExtremesBuilder.build();
    return effects()
            .updateState(newExtremes)
            .thenReply(Empty.getDefaultInstance());
  }
}
