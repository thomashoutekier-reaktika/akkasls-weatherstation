package be.reaktika;

import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static be.reaktika.MainComponentRegistrations.withGeneratedComponentsAdded;

public final class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
        withGeneratedComponentsAdded(new AkkaServerless())
                .start().toCompletableFuture().get();
    }
}