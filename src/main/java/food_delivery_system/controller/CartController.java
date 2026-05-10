package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
public class CartController {
    @Autowired private CartService cartService;
    @Autowired private FoodService foodService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;
    @Autowired private ReviewService reviewService;

    private User requireCustomer(HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"CUSTOMER".equalsIgnoreCase(u.getRole())) return null;
        return u;
    }

    @GetMapping("/customer")
    public String customerDashboard(HttpSession session, Model model) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("restaurants", restaurantService.all());
        model.addAttribute("orders", orderService.byCustomer(u.getId()));
        model.addAttribute("reviews", reviewService.byCustomer(u.getId()));
        model.addAttribute("cartCount", cartService.getCart(u.getId()).size());
        return "customer-dashboard";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";
        List<Cart> items = cartService.getCart(u.getId());
        double sub = items.stream().mapToDouble(Cart::getSubtotal).sum();
        // delivery fee based on first item's restaurant city vs customer city (we don't have city yet — preview)
        double fee = items.isEmpty() ? 0 : 150.0;
        model.addAttribute("items", items);
        model.addAttribute("subtotal", sub);
        model.addAttribute("deliveryFee", fee);
        model.addAttribute("total", sub + fee);
        model.addAttribute("foodService", foodService);
        model.addAttribute("restaurantService", restaurantService);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String foodId,
                            @RequestParam(defaultValue = "1") int qty,
                            HttpSession session,
                            HttpServletRequest request,
                            RedirectAttributes ra) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";

        cartService.addToCart(u.getId(), foodId, qty);
        ra.addFlashAttribute("msg", "Added to cart");

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/foods");
    }


    @PostMapping("/cart/update/{cartId}")
    public String updateQty(@PathVariable String cartId, @RequestParam int qty, HttpSession s) {
        if (requireCustomer(s) == null) return "redirect:/login";
        cartService.updateQuantity(cartId, qty);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{cartId}")
    public String remove(@PathVariable String cartId, HttpSession s) {
        if (requireCustomer(s) == null) return "redirect:/login";
        cartService.remove(cartId);
        return "redirect:/cart";
    }
}
