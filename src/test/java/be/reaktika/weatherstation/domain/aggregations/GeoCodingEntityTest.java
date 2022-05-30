package be.reaktika.weatherstation.domain.aggregations;


import be.reaktika.weatherstation.action.WeatherStationToTopic;
import be.reaktika.weatherstation.domain.geocoding.GeoCoding;
import be.reaktika.weatherstation.domain.geocoding.GeoCodingTestKit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeoCodingEntityTest {


    private GeoCodingService geoCodingService = Mockito.mock(GeoCodingService.class);


    @Before
    public void init(){
        Mockito.when(geoCodingService.getCountryCode(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Optional.of("be"));
        GeoCodingService.setInstance(geoCodingService);
    }



    @Test
    public void stationRegistrationShouldGeoCodeLocation() {
        GeoCodingTestKit kit = GeoCodingTestKit.of(GeoCoding::new);
        var data = WeatherStationToTopic.WeatherStationData.newBuilder()
                .setStationId("id1")
                .setStationName("name1")
                .setLongitude(2.)
                .setLatitude(2.)
                .build();
        var command = WeatherStationAggregation.AddToAggregationCommand.newBuilder()
                .setWeatherdata(data)
                .build();

        kit.registerData(command);

        assertTrue(kit.getState().containsStationIdToCountry("id1"));
        assertEquals("be", kit.getState().getStationIdToCountryOrDefault("id1", null));

    }

}
