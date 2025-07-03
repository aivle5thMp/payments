package mp.infra;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor

public class PointIncreased {
    private UUID userId;
    private int point;
    private LocalDateTime createdAt;
}
