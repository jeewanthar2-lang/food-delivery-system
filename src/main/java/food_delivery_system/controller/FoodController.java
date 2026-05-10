package food_delivery_system.controller;

import food_delivery_system.model.*;
import food_delivery_system.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
public class FoodController {

    @Autowired private FoodService foodService;
    @Autowired private RestaurantService restaurantService;
    @Value("${foodiego.uploads.dir:uploads}") private String uploadsDir;

    private User requireOwner(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole())) return null;
        return u;
    }

    private boolean ownsRestaurant(User owner, Restaurant restaurant) {
        return owner != null && restaurant != null && owner.getId().equals(restaurant.getOwnerId());
    }

    private boolean ownsFood(User owner, Food food) {
        if (owner == null || food == null) return false;
        Restaurant restaurant = restaurantService.byId(food.getRestaurantId());
        return ownsRestaurant(owner, restaurant);
    }

    @GetMapping("/foods")
    public String viewFoods(@RequestParam(required=false) String q,
                            @RequestParam(required=false) String restaurantId, Model model) {
        if (restaurantId != null && !restaurantId.isBlank()) {
            model.addAttribute("foods", foodService.byRestaurant(restaurantId));
            model.addAttribute("restaurant", restaurantService.byId(restaurantId));
        } else if (q != null) {
            model.addAttribute("foods", foodService.search(q));
            model.addAttribute("q", q);
        } else {
            model.addAttribute("foods", foodService.all());
        }
        model.addAttribute("restaurantService", restaurantService);
        return "view-foods";
    }

    @GetMapping("/owner/food/add/{restaurantId}")
    public String addFood(@PathVariable String restaurantId, HttpSession session, Model model) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Restaurant r = restaurantService.byId(restaurantId);
        if (!ownsRestaurant(u, r)) return "redirect:/owner";
        model.addAttribute("restaurant", r);
        return "add-food";
    }

    @PostMapping("/owner/food/add/{restaurantId}")
    public String addFoodPost(@PathVariable String restaurantId,
                              @RequestParam String name, @RequestParam String category,
                              @RequestParam double price, @RequestParam String description,
                              @RequestParam(required=false) MultipartFile image,
                              HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Restaurant r = restaurantService.byId(restaurantId);
        if (!ownsRestaurant(u, r)) return "redirect:/owner";
        String img = saveImage(image);
        foodService.add(new Food(null, restaurantId, name, category, price, img, description));
        return "redirect:/owner";
    }

    @GetMapping("/owner/food/edit/{id}")
    public String editFood(@PathVariable String id, HttpSession session, Model model) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Food f = foodService.byId(id);
        if (!ownsFood(u, f)) return "redirect:/owner";
        model.addAttribute("f", f);
        model.addAttribute("restaurant", restaurantService.byId(f.getRestaurantId()));
        return "edit-food";
    }

    @PostMapping("/owner/food/edit/{id}")
    public String editFoodPost(@PathVariable String id,
                               @RequestParam String name, @RequestParam String category,
                               @RequestParam double price, @RequestParam String description,
                               @RequestParam(required=false) MultipartFile image,
                               HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Food f = foodService.byId(id);
        if (!ownsFood(u, f)) return "redirect:/owner";
        f.setName(name); f.setCategory(category); f.setPrice(price); f.setDescription(description);
        String img = saveImage(image);
        if (img != null) f.setImage(img);
        foodService.update(f);
        return "redirect:/owner";
    }

    @PostMapping("/owner/food/delete/{id}")
    public String deleteFood(@PathVariable String id, HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Food f = foodService.byId(id);
        if (ownsFood(u, f)) foodService.delete(id);
        return "redirect:/owner";
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            Path dir = Paths.get(uploadsDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String original = image.getOriginalFilename() == null ? "image" : image.getOriginalFilename();
            String fn = System.currentTimeMillis() + "_" + original.replaceAll("\\s+","_");
            Files.copy(image.getInputStream(), dir.resolve(fn), StandardCopyOption.REPLACE_EXISTING);
            return fn;
        } catch (IOException e) { return null; }
    }
}
