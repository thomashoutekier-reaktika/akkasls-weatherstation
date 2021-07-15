package be.reaktika.weatherstation;

import be.reaktika.weatherstation.actions.api.WeatherStationPublishApiImpl;
import be.reaktika.weatherstation.api.WeatherStationApi;
import be.reaktika.weatherstation.domain.Aggregations;
import be.reaktika.weatherstation.domain.WeatherStationAggregations;
import be.reaktika.weatherstation.domain.WeatherStationDomain;
import be.reaktika.weatherstation.domain.WeatherStationEntity;
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
            .registerValueEntity(Aggregations.class,
                    WeatherStationAggregations.getDescriptor().findServiceByName("WeatherStationAggregationService"),
                    WeatherStationAggregations.getDescriptor())
            .registerAction(WeatherStationPublishApiImpl.class,
                    WeatherStationApi.getDescriptor().findServiceByName("WeatherStationApiService"),
                    WeatherStationApi.getDescriptor()
                    )
            .registerView(WeatherStationAverageViewImpl.class,
                    WeatherstationAverageView.getDescriptor().findServiceByName("WeatherStationOverallAverage"),
                    "weatherstationOverallAverage",
                    WeatherstationAverageView.getDescriptor())
            .registerView(
                    WeatherStationAggregationsView.class,
                    WeatherstationExtremesView.getDescriptor().findServiceByName("WeatherStationExtremes"),
                    "aggregations",
                    WeatherstationExtremesView.getDescriptor());
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
            SERVICE.start().toCompletableFuture().get();
    }
}