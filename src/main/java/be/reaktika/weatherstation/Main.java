package be.reaktika.weatherstation;

import be.reaktika.weatherstation.api.WeatherStationApi;
import be.reaktika.weatherstation.domain.WeatherStationDomain;
import be.reaktika.weatherstation.actions.api.WeatherStationPublishApiImpl;
import be.reaktika.weatherstation.domain.WeatherStationEntity;
import be.reaktika.weatherstation.view.WeatherStationViewImpl;
import be.reaktika.weatherstation.view.WeatherstationView;
import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    public static final AkkaServerless SERVICE =
            new AkkaServerless()
            .registerEventSourcedEntity(WeatherStationEntity.class,
                    WeatherStationDomain.getDescriptor().findServiceByName("WeatherStationEntityService"),
                    WeatherStationDomain.getDescriptor())
            .registerAction(WeatherStationPublishApiImpl.class,
                    WeatherStationApi.getDescriptor().findServiceByName("WeatherStationApiService"),
                    WeatherStationApi.getDescriptor()
                    )
            .registerView(WeatherStationViewImpl.class,
                    WeatherstationView.getDescriptor().findServiceByName("StationByIdView"),
                    "weatherstationByid",
                    WeatherstationView.getDescriptor());
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
            SERVICE.start().toCompletableFuture().get();
    }
}