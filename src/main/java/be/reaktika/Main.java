package be.reaktika;

import be.reaktika.domain.WeatherStationDomain;
import be.reaktika.domain.WeatherStationImpl;
import be.reaktika.weatherstation.actions.WeatherStationPublishApiImpl;
import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    public static final AkkaServerless SERVICE =
            new AkkaServerless()
            .registerEventSourcedEntity(WeatherStationImpl.class,
                    WeatherStationDomain.getDescriptor().findServiceByName("WeatherStationService"),
                    WeatherStationDomain.getDescriptor())
            .registerAction(WeatherStationPublishApiImpl.class,
                    WeatherStationPublishApi.getDescriptor().findServiceByName("WeatherStationPublishService"),
                    WeatherStationPublishApi.getDescriptor()
                    );
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
            SERVICE.start().toCompletableFuture().get();
    }
}