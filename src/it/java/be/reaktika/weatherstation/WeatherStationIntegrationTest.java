package be.reaktika.weatherstation;

import be.reaktika.weatherstation.api.WeatherStationApi.*;
import be.reaktika.weatherstation.api.WeatherStationApiServiceClient;
import be.reaktika.weatherstation.view.StationByIdViewClient;
import be.reaktika.weatherstation.view.WeatherstationView;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import com.google.protobuf.util.Timestamps;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

// Example of an integration test calling our service via the Akka Serverless proxy
// Run all test classes ending with "IntegrationTest" using `mvn verify -Pit`
public class WeatherStationIntegrationTest {
    
    /**
     * The test kit starts both the service container and the Akka Serverless proxy.
     */
    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(Main.SERVICE);
    
    /**
     * Use the generated gRPC client to call the service through the Akka Serverless proxy.
     */
    private final WeatherStationApiServiceClient client;
    private final StationByIdViewClient viewClient;

    public WeatherStationIntegrationTest() {
        client = WeatherStationApiServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        viewClient = StationByIdViewClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }
    
    @Test
    public void registerStationOnNonExistingEntity() throws Exception {
        var id = UUID.randomUUID().toString();
        client.registerStation(StationRegistrationRequest.newBuilder()
                    .setStationId(id)
                .setStationName(id + "name")
                .setLatitude(10)
                .setLongitude(20)
                .build()).toCompletableFuture().get(2, SECONDS);


        Thread.sleep(1000);

        var state = viewClient
                .getStationState(WeatherstationView.StationByIdRequest.newBuilder()
                        .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);

        System.out.println("received response " + state);
        assertEquals(id,state.getStationId());
        assertEquals(id + "name",state.getStationName());
        assertEquals(10, state.getLatitude(), 0.001);
        assertEquals(20, state.getLongitude(),0.001);
        assertEquals(0, state.getAverageTempCelciusOverall(),0.001);
        assertEquals(0, state.getAverageWindspeedOverall(),0.001);

    }
    
    @Test
    public void publishTemperatureReportOnNonExistingEntity() throws Exception {
        var id = UUID.randomUUID().toString();
        var first = Timestamps.fromMillis(System.currentTimeMillis());
        var second = Timestamps.fromMillis(System.currentTimeMillis() + 1000);
        client.publishTemperatureReport(StationTemperaturePublishRequest.newBuilder()
                .setStationId(id)
                .addTempMeasurements(TemperatureMeasurements.newBuilder()
                        .setTemperatureCelcius(10)
                        .setMeasurementTime(first))
                .addTempMeasurements(TemperatureMeasurements.newBuilder()
                        .setTemperatureCelcius(20)
                        .setMeasurementTime(second))
                .build()).toCompletableFuture().get(2, SECONDS);

        Thread.sleep(1000);

        var state = viewClient
                .getStationState(WeatherstationView.StationByIdRequest.newBuilder()
                        .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);
        System.out.println("received response " + state);

        assertEquals(id,state.getStationId());
        assertEquals("",state.getStationName());
        assertEquals(0, state.getLatitude(), 0.001);
        assertEquals(0, state.getLongitude(),0.001);
        assertEquals(15, state.getAverageTempCelciusOverall(),0.001);
        assertEquals(0, state.getAverageWindspeedOverall(),0.001);


    }
    
    @Test
    public void publishWindspeedReportOnNonExistingEntity() throws Exception {
        var id = UUID.randomUUID().toString();
        var first = Timestamps.fromMillis(System.currentTimeMillis());
        var second = Timestamps.fromMillis(System.currentTimeMillis() + 1000);
        client.publishWindspeedReport(StationWindspeedPublishRequest.newBuilder()
                .setStationId(id)
                .addWindspeedMeasurements(WindspeedMeasurement.newBuilder()
                        .setMeasurementTime(first)
                        .setWindspeedMPerS(30)
                        .build())
                .addWindspeedMeasurements(WindspeedMeasurement.newBuilder()
                        .setMeasurementTime(second)
                        .setWindspeedMPerS(40)
                        .build())
                .build()).toCompletableFuture().get(2, SECONDS);

        Thread.sleep(1000);

        var state = viewClient
                .getStationState(WeatherstationView.StationByIdRequest.newBuilder()
                        .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);
        System.out.println("received response " + state);

        assertEquals(id,state.getStationId());
        assertEquals("",state.getStationName());
        assertEquals(0, state.getLatitude(), 0.001);
        assertEquals(0, state.getLongitude(),0.001);
        assertEquals(0, state.getAverageTempCelciusOverall(),0.001);
        assertEquals(35, state.getAverageWindspeedOverall(),0.001);
    }
}