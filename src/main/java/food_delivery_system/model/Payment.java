package food_delivery_system.model;

/** Dummy payment record. */
public class Payment {
    private String id;
    private String orderId;
    private String customerId;
    private double amount;
    private String cardLast4;
    private String status; // PAID
    private String paidAt;

    public Payment() {}
    public Payment(String id, String orderId, String customerId, double amount,
                   String cardLast4, String status, String paidAt) {
        this.id=id; this.orderId=orderId; this.customerId=customerId; this.amount=amount;
        this.cardLast4=cardLast4; this.status=status; this.paidAt=paidAt;
    }
    public String getId(){return id;} public void setId(String i){this.id=i;}
    public String getOrderId(){return orderId;} public void setOrderId(String o){this.orderId=o;}
    public String getCustomerId(){return customerId;} public void setCustomerId(String c){this.customerId=c;}
    public double getAmount(){return amount;} public void setAmount(double a){this.amount=a;}
    public String getCardLast4(){return cardLast4;} public void setCardLast4(String c){this.cardLast4=c;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public String getPaidAt(){return paidAt;} public void setPaidAt(String p){this.paidAt=p;}
}
