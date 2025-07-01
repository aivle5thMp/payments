package mp.domain;

import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import mp.domain.*;
import mp.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class Purchased extends AbstractEvent {

    private UUID id;
    private UUID userId;
    private String item;
    private int amount;
    private String status;
    private LocalDateTime createdAt;

    public Purchased(Payment aggregate) {
        super(aggregate);
    }

    public Purchased() {
        super();
    }
}
//>>> DDD / Domain Event
