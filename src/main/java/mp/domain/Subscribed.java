package mp.domain;

import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import mp.domain.*;
import mp.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class Subscribed extends AbstractEvent {

    private UUID id;
    private UUID userId;
    private String item;
    private int amount;
    private String status;
    private LocalDateTime createdAt;

    public Subscribed(Payment aggregate) {
        super(aggregate);
    }

    public Subscribed() {
        super();
    }
}
//>>> DDD / Domain Event
