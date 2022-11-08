package se77;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreMissingStateChange;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("CustomerService")
@PactFolder("pacts")
@IgnoreMissingStateChange
public class PactVerificationTest {
	
	 @LocalServerPort
	  private int port;

	  @BeforeEach
	  void setup(PactVerificationContext context) {
	    context.setTarget(new HttpTestTarget("localhost", port));
	  }
	  
	  @TestTemplate
	  @ExtendWith(PactVerificationInvocationContextProvider.class)
	  void pactVerificationTestTemplate(PactVerificationContext context) {
	    context.verifyInteraction();
	  }
}
