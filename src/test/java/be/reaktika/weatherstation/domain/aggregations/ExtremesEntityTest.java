package be.reaktika.weatherstation.domain.aggregations;

import be.reaktika.weatherstation.domain.WeatherStationPublish;
import com.akkaserverless.javasdk.ServiceCallFactory;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.google.protobuf.Timestamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;

public class ExtremesEntityTest {
    private CommandContext context = Mockito.mock(CommandContext.class);
    private ServiceCallFactory factoryMock = Mockito.mock(ServiceCallFactory.class);
    private ExtremesEntity entity;

    @Before
    public void init(){
        Mockito.reset(context);
        Mockito.reset(factoryMock);
        Mockito.when(context.serviceCallFactory()).thenReturn(factoryMock);
    }

    @Test
    public void registerStation(){
        entity = new ExtremesEntity(WeatherStationAggregation.AggregationType.EXTREMES.name());
        var data = WeatherStationPublish.WeatherStationData.newBuilder()
                .setStationId("id1")
                .setStationName("name1")
                .setLongitude(2.)
                .setLongitude(2.)
                .build();
        var command = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
                .setWeatherdata(data)
                .build();
        entity.registerData(command,context);
        Mockito.verify(context,Mockito.never()).getState();
        Mockito.verify(context,Mockito.never()).updateState(Mockito.any(WeatherStationExtremesAggregation.WeatherStationExtremes.class));
    }

    @Test
    public void registerTemperatureOnEmptyState(){
        entity = new ExtremesEntity(WeatherStationAggregation.AggregationType.EXTREMES.name());
        var temp1 = WeatherStationPublish.WeatherStationTemperatures
                .newBuilder()
                .setMeasurementTime(Timestamp.newBuilder().build())
                .setTemperatureCelcius(10)
                .build();
        var temp2 = WeatherStationPublish.WeatherStationTemperatures
                .newBuilder()
                .setMeasurementTime(Timestamp.newBuilder().build())
                .setTemperatureCelcius(5)
                .build();

        var data1 = WeatherStationPublish.WeatherStationData.newBuilder()
                .setStationId("id1")
                .addTemperatures(temp1)
                .addTemperatures(temp2)
                .build();
        var command1 = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
                .setWeatherdata(data1)
                .build();

        var statecapture = ArgumentCaptor.forClass(WeatherStationExtremesAggregation.WeatherStationExtremes.class);
        entity.registerData(command1,context);
        Mockito.verify(context).updateState(statecapture.capture());

        Assert.assertEquals(10,statecapture.getValue().getMaxTemperature().getCurrent().getMeasuredTemperature(),0.001);
        Assert.assertEquals(5,statecapture.getValue().getMinTemperature().getCurrent().getMeasuredTemperature(),0.001);
        Assert.assertEquals("id1",statecapture.getValue().getMinTemperature().getCurrent().getStationId());
        Assert.assertEquals("id1",statecapture.getValue().getMaxTemperature().getCurrent().getStationId());


    }

    @Test
    public void updateMaximumAndMinimum(){
        entity = new ExtremesEntity(WeatherStationAggregation.AggregationType.EXTREMES.name());
        var max = WeatherStationAggregation.TemperatureMeasurement.newBuilder()
                .setStationId("id1")
                .setMeasuredTemperature(30.)
                .build();
        var min = WeatherStationAggregation.TemperatureMeasurement.newBuilder()
                .setStationId("id1")
                .setMeasuredTemperature(-10)
                .build();
        var currentExtremes = WeatherStationExtremesAggregation.WeatherStationExtremes
                .newBuilder()
                .setMaxTemperature(WeatherStationExtremesAggregation.TemperatureRecord.newBuilder().setCurrent(max))
                .setMinTemperature(WeatherStationExtremesAggregation.TemperatureRecord.newBuilder().setCurrent(min))
                .build();

        Mockito.when(context.getState()).thenReturn(Optional.of(currentExtremes));

        var temp1 = WeatherStationPublish.WeatherStationTemperatures
                .newBuilder()
                .setMeasurementTime(Timestamp.newBuilder().build())
                .setTemperatureCelcius(40)
                .build();
        var temp2 = WeatherStationPublish.WeatherStationTemperatures
                .newBuilder()
                .setMeasurementTime(Timestamp.newBuilder().build())
                .setTemperatureCelcius(-20)
                .build();

        var data = WeatherStationPublish.WeatherStationData.newBuilder()
                .setStationId("id2")
                .addTemperatures(temp1)
                .addTemperatures(temp2)
                .build();
        var command = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
                .setWeatherdata(data)
                .build();

        var statecapture = ArgumentCaptor.forClass(WeatherStationExtremesAggregation.WeatherStationExtremes.class);
        entity.registerData(command,context);
        Mockito.verify(context).updateState(statecapture.capture());

        Assert.assertEquals(40,statecapture.getValue().getMaxTemperature().getCurrent().getMeasuredTemperature(),0.001);
        Assert.assertEquals(-20,statecapture.getValue().getMinTemperature().getCurrent().getMeasuredTemperature(),0.001);
        Assert.assertEquals("id2",statecapture.getValue().getMaxTemperature().getCurrent().getStationId());
        Assert.assertEquals("id2",statecapture.getValue().getMinTemperature().getCurrent().getStationId());


    }
}
