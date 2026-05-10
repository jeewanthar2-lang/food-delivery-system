package food_delivery_system.controller;

import food_delivery_system.model.User;
import food_delivery_system.service.AuthService;
import food_delivery_system.service.RestaurantService;
import food_delivery_system.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    @Autowired private AuthService authService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private ReviewService reviewService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("restaurants", restaurantService.all());
        model.addAttribute("reviews", reviewService.all());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required=false) String role, Model model) {
        model.addAttribute("role", role == null ? "" : role);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email, @RequestParam String password,
                          HttpSession session, Model model) {
        User u = authService.login(email, password);
        if (u == null) { model.addAttribute("error", "Invalid credentials"); return "login"; }
        session.setAttribute("user", u);
        return "redirect:" + dashboardFor(u.getRole());
    }

    @GetMapping("/admin-login")
    public String adminLogin() { return "admin-login"; }

    @PostMapping("/admin-login")
    public String adminLoginPost(@RequestParam String email, @RequestParam String password,
                                 HttpSession session, Model model) {
        User u = authService.login(email, password);
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRole())) {
            model.addAttribute("error", "Invalid admin credentials"); return "admin-login";
        }
        session.setAttribute("user", u);
        return "redirect:/admin";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required=false) String role, Model model) {
        model.addAttribute("role", role == null ? "CUSTOMER" : role.toUpperCase());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String name, @RequestParam String email,
                             @RequestParam String password, @RequestParam String phone,
                             @RequestParam String role,
                             @RequestParam(required=false) String city,
                             @RequestParam(required=false) String vehicle,
                             @RequestParam(required=false) String licenseNumber,
                             @RequestParam(required=false) String licensePlate,
                             HttpSession session, Model model) {
        User u = new User(null, name, email, password, phone, role.toUpperCase(),
                city == null ? "" : city, vehicle == null ? "" : vehicle,
                licenseNumber == null ? "" : licenseNumber,
                licensePlate == null ? "" : licensePlate);
        String err = authService.register(u);
        if (err != null) { model.addAttribute("error", err); model.addAttribute("role", role); return "register"; }
        session.setAttribute("user", u);
        return "redirect:" + dashboardFor(u.getRole());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        model.addAttribute("user", u);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name, @RequestParam String phone,
                                @RequestParam(required=false) String city,
                                @RequestParam(required=false) String vehicle,
                                @RequestParam(required=false) String licenseNumber,
                                @RequestParam(required=false) String licensePlate,
                                @RequestParam(required=false) String password,
                                HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        u.setName(name); u.setPhone(phone);
        if (city != null) u.setCity(city);
        if (vehicle != null) u.setVehicle(vehicle);
        if ("RIDER".equalsIgnoreCase(u.getRole())) {
            if (licenseNumber != null) u.setLicenseNumber(licenseNumber);
            if (licensePlate != null) u.setLicensePlate(licensePlate);
            String err = authService.validateRiderFields(u);
            if (err != null) {
                model.addAttribute("user", u);
                model.addAttribute("error", err);
                return "profile";
            }
        }
        if (password != null && !password.isBlank()) u.setPassword(password);
        // Persist via Auth/Admin services using UserRepository
        // We'll use AdminService for update for simplicity.
        adminUpdate(u);
        session.setAttribute("user", u);
        return "redirect:/profile?saved=1";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) return "redirect:/login";
        adminDelete(u.getId());
        session.invalidate();
        return "redirect:/";
    }

    @Autowired private food_delivery_system.service.AdminService adminService;
    private void adminUpdate(User u){ adminService.updateUser(u); }
    private void adminDelete(String id){ adminService.deleteUser(id); }

    private String dashboardFor(String role) {
        return switch (role.toUpperCase()) {
            case "OWNER" -> "/owner";
            case "RIDER" -> "/rider";
            case "ADMIN" -> "/admin";
            default -> "/customer";
        };
    }
}
