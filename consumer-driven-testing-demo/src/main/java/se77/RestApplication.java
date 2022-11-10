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

record Customer (Integer id, String name) {};


// complex data object
record CustomerDetails(Customer customer, Address address, PaymentData paymentData ) {};
record Address(String streetname, String city, String country, Integer number, String zipCode) {};
record PaymentData(String accountNumber) {};

@Component
class CustomerClient {
	
	private String baseUrl;
	
	public void setBaseUrl(String url) {
		baseUrl = url;
	}
	
	@Autowired
	private RestTemplate template;
	
	public List<Customer> getAllCustomers() {
		Customer[] customers = template
				.getForObject(baseUrl+"/customers", Customer[].class);
		return Arrays.asList(customers);
	}
	
	public Customer getOneCustomer(Integer id) {
		Customer customer = template
				.getForObject(baseUrl+"/customers/"+id, Customer.class);
		return customer;
	}
	
	public CustomerDetails getCustomerDetails(Integer id) {
		CustomerDetails customerDetails = template
				.getForObject(baseUrl+"/customers/" + id + "/details", CustomerDetails.class);
		return customerDetails;
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
	
	@GetMapping("/{id}/details") 
	public CustomerDetails getDetails(@PathVariable Integer id) {
		return new CustomerDetails(
				new Customer(1,"Ernie"), 
				new Address("Laufener Str.", "Freilassing", "Germany", 5, "833395"), 
				new PaymentData("12345678") );
	}
}