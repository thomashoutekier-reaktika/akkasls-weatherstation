package be.reaktika.domain;

import be.reaktika.Main;
import be.reaktika.WeatherStationPublishApi;
import be.reaktika.WeatherStationPublishServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.Instant;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.*;
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
    private final WeatherStationPublishServiceClient client;
    
    public WeatherStationIntegrationTest() {
        client = WeatherStationPublishServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }
    
    @Test
    public void registerStationOnNonExistingEntity() throws Exception {
        var id = UUID.randomUUID().toString();
        client.registerStation(WeatherStationPublishApi.StationRegistrationCommand.newBuilder()
                    .setStationId(id)
                .setStationName(id + "name")
                .setLatitude(10)
                .setLongitude(20)
                .build()).toCompletableFuture().get(2, SECONDS);

        var state = client.getState(WeatherStationPublishApi.GetStationStateCommand.newBuilder()
                    .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);
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
        client.publishTemperatureReport(WeatherStationPublishApi.StationTemperatureCommand.newBuilder()
                .setStationId(id)
                .addTempMeasurements(WeatherStationPublishApi.TemperatureMeasurements.newBuilder()
                        .setTemperatureCelcius(10)
                        .setMeasurementTime(first))
                .addTempMeasurements(WeatherStationPublishApi.TemperatureMeasurements.newBuilder()
                        .setTemperatureCelcius(20)
                        .setMeasurementTime(second))
                .build()).toCompletableFuture().get(2, SECONDS);

        var state = client.getState(WeatherStationPublishApi.GetStationStateCommand.newBuilder()
                .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);
        assertEquals(id,state.getStationId());
        assertEquals("unknown",state.getStationName());
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
        client.publishWindspeedReport(WeatherStationPublishApi.StationWindspeedCommand.newBuilder()
                .setStationId(id)
                .addWindspeedMeasurements(WeatherStationPublishApi.WindspeedMeasurement.newBuilder()
                        .setMeasurementTime(first)
                        .setWindspeedMPerS(30)
                        .build())
                .addWindspeedMeasurements(WeatherStationPublishApi.WindspeedMeasurement.newBuilder()
                        .setMeasurementTime(second)
                        .setWindspeedMPerS(40)
                        .build())
                .build()).toCompletableFuture().get(2, SECONDS);

        var state = client.getState(WeatherStationPublishApi.GetStationStateCommand.newBuilder()
                .setStationId(id).build()).toCompletableFuture().get(2, SECONDS);
        assertEquals(id,state.getStationId());
        assertEquals("unknown",state.getStationName());
        assertEquals(0, state.getLatitude(), 0.001);
        assertEquals(0, state.getLongitude(),0.001);
        assertEquals(0, state.getAverageTempCelciusOverall(),0.001);
        assertEquals(35, state.getAverageWindspeedOverall(),0.001);
    }
}