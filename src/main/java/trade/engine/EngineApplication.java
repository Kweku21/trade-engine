package trade.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import trade.engine.orders.Order;
import trade.engine.orders.Spliting;

import java.time.LocalDate;
import java.util.Locale;

@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) throws JsonProcessingException {

        SpringApplication.run(EngineApplication.class, args);

        Order order = new Order(1L,"GOOGL",10L,0.5,
                "BUY","PENDING",2L,2L,"done", LocalDate.now());

        Order order1 = new Order(1L,"GOOGL",1000L,0.4,
                "SELL","PENDING",2L,2L,"VALIDATED", LocalDate.now());



        Jedis jedis = new Jedis("redis-17587.c92.us-east-1-3.ec2.cloud.redislabs.com", 17587);
        jedis.auth("rLAKmB4fpXsRZEv9eJBkbddhTYc1RWtK");

        Spliting spliting = new Spliting(order,jedis, order.getSide().toLowerCase(Locale.ROOT));

        Spliting spliting2 = new Spliting(order1,jedis, order.getSide().toLowerCase(Locale.ROOT));

        while(true){
            for (int i = 1; i<=200;i++){
                System.out.println("Selling");
                spliting2.sendExchange();
            }

            for (int i = 1; i<=100;i++){
                System.out.println("Buying");
                spliting.sendExchange();
            }
        }
    }

}
