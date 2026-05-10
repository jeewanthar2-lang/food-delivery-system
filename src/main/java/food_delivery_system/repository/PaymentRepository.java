package food_delivery_system.repository;

import food_delivery_system.model.Payment;
import food_delivery_system.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository {
    private static final String FILE = "payments.txt";
    @Autowired private FileUtil fileUtil;

    public List<Payment> findAll() {
        return fileUtil.readAllLines(FILE).stream().filter(l -> !l.isBlank())
                .map(this::parse).collect(Collectors.toList());
    }
    public Payment save(Payment p) {
        if (p.getId()==null||p.getId().isBlank()) p.setId("P-" + FileUtil.nextId());
        fileUtil.appendLine(FILE, toLine(p));
        return p;
    }
    public List<Payment> findByCustomer(String cid){
        return findAll().stream().filter(x -> cid.equals(x.getCustomerId())).collect(Collectors.toList());
    }
    public Payment findByOrder(String orderId){
        return findAll().stream().filter(x -> orderId.equals(x.getOrderId())).findFirst().orElse(null);
    }
    public void delete(String id){
        List<String> lines = findAll().stream().filter(x -> !x.getId().equals(id))
                .map(this::toLine).collect(Collectors.toList());
        fileUtil.writeAllLines(FILE, lines);
    }
    private String toLine(Payment p) {
        return FileUtil.join(p.getId(), p.getOrderId(), p.getCustomerId(), p.getAmount(),
                p.getCardLast4(), p.getStatus(), p.getPaidAt());
    }
    private Payment parse(String l) {
        String[] p = FileUtil.split(l);
        double amt=0; try{amt=Double.parseDouble(g(p,3));}catch(Exception ignored){}
        return new Payment(g(p,0),g(p,1),g(p,2),amt,g(p,4),g(p,5),g(p,6));
    }
    private static String g(String[] a, int i){ return i<a.length? a[i] : ""; }
}
