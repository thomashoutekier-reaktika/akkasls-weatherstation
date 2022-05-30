package be.reaktika.weatherstation.domain.geocoding;

import be.reaktika.weatherstation.Main;
import be.reaktika.weatherstation.domain.aggregations.GeoCodingService;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationAggregation;
import kalix.javasdk.testkit.junit.KalixTestKitResource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.*;

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class GeoCodingIntegrationTest {

  /**
   * The test kit starts both the service container and the Akka Serverless proxy.
   */
  @ClassRule
  public static final KalixTestKitResource testKit =
    new KalixTestKitResource(Main.createKalix());


  private GeoCodingService geoCodingService = Mockito.mock(GeoCodingService.class);


  @Before
  public void init(){
    Mockito.when(geoCodingService.getCountryCode(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Optional.of("be"));
    GeoCodingService.setInstance(geoCodingService);
  }

  /**
   * Use the generated gRPC client to call the service through the Akka Serverless proxy.
   */
  private final GeoCodingEntityService client;

  public GeoCodingIntegrationTest() {
    client = testKit.getGrpcClient(GeoCodingEntityService.class);
  }

  @Test
  public void registerDataOnNonExistingEntity() throws Exception {
    // TODO: set fields in command, and provide assertions to match replies
    client.registerData(WeatherStationAggregation.AddToAggregationCommand.newBuilder().setType(WeatherStationAggregation.AggregationType.COUNTRY).build())
            .toCompletableFuture().get(5, SECONDS);
  }
}
