package mp.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mp.PaymentsApplication;
import mp.domain.Purchased;
import mp.domain.Subscribed;


//@Data
//<<< DDD / Aggregate Root

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    private UUID userId;
    private String item;
    private int amount;
    private String status;

    @Column(name="createdAt")
    private LocalDateTime createdAt;

    @PostPersist
    public void onPostPersist() {
        Purchased purchased = new Purchased(this);
        purchased.setId(this.getId());
        purchased.publishAfterCommit();

        Subscribed subscribed = new Subscribed(this);
        purchased.setId(this.getId());
        subscribed.publishAfterCommit();
    }

    public static PaymentRepository repository() {
        PaymentRepository paymentRepository = PaymentsApplication.applicationContext.getBean(
            PaymentRepository.class
        );
        return paymentRepository;
    }
}
//>>> DDD / Aggregate Root
