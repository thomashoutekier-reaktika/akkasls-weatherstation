package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.ports.geocoding.WeatherstationGeocoding;
import com.akkaserverless.javasdk.ServiceCallFactory;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.google.protobuf.Timestamp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class WeatherStationTest {
    private String entityId = "entityId1";
    private WeatherStationEntity entity;
    private CommandContext context = Mockito.mock(CommandContext.class);
    private ServiceCallFactory factoryMock = Mockito.mock(ServiceCallFactory.class);




    private class MockedContextFailure extends RuntimeException {};

    @Before
    public void init(){
        Mockito.when(context.serviceCallFactory()).thenReturn(factoryMock);

    }

    @Test
    public void registerStationTest() {
        entity = new WeatherStationEntity(entityId, context);

        entity.registerStation(WeatherStationDomain.StationRegistrationCommand.newBuilder()
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
        entity = new WeatherStationEntity(entityId, context);

        var command = WeatherStationDomain.StationTemperatureCommand.newBuilder()
                .setStationId("stationId")
                    .addTempMeasurements(WeatherStationDomain.TemperatureMeasurements.newBuilder().setTemperatureCelcius(10).setMeasurementTime(Timestamp.newBuilder().build()))
                    .addTempMeasurements(WeatherStationDomain.TemperatureMeasurements.newBuilder().setTemperatureCelcius(20).setMeasurementTime(Timestamp.newBuilder().build()))
                .build();
        entity.publishTemperatureReport(command, context);


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
        entity = new WeatherStationEntity(entityId, context);

        entity.publishWindspeedReport(WeatherStationDomain.StationWindspeedCommand.newBuilder()
                .setStationId("stationId")
                .addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
                        .setWindspeedMPerS(10)
                        .setMeasurementTime(Timestamp.newBuilder().build()).build())
                .addWindspeedMeasurements(WeatherStationDomain.WindspeedMeasurement.newBuilder()
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