package mp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mp.infra.AbstractEvent;

@Getter @Setter
@NoArgsConstructor
public class PointUsed extends AbstractEvent{
    private String userId; // snake_case, 토픽 메시지 명세 따라감
    private int point;

    public PointUsed(Payment payment) {
        super(payment);
        this.userId = payment.getUserId().toString();
        this.point = payment.getUsedPoint();
    }
}
