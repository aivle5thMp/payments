package mp.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mp.PaymentsApplication;
import mp.config.kafka.KafkaProcessor;
import mp.domain.Purchased;
import mp.domain.Subscribed;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.MimeTypeUtils;

//<<< Clean Arch / Outbound Adaptor
public class AbstractEvent {

    String eventType;
    Long timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        BeanUtils.copyProperties(aggregate, this);
    }

    public AbstractEvent() {
        this.setEventType(this.getClass().getSimpleName());
        this.timestamp = System.currentTimeMillis();
    }

    public void publish() {
        /**
         * spring streams 방식
         */
        KafkaProcessor processor = PaymentsApplication.applicationContext.getBean(
            KafkaProcessor.class
        );
        
        System.out.println("=== Publishing Event ===");
        System.out.println("Event Type: " + this.getEventType());
        System.out.println("Timestamp: " + this.getTimestamp());
        System.out.println("Event Data: " + this.toJson());

        // 이벤트 타입에 따라 적절한 채널로만 발행
        if (this instanceof Purchased) {
            System.out.println("Publishing Purchased event to points channel (payment.points.v1)");
            processor.pointsOutbound().send(
                MessageBuilder
                    .withPayload(this)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", getEventType())
                    .build()
            );
        } else if (this instanceof Subscribed) {
            System.out.println("Publishing Subscribed event to subscription channel (subscription-out)");
            processor.subscriptionOutbound().send(
                MessageBuilder
                    .withPayload(this)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", getEventType())
                    .build()
            );
        } else {
            System.out.println("Publishing unknown event type to default channel (event-out)");
            processor.outboundTopic().send(
                MessageBuilder
                    .withPayload(this)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .setHeader("type", getEventType())
                    .build()
            );
        }
        
        System.out.println("Event published successfully");
        System.out.println("=== Event Publishing Complete ===");
    }

    public void publishAfterCommit() {
        System.out.println("=== Registering Event for After-Commit Publishing ===");
        System.out.println("Event Type: " + this.getEventType());
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    System.out.println("=== Transaction Completed (status: " + status + ") ===");
                    if (status == STATUS_COMMITTED) {
                        System.out.println("Transaction committed, publishing event: " + getEventType());
                        AbstractEvent.this.publish();
                    } else {
                        System.out.println("Transaction not committed, event not published: " + getEventType());
                    }
                }
            }
        );
        System.out.println("Event registered for after-commit publishing");
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean validate() {
        return getEventType().equals(getClass().getSimpleName());
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        return json;
    }
}
//>>> Clean Arch / Outbound Adaptor
