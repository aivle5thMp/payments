package mp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class PaymentRequestDto {
    private UUID userId;
    private String item;
    private Integer amount;
    private Integer point;
}
