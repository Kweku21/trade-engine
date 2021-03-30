package trade.engine.orders;

public class ExchangeOrder {

    private long clientOrderId;
    private String product;
    private Long quantity;
    private double price;
    private String side;
    private String status;
    private String exchange;

    public ExchangeOrder(Long clientOrderId, String product, Long quantity, double price, String side, String status, String exchange) {
        this.clientOrderId = clientOrderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.status = status;
        this.exchange = exchange;
    }

    public ExchangeOrder() {
    }

    public Long getOrderId() {
        return clientOrderId;
    }

    public void setOrderId(Long orderId) {
        this.clientOrderId = orderId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "ExchangeOrder{" +
                "clientOrderId=" + clientOrderId +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
