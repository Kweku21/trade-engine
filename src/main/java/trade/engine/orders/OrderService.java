package trade.engine.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper mapper = new ObjectMapper();

    public OrderService() {

        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public void makeOrder(Order order,String buyFrom) throws JsonProcessingException {

        String requestJson = mapper.writeValueAsString(order);
        HttpEntity<String> entity = new HttpEntity<>(requestJson,headers);
        Order orderPrice = restTemplate.postForObject(buyFrom, entity, Order.class);


    }
}
