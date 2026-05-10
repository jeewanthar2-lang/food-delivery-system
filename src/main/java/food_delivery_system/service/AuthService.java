package food_delivery_system.service;

import food_delivery_system.model.User;
import food_delivery_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Authentication & registration service. */
@Service
public class AuthService {
    @Autowired private UserRepository userRepo;

    /** Sri Lankan NIC format: 12 digits or old format 9 digits + V/X. */
    public static final String NIC_NUMBER_REGEX = "^(\\d{12}|\\d{9}[VX])$";
    /** Vehicle number: ABC-1234 or province-prefixed WP-CAB-1234. */
    public static final String VEHICLE_NUMBER_REGEX = "^([A-Z]{2,3}-)?[A-Z]{2,3}-\\d{4}$";

    public User login(String email, String password) {
        User u = userRepo.findByEmail(email);
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }

    public String register(User u) {
        if (u.getEmail()==null || u.getEmail().isBlank()) return "Email required";
        if (u.getPassword()==null || u.getPassword().length() < 4) return "Password must be at least 4 chars";
        if (userRepo.findByEmail(u.getEmail()) != null) return "Email already registered";
        if (u.getRole() == null || u.getRole().isBlank()) u.setRole("CUSTOMER");

        if ("RIDER".equalsIgnoreCase(u.getRole())) {
            String err = validateRiderFields(u);
            if (err != null) return err;
        }

        userRepo.save(u);
        return null; // success
    }

    /** Validate rider-specific fields. Returns error message or null when OK. */
    public String validateRiderFields(User u) {
        String city = u.getCity() == null ? "" : u.getCity().trim();
        String vehicle = u.getVehicle() == null ? "" : u.getVehicle().trim();
        String nic = u.getLicenseNumber() == null ? "" : u.getLicenseNumber().trim().toUpperCase();
        String vehicleNumber = u.getLicensePlate() == null ? "" : u.getLicensePlate().trim().toUpperCase();

        if (city.isBlank()) return "Service city is required for rider registration";
        if (vehicle.isBlank()) return "Vehicle type is required for rider registration";
        if (!nic.matches(NIC_NUMBER_REGEX))
            return "ID / NIC number must be 12 digits or old NIC format: 9 digits followed by V or X";
        if (!vehicleNumber.matches(VEHICLE_NUMBER_REGEX))
            return "Vehicle number must use ABC-1234 or WP-CAB-1234 format";

        u.setCity(city);
        u.setVehicle(vehicle);
        u.setLicenseNumber(nic);
        u.setLicensePlate(vehicleNumber);
        return null;
    }
}
