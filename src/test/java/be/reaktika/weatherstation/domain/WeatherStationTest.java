package be.reaktika.weatherstation.domain;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremes;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesTestKit;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;


public class WeatherStationTest {

    @Test
    public void registerStationTest() {
        WeatherStationTestKit testKit = WeatherStationTestKit.of(WeatherStation::new);
        var command = WeatherStationDomain.StationRegistrationCommand.newBuilder()
                .setStationId("stationId")
                .setStationName("name")
                .setLatitude(10)
                .setLongitude(20).build();
        testKit.registerStation(command);

        assertEquals("stationId",((WeatherStationDomain.StationRegistered)testKit.getAllEvents().get(0)).getStationId());

    }
   /*
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

    */


}