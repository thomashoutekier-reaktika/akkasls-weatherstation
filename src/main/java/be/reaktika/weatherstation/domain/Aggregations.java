package be.reaktika.weatherstation.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.akkaserverless.javasdk.valueentity.CommandHandler;
import com.akkaserverless.javasdk.valueentity.ValueEntity;
import be.reaktika.weatherstation.domain.WeatherStationAggregations.*;
import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ValueEntity(entityType = "aggregations")
public class Aggregations {

    private final static Logger logger = LoggerFactory.getLogger(Aggregations.class);
    private final AggregationType type;


    public Aggregations(@EntityId String type) {
        this.type = AggregationType.valueOf(type);
    }


    @CommandHandler
    public Reply<Empty> registerTemperature(RecordTemperatureCommand command, CommandContext<WeatherStationAggregations.Aggregations> ctx) {
        logger.info("registering Temperature " + command);
        return Reply.message(Empty.getDefaultInstance());
    }


    @CommandHandler
    public Reply<Empty> RegisterWindspeed(RecordWindspeedCommand command, CommandContext<WeatherStationAggregations.Aggregations> ctx) {
        logger.info("registering Windspeed " + command);
        return Reply.message(Empty.getDefaultInstance());
    }
}
