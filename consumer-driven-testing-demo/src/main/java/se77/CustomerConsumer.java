package se77;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerConsumer {
	
	@Autowired
	private RestTemplate template;
	
	public void getAllCustomers() {
		Customer[] customers = template.getForObject("http://localhost:8080/customer", Customer[].class);
		Arrays.stream(customers).forEach(System.out::println);
	}

}
