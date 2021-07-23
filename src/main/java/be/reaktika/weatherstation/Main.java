package be.reaktika.weatherstation;

import be.reaktika.weatherstation.actions.GeoCodingPublishAction;
import be.reaktika.weatherstation.actions.WeatherStationDataConsumeAction;
import be.reaktika.weatherstation.actions.WeatherStationDataPublishAction;
import be.reaktika.weatherstation.actions.api.WeatherStationPublishApiImpl;
import be.reaktika.weatherstation.api.WeatherStationApi;
import be.reaktika.weatherstation.domain.*;
import be.reaktika.weatherstation.domain.aggregations.ExtremesEntity;
import be.reaktika.weatherstation.domain.aggregations.GeoCodingEntity;
import be.reaktika.weatherstation.domain.aggregations.GeoCodingService;
import be.reaktika.weatherstation.domain.aggregations.WeatherStationExtremesAggregation;
import be.reaktika.weatherstation.domain.geocoding.WeatherstationGeocoding;
import be.reaktika.weatherstation.domain.geocoding.publishing.WeatherstationGeocodingPublishing;
import be.reaktika.weatherstation.ports.OpenCageGeoCodingService;
import be.reaktika.weatherstation.view.WeatherStationExtremesViewImpl;
import be.reaktika.weatherstation.view.WeatherStationAverageViewImpl;
import be.reaktika.weatherstation.view.WeatherstationAverageView;
import be.reaktika.weatherstation.view.WeatherstationExtremesView;
import com.akkaserverless.javasdk.AkkaServerless;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    public static final AkkaServerless SERVICE =
            new AkkaServerless()
//the entities
            .registerEventSourcedEntity(WeatherStationEntity.class,
                    WeatherStationDomain.getDescriptor().findServiceByName("WeatherStationEntityService"),
                    WeatherStationDomain.getDescriptor())
            .registerValueEntity(ExtremesEntity.class,
                    WeatherStationExtremesAggregation.getDescriptor().findServiceByName("WeatherStationAggregationService"),
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
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");

        //create geocoding implementation.
        GeoCodingService geoCoding = new OpenCageGeoCodingService(ConfigFactory.load());
        GeoCodingService.setInstance(geoCoding);

        SERVICE.start().toCompletableFuture().get();
    }
}