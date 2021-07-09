package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.akkaserverless.javasdk.view.UpdateHandler;
import com.akkaserverless.javasdk.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@View
public class WeatherStationViewImpl {

    private final Logger logger = LoggerFactory.getLogger(WeatherStationViewImpl.class);

    @UpdateHandler
    public WeatherstationView.WeatherStationStateView processStationRegistered(WeatherStationDomain.StationRegistered event, Optional<WeatherstationView.WeatherStationStateView> state) {
        logger.info("processStationRegistered with event " + event + " on state " + state);
        WeatherstationView.WeatherStationStateView.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state");
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder().setStationId(event.getStationId());
        }
        stateBuilder.setStationName(event.getStationName())
                .setLatitude(event.getLatitude())
                .setLongitude(event.getLongitude());

        return stateBuilder.build();
    }


    @UpdateHandler
    public WeatherstationView.WeatherStationStateView processTemperatureAdded(WeatherStationDomain.TemperaturesCelciusAdded event, Optional<WeatherstationView.WeatherStationStateView> state) {
        logger.info("processTemperatureAdded with event " + event + " on state " + state);
        WeatherstationView.WeatherStationStateView.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state");
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder()
                    .setStationId(event.getStationId())
                    .setAverageTempCelciusOverall(0.)
                    .setNumberOfTemperatureMeasurements(0L);
        }
        var eventAvg = event.getTemperatureList().stream().mapToDouble(WeatherStationDomain.Temperature::getTemperatureCelcius).average().orElse(0);
        var oldAvg = state.map(WeatherstationView.WeatherStationStateView::getAverageTempCelciusOverall).orElse(eventAvg);
        var currentNumber = state.map(WeatherstationView.WeatherStationStateView::getNumberOfTemperatureMeasurements).orElse(0L) + event.getTemperatureList().size();
        var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

        stateBuilder.setAverageTempCelciusOverall(newAvg)
                .setNumberOfTemperatureMeasurements(currentNumber);
        return stateBuilder.build();
    }


    @UpdateHandler
    public WeatherstationView.WeatherStationStateView processWindspeedAdded(WeatherStationDomain.WindspeedsAdded event, Optional<WeatherstationView.WeatherStationStateView> state) {
        logger.info("processWindspeedAdded with event " + event + " on state " + state);
        WeatherstationView.WeatherStationStateView.Builder stateBuilder;
        if (state.isPresent()){
            logger.info("updating state from " + state.get());
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder(state.get());
        }else {
            logger.info("creating new state");
            stateBuilder = WeatherstationView.WeatherStationStateView.newBuilder()
                    .setStationId(event.getStationId())
                    .setAverageWindspeedOverall(0.)
                    .setNumberOfWindMeasurements(0L);
        }
        var eventAvg = event.getWindspeedList().stream().mapToDouble(WeatherStationDomain.Windspeed::getWindspeedMPerS).average().orElse(0);
        var oldAvg = state.map(WeatherstationView.WeatherStationStateView::getAverageWindspeedOverall).orElse(eventAvg);
        var currentNumber = state.map(WeatherstationView.WeatherStationStateView::getNumberOfWindMeasurements).orElse(0L) + event.getWindspeedList().size();
        var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

        stateBuilder.setAverageWindspeedOverall(newAvg)
                .setNumberOfWindMeasurements(currentNumber);
        return stateBuilder.build();
    }
}
