package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import kalix.javasdk.view.View;
import kalix.javasdk.view.ViewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class was initially generated based on the .proto definition by Kalix tooling.
// This is the implementation for the View Service described in your view/weatherstation_average_view.proto file.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class WeatherStationOverallAverageServiceView extends AbstractWeatherStationOverallAverageServiceView {

  public WeatherStationOverallAverageServiceView(ViewContext context) {}

  private final Logger logger = LoggerFactory.getLogger(WeatherStationOverallAverageServiceView.class);

  @Override
  public WeatherStationAverageViewModel.WeatherStationOverallAverageState emptyState() {
    return WeatherStationAverageViewModel.WeatherStationOverallAverageState.getDefaultInstance();
  }

  @Override
  public UpdateEffect<WeatherStationAverageViewModel.WeatherStationOverallAverageState> processStationRegistered(WeatherStationAverageViewModel.WeatherStationOverallAverageState state, WeatherStationDomain.StationRegistered event) {
    logger.info("processStationRegistered with event " + event + " on state " + state);
    WeatherStationAverageViewModel.WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationAverageViewModel.WeatherStationOverallAverageState.newBuilder(state);

    stateBuilder.setStationName(event.getStationName())
            .setStationId(event.getStationId())
            .setLatitude(event.getLatitude())
            .setLongitude(event.getLongitude());

    return effects().updateState(stateBuilder.build());
  }
  @Override
  public UpdateEffect<WeatherStationAverageViewModel.WeatherStationOverallAverageState> processTemperatureAdded(WeatherStationAverageViewModel.WeatherStationOverallAverageState state, WeatherStationDomain.TemperaturesCelciusAdded event) {
    logger.info("processTemperatureAdded with event " + event + " on state " + state);
    WeatherStationAverageViewModel.WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationAverageViewModel.WeatherStationOverallAverageState.newBuilder(state);
    //the average of the event
    var eventAvg = event.getTemperatureList().
            stream().mapToDouble(WeatherStationDomain.Temperature::getTemperatureCelcius)
            .average()
            .orElse(0.);

    //previous average. If this is the first measurement, use eventAvg as base
    var oldAvg = state.getNumberOfTemperatureMeasurements() == 0 ? eventAvg : state.getAverageTempCelciusOverall();
    var currentNumber = state.getNumberOfTemperatureMeasurements() + event.getTemperatureCount();
    var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

    var newstate = stateBuilder.setAverageTempCelciusOverall(newAvg)
            .setNumberOfTemperatureMeasurements(currentNumber)
            .build();
    logger.info("new state " + newstate);
    return effects().updateState(newstate);
  }
  @Override
  public UpdateEffect<WeatherStationAverageViewModel.WeatherStationOverallAverageState> processWindspeedAdded(WeatherStationAverageViewModel.WeatherStationOverallAverageState state, WeatherStationDomain.WindspeedsAdded event) {
    logger.info("processWindspeedAdded with event " + event + " on state " + state);
    WeatherStationAverageViewModel.WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationAverageViewModel.WeatherStationOverallAverageState.newBuilder(state);
    //the average of the event
    var eventAvg = event.getWindspeedList()
            .stream().mapToDouble(WeatherStationDomain.Windspeed::getWindspeedMPerS)
            .average()
            .orElse(0.);
    //previous average. If this is the first measurement, use eventAvg as base
    var oldAvg = state.getNumberOfWindMeasurements() == 0 ? eventAvg : state.getAverageWindspeedOverall();
    var currentNumber = state.getNumberOfWindMeasurements() + event.getWindspeedCount();
    var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

    stateBuilder.setAverageWindspeedOverall(newAvg)
            .setNumberOfWindMeasurements(currentNumber);
    return effects().updateState(stateBuilder.build());

  }
}

