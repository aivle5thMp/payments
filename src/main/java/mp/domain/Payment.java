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
    private int usedPoint;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PostPersist
    public void onPostPersist() {
        // 엔티티의 책임: 이벤트 객체 생성, 외부 시스템 호출은 서비스에 위임!
        PaymentEventPublisher.publishEvents(this);
    }

    public static PaymentRepository repository() {
        PaymentRepository paymentRepository = PaymentsApplication.applicationContext.getBean(
            PaymentRepository.class
        );
        return paymentRepository;
    }
}