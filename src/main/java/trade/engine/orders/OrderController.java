package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.Locale;

@RestController
@RequestMapping("/trade")
public class OrderController {

    @Autowired
    private final OrderService orderService;
    Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);

    public OrderController(OrderService orderService) {
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<String> makeTrade(@RequestBody Order order) throws JsonProcessingException {

        jedis.publish("report-message",order.toString()+" has been received into the trade-engine service");
        Spliting orderSpliting = new Spliting(order,jedis, order.getSide().toLowerCase(Locale.ROOT));

        orderSpliting.sendExchange();

        return new ResponseEntity<>("Order is been processed ", HttpStatus.OK);
    }

//    @PostMapping("/")



}
