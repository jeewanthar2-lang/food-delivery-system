package food_delivery_system.repository;

import food_delivery_system.model.User;
import food_delivery_system.util.FileUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/** Stores users (Customer, Owner, Rider, Admin) in users.txt. */
@Repository
public class UserRepository {

    private static final String FILE = "users.txt";
    @Autowired private FileUtil fileUtil;

    @PostConstruct
    public void seed() {
        if (findByEmail("admin@foodiego.com") == null) {
            save(new User("U-ADMIN", "Admin", "admin@foodiego.com", "admin123",
                    "0000000000", "ADMIN", "HQ", "", "", ""));
        }
    }

    public List<User> findAll() {
        return fileUtil.readAllLines(FILE).stream()
                .filter(l -> !l.isBlank())
                .map(this::parse).collect(Collectors.toList());
    }

    public User findById(String id) {
        return findAll().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public User findByEmail(String email) {
        return findAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    public List<User> findByRole(String role) {
        return findAll().stream().filter(u -> u.getRole().equalsIgnoreCase(role)).collect(Collectors.toList());
    }

    public User save(User u) {
        if (u.getId() == null || u.getId().isBlank()) u.setId("U-" + FileUtil.nextId());
        fileUtil.appendLine(FILE, toLine(u));
        return u;
    }

    public void update(User u) {
        List<User> all = findAll();
        List<String> lines = new ArrayList<>();
        for (User x : all) lines.add(toLine(x.getId().equals(u.getId()) ? u : x));
        fileUtil.writeAllLines(FILE, lines);
    }

    public void delete(String id) {
        List<String> lines = findAll().stream()
                .filter(u -> !u.getId().equals(id))
                .map(this::toLine).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }

    private String toLine(User u) {
        return FileUtil.join(u.getId(), u.getName(), u.getEmail(), u.getPassword(),
                u.getPhone(), u.getRole(), u.getCity(), u.getVehicle(),
                u.getLicenseNumber() == null ? "" : u.getLicenseNumber(),
                u.getLicensePlate() == null ? "" : u.getLicensePlate());
    }

    private User parse(String line) {
        String[] p = FileUtil.split(line);
        return new User(get(p,0), get(p,1), get(p,2), get(p,3), get(p,4), get(p,5),
                get(p,6), get(p,7), get(p,8), get(p,9));
    }
    private static String get(String[] a, int i){ return i<a.length? a[i] : ""; }
}
