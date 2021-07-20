package be.reaktika.weatherstation;

import be.reaktika.weatherstation.actions.GeoCodingPublishAction;
import be.reaktika.weatherstation.actions.WeatherStationDataPublishAction;
import be.reaktika.weatherstation.actions.api.WeatherStationPublishApiImpl;
import be.reaktika.weatherstation.api.WeatherStationApi;
import be.reaktika.weatherstation.domain.*;
import be.reaktika.weatherstation.ports.geocoding.WeatherstationGeocoding;
import be.reaktika.weatherstation.ports.geocoding.publishing.WeatherstationGeocodingPublishing;
import be.reaktika.weatherstation.view.WeatherStationAggregationsView;
import be.reaktika.weatherstation.view.WeatherStationAverageViewImpl;
import be.reaktika.weatherstation.view.WeatherstationAverageView;
import be.reaktika.weatherstation.view.WeatherstationExtremesView;
import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    public static final AkkaServerless SERVICE =
            new AkkaServerless()
            .registerEventSourcedEntity(WeatherStationEntity.class,
                    WeatherStationDomain.getDescriptor().findServiceByName("WeatherStationEntityService"),
                    WeatherStationDomain.getDescriptor())
            .registerValueEntity(AggregationsEntity.class,
                    WeatherStationAggregations.getDescriptor().findServiceByName("WeatherStationAggregationService"),
                    WeatherStationAggregations.getDescriptor())
            .registerValueEntity(GeoCodingEntity.class,
                    WeatherstationGeocoding.getDescriptor().findServiceByName("GeoCodingEntityService"),
                    WeatherstationGeocoding.getDescriptor())
            .registerAction(WeatherStationPublishApiImpl.class,
                    WeatherStationApi.getDescriptor().findServiceByName("WeatherStationApiService"),
                    WeatherStationApi.getDescriptor())
            .registerAction(GeoCodingPublishAction.class,
                    WeatherstationGeocodingPublishing.getDescriptor().findServiceByName("GeoCodingPublishService"),
                    WeatherstationGeocodingPublishing.getDescriptor())
            .registerAction(WeatherStationDataPublishAction.class,
                    WeatherStationPublish.getDescriptor().findServiceByName("WeatherStationPublishService"),
                    WeatherStationPublish.getDescriptor())
            .registerView(WeatherStationAverageViewImpl.class,
                    WeatherstationAverageView.getDescriptor().findServiceByName("WeatherStationOverallAverage"),
                    "weatherstationOverallAverage",
                    WeatherstationAverageView.getDescriptor())
            .registerView(
                    WeatherStationAggregationsView.class,
                    WeatherstationExtremesView.getDescriptor().findServiceByName("WeatherStationExtremes"),
                    "aggregations",
                    WeatherstationExtremesView.getDescriptor())

            ;
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
            SERVICE.start().toCompletableFuture().get();
    }
}