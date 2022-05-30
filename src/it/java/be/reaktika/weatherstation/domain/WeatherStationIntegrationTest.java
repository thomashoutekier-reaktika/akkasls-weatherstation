package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.Main;
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
public class WeatherStationIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final KalixTestKitResource testKit = new KalixTestKitResource(Main.createKalix());

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final WeatherStationEntityService client;

  public WeatherStationIntegrationTest() {
    client = testKit.getGrpcClient(WeatherStationEntityService.class);
  }

  @Test
  public void registerStationOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
     client.registerStation(WeatherStationDomain.StationRegistrationCommand.newBuilder()
             .setStationId("1")
             .setLatitude(2.)
             .setLongitude(2.)
             .build())
             .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  public void publishTemperatureReportOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
     client.publishTemperatureReport(WeatherStationDomain.StationTemperatureCommand.newBuilder()
             .setStationId("1").build())
             .toCompletableFuture().get(5, SECONDS);
  }

  @Test
  public void publishWindspeedReportOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
     client.publishWindspeedReport(WeatherStationDomain.StationWindspeedCommand.newBuilder().setStationId("1").build())
             .toCompletableFuture().get(5, SECONDS);
  }
}
