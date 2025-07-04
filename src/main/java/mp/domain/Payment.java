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

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PostPersist
    public void onPostPersist() {
        System.out.println("=== Payment.onPostPersist() ===");
        System.out.println("Payment ID: " + this.getId());
        System.out.println("User ID: " + this.getUserId());
        System.out.println("Status: " + this.getStatus());
        System.out.println("Amount: " + this.getAmount());
        System.out.println("Item: " + this.getItem());
        
        // 결제 상태가 승인된 경우에만 이벤트 발행
        if ("APPROVED".equals(this.status)) {
            // 구독 결제인 경우
            if (this.item != null && this.item.toLowerCase().contains("subscription")) {
                System.out.println("Publishing Subscribed event for subscription payment...");
                Subscribed subscribed = new Subscribed(this);
                subscribed.publishAfterCommit();
                System.out.println("Subscribed event published successfully");
            }
            // 일반 결제인 경우
            else {
                System.out.println("Publishing Purchased event for regular payment...");
                Purchased purchased = new Purchased(this);
                purchased.publishAfterCommit();
                System.out.println("Purchased event published successfully");
            }
        } else {
            System.out.println("Payment status is not APPROVED. No events will be published.");
        }
        System.out.println("=== Payment.onPostPersist() End ===");
    }

    public static PaymentRepository repository() {
        PaymentRepository paymentRepository = PaymentsApplication.applicationContext.getBean(
            PaymentRepository.class
        );
        return paymentRepository;
    }
}