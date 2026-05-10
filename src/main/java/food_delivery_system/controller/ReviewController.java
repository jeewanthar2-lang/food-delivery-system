package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReviewController {
    @Autowired private ReviewService reviewService;
    @Autowired private OrderService orderService;
    @Autowired private RestaurantService restaurantService;

    @GetMapping("/review/add/{orderId}")
    public String form(@PathVariable String orderId, HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        Order o = orderService.byId(orderId);
        if (o == null || !"DELIVERED".equals(o.getStatus())) return "redirect:/customer/orders";
        model.addAttribute("order", o);
        model.addAttribute("restaurant", restaurantService.byId(o.getRestaurantId()));
        return "add-review";
    }

    @PostMapping("/review/add/{orderId}")
    public String submit(@PathVariable String orderId, @RequestParam int rating,
                         @RequestParam String comment, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        Order o = orderService.byId(orderId);
        if (o == null) return "redirect:/customer/orders";
        Review r = new Review(null, u.getId(), u.getName(), o.getRestaurantId(), o.getId(),
                rating, comment, "");
        reviewService.add(r);
        return "redirect:/reviews";
    }

    @GetMapping("/reviews")
    public String myReviews(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        if ("CUSTOMER".equalsIgnoreCase(u.getRole())) {
            model.addAttribute("reviews", reviewService.byCustomer(u.getId()));
        } else {
            model.addAttribute("reviews", reviewService.all());
        }
        model.addAttribute("restaurantService", restaurantService);
        return "view-reviews";

    }
    @GetMapping("/review/edit/{id}")
    public String editForm(@PathVariable String id, HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        Review r = reviewService.byId(id);
        if (r == null || !r.getCustomerId().equals(u.getId())) return "redirect:/reviews";
        model.addAttribute("review", r);
        model.addAttribute("restaurant", restaurantService.byId(r.getRestaurantId()));
        return "edit-review";
    }

    @PostMapping("/review/edit/{id}")
    public String editSubmit(@PathVariable String id,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        Review r = reviewService.byId(id);
        if (r == null || !r.getCustomerId().equals(u.getId())) return "redirect:/reviews";
        r.setRating(rating);
        r.setComment(comment);
        reviewService.update(r);
        return "redirect:/reviews";
    }


}
