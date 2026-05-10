package food_delivery_system.service;

import food_delivery_system.model.Review;
import food_delivery_system.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReviewService {
    @Autowired private ReviewRepository repo;

    public Review add(Review r) {
        r.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return repo.save(r);
    }
    public List<Review> all() { return repo.findAll(); }
    public List<Review> byCustomer(String cid) { return repo.findByCustomer(cid); }
    public List<Review> byRestaurant(String rid) { return repo.findByRestaurant(rid); }
    public void delete(String id) { repo.delete(id); }
    public Review byId(String id) { return repo.findById(id); }
    public Review update(Review r) { return repo.update(r); }

}
