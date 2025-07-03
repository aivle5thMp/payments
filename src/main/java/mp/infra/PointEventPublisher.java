package mp.infra;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import mp.config.kafka.KafkaProcessor;

@Component
@RequiredArgsConstructor
public class PointEventPublisher {
    private final KafkaProcessor kafkaProcessor; // interface KafkaProcessor

    public void publishPointIncreased(PointIncreased event) {
        MessageChannel outputChannel = kafkaProcessor.pointsOut();
        outputChannel.send(MessageBuilder.withPayload(event).build());
    }
}
