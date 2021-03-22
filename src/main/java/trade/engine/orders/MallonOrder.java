package trade.engine.orders;

import java.util.List;

public class MallonOrder {

    private String product;
    private Double price;
    private int quantity;
    private String side;
    private List executions;
    private Long cumulatitiveQuantity;

    public MallonOrder(String product,
                       Double price,
                       int quantity,
                       String side,
                       List executions,
                       Long cumulatitiveQuantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.executions = executions;
        this.cumulatitiveQuantity = cumulatitiveQuantity;
    }

    public Double getPrice() {
        return price;
    }

    public Long getCumulatitiveQuantity() {
        return cumulatitiveQuantity;
    }

    public MallonOrder() {
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSide() {
        return side;
    }

    public List getExecutions() {
        return executions;
    }

    @Override
    public String toString() {
        return "MallonOrder{" +
                "product='" + product + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", side='" + side + '\'' +
                ", executions=" + executions +
                ", cumulatitiveQuantity=" + cumulatitiveQuantity +
                '}';
    }
}
