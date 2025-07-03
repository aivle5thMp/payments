package mp.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface KafkaProcessor {
    String INPUT = "event-in";
    String OUTPUT = "event-out";

    String POINTS_OUTPUT = "points-out";
    String SUBSCRIPTION_OUTPUT = "subscription-out";
    
    @Output("points-out")
    MessageChannel pointsOut();
    @Output(POINTS_OUTPUT)
    MessageChannel pointsOutbound();

    @Output(SUBSCRIPTION_OUTPUT)
    MessageChannel subscriptionOutbound();
    
    @Input(INPUT)
    SubscribableChannel inboundTopic();

    @Output(OUTPUT)
    MessageChannel outboundTopic();
}
