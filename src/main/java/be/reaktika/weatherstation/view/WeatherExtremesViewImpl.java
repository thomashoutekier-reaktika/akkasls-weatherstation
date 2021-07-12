package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import  be.reaktika.weatherstation.view.WeatherstationExtremesView.*;
import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@View
public class WeatherExtremesViewImpl {

    private final Logger logger = LoggerFactory.getLogger(WeatherExtremesViewImpl.class);

    @UpdateHandler
    public WeatherStationRecords processTemperatureAdded(WeatherStationDomain.TemperaturesCelciusAdded event, Optional<WeatherStationRecords> state) {
        logger.info("processTemperatureAdded with event " + event + " on state " + state);
        if (event.getTemperatureList().isEmpty()){
            return state.orElse(WeatherStationRecords.getDefaultInstance());
        }
        var sorted = event.getTemperatureList()
                .stream()
                .sorted(Comparator.comparingDouble(WeatherStationDomain.Temperature::getTemperatureCelcius))
                .collect(Collectors.toList());

        WeatherStationDomain.Temperature highestInEvent = sorted.get(sorted.size()-1);
        WeatherStationDomain.Temperature lowestInEvent = sorted.get(0);

        WeatherStationRecords.Builder tempRecordBuilder;
        TemperatureRecord previousMaxRecord = state.map(WeatherStationRecords::getMaxTemperature).orElse(TemperatureRecord.getDefaultInstance());
        TemperatureRecord previousMinRecord = state.map(WeatherStationRecords::getMinTemperature).orElse(TemperatureRecord.getDefaultInstance());
        if (state.isPresent()){
            tempRecordBuilder = WeatherStationRecords.newBuilder(state.get());
        } else {
            logger.info("creating new state");
            tempRecordBuilder = WeatherStationRecords.newBuilder()
                .setMaxTemperature(TemperatureRecord.newBuilder()
                        .setStationId(event.getStationId())
                        .setMeasuredTemperature(highestInEvent.getTemperatureCelcius())
                        .setMeasurementTime(highestInEvent.getMeasurementTime()))
                .setMinTemperature(TemperatureRecord.newBuilder()
                        .setStationId(event.getStationId())
                        .setMeasuredTemperature(lowestInEvent.getTemperatureCelcius())
                        .setMeasurementTime(lowestInEvent.getMeasurementTime()));
        }
        if (highestInEvent.getTemperatureCelcius() > previousMaxRecord.getMeasuredTemperature()) {
            logger.info("high temperature record is broken");
            tempRecordBuilder.setMaxTemperature(TemperatureRecord.newBuilder()
                    .setStationId(event.getStationId())
                    .setPreviousRecord(previousMaxRecord)
                    .setMeasuredTemperature(highestInEvent.getTemperatureCelcius())
                    .setMeasurementTime(highestInEvent.getMeasurementTime())
                    .build());
        } else {
            logger.info("no new high temperatureRecord");
        }
        if (lowestInEvent.getTemperatureCelcius() < previousMinRecord.getMeasuredTemperature()){
            logger.info("low temperature record is broken");
            tempRecordBuilder.setMinTemperature(TemperatureRecord.newBuilder()
                    .setStationId(event.getStationId())
                    .setPreviousRecord(previousMinRecord)
                    .setMeasuredTemperature(lowestInEvent.getTemperatureCelcius())
                    .setMeasurementTime(lowestInEvent.getMeasurementTime())
                    .build());
        }else {
            logger.info("no new low temperatureRecord");
        }
        return tempRecordBuilder.build();
    }

    @UpdateHandler
    public WeatherStationRecords processWindspeedAdded(WeatherStationDomain.WindspeedsAdded event, Optional<WeatherStationRecords> state) {
        logger.info("processWindspeedAdded with event " + event + " on state " + state);
        return state.orElse(WeatherStationRecords.getDefaultInstance());
    }



}

