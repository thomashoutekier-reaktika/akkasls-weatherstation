package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.Main;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestKitResource;
import com.google.protobuf.Empty;
import org.junit.ClassRule;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class WeatherStationExtremesIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final AkkaServerlessTestKitResource testKit =
    new AkkaServerlessTestKitResource(Main.createAkkaServerless());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final WeatherStationExtremesEntityService client;

  public WeatherStationExtremesIntegrationTest() {
    client = testKit.getGrpcClient(WeatherStationExtremesEntityService.class);
  }

  @Test
  public void registerDataOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    // client.registerData(WeatherStationAggregation.AddToAggregationCommand.newBuilder().build())
    //         .toCompletableFuture().get(5, SECONDS);
  }
}
