package se77;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/customer")
public class CustomerController {
	
	@GetMapping
	public List<Customer> getAll(){
		return List.of(new Customer(1,"Ernie"), new Customer(2,"Bert") );
	}
	
}
