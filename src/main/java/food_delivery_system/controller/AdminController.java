package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {
    @Autowired private AdminService adminService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private OrderService orderService;
    @Autowired private FoodService foodService;
    @Autowired private ReviewService reviewService;
    @Autowired private PaymentService paymentService;

    private User requireAdmin(HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRole())) return null;
        return u;
    }

    @GetMapping("/admin")
    public String dashboard(HttpSession s, Model m) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        m.addAttribute("usersCount", adminService.allUsers().size());
        m.addAttribute("restaurantsCount", restaurantService.all().size());
        m.addAttribute("ordersCount", orderService.all().size());
        m.addAttribute("foodsCount", foodService.all().size());
        m.addAttribute("reviewsCount", reviewService.all().size());
        m.addAttribute("paymentsCount", paymentService.all().size());
        return "admin-dashboard";
    }

    @GetMapping("/admin/users")
    public String users(HttpSession s, Model m) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        m.addAttribute("users", adminService.allUsers());
        return "manage-users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable String id, HttpSession s) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/restaurants")
    public String restaurants(HttpSession s, Model m) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        m.addAttribute("restaurants", restaurantService.all());
        return "manage-restaurants";
    }

    @PostMapping("/admin/restaurants/delete/{id}")
    public String deleteRestaurant(@PathVariable String id, HttpSession s) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        restaurantService.delete(id);
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/admin/orders")
    public String orders(HttpSession s, Model m) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        m.addAttribute("orders", orderService.all());
        m.addAttribute("restaurantService", restaurantService);
        return "manage-orders";
    }

    @PostMapping("/admin/orders/delete/{id}")
    public String deleteOrder(@PathVariable String id, HttpSession s) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        orderService.delete(id);
        return "redirect:/admin/orders";
    }

    @PostMapping("/admin/reviews/delete/{id}")
    public String deleteReview(@PathVariable String id, HttpSession s) {
        if (requireAdmin(s) == null) return "redirect:/admin-login";
        reviewService.delete(id);
        return "redirect:/reviews";
    }

    // ----- Rider area (kept here for brevity) -----
    @GetMapping("/rider")
    public String riderDashboard(HttpSession s, Model m) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"RIDER".equalsIgnoreCase(u.getRole())) return "redirect:/login";
        java.util.List<Order> myOrders = orderService.byRider(u.getId());
        m.addAttribute("available", orderService.unassigned());
        m.addAttribute("mine", myOrders.stream()
                .filter(o -> !"DELIVERED".equalsIgnoreCase(o.getStatus())
                        && !"CANCELLED".equalsIgnoreCase(o.getStatus()))
                .toList());
        m.addAttribute("completed", myOrders.stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()))
                .toList());
        m.addAttribute("payoutToday", orderService.riderPayoutToday(u.getId()));
        m.addAttribute("payoutTotal", orderService.riderPayoutTotal(u.getId()));
        m.addAttribute("restaurantService", restaurantService);
        return "rider-dashboard";
    }

    @PostMapping("/rider/pick/{orderId}")
    public String pick(@PathVariable String orderId, HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"RIDER".equalsIgnoreCase(u.getRole())) return "redirect:/login";
        orderService.assignRider(orderId, u.getId());
        return "redirect:/rider";
    }

    @PostMapping("/rider/status/{orderId}")
    public String riderStatus(@PathVariable String orderId, @RequestParam String status, HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"RIDER".equalsIgnoreCase(u.getRole())) return "redirect:/login";
        orderService.updateStatus(orderId, status);
        return "redirect:/rider";
    }

    @PostMapping("/owner/order/status/{orderId}")
    public String ownerStatus(@PathVariable String orderId, @RequestParam String status, HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole())) return "redirect:/login";
        Order order = orderService.byId(orderId);
        Restaurant restaurant = order == null ? null : restaurantService.byId(order.getRestaurantId());
        if (restaurant != null && u.getId().equals(restaurant.getOwnerId())) {
            orderService.updateStatus(orderId, status);
        }
        return "redirect:/owner";
    }
}
