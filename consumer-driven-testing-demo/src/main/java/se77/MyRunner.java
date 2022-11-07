package se77;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements CommandLineRunner {

	@Autowired
	private CustomerConsumer consumer;
	
	@Override
	public void run(String... args) throws Exception {
		consumer.getAllCustomers();

	}

}
