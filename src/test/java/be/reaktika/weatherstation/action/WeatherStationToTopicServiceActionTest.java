package be.reaktika.weatherstation.action;

import be.reaktika.weatherstation.domain.WeatherStationDomain;
import com.google.protobuf.Timestamp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

public class WeatherStationToTopicServiceActionTest {

  @Test
  public void exampleTest() {
    WeatherStationToTopicServiceActionTestKit testKit = WeatherStationToTopicServiceActionTestKit.of(WeatherStationToTopicServiceAction::new);
    // use the testkit to execute a command
    // ActionResult<SomeResponse> result = testKit.someOperation(SomeRequest);
    // verify the response
    // SomeResponse actualResponse = result.getReply();
    // assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void publishStationRegisteredTest() {
    WeatherStationToTopicServiceActionTestKit testKit = WeatherStationToTopicServiceActionTestKit.of(WeatherStationToTopicServiceAction::new);

    var registeredResult = testKit.publishStationRegistered(WeatherStationDomain.StationRegistered
            .newBuilder()
            .setStationId("1").build());
    assertEquals("1", registeredResult.getReply().getStationId());
    // ActionResult<WeatherStationToTopic.WeatherStationData> result = testKit.publishStationRegistered(WeatherStationDomain.StationRegistered.newBuilder()...build());
  }

  @Test
  public void publishTemperatureRegisteredTest() {
    WeatherStationToTopicServiceActionTestKit testKit = WeatherStationToTopicServiceActionTestKit.of(WeatherStationToTopicServiceAction::new);
    var temp1 = WeatherStationDomain.Temperature
            .newBuilder()
            .setMeasurementTime(Timestamp.newBuilder().build())
            .setTemperatureCelcius(10)
            .build();
    var temp2 = WeatherStationDomain.Temperature
            .newBuilder()
            .setMeasurementTime(Timestamp.newBuilder().build())
            .setTemperatureCelcius(5)
            .build();
    var tempRegisteredResult = testKit.publishTemperatureRegistered(WeatherStationDomain.TemperaturesCelciusAdded
            .newBuilder().setStationId("1")
            .addTemperature(temp1)
            .addTemperature(temp2)
            .build());
    assertEquals("1", tempRegisteredResult.getReply().getStationId());
  }

  @Test
  public void publishWindspeedRegisteredTest() {
    WeatherStationToTopicServiceActionTestKit testKit = WeatherStationToTopicServiceActionTestKit.of(WeatherStationToTopicServiceAction::new);
    // ActionResult<WeatherStationToTopic.WeatherStationData> result = testKit.publishWindspeedRegistered(WeatherStationDomain.WindspeedsAdded.newBuilder()...build());
  }

}
