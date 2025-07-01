package mp.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

// <엔티티, ID타입>
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    // 사용자별 결제 내역 조회
    List<Payment> findByUserId(UUID userId);
}
