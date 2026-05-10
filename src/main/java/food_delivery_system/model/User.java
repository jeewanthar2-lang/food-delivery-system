package food_delivery_system.model;

/**
 * User — base class for Customer, Owner, Rider (Encapsulation + Inheritance).
 * Role: CUSTOMER | OWNER | RIDER | ADMIN
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String city;           // for owners/riders
    private String vehicle;        // for riders
    private String licenseNumber;  // riders: ID / NIC number
    private String licensePlate;   // riders: vehicle number e.g. WP-CAB-1234

    public User() {}

    public User(String id, String name, String email, String password,
                String phone, String role, String city, String vehicle) {
        this(id, name, email, password, phone, role, city, vehicle, "", "");
    }

    public User(String id, String name, String email, String password,
                String phone, String role, String city, String vehicle,
                String licenseNumber, String licensePlate) {
        this.id = id; this.name = name; this.email = email; this.password = password;
        this.phone = phone; this.role = role; this.city = city; this.vehicle = vehicle;
        this.licenseNumber = licenseNumber; this.licensePlate = licensePlate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
}
