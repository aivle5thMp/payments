package mp.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeRequestDto {
    private UUID userId;
    private int point;  // 충전할 포인트
}