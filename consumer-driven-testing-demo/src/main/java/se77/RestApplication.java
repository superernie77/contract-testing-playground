package se77;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}
	
	@Bean
	public RestTemplate createTemplate() {
		return new RestTemplate();
	}	
}

@RestController
@RequestMapping(path = "/customers")
class CustomerController {
	
	@GetMapping
	public List<Customer> getAll(){
		return List.of(new Customer(1,"Ernie"), new Customer(2,"Bert") );
	}
	
	@GetMapping("/{id}")
	public Customer getOneCustomer(@PathVariable Integer id) {
		return new Customer(id,"Ernie");
	}
}

@Component
class CustomerConsumer {
	
	private String baseUrl;
	
	public void setBaseUrl(String url) {
		baseUrl = url;
	}
	
	@Autowired
	private RestTemplate template;
	
	public List<Customer> printAllCustomers() {
		Customer[] customers = template
				.getForObject(baseUrl+"/customers", Customer[].class);
		return Arrays.asList(customers);
	}
	
	public Customer printOneCustomer(Integer id) {
		Customer customer = template
				.getForObject(baseUrl+"/customers/"+id, Customer.class);
		return customer;
	}

}

record Customer (Integer id, String name) {};