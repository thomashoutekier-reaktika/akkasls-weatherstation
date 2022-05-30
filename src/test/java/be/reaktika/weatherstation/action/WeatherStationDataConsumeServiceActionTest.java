package be.reaktika.weatherstation.action;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesEntityService;
import be.reaktika.weatherstation.domain.geocoding.GeoCodingEntityService;
import com.google.protobuf.Timestamp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class WeatherStationDataConsumeServiceActionTest {

  @Test
  public void exampleTest() {
    WeatherStationDataConsumeServiceActionTestKit testKit = WeatherStationDataConsumeServiceActionTestKit.of(WeatherStationDataConsumeServiceAction::new);

    // use the testkit to execute a command
    // ActionResult<SomeResponse> result = testKit.someOperation(SomeRequest);
    // verify the response
    // SomeResponse actualResponse = result.getReply();
    // assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void dispatchWeatherStationDataTest() {
    WeatherStationDataConsumeServiceActionTestKit testKit = WeatherStationDataConsumeServiceActionTestKit.of(WeatherStationDataConsumeServiceAction::new);

    var temp1 = WeatherStationToTopic.WeatherStationTemperatures
            .newBuilder()
            .setMeasurementTime(Timestamp.newBuilder().build())
            .setTemperatureCelcius(10)
            .build();
    var temp2 = WeatherStationToTopic.WeatherStationTemperatures
            .newBuilder()
            .setMeasurementTime(Timestamp.newBuilder().build())
            .setTemperatureCelcius(5)
            .build();


    var data = WeatherStationToTopic.WeatherStationData.newBuilder()
            .setStationId("1")
            .addTemperatures(temp1)
            .addTemperatures(temp2)
            .build();

    var response = testKit.dispatchWeatherStationData(data);

    assertEquals(2, response.getSideEffects().size());
    assertTrue(response.getSideEffects().stream().anyMatch(effect -> effect.getServiceName().contains(WeatherStationExtremesEntityService.name)));
    assertTrue(response.getSideEffects().stream().anyMatch(effect -> effect.getServiceName().contains(GeoCodingEntityService.name)));


  }

}
