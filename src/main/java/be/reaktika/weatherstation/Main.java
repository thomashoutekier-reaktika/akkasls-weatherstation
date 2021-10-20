package be.reaktika.weatherstation;


import be.reaktika.weatherstation.action.WeatherStationToTopicServiceAction;
import be.reaktika.weatherstation.api.WeatherStationApiAction;
import be.reaktika.weatherstation.domain.WeatherStation;
import be.reaktika.weatherstation.domain.aggregations.*;

import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremes;
import be.reaktika.weatherstation.domain.geocoding.GeoCoding;
import be.reaktika.weatherstation.ports.OpenCageGeoCodingService;
import be.reaktika.weatherstation.view.*;
import be.reaktika.weatherstation.view.WeatherStationExtremesView;
import com.akkaserverless.javasdk.AkkaServerless;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {


                    /*
//the entities
            .registerEventSourcedEntity(WeatherStationEntity.class,
                    WeatherStationDomain.getDescriptor().findServiceByName("WeatherStationEntityService"),
                    WeatherStationDomain.getDescriptor())
            .registerValueEntity(ExtremesEntity.class,
                    WeatherStationExtremesAggregation.getDescriptor().findServiceByName("WeatherStationExtremesEntityService"),
                    WeatherStationExtremesAggregation.getDescriptor())
            .registerValueEntity(GeoCodingEntity.class,
                    WeatherstationGeocoding.getDescriptor().findServiceByName("GeoCodingEntityService"),
                    WeatherstationGeocoding.getDescriptor())
//the actions
            .registerAction(WeatherStationPublishApiImpl.class,
                    WeatherStationApi.getDescriptor().findServiceByName("WeatherStationApiService"),
                    WeatherStationApi.getDescriptor())
            .registerAction(WeatherStationDataConsumeAction.class,
                    WeatherStationDataConsume.getDescriptor().findServiceByName("WeatherStationConsumeService"),
                    WeatherStationDataConsume.getDescriptor())
            .registerAction(GeoCodingPublishAction.class,
                    WeatherstationGeocodingPublishing.getDescriptor().findServiceByName("GeoCodingPublishService"),
                    WeatherstationGeocodingPublishing.getDescriptor())
            .registerAction(WeatherStationDataPublishAction.class,
                    WeatherStationPublish.getDescriptor().findServiceByName("WeatherStationPublishService"),
                    WeatherStationPublish.getDescriptor())
//the views
            .registerView(WeatherStationAverageViewImpl.class,
                    WeatherstationAverageView.getDescriptor().findServiceByName("WeatherStationOverallAverage"),
                    "weatherstationOverallAverageView",
                    WeatherstationAverageView.getDescriptor())
            .registerView(
                    WeatherStationExtremesViewImpl.class,
                    WeatherstationExtremesView.getDescriptor().findServiceByName("WeatherStationExtremes"),
                    "extemesView",
                    WeatherstationExtremesView.getDescriptor())

            ;
            */
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static AkkaServerless createAkkaServerless() {
        return AkkaServerlessFactory.withComponents(
                GeoCoding::new,
                WeatherStation::new,
                WeatherStationExtremes::new,
                WeatherStationApiAction::new,
                WeatherStationExtremesView::new,
                WeatherStationOverallAverageView::new,
                WeatherStationToTopicServiceAction::new);

    }


    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");

        //create geocoding implementation.
        GeoCodingService geoCoding = new OpenCageGeoCodingService(ConfigFactory.load());
        GeoCodingService.setInstance(geoCoding);

        createAkkaServerless().start().toCompletableFuture().get();
    }
}