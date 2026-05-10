package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private CartService cartService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private FoodService foodService;
    @Autowired private PaymentService paymentService;

    /* ---------- helpers ---------- */

    private User requireCustomer(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null || !"CUSTOMER".equalsIgnoreCase(u.getRole())) return null;
        return u;
    }

    private void addCheckoutMapAttributes(Model model, Restaurant r) {
        model.addAttribute("storeMapQuery", r != null ? r.getMapQuery() : "Sri Lanka");
        model.addAttribute("storeMapEmbedUrl", r != null ? r.getMapEmbedUrl() : "https://maps.google.com/maps?q=Sri+Lanka&output=embed");
        model.addAttribute("storeMapsUrl", r != null ? r.getGoogleMapsUrl() : "https://www.google.com/maps/search/?api=1&query=Sri+Lanka");
    }

    /* ---------- CART CHECKOUT ---------- */

    @GetMapping("/order/checkout")
    public String checkout(HttpSession session, Model model) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";

        List<Cart> items = cartService.getCart(u.getId());
        if (items.isEmpty()) return "redirect:/cart";

        Restaurant r = restaurantService.byId(items.get(0).getRestaurantId());
        double sub = items.stream().mapToDouble(Cart::getSubtotal).sum();

        model.addAttribute("items", items);
        model.addAttribute("subtotal", sub);
        model.addAttribute("restaurant", r);
        model.addAttribute("buyNow", false);
        addCheckoutMapAttributes(model, r);
        return "payment";
    }

    @PostMapping("/order/place")
    public String place(@RequestParam String address, @RequestParam String city,
                        @RequestParam(required=false) String customerLatitude,
                        @RequestParam(required=false) String customerLongitude,
                        @RequestParam String cardName, @RequestParam String cardNumber,
                        @RequestParam String cardExpiry, @RequestParam String cardCvv,
                        HttpSession session) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";

        List<Cart> items = cartService.getCart(u.getId());
        if (items.isEmpty()) return "redirect:/cart";

        Restaurant r = restaurantService.byId(items.get(0).getRestaurantId());
        Order o = orderService.place(u.getId(), items, address, city,
                r != null ? r.getCity() : city,
                customerLatitude, customerLongitude,
                r != null ? r.getLatitude() : "",
                r != null ? r.getLongitude() : "",
                r != null ? r.getAddress() : "",
                r != null ? r.getCity() : "");
        paymentService.pay(o.getId(), u.getId(), o.getTotal(), cardNumber);
        cartService.clear(u.getId());
        return "redirect:/customer/orders?placed=" + o.getId();
    }

    /* ---------- BUY NOW (bypasses cart) ---------- */

    @PostMapping("/order/buy-now")
    public String buyNow(@RequestParam String foodId,
                         @RequestParam(defaultValue = "1") int qty,
                         HttpSession session, Model model) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";

        Food f = foodService.byId(foodId);
        if (f == null) return "redirect:/foods";
        if (qty < 1) qty = 1;

        Cart line = new Cart(null, u.getId(), f.getId(), f.getName(),
                f.getRestaurantId(), f.getPrice(), qty);
        List<Cart> items = new ArrayList<>();
        items.add(line);
        session.setAttribute("buyNowItems", items);

        Restaurant r = restaurantService.byId(f.getRestaurantId());
        model.addAttribute("items", items);
        model.addAttribute("subtotal", line.getSubtotal());
        model.addAttribute("restaurant", r);
        model.addAttribute("buyNow", true);
        addCheckoutMapAttributes(model, r);
        return "payment";
    }

    @PostMapping("/order/buy-now/place")
    public String buyNowPlace(@RequestParam String address, @RequestParam String city,
                              @RequestParam(required=false) String customerLatitude,
                              @RequestParam(required=false) String customerLongitude,
                              @RequestParam String cardName, @RequestParam String cardNumber,
                              @RequestParam String cardExpiry, @RequestParam String cardCvv,
                              HttpSession session) {
        User u = requireCustomer(session);
        if (u == null) return "redirect:/login";

        @SuppressWarnings("unchecked")
        List<Cart> items = (List<Cart>) session.getAttribute("buyNowItems");
        if (items == null || items.isEmpty()) return "redirect:/foods";

        Restaurant r = restaurantService.byId(items.get(0).getRestaurantId());
        Order o = orderService.place(u.getId(), items, address, city,
                r != null ? r.getCity() : city,
                customerLatitude, customerLongitude,
                r != null ? r.getLatitude() : "",
                r != null ? r.getLongitude() : "",
                r != null ? r.getAddress() : "",
                r != null ? r.getCity() : "");
        paymentService.pay(o.getId(), u.getId(), o.getTotal(), cardNumber);
        session.removeAttribute("buyNowItems");
        return "redirect:/customer/orders?placed=" + o.getId();
    }

    /* ---------- ORDER HISTORY ---------- */

    @GetMapping("/customer/orders")
    public String myOrders(HttpSession session, Model model,
                           @RequestParam(required = false) String placed) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";

        model.addAttribute("orders", orderService.byCustomer(u.getId()));
        model.addAttribute("placed", placed);
        model.addAttribute("restaurantService", restaurantService);
        return "view-orders";
    }
}
