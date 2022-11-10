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
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.LambdaDslJsonBody;
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
	        // define response array with randoly generated values
	        PactDslJsonArray
	          .arrayMinLike(2) // should be 2 items in the array
	          .integerType("id") // random ID
	          .stringType("name") // random name
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
				// 	define a response with concrete values
	        new PactDslJsonBody()
	          .integerType("id", 1)
	          .stringType("name", "Ernie")
	      )
	      .toPact();
	  }
	 
	 @Pact(consumer = "CustomerClient")
	 public RequestResponsePact singleCustomerDetails(PactDslWithProvider builder) {
		 
		 // example for a nested object return value specified with LambdaDsl
		 DslPart body = LambdaDsl.newJsonBody( o -> {
				o.object("customer",  c -> {
					c.stringType("name","Ernie");
					c.integerType("id", 1);
				});
				o.object("address",  a -> {
					a.stringType("streetname");
					a.stringType("city");
					a.stringType("country");
					a.integerType("number");
					a.stringType("zipCode");
				});
				o.object("paymentData", pd -> {
					pd.stringType("accountNumber", "123456");
				});
			}).build();
		 
		 return builder
				.given("customerdetails with ID 1 exists", "id", 1)
				.uponReceiving("get customerdetails for ID 1")
				.path("/customers/1/details")
				.willRespondWith()
				.status(200)
				.body(body)
				.toPact();
	  }
	 
	  @Test
	  @PactTestFor(pactMethod = "allCustomers", port="9999")
	  void testAllCustomers(MockServer mockServer) throws IOException {
		  customerClient.setBaseUrl(mockServer.getUrl());
		  List<Customer> customers = customerClient.getAllCustomers();
		  
		  // test against size of response
		  // values in the customer object are random
		  assertThat(customers, hasSize(2));
	  }

	  @Test
	  @PactTestFor(pactMethod = "singleCustomer", port="9999")
	  void testSingleCustomer(MockServer mockServer) throws IOException {
		  customerClient.setBaseUrl(mockServer.getUrl());
		  Customer customer = customerClient.getOneCustomer(1);
		  // test client against concrete values
		  assertThat(customer, is(equalTo(new Customer(1, "Ernie"))));
	  }
	  
	  @Test
	  @PactTestFor(pactMethod = "singleCustomerDetails", port="9999")
	  void testSingleCustomerDetails(MockServer mockServer) throws IOException {
		  customerClient.setBaseUrl(mockServer.getUrl());
		  CustomerDetails customerDetails = customerClient.getCustomerDetails(1);
		  assertThat(customerDetails.customer(), is(equalTo(new Customer(1, "Ernie"))));
		  assertThat(customerDetails.paymentData(), is(equalTo(new PaymentData("123456"))));
	  }
}

