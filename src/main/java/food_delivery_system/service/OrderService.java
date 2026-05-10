package food_delivery_system.service;

import food_delivery_system.model.*;
import food_delivery_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {
    @Autowired private OrderRepository repo;
    @Autowired private CartService cartService;

    /** Delivery fee = base 150 + 50 per km (city-based pseudo distance). */
    public double calculateDeliveryFee(String city, String restaurantCity) {
        if (city != null && restaurantCity != null && city.equalsIgnoreCase(restaurantCity)) return 150.0;
        return 350.0; // out of city
    }

    public Order place(String customerId, List<Cart> items, String address, String city,
                       String restaurantCity) {
        return place(customerId, items, address, city, restaurantCity, "", "", "", "", "", restaurantCity);
    }

    public Order place(String customerId, List<Cart> items, String address, String city,
                       String restaurantCity, String customerLatitude, String customerLongitude,
                       String restaurantLatitude, String restaurantLongitude,
                       String restaurantAddress, String restaurantMapCity) {
        if (items == null || items.isEmpty()) return null;
        double sub = items.stream().mapToDouble(Cart::getSubtotal).sum();
        double fee = calculateDeliveryFee(city, restaurantCity);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<items.size();i++) {
            if (i>0) sb.append("; ");
            Cart c = items.get(i);
            sb.append(c.getFoodName()).append(" x").append(c.getQuantity());
        }
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Order o = new Order(null, customerId, items.get(0).getRestaurantId(), sb.toString(),
                sub, fee, sub+fee, address, city, "PENDING", "", now,
                customerLatitude, customerLongitude, restaurantLatitude, restaurantLongitude,
                restaurantAddress, restaurantMapCity);
        return repo.save(o);
    }

    public List<Order> all() { return repo.findAll(); }
    public Order byId(String id) { return repo.findById(id); }
    public List<Order> byCustomer(String cid) { return repo.findByCustomer(cid); }
    public List<Order> byRestaurant(String rid) { return repo.findByRestaurant(rid); }
    public List<Order> byRider(String rid) { return repo.findByRider(rid); }
    public List<Order> unassigned() { return repo.findUnassigned(); }

    public void updateStatus(String orderId, String status) {
        Order o = repo.findById(orderId);
        if (o == null) return;
        o.setStatus(status);
        repo.update(o);
    }

    public void assignRider(String orderId, String riderId) {
        Order o = repo.findById(orderId);
        if (o == null) return;
        o.setRiderId(riderId);
        if ("PENDING".equals(o.getStatus())) o.setStatus("OUT_FOR_DELIVERY");
        repo.update(o);
    }

    public void delete(String id) { repo.delete(id); }

    /** Today's payout for a rider = sum of delivery fees for delivered orders today. */
    public double riderPayoutToday(String riderId) {
        String today = LocalDate.now().toString();
        return byRider(riderId).stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().startsWith(today))
                .mapToDouble(Order::getDeliveryFee).sum();
    }
    public double riderPayoutTotal(String riderId) {
        return byRider(riderId).stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .mapToDouble(Order::getDeliveryFee).sum();
    }
}
