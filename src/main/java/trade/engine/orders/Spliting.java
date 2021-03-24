package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.util.*;


public class Spliting {

    private final Order order;
    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper mapper = new ObjectMapper();
    private final Jedis jedis;
    private final String side;
    private final String exchangeSide;
    Map<String, Object> mallonData = new HashMap<>();

    public Spliting(Order order, Jedis jedis, String side) {
        this.jedis = jedis;
        this.order = order;

        if (side.equals("buy")){
            this.exchangeSide = "BUY_LIMIT";
        }else{
            this.exchangeSide = "SELL_LIMIT";
        }
        this.side = side;
        headers.setContentType(MediaType.APPLICATION_JSON);
    }


    public Map<String, Object> getFirstPrice() {

        return  getMallonOrder("1");
    }

    public Map<String, Object> getSecondPrice() {

        return getMallonOrder("2");
    }

    private Map<String, Object> getMallonOrder(String exchange) {
        String url;
        if (exchange.equals("1")){
           url = "https://exchange.matraining.com/md/"+order.getProduct();
        }else{
            url = "https://exchange2.matraining.com/md/"+order.getProduct();
        }

        System.out.println(url);

        try {
//            ResponseEntity<MallonOrder> responseEntity =
//                    restTemplate.exchange(url,
//                            HttpMethod.GET, null, new ParameterizedTypeReference<MallonOrder>() {
//                            });

//            MallonOrder mallonOrder = restTemplate.getForObject(url,MallonOrder.class);
//            System.out.println(mallonOrder);

//            ;

            ResponseEntity<Map<String, Object>> responseEntity =
                    restTemplate.exchange(url,
                            HttpMethod.GET, null, new ParameterizedTypeReference<Map<String,Object>>() {
                            });

            mallonData = responseEntity.getBody();

//            Optional<MallonOrder> mallon =Optional.of((MallonOrder) Objects.requireNonNull(responseEntity.getBody()));
//             System.out.println(responseEntity.getBody());


            return mallonData;
        }catch (Exception ignored){

        }

        return null;

    }


    public void sendToExchange() throws JsonProcessingException {

        String orderString;
        ExchangeOrder exchangeOrder;
        double buyQuantity,orderQuantity,leftQuantity,quantityDiff;

        Map<String, Object> firstMallonOrder = getFirstPrice();
        Map<String, Object> secondMallonOrder = getSecondPrice();
//
        System.out.println(firstMallonOrder);
        System.out.println(secondMallonOrder);

        //If first section is available and second is not
        if ( firstMallonOrder != null && secondMallonOrder == null){

            quantityDiff = order.getQuantity() - (Integer) firstMallonOrder.get(exchangeSide);

            if (quantityDiff < 0){
                createExchangeObject(order.getOrderId(), order.getProduct(),
                        order.getQuantity(), order.getPrice(),order.getSide(),order.getStatus(),"1");
            }else {
                createExchangeObject(order.getOrderId(), order.getProduct(),
                        (Long) firstMallonOrder.get(exchangeSide), order.getPrice(),order.getSide(),
                        order.getStatus(),"1");
            }

           System.out.println(" 1 ");
        }

        //If second section is available and first is not
        else if ( firstMallonOrder != null && secondMallonOrder == null){

            quantityDiff = order.getQuantity() - (Integer) firstMallonOrder.get(exchangeSide);

            if (quantityDiff < 0){
                createExchangeObject(order.getOrderId(), order.getProduct(),
                        order.getQuantity(), order.getPrice(),order.getSide(),order.getStatus(),"1");
            }else {
                createExchangeObject(order.getOrderId(), order.getProduct(),
                        (Long) firstMallonOrder.get(exchangeSide), order.getPrice(),order.getSide(),
                        order.getStatus(),"1");
            }

           System.out.println(" 2 ");
        }
//
//
        else {

            if (!firstMallonOrder.isEmpty() && !secondMallonOrder.isEmpty()){

                if ((Double)firstMallonOrder.get("BID_PRICE") > (Double)secondMallonOrder.get("BID_PRICE")){

                    //Check how many quantity and buy from first
                    buyQuantity = (int) firstMallonOrder.get(exchangeSide);
                    orderQuantity = order.getQuantity();

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity < 0){

                        //Buy all from here

                        createExchangeObject(order.getOrderId(),order.getProduct(),order.getQuantity(),
                                order.getPrice(),order.getSide(),order.getStatus(),"1");

                        System.out.println(" 3 ");

                    }
                    else{

                        //How much you can get
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(buyQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"1");

                        System.out.println(" 4 ");


                        //Left
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(leftQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"2");

                        System.out.println(" 5 ");

                    }

                    //Buy the rest from other
                }

                else if ((Double)firstMallonOrder.get("BID_PRICE") < (Double)secondMallonOrder.get("BID_PRICE")){

                    //check how many to and buy from second

                    //Buy the rest from other
                    buyQuantity = (double) secondMallonOrder.get("BID_PRICE");
                    orderQuantity = order.getQuantity();

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity < 0){

                        //Buy all from here

                        createExchangeObject(order.getOrderId(),order.getProduct(),order.getQuantity(),
                                order.getPrice(),order.getSide(),order.getStatus(),"2");

                        System.out.println(" 6 ");

                    }
                    else{

                       //How much you can get
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(buyQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"2");
                        System.out.println(" 7 ");


                        //Left
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(leftQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"1");

                        System.out.println(" 8 ");
                    }
                }

                else{

                    //Check how many to get from first and if all can be bought buy from there
                    buyQuantity = (int) firstMallonOrder.get("BID_PRICE");
                    orderQuantity = order.getQuantity();

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity < 0){

                        //All from here
                        createExchangeObject(order.getOrderId(),order.getProduct(),order.getQuantity(),
                                order.getPrice(),order.getSide(),order.getStatus(),"1");

                        System.out.println(" 9 ");
                    }
                    else{

                        //How much you can buy
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(buyQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"1");
                        System.out.println(" 10 ");

                        //Left
                        createExchangeObject(order.getOrderId(),order.getProduct(), Math.round(leftQuantity),
                                order.getPrice(),order.getSide(),order.getStatus(),"2");
                        System.out.println(" 11 ");
                    }

                    //Buy the rest from the second
                }

            }
            else {
                System.out.println("No place to trade order");
            }
        }

    }

    public void createExchangeObject(Long orderId,String product,Long quantity,
                                      double price,String side, String status,String exchange) throws JsonProcessingException {

        ExchangeOrder exchangeOrder= new ExchangeOrder(orderId,product,quantity,price,side,status,exchange);
        pushToQueue(changeObjectToString(exchangeOrder));
    }

    public String changeObjectToString(ExchangeOrder exchangeOrder) throws JsonProcessingException {

        return mapper.writeValueAsString(exchangeOrder);

    }

    public void pushToQueue(String order){

        jedis.rpush("incoming-orders", order);
        jedis.publish("report-message",order+" has been sent to the Exchange service");
    }



    public static void main(String[] args) throws JsonProcessingException {

        Order order = new Order(1L,"GOOGL",1L,2.5,
                        "SELL","PENDING",1L,2L,"done", LocalDate.now());

        Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");

        Spliting spliting = new Spliting(order,jedis, order.getSide().toLowerCase(Locale.ROOT));

//        spliting.getMallonOrder("1");

//        System.out.println(spliting.getSecondPrice());
        spliting.sendToExchange();
    }


}
