package food_delivery_system.model;

/** Food item in a restaurant menu. */
public class Food {
    private String id;
    private String restaurantId;
    private String name;
    private String category;
    private double price;
    private String image;
    private String description;

    public Food() {}
    public Food(String id, String restaurantId, String name, String category,
                double price, String image, String description) {
        this.id=id; this.restaurantId=restaurantId; this.name=name; this.category=category;
        this.price=price; this.image=image; this.description=description;
    }
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getRestaurantId(){return restaurantId;} public void setRestaurantId(String r){this.restaurantId=r;}
    public String getName(){return name;} public void setName(String n){this.name=n;}
    public String getCategory(){return category;} public void setCategory(String c){this.category=c;}
    public double getPrice(){return price;} public void setPrice(double p){this.price=p;}
    public String getImage(){return image;} public void setImage(String i){this.image=i;}
    public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
}
