package food_delivery_system.model;

/** Customer review for a food/restaurant after delivery. */
public class Review {
    private String id;
    private String customerId;
    private String customerName;
    private String restaurantId;
    private String orderId;
    private int rating;       // 1..5
    private String comment;
    private String createdAt;

    public Review() {}
    public Review(String id, String customerId, String customerName, String restaurantId,
                  String orderId, int rating, String comment, String createdAt) {
        this.id=id; this.customerId=customerId; this.customerName=customerName;
        this.restaurantId=restaurantId; this.orderId=orderId; this.rating=rating;
        this.comment=comment; this.createdAt=createdAt;
    }
    public String getId(){return id;} public void setId(String i){this.id=i;}
    public String getCustomerId(){return customerId;} public void setCustomerId(String c){this.customerId=c;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String c){this.customerName=c;}
    public String getRestaurantId(){return restaurantId;} public void setRestaurantId(String r){this.restaurantId=r;}
    public String getOrderId(){return orderId;} public void setOrderId(String o){this.orderId=o;}
    public int getRating(){return rating;} public void setRating(int r){this.rating=r;}
    public String getComment(){return comment;} public void setComment(String c){this.comment=c;}
    public String getCreatedAt(){return createdAt;} public void setCreatedAt(String c){this.createdAt=c;}
}
