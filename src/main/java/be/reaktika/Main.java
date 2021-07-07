package be.reaktika;

import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.reaktika.MainComponentRegistrations.withGeneratedComponentsAdded;

public final class Main {

    public static final AkkaServerless SERVICE = withGeneratedComponentsAdded(new AkkaServerless());
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
            SERVICE.start().toCompletableFuture().get();
    }
}