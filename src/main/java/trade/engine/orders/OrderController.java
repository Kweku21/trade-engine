package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trade")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order")
    public ResponseEntity<String> makeTrade(@RequestBody Order order) throws JsonProcessingException {

        Spliting orderSpliting = new Spliting(order);

        orderSpliting.sendToExchange();

        return new ResponseEntity<>("Order is been processed ", HttpStatus.OK);
    }



}
