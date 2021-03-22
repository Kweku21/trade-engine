package trade.engine.orders;

import java.time.LocalDate;
import java.time.LocalTime;

public class Order {

    private Long orderId;
    private String product;
    private int quantity;
    private double price;
    private String side;
    private String status;
    private Long portfolioId;
    private Long clientId;
    private String validationStatus;
    private LocalDate createAt;


    public Order(Long orderId,
                 String product,
                 int quantity,
                 double price,
                 String side,
                 String status,
                 Long portfolioId,
                 Long clientId,
                 String validationStatus,
                 LocalDate createAt) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.status = status;
        this.portfolioId = portfolioId;
        this.clientId = clientId;
        this.validationStatus = validationStatus;
        this.createAt = createAt;
    }

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", portfolioId=" + portfolioId +
                ", clientId=" + clientId +
                ", validationStatus='" + validationStatus + '\'' +
                ", createAt=" + createAt +
                '}';
    }
}
