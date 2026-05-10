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
import java.util.List;

@Controller
@RequestMapping("/owner")
public class RestaurantController {

    @Autowired private RestaurantService restaurantService;
    @Autowired private FoodService foodService;
    @Autowired private OrderService orderService;
    @Value("${foodiego.uploads.dir:uploads}") private String uploadsDir;

    private User requireOwner(HttpSession s) {
        User u = (User) s.getAttribute("user");
        if (u == null || !"OWNER".equalsIgnoreCase(u.getRole())) return null;
        return u;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        List<Restaurant> mine = restaurantService.byOwner(u.getId());
        model.addAttribute("restaurants", mine);
        model.addAttribute("foodService", foodService);
        model.addAttribute("orders", orderService.all().stream()
                .filter(o -> mine.stream().anyMatch(r -> r.getId().equals(o.getRestaurantId())))
                .toList());
        return "owner-dashboard";
    }

    @GetMapping("/restaurant/add")
    public String addRestaurant(HttpSession session, Model model) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        if (restaurantService.ownerHasRestaurant(u.getId())) {
            return "redirect:/owner?restaurantLimit=1";
        }
        return "add-restaurant";
    }

    @PostMapping("/restaurant/add")
    public String addRestaurantPost(@RequestParam String name, @RequestParam String city,
                                    @RequestParam String address, @RequestParam String cuisine,
                                    @RequestParam String description,
                                    @RequestParam(required=false) String latitude,
                                    @RequestParam(required=false) String longitude,
                                    @RequestParam(required=false) MultipartFile image,
                                    HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        if (restaurantService.ownerHasRestaurant(u.getId())) {
            return "redirect:/owner?restaurantLimit=1";
        }
        String img = saveImage(image);
        restaurantService.add(new Restaurant(null, u.getId(), name, city, address, cuisine, img, description, latitude, longitude));
        return "redirect:/owner";
    }

    @GetMapping("/restaurant/edit/{id}")
    public String editRestaurant(@PathVariable String id, HttpSession session, Model model) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Restaurant r = restaurantService.byId(id);
        if (r == null || !u.getId().equals(r.getOwnerId())) return "redirect:/owner";
        model.addAttribute("r", r);
        return "edit-restaurant";
    }

    @PostMapping("/restaurant/edit/{id}")
    public String editRestaurantPost(@PathVariable String id, @RequestParam String name,
                                     @RequestParam String city, @RequestParam String address,
                                     @RequestParam String cuisine, @RequestParam String description,
                                     @RequestParam(required=false) String latitude,
                                     @RequestParam(required=false) String longitude,
                                     @RequestParam(required=false) MultipartFile image,
                                     HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Restaurant r = restaurantService.byId(id);
        if (r == null || !u.getId().equals(r.getOwnerId())) return "redirect:/owner";
        r.setName(name); r.setCity(city); r.setAddress(address);
        r.setCuisine(cuisine); r.setDescription(description);
        r.setLatitude(latitude); r.setLongitude(longitude);
        String img = saveImage(image);
        if (img != null) r.setImage(img);
        restaurantService.update(r);
        return "redirect:/owner";
    }

    @PostMapping("/restaurant/delete/{id}")
    public String deleteRestaurant(@PathVariable String id, HttpSession session) {
        User u = requireOwner(session);
        if (u == null) return "redirect:/login";
        Restaurant r = restaurantService.byId(id);
        if (r != null && u.getId().equals(r.getOwnerId())) restaurantService.delete(id);
        return "redirect:/owner";
    }

    private String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            Path dir = Paths.get(uploadsDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String fn = System.currentTimeMillis() + "_" + image.getOriginalFilename().replaceAll("\\s+","_");
            Files.copy(image.getInputStream(), dir.resolve(fn), StandardCopyOption.REPLACE_EXISTING);
            return fn;
        } catch (IOException e) { return null; }
    }
}
