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
import mp.dto.PaymentHistoryResponseDto;
import mp.dto.PaymentRequestDto;
import mp.dto.PaymentResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/payments")
// @Transactional
public class PaymentController {

    @Autowired
    PaymentRepository paymentRepository;

    @PostMapping("")
    public PaymentResponseDto pay(@RequestBody PaymentRequestDto req) {
        Payment payment = new Payment();
        payment.setUserId(req.getUserId());
        payment.setItem(req.getItem());
        payment.setAmount(req.getAmount());
        payment.setStatus("APPROVED");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

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
                p.getCreatedAt()
            )).collect(Collectors.toList());
    }
}
//>>> Clean Arch / Inbound Adaptor
