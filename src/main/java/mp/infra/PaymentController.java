package mp.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import mp.domain.*;
import mp.dto.ChargeRequestDto;
import mp.dto.PaymentHistoryResponseDto;
import mp.dto.PaymentRequestDto;
import mp.dto.PaymentResponseDto;
import mp.util.UserHeaderUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value = "/payments")
@Transactional
public class PaymentController {

    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping("")
    public PaymentResponseDto pay(@RequestBody PaymentRequestDto req, HttpServletRequest request) {
        UUID userId = UserHeaderUtil.getUserId(request);
        if (userId == null) {
            throw new RuntimeException("사용자 인증이 필요합니다.");
        }

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setItem(req.getItem());
        payment.setAmount(req.getAmount());
        payment.setStatus("APPROVED");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        
        // 이벤트는 @PostPersist에서 발행되므로 여기서는 제거
        
        return new PaymentResponseDto(payment.getStatus(), payment.getCreatedAt());
    }

    // 결제 내역 조회
    @GetMapping("/history")
    public List<PaymentHistoryResponseDto> getHistory(HttpServletRequest request) {
        UUID userId = UserHeaderUtil.getUserId(request);
        if (userId == null) {
            throw new RuntimeException("사용자 인증이 필요합니다.");
        }

        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(p -> new PaymentHistoryResponseDto(
                        p.getId(),
                        p.getItem(),
                        p.getAmount(),
                        p.getStatus(),
                        p.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @PostMapping("/charge")
    public ResponseEntity<?> charge(@RequestBody ChargeRequestDto req, HttpServletRequest request) {
        UUID userId = UserHeaderUtil.getUserId(request);
        if (userId == null) {
            throw new RuntimeException("사용자 인증이 필요합니다.");
        }

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setStatus("CHARGED");
        payment.setAmount(req.getPoint()); // 포인트 금액을 amount에 저장
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Purchased 이벤트가 points-out 토픽으로 발행되어 포인트 서비스에서 처리됨
        return ResponseEntity.ok("포인트 충전 완료");
    }
}
// >>> Clean Arch / Inbound Adaptor
