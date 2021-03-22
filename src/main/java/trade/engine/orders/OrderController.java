package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/trade")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<String> makeTrade(@RequestBody Order order) throws JsonProcessingException {

        Spliting orderSpliting = new Spliting(order);

        orderSpliting.sendToExchange();

        return new ResponseEntity<>("Order is been processed ", HttpStatus.OK);
    }

    @GetMapping("/try")
    public String trySomething(){

//        Jedis jedis = new Jedis();
        Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");
        Object object = jedis.publish("report-message", "order made successfully");
//        System.out.println(object);

        return "hello";
    }



}
