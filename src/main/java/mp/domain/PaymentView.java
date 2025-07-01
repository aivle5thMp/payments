package mp.domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "PaymentView_table")
@Data
public class PaymentView {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;
    private UUID userId;
    private String item;
    private int amount;
    private String paymentStatus;
    private String status;
    private LocalDateTime createdAt;
}
