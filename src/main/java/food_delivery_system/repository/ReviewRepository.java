package food_delivery_system.repository;

import food_delivery_system.model.Review;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewRepository {
    private static final String FILE = "reviews.txt";
    @Autowired private FileUtil fileUtil;

    public List<Review> findAll() {
        return fileUtil.readAllLines(FILE).stream().filter(l -> !l.isBlank())
                .map(this::parse).collect(Collectors.toList());
    }
    public Review save(Review r) {
        if (r.getId()==null||r.getId().isBlank()) r.setId("RV-" + FileUtil.nextId());
        fileUtil.appendLine(FILE, toLine(r));
        return r;
    }
    public Review update(Review updated) {
        List<Review> all = findAll();
        List<String> lines = all.stream().map(r -> {
            if (r.getId().equals(updated.getId())) return toLine(updated);
            return toLine(r);
        }).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
        return updated;
    }

    public Review findById(String id) {
        return findAll().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Review> findByCustomer(String cid){
        return findAll().stream().filter(r -> cid.equals(r.getCustomerId())).collect(Collectors.toList());
    }
    public List<Review> findByRestaurant(String rid){
        return findAll().stream().filter(r -> rid.equals(r.getRestaurantId())).collect(Collectors.toList());
    }
    public void delete(String id){
        List<String> lines = findAll().stream().filter(r -> !r.getId().equals(id))
                .map(this::toLine).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }
    private String toLine(Review r) {
        return FileUtil.join(r.getId(), r.getCustomerId(), r.getCustomerName(), r.getRestaurantId(),
                r.getOrderId(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
    private Review parse(String l) {
        String[] p = FileUtil.split(l);
        int rating=0; try{rating=Integer.parseInt(g(p,5));}catch(Exception ignored){}
        return new Review(g(p,0),g(p,1),g(p,2),g(p,3),g(p,4),rating,g(p,6),g(p,7));
    }
    private static String g(String[] a, int i){ return i<a.length? a[i] : ""; }
}
