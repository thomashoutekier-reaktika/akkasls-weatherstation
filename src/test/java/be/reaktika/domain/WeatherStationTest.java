package be.reaktika.domain;

import be.reaktika.WeatherStationPublishApi;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.google.protobuf.Timestamp;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertThrows;

public class WeatherStationTest {
    private String entityId = "entityId1";
    private WeatherStationImpl entity;
    private CommandContext context = Mockito.mock(CommandContext.class);
    
    private class MockedContextFailure extends RuntimeException {};
    
    @Test
    public void registerStationTest() {
        entity = new WeatherStationImpl(entityId);

        entity.registerStationWithReply(WeatherStationPublishApi.StationRegistrationCommand.newBuilder()
                .setStationId("stationId")
                .setStationName("name")
                .setLatitude(10)
                .setLongitude(20)
                .build(), context);

        WeatherStationDomain.StationRegistered event = WeatherStationDomain.StationRegistered.newBuilder()
                .setStationId("stationId")
                .setStationName("name")
                .setLatitude(10)
                .setLongitude(20)
                .build();
        Mockito.verify(context).emit(event);
    }
    
    @Test
    public void publishTemperatureReportTest() {
        entity = new WeatherStationImpl(entityId);

        var command = WeatherStationPublishApi.StationTemperatureCommand.newBuilder()
                .setStationId("stationId")
                    .addTempMeasurements(WeatherStationPublishApi.TemperatureMeasurements.newBuilder().setTemperatureCelcius(10).setMeasurementTime(Timestamp.newBuilder().build()))
                    .addTempMeasurements(WeatherStationPublishApi.TemperatureMeasurements.newBuilder().setTemperatureCelcius(20).setMeasurementTime(Timestamp.newBuilder().build()))
                .build();
        entity.publishTemperatureReportWithReply(command, context);


        WeatherStationDomain.TemperaturesCelciusAdded event = WeatherStationDomain.TemperaturesCelciusAdded.newBuilder()
                    .setStationId("stationId")
                .addTemperature(WeatherStationDomain.Temperature.newBuilder().setTemperatureCelcius(10).setMeasurementTime(Timestamp.newBuilder().build()).build())
                .addTemperature(WeatherStationDomain.Temperature.newBuilder().setTemperatureCelcius(20).setMeasurementTime(Timestamp.newBuilder().build()).build())
                .build();

        Mockito.verify(context).emit(event);

    }
    
    @Test
    public void publishWindspeedReportTest() {
        entity = new WeatherStationImpl(entityId);
        
        Mockito.when(context.fail("The command handler for `PublishWindspeedReport` is not implemented, yet"))
            .thenReturn(new MockedContextFailure());
        
        // TODO: set fields in command, and update assertions to match implementation
        assertThrows(MockedContextFailure.class, () -> {
            entity.publishWindspeedReportWithReply(WeatherStationPublishApi.StationWindspeedCommand.newBuilder().build(), context);
        });
        
        // TODO: if you wish to verify events:
        //    Mockito.verify(context).emit(event);
    }
}