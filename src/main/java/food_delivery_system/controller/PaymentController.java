package food_delivery_system.controller;

import food_delivery_system.model.User;
import food_delivery_system.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentController {
    @Autowired private PaymentService paymentService;

    @GetMapping("/payments")
    public String myPayments(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        model.addAttribute("payments", paymentService.byCustomer(u.getId()));
        return "view-payments";
    }
}
