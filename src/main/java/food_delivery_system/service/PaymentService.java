package food_delivery_system.service;

import food_delivery_system.model.Payment;
import food_delivery_system.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {
    @Autowired private PaymentRepository repo;

    public Payment pay(String orderId, String customerId, double amount, String cardNumber) {
        String last4 = cardNumber == null || cardNumber.length() < 4 ? "0000"
                : cardNumber.replaceAll("\\s","").substring(Math.max(0, cardNumber.replaceAll("\\s","").length()-4));
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return repo.save(new Payment(null, orderId, customerId, amount, last4, "PAID", now));
    }

    public List<Payment> all() { return repo.findAll(); }
    public List<Payment> byCustomer(String cid) { return repo.findByCustomer(cid); }
    public Payment byOrder(String orderId) { return repo.findByOrder(orderId); }
    public void delete(String id) { repo.delete(id); }
}
