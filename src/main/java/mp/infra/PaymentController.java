package mp.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import mp.domain.*;
import mp.dto.ChargeRequestDto;
import mp.dto.PaymentHistoryResponseDto;
import mp.dto.PaymentRequestDto;
import mp.dto.PaymentResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value = "/payments")
// @Transactional
public class PaymentController {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    private PointEventPublisher pointEventPublisher;

    @PostMapping("")
    public PaymentResponseDto pay(@RequestBody PaymentRequestDto req) {
        Payment payment = new Payment();
        payment.setUserId(req.getUserId());
        payment.setItem(req.getItem());
        payment.setAmount(req.getAmount());
        payment.setStatus("APPROVED");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        int point = (int) (payment.getAmount() * 0.05); // 예시: 결제 금액의 5% 적립
        PointIncreased pointIncreased = new PointIncreased();
        pointIncreased.setUserId(payment.getUserId());
        pointIncreased.setPoint(point);
        pointIncreased.setCreatedAt(LocalDateTime.now());

        pointEventPublisher.publishPointIncreased(pointIncreased);
        return new PaymentResponseDto(payment.getStatus(), payment.getCreatedAt());
    }

    // 결제 내역 조회
    @GetMapping("/history")
    public List<PaymentHistoryResponseDto> getHistory(@RequestParam("userId") UUID userId) {
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
    public ResponseEntity<?> charge(@RequestBody ChargeRequestDto req) {
        // 1. 결제 레코드 저장 (포인트 충전용)
        Payment payment = new Payment();
        payment.setUserId(req.getUserId());
        payment.setStatus("CHARGED"); // 예: "CHARGED" 상태로 저장
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 2. 포인트 충전 이벤트 발행
        PointIncreased event = new PointIncreased();
        event.setUserId(req.getUserId());
        event.setPoint(req.getPoint());
        event.setCreatedAt(LocalDateTime.now());

        pointEventPublisher.publishPointIncreased(event);

        return ResponseEntity.ok("포인트 충전 완료");
    }
}
// >>> Clean Arch / Inbound Adaptor
