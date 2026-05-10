package food_delivery_system.repository;

import food_delivery_system.model.Food;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FoodRepository {
    private static final String FILE = "foods.txt";
    @Autowired private FileUtil fileUtil;

    public List<Food> findAll() {
        return fileUtil.readAllLines(FILE).stream().filter(l -> !l.isBlank())
                .map(this::parse).collect(Collectors.toList());
    }
    public Food findById(String id) {
        return findAll().stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }
    public List<Food> findByRestaurant(String rid) {
        return findAll().stream().filter(f -> rid.equals(f.getRestaurantId())).collect(Collectors.toList());
    }
    public List<Food> search(String q) {
        String s = q == null ? "" : q.toLowerCase();
        return findAll().stream().filter(f -> f.getName().toLowerCase().contains(s)
                || (f.getCategory()!=null && f.getCategory().toLowerCase().contains(s)))
                .collect(Collectors.toList());
    }
    public Food save(Food f) {
        if (f.getId() == null || f.getId().isBlank()) f.setId("F-" + FileUtil.nextId());
        fileUtil.appendLine(FILE, toLine(f));
        return f;
    }
    public void update(Food f) {
        List<String> lines = findAll().stream()
                .map(x -> toLine(x.getId().equals(f.getId()) ? f : x))
                .collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }
    public void delete(String id) {
        List<String> lines = findAll().stream().filter(f -> !f.getId().equals(id))
                .map(this::toLine).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }
    public void deleteByRestaurant(String rid) {
        List<String> lines = findAll().stream().filter(f -> !rid.equals(f.getRestaurantId()))
                .map(this::toLine).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }
    private String toLine(Food f) {
        return FileUtil.join(f.getId(), f.getRestaurantId(), f.getName(), f.getCategory(),
                f.getPrice(), f.getImage(), f.getDescription());
    }
    private Food parse(String l) {
        String[] p = FileUtil.split(l);
        double price = 0; try { price = Double.parseDouble(g(p,4)); } catch (Exception ignored) {}
        return new Food(g(p,0),g(p,1),g(p,2),g(p,3),price,g(p,5),g(p,6));
    }
    private static String g(String[] a, int i){ return i<a.length? a[i] : ""; }
}
