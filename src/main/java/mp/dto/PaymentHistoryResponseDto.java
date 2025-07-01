package mp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponseDto {
    private UUID paymentsId;
    private String item;
    private Integer amount;
    private String status;
    private LocalDateTime createdAt;
}
