package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import be.reaktika.weatherstation.view.WeatherstationAverageView.WeatherStationOverallAverageState;
import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@View
public class WeatherStationAverageViewImpl {

    private final Logger logger = LoggerFactory.getLogger(WeatherStationAverageViewImpl.class);

    @UpdateHandler
    public WeatherStationOverallAverageState processStationRegistered(WeatherStationDomain.StationRegistered event, Optional<WeatherStationOverallAverageState> state) {
        logger.info("processStationRegistered with event " + event + " on state " + state);
        WeatherStationOverallAverageState.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state");
            stateBuilder = WeatherStationOverallAverageState.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherStationOverallAverageState.newBuilder().setStationId(event.getStationId());
        }
        stateBuilder.setStationName(event.getStationName())
                .setLatitude(event.getLatitude())
                .setLongitude(event.getLongitude());

        return stateBuilder.build();
    }


    @UpdateHandler
    public WeatherStationOverallAverageState processTemperatureAdded(WeatherStationDomain.TemperaturesCelciusAdded event, Optional<WeatherStationOverallAverageState> state) {
        logger.info("processTemperatureAdded with event " + event + " on state " + state);
        WeatherStationOverallAverageState.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state");
            stateBuilder = WeatherStationOverallAverageState.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherStationOverallAverageState.newBuilder()
                    .setStationId(event.getStationId())
                    .setAverageTempCelciusOverall(0.)
                    .setNumberOfTemperatureMeasurements(0L);
        }
        var eventAvg = event.getTemperatureList().stream().mapToDouble(WeatherStationDomain.Temperature::getTemperatureCelcius).average().orElse(0);
        var oldAvg = state.map(WeatherStationOverallAverageState::getAverageTempCelciusOverall).orElse(eventAvg);
        var currentNumber = state.map(WeatherStationOverallAverageState::getNumberOfTemperatureMeasurements).orElse(0L) + event.getTemperatureList().size();
        var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

        stateBuilder.setAverageTempCelciusOverall(newAvg)
                .setNumberOfTemperatureMeasurements(currentNumber);
        return stateBuilder.build();
    }


    @UpdateHandler
    public WeatherStationOverallAverageState processWindspeedAdded(WeatherStationDomain.WindspeedsAdded event, Optional<WeatherStationOverallAverageState> state) {
        logger.info("processWindspeedAdded with event " + event + " on state " + state);
        WeatherStationOverallAverageState.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state from " + state.get());
            stateBuilder = WeatherStationOverallAverageState.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherStationOverallAverageState.newBuilder()
                    .setStationId(event.getStationId())
                    .setAverageWindspeedOverall(0.)
                    .setNumberOfWindMeasurements(0L);
        }
        var eventAvg = event.getWindspeedList().stream().mapToDouble(WeatherStationDomain.Windspeed::getWindspeedMPerS).average().orElse(0);
        var oldAvg = state.map(WeatherStationOverallAverageState::getAverageWindspeedOverall).orElse(eventAvg);
        var currentNumber = state.map(WeatherStationOverallAverageState::getNumberOfWindMeasurements).orElse(0L) + event.getWindspeedList().size();
        var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

        stateBuilder.setAverageWindspeedOverall(newAvg)
                .setNumberOfWindMeasurements(currentNumber);
        return stateBuilder.build();
    }
}
