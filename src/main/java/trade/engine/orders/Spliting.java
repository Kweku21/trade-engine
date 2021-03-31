package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;


public class Spliting {

    private final Order order;
    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper mapper = new ObjectMapper();
    private final Jedis jedis;
    private final String side;
    private final String exchangeSide;
    private final String exchangeType;
    Map<String, Object> mallonData = new HashMap<>();

    public Spliting(Order order, Jedis jedis, String side) {
        this.jedis = jedis;
        this.order = order;

        if (side.equals("buy".toLowerCase(Locale.ROOT))){
            this.exchangeSide = "BUY_LIMIT";
            this.exchangeType = "BID_PRICE";
        }else{
            this.exchangeSide = "SELL_LIMIT";
            this.exchangeType = "ASK_PRICE";
        }
        this.side = side;
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Logging to report
//        publishToReport(order+" has been received into trade engine");
    }


    public Map<String, Object> getFirstPrice() {

        return  getMallonOrder("1");
    }

    public Map<String, Object> getSecondPrice() {

        return getMallonOrder("2");
    }

    protected Map<String, Object> getMallonOrder(String exchange) {
        String url;
        if (exchange.equals("1")){
           url = "https://exchange.matraining.com/md/"+order.getProduct();
        }else{
            url = "https://exchange2.matraining.com/md/"+order.getProduct();
        }

        try {

            ResponseEntity<Map<String, Object>> responseEntity =
                    restTemplate.exchange(url,
                            HttpMethod.GET, null, new ParameterizedTypeReference<Map<String,Object>>() {
                            });

            return responseEntity.getBody();
        }catch (Exception ignored){

        }

        return null;

    }

    //Send an exchange
    public void sendExchange() throws JsonProcessingException {

        int buyQuantity,orderQuantity,leftQuantity,quantityDiff;

        Map<String, Object> firstMallonOrder = getFirstPrice();
        Map<String, Object> secondMallonOrder = getSecondPrice();
//
        System.out.println(firstMallonOrder);
        System.out.println(secondMallonOrder);

        //If first section is available and second is not
        if ( firstMallonOrder != null && secondMallonOrder == null){

            quantityDiff = (int) (order.getQuantity() - (int) firstMallonOrder.get(exchangeSide));

            // if quantity is less than 0 or 0 buy all from here
            if (quantityDiff <= 0){
                checkPriceBidBeforeMakingOrder(firstMallonOrder,"1",order.getQuantity());
            }else {
                // or buy the 1 you can get from there
                checkPriceBidBeforeMakingOrder(firstMallonOrder,"1",(Long) firstMallonOrder.get(exchangeSide));
            }

            System.out.println(" 1 ");
        }

        //If second section is available and first is not
        else if ( firstMallonOrder == null && secondMallonOrder != null){

            quantityDiff = (int) (order.getQuantity() - (Integer) secondMallonOrder.get(exchangeSide));

            if (quantityDiff < 0){
                checkPriceBidBeforeMakingOrder(secondMallonOrder,"2",order.getQuantity());
            }
            else {
                checkPriceBidBeforeMakingOrder(secondMallonOrder,"2",(Long) secondMallonOrder.get(exchangeSide));
            }

            System.out.println(" 2 ");
        }
//
//      now split to other changes
        else {

            if (firstMallonOrder != null && secondMallonOrder != null){

                Number firstMallon = (Number) firstMallonOrder.get(exchangeType);
                Number secondMallon = (Number) secondMallonOrder.get(exchangeType);

                if (firstMallon.intValue() > secondMallon.intValue()){

                    //Check how many quantity and buy from first
                    buyQuantity = (int) firstMallonOrder.get(exchangeSide);
                    orderQuantity = Math.toIntExact(order.getQuantity());

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity <= 0){

                        //Buy all from first
                        checkPriceBidBeforeMakingOrder(firstMallonOrder,"1",(Long) order.getQuantity());
                        System.out.println(" 3 ");

                    }
                    else{

                        //How much you can get from first
                        checkPriceBidBeforeMakingOrder(firstMallonOrder,"1", (long) buyQuantity);
                        System.out.println(" 4 ");

                        //buy the rest from the second
                        checkPriceBidBeforeMakingOrder(secondMallonOrder,"2", (long) leftQuantity);
                        System.out.println(" 5 ");

                    }

                    //Buy the rest from other
                }

                else if (firstMallon.intValue() < secondMallon.intValue()){

                    //check how many to and buy from second

                    //Check the quantity difference
                    buyQuantity = (int) secondMallonOrder.get(exchangeSide);
                    orderQuantity = Math.toIntExact(order.getQuantity());

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity <= 0){

                        //Buy all from second
                        checkPriceBidBeforeMakingOrder(secondMallonOrder,"2",(Long) order.getQuantity());
                        System.out.println(" 6 ");

                    }
                    else{

                        //Buy how much you can get from second
                        checkPriceBidBeforeMakingOrder(secondMallonOrder,"2", (long) buyQuantity);
                        System.out.println(" 7 ");


                        //Buy the rest from the first
                        checkPriceBidBeforeMakingOrder(firstMallonOrder,"1", (long) leftQuantity);
                        System.out.println(" 8 ");
                    }
                }

                else{

                    //Check how many to get from first and if all can be bought buy from there
                    buyQuantity = (int) firstMallonOrder.get(exchangeSide);
                    orderQuantity = Math.toIntExact(order.getQuantity());

                    leftQuantity = orderQuantity - buyQuantity;

                    if (leftQuantity <= 0){

                        //Buy all from 1st
                        checkPriceBidBeforeMakingOrder(firstMallonOrder,"1",(Long) order.getQuantity());
                        System.out.println(" 9 ");
                    }
                    else{

                        //Buy how many you can get from first
                        checkPriceBidBeforeMakingOrder(firstMallonOrder,"1", (long) buyQuantity);
                        System.out.println(" 10 ");

                        //Buy the rest from second
                        checkPriceBidBeforeMakingOrder(secondMallonOrder,"2", (long) leftQuantity);
                        System.out.println(" 11 ");
                    }

                    //Buy the rest from the second
                }

            }
            else {
                publishToReport("No place to trade order");
                System.out.println("No place to trade order");
            }
        }

    }


    protected void checkPriceBidBeforeMakingOrder(Map<String, Object> exchange,String exchangeType,Long quantity) throws JsonProcessingException {

        if (checkPrice((Number) exchange.get("BID_PRICE"),order.getPrice(), (Integer) exchange.get("MAX_PRICE_SHIFT"))){
            createExchangeObject(order.getClientOrderId(), order.getProduct(),quantity,
                     order.getPrice(),order.getSide(),order.getStatus(),exchangeType);

        }else {
            publishToReport(order+" has invalid price against the "+exchangeType+" mallon exchange ");

        }

    }

    public Boolean checkPrice(Number marketValue,Double orderValue,int maxShift){
        return orderValue <= marketValue.intValue()+maxShift && orderValue >= marketValue.intValue() - maxShift;
    }

    public void createExchangeObject(Long clientOrderId,String product,Long quantity,
                                      double price,String side, String status,String exchange) throws JsonProcessingException {

        ExchangeOrder exchangeOrder= new ExchangeOrder(clientOrderId,product,quantity,price,side,status,exchange);
        System.out.println(exchangeOrder);
        pushToQueue(changeObjectToString(exchangeOrder));
    }

    public String changeObjectToString(ExchangeOrder exchangeOrder) throws JsonProcessingException {
        return mapper.writeValueAsString(exchangeOrder);
    }

    public void pushToQueue(String order){

        jedis.rpush("incoming-orders", order);
        publishToReport(order+" has been sent to the Exchange service");
    }

    public void publishToReport(String message ){
        jedis.publish("report-message","Trade Engine "+message);
    }


    public static void main(String[] args) throws JsonProcessingException {

        Order order = new Order(1L,"GOOGL",100L,1.5,
                "BUY","PENDING",1L,2L,"done", LocalDate.now());

        Order order1 = new Order(1L,"ORCL",51L,5.0,
                "BUY","PENDING",2L,1L,"VALIDATED", LocalDate.now());



        Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");

        Spliting spliting = new Spliting(order,jedis, order.getSide().toLowerCase(Locale.ROOT));

//        spliting.getMallonOrder("1");

//        System.out.println(spliting.getSecondPrice());
//        spliting.sendExchange();
        for (int i = 1; i<=10;i++){
            spliting.sendExchange();
        }

        IntStream intStream = IntStream.range(20, 30);
//        intStream.forEach(spliting.sendExchange());
    }

}
