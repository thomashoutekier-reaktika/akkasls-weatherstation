package be.reaktika.domain;

import be.reaktika.WeatherStationPublishApi;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.google.protobuf.Timestamp;
import org.junit.Test;
import org.mockito.Mockito;


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
                .addTemperature(WeatherStationDomain.Temperature.newBuilder()
                        .setTemperatureCelcius(10)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .addTemperature(WeatherStationDomain.Temperature.newBuilder()
                        .setTemperatureCelcius(20)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .build();

        Mockito.verify(context).emit(event);

    }
    
    @Test
    public void publishWindspeedReportTest() {
        entity = new WeatherStationImpl(entityId);

        entity.publishWindspeedReportWithReply(WeatherStationPublishApi.StationWindspeedCommand.newBuilder()
                .setStationId("stationId")
                .addWindspeedMeasurements(WeatherStationPublishApi.WindspeedMeasurement.newBuilder()
                        .setWindspeedMPerS(10)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .addWindspeedMeasurements(WeatherStationPublishApi.WindspeedMeasurement.newBuilder()
                        .setWindspeedMPerS(20)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .build(), context);

        WeatherStationDomain.WindspeedsAdded event = WeatherStationDomain.WindspeedsAdded.newBuilder()
                .setStationId("stationId")
                .addWindspeed(WeatherStationDomain.Windspeed.newBuilder()
                        .setWindspeedMPerS(10)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .addWindspeed(WeatherStationDomain.Windspeed.newBuilder()
                        .setWindspeedMPerS(20)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .build();
        
        Mockito.verify(context).emit(event);
    }
}