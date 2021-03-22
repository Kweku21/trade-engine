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
    public Object trySomething(){
        Jedis jedis = new Jedis();
        Object object = jedis.publish("report-message", "order made successfully");
        System.out.println(object);

        return object;
    }



}
