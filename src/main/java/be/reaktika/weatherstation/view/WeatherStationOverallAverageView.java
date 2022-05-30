/* This code was generated by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */
package be.reaktika.weatherstation.view;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import be.reaktika.weatherstation.view.WeatherStationAverageViewModel.WeatherStationOverallAverageState;
import kalix.javasdk.view.ViewContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherStationOverallAverageView extends AbstractWeatherStationOverallAverageView {
  private final Logger logger = LoggerFactory.getLogger(WeatherStationOverallAverageView.class);

  public WeatherStationOverallAverageView(ViewContext context) {}

  @Override
  public WeatherStationOverallAverageState emptyState() {
    return WeatherStationOverallAverageState.getDefaultInstance();
  }

  @Override
  public UpdateEffect<WeatherStationOverallAverageState> processStationRegistered(WeatherStationOverallAverageState state, WeatherStationDomain.StationRegistered event) {
    logger.info("processStationRegistered with event " + event + " on state " + state);
    WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationOverallAverageState.newBuilder(state);

    stateBuilder.setStationName(event.getStationName())
            .setStationId(event.getStationId())
            .setLatitude(event.getLatitude())
            .setLongitude(event.getLongitude());

    return effects().updateState(stateBuilder.build());
  }
  @Override
  public UpdateEffect<WeatherStationOverallAverageState> processTemperatureAdded(WeatherStationOverallAverageState state, WeatherStationDomain.TemperaturesCelciusAdded event) {
    logger.info("processTemperatureAdded with event " + event + " on state " + state);
    WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationOverallAverageState.newBuilder(state);
    //the average of the event
    var eventAvg = event.getTemperatureList().
            stream().mapToDouble(WeatherStationDomain.Temperature::getTemperatureCelcius)
            .average()
            .orElse(0.);

    //previous average. If this is the first measurement, use eventAvg as base
    var oldAvg = state.getNumberOfTemperatureMeasurements() == 0 ? eventAvg : state.getAverageTempCelciusOverall();
    var currentNumber = state.getNumberOfTemperatureMeasurements() + event.getTemperatureCount();
    var newAvg = oldAvg + ((eventAvg - oldAvg)/currentNumber);

    stateBuilder.setAverageTempCelciusOverall(newAvg)
                .setNumberOfTemperatureMeasurements(currentNumber);
    return effects().updateState(stateBuilder.build());
  }
  @Override
  public UpdateEffect<WeatherStationOverallAverageState> processWindspeedAdded(WeatherStationOverallAverageState state, WeatherStationDomain.WindspeedsAdded event) {
    logger.info("processWindspeedAdded with event " + event + " on state " + state);
    WeatherStationOverallAverageState.Builder stateBuilder = WeatherStationOverallAverageState.newBuilder(state);
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
