package mp.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import mp.config.kafka.KafkaProcessor;

@Component
public class PaymentEventPublisher {
     private static KafkaProcessor kafkaProcessor;

    @Autowired
    public PaymentEventPublisher(KafkaProcessor kafkaProcessor) {
        PaymentEventPublisher.kafkaProcessor = kafkaProcessor;
    }

    public static void publishEvents(Payment payment) {
        // 1. Purchased (기존 토픽 - 필요 없으면 삭제)
        Purchased purchased = new Purchased(payment);
        purchased.setId(payment.getId());
        kafkaProcessor.pointsOutbound().send(
            MessageBuilder.withPayload(purchased)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .setHeader("type", purchased.getEventType())
                .build()
        );
        // 2. Subscribed (구독 토픽)
        Subscribed subscribed = new Subscribed(payment);
        subscribed.setId(payment.getId());
        kafkaProcessor.subscriptionOutbound().send(
            MessageBuilder.withPayload(subscribed)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .setHeader("type", subscribed.getEventType())
                .build()
        );
    }
}
