package mp.infra;

import mp.domain.*;
import mp.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentViewViewHandler {

    @Autowired
    private PaymentViewRepository paymentViewRepository;

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='Purchased'")
    public void whenPurchased_then_UPDATE_1(@Payload Purchased purchased) {
        try {
            if (!purchased.validate()) return;

            List<PaymentView> paymentViewList = paymentViewRepository.findByUserId(purchased.getUserId());

            if (paymentViewList.isEmpty()) {
                // 신규 View 생성
                PaymentView newView = new PaymentView();
                newView.setId(purchased.getId()); // 이벤트에서 받은 id 세팅!
                newView.setUserId(purchased.getUserId());
                newView.setItem(purchased.getItem());
                newView.setAmount(purchased.getAmount());
                newView.setPaymentStatus("APPROVED");
                newView.setStatus(purchased.getStatus());
                newView.setCreatedAt(purchased.getCreatedAt());
                paymentViewRepository.save(newView);
                System.out.println("PaymentView 신규 저장 완료: " + newView.getId());
            } else {
                // 기존 View 업데이트
                for (PaymentView paymentView : paymentViewList) {
                    System.out.println("이벤트 소비, PaymentView: " + paymentView.getId());
                    paymentView.setPaymentStatus("APPROVED");
                    paymentViewRepository.save(paymentView);
                    System.out.println("PaymentView 저장 완료: " + paymentView.getId());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='Subscribed'")
    public void whenSubscribed_then_UPDATE_2(@Payload Subscribed subscribed) {
        try {
            if (!subscribed.validate()) return;

            List<PaymentView> paymentViewList = paymentViewRepository.findByUserId(subscribed.getUserId());

            if (paymentViewList.isEmpty()) {
                // 신규 View 생성
                PaymentView newView = new PaymentView();
                newView.setId(subscribed.getId()); // 이벤트에서 받은 id 세팅!
                newView.setUserId(subscribed.getUserId());
                newView.setItem(subscribed.getItem());
                newView.setAmount(subscribed.getAmount());
                newView.setPaymentStatus("SUBSCRIBED");
                newView.setStatus(subscribed.getStatus());
                newView.setCreatedAt(subscribed.getCreatedAt());
                paymentViewRepository.save(newView);
                System.out.println("PaymentView 신규 저장 완료: " + newView.getId());
            } else {
                for (PaymentView paymentView : paymentViewList) {
                    System.out.println("이벤트 소비, PaymentView: " + paymentView.getId());
                    paymentView.setPaymentStatus("SUBSCRIBED");
                    paymentViewRepository.save(paymentView);
                    System.out.println("PaymentView 저장 완료: " + paymentView.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}