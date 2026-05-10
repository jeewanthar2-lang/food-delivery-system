package food_delivery_system.service;

import food_delivery_system.model.Food;
import food_delivery_system.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {
    @Autowired private FoodRepository repo;

    public List<Food> all() { return repo.findAll(); }
    public Food byId(String id) { return repo.findById(id); }
    public List<Food> byRestaurant(String rid) { return repo.findByRestaurant(rid); }
    public List<Food> search(String q) { return repo.search(q); }
    public Food add(Food f) { return repo.save(f); }
    public void update(Food f) { repo.update(f); }
    public void delete(String id) { repo.delete(id); }
}
