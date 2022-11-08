package se77;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.tomakehurst.wiremock.WireMockServer;

import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ExtendWith({ WiremockResolver.class, WiremockUriResolver.class })
public class CustomerConsumerTest {
	
	@Autowired
	CustomerConsumer consumer;
	
	@Test
	  void fetchCustomers(@Wiremock WireMockServer server, @WiremockUri String uri) {
		consumer.setBaseUrl(uri);
	    server.stubFor(
	      get(urlPathEqualTo("/customers"))
	        .willReturn(aResponse()
	          .withStatus(200)
	          .withBody("[\n" +
	            "            {\n" +
	            "                \"id\": 1,\n" +
	            "                \"name\": \"Ernie\",\n" +
	            "            },\n" +
	            "            {\n" +
	            "                \"id\": 2,\n" +
	            "                \"name\": \"Bert\",\n" +
	            "            }\n" +
	            "        ]\n")
	          .withHeader("Content-Type", "application/json"))
	    );

	    List<Customer> response = consumer.printAllCustomers();
	    assertThat(response, hasSize(2));
	  }

	  @Test
	  void getCustomerById(@Wiremock WireMockServer server, @WiremockUri String uri) {
		  consumer.setBaseUrl(uri);
	    server.stubFor(
	      get(urlPathEqualTo("/products/10"))
	        .willReturn(aResponse()
	          .withStatus(200)
	          .withBody("{\n" +
	            "            \"id\": 10,\n" +
	            "            \"name\": \"28 Degrees\",\n" +
	            "        }\n")
	          .withHeader("Content-Type", "application/json"))
	    );

	    Customer customer = consumer.printOneCustomer(1);
	    assertThat(customer, is(equalTo(new Customer(1, "Ernie"))));
	  }
}
