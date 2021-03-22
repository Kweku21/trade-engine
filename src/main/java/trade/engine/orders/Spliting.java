package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public class Spliting {

    private final Order order;
    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper mapper = new ObjectMapper();
    Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);


    public Spliting(Order order) {
        this.order = order;
        headers.setContentType(MediaType.APPLICATION_JSON);
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");
    }

    public Optional<MallonOrder> getFirstPrice(Order order) {

        return getMallonOrder(order,"1");
    }

    public Optional<MallonOrder> getSecondPrice(Order order) {

        return getMallonOrder(order,"2");
    }

    private Optional<MallonOrder> getMallonOrder(Order order,String exchange) {
        String url;
        if (exchange.equals("1")){
           url = "https://exchange.matraining.com/orderbook/"+order.getProduct()+"/buy";
        }else{
            url = "https://exchange1.matraining.com/orderbook/"+order.getProduct()+"/buy";
        }

        try {
            ResponseEntity<List<MallonOrder>> responseEntity =
                    restTemplate.exchange(url,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<MallonOrder>>() {
                            });
            Optional<MallonOrder> mallonOrder = Optional.ofNullable((MallonOrder) responseEntity.getBody().get(0));

            return mallonOrder;
        }catch (Exception e){

        }

        return Optional.empty();
    }


    public void sendToExchange() throws JsonProcessingException {

        String orderString;
        ExchangeOrder exchangeOrder;
        int buyQuantity,orderQuantity,leftQuantity;

        Optional<MallonOrder> firstmallonOrder = getFirstPrice(order);
        Optional<MallonOrder> secondmallonOrder = getSecondPrice(order);

//        System.out.println(secondmallonOrder.isPresent());

        if (firstmallonOrder.isPresent() && secondmallonOrder.isEmpty()){
           exchangeOrder = new ExchangeOrder(order.getOrderId(),
                    order.getProduct(),
                    firstmallonOrder.get().getQuantity(),
                    order.getPrice(),
                    order.getSide(),
                    order.getStatus(),
                    "1");

//           exchangeOrder = createExchangeObject(order.getOrderId(),order.getProduct(),3)

            pushToQueue(changeObjectToString(exchangeOrder));
            System.out.println(exchangeOrder +" 1 ");
        }

        else if (firstmallonOrder.isEmpty() && secondmallonOrder.isPresent()){
            exchangeOrder = new ExchangeOrder(order.getOrderId(),
                    order.getProduct(),
                    secondmallonOrder.get().getQuantity(),
                    order.getPrice(),
                    order.getSide(),
                    order.getStatus(),
                    "2");
            pushToQueue(changeObjectToString(exchangeOrder));
            System.out.println(exchangeOrder+" 2 ");
        }


        else if (firstmallonOrder.isPresent() && secondmallonOrder.isPresent()){

            System.out.println("here also");

            if (firstmallonOrder.get().getPrice() > secondmallonOrder.get().getPrice()){

                //Check how many quantity and buy from first
                buyQuantity = firstmallonOrder.get().getQuantity();
                orderQuantity = order.getQuantity();

                leftQuantity = orderQuantity - buyQuantity;

                if (leftQuantity < 0){

                    //Buy all from here

                   exchangeOrder = new ExchangeOrder(order.getOrderId(),
                                                    order.getProduct(),
                                                    order.getQuantity(),
                                                    order.getPrice(),
                                                    order.getSide(),
                                                    order.getStatus(),
                                                    "1");
                    pushToQueue(changeObjectToString(exchangeOrder));
                    System.out.println(exchangeOrder+" 3 ");
                    
                }
                else{

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                                                    order.getProduct(),
                                                    buyQuantity,
                                                    order.getPrice(),
                                                    order.getSide(),
                                                    order.getStatus(),
                                                    "1");
                    pushToQueue(changeObjectToString(exchangeOrder));

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            leftQuantity,
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "2");

                    pushToQueue(changeObjectToString(exchangeOrder));

                }

                //Buy the rest from other
            }

            else if (firstmallonOrder.get().getPrice() < secondmallonOrder.get().getPrice()){

                //check how many to and buy from second

                //Buy the rest from other
                buyQuantity = secondmallonOrder.get().getQuantity();
                orderQuantity = order.getQuantity();

                leftQuantity = orderQuantity - buyQuantity;

                if (leftQuantity < 0){

                    //Buy all from here

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            order.getQuantity(),
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "2");
                    pushToQueue(changeObjectToString(exchangeOrder));

                }
                else{

                   exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            buyQuantity,
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "1");
                    pushToQueue(changeObjectToString(exchangeOrder));

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            leftQuantity,
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "1");
                    pushToQueue(changeObjectToString(exchangeOrder));
                }
            }

            else{

                //Check how many to get from first and if all can be bought buy from there
                buyQuantity = firstmallonOrder.get().getQuantity();
                orderQuantity = order.getQuantity();

                leftQuantity = orderQuantity - buyQuantity;

                if (leftQuantity < 0){

                    //Buy all from here

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            order.getQuantity(),
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "1");
                    pushToQueue(changeObjectToString(exchangeOrder));
                }
                else{

                    exchangeOrder = new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            buyQuantity,
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "1");
                    pushToQueue(changeObjectToString(exchangeOrder));

                    exchangeOrder= new ExchangeOrder(order.getOrderId(),
                            order.getProduct(),
                            leftQuantity,
                            order.getPrice(),
                            order.getSide(),
                            order.getStatus(),
                            "2");
                    pushToQueue(changeObjectToString(exchangeOrder));
                }

                //Buy the rest from the second
            }

        }else {
            System.out.println("No place to buy orders");
        }

    }

    public ExchangeOrder createExchangeObject(Long orderId,String product,int quantity,
                                      double price,String side, String status,String exchange){

        return new ExchangeOrder(orderId,product,quantity,price,side,status,exchange);
    }

    public String changeObjectToString(ExchangeOrder exchangeOrder) throws JsonProcessingException {

        return mapper.writeValueAsString(exchangeOrder);

    }

    public void pushToQueue(String order){

        jedis.rpush("incoming-orders", order);
    }



    public static void main(String[] args) throws JsonProcessingException {
//
//        Order order = new Order(1L, orderId, "IBM",10,10,"buy","pending", portfolioId, clientId, validationStatus, createAt);

        Order order = new Order(1L,"IBM",1,10,
                        "buy","pending",1L,2L,"done", LocalDate.now());

        Spliting spliting = new Spliting(order);

        spliting.sendToExchange();
//
////        System.out.println(order);
////        System.out.println(spliting);
//
////        String url = "https://exchange.matraining.com/orderbook/IBM/buy";
////
////        RestTemplate restTemplate = new RestTemplate();
////
////        ResponseEntity<List<MallonOrder>> responseEntity =
////                restTemplate.exchange(url,
////                        HttpMethod.GET, null, new ParameterizedTypeReference<List<MallonOrder>>() {
////                        });
////        List<MallonOrder> mallonOrder = responseEntity.getBody();
////        System.out.println(mallonOrder);
    }


}
