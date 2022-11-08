package se77;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CustomerService")
public class CustomerConsumerPactTest {
	
	@Autowired
	private CustomerClient customerClient;
	
	 @Pact(consumer = "CustomerClient")
	  public RequestResponsePact allCustomers(PactDslWithProvider builder) {
	    return builder
	      .given("customers exists")
	      .uponReceiving("get all customers")
	      .path("/customers")
	      .willRespondWith()
	      .status(200)
	      .body(
	        PactDslJsonArray
	          .arrayMaxLike(2)
	          .integerType("id", 1)
	          .stringType("name", "Ernie")
	      )
	      .toPact();
	  }
	 
	 @Pact(consumer = "CustomerClient")
	  public RequestResponsePact singleCustomer(PactDslWithProvider builder) {
	    return builder
	      .given("customer with ID 1 exists", "id", 1)
	      .uponReceiving("get customer with ID 1")
	      .path("/customers/1")
	      .willRespondWith()
	      .status(200)
	      .body(
	        new PactDslJsonBody()
	          .integerType("id", 1)
	          .stringType("name", "Ernie")
	      )
	      .toPact();
	  }
	 
	  @Test
	  @PactTestFor(pactMethod = "allCustomers", port="9999")
	  void testAllProducts(MockServer mockServer) throws IOException {
		  customerClient.setBaseUrl(mockServer.getUrl());
		  List<Customer> customers = customerClient.getAllCustomers();
		  assertThat(customers, hasSize(1));
	  }

	  @Test
	  @PactTestFor(pactMethod = "singleCustomer", port="9999")
	  void testSingleProduct(MockServer mockServer) throws IOException {
		  customerClient.setBaseUrl(mockServer.getUrl());
		  Customer customer = customerClient.getOneCustomer(1);
		  assertThat(customer, is(equalTo(new Customer(1, "Ernie"))));
	  }
}