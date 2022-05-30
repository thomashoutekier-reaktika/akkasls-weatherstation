package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.Main;
import be.reaktika.weatherstation.action.AbstractWeatherStationDataConsumeServiceAction;
import be.reaktika.weatherstation.action.WeatherStationDataConsumeService;
import be.reaktika.weatherstation.action.WeatherStationDataConsumeServiceAction;
import be.reaktika.weatherstation.action.WeatherStationToTopic;
import kalix.javasdk.testkit.junit.KalixTestKitResource;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class WeatherStationDataConsumeServiceActionTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final KalixTestKitResource testKit = new KalixTestKitResource(Main.createKalix());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final WeatherStationDataConsumeService client;

  public WeatherStationDataConsumeServiceActionTest() {
    client = testKit.getGrpcClient(WeatherStationDataConsumeService.class);
  }

  @Test
  public void registerDataOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    var command = WeatherStationToTopic.WeatherStationData.newBuilder()
            .setStationId("1")
            .build();

    var reply = client.dispatchWeatherStationData(command);


    var result = reply.toCompletableFuture().get(5, SECONDS);
    System.out.println("result " + result);

  }
}
