package com.example.demoPactConsumer;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.demoPactConsumer.model.User;
import com.example.demoPactConsumer.repository.UserRepository;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.ZoneId;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "test_provider.base-url:http://localhost:8095",
        classes = UserRepository.class)
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "test_provider", port = "8095")
public class PactTest {
    @Autowired
    private UserRepository userServiceRepository;

    @Pact(consumer =  "test_consumer")
    public RequestResponsePact pactUserExists(PactDslWithProvider builder) {
        return builder.given(
                "default")
                .uponReceiving("A request to /findUser/Vitalik")
                .path("/findUser/Vitalik")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(new PactDslJsonBody()
                        .integerType("age",12)
                        .stringValue("postalCode", "1111")
                        .and("firstName","Vitalik")
                        .and("lastName","Johnson")
                        .and("position","Admin"))
                .toPact();

    }
    @PactTestFor(pactMethod = "pactUserExists")
    @Test
    public void userExists() {
        final User user = userServiceRepository.getUserByFirstName("Vitalik");
        assertThat(user.getFirstName().equals("Vitalik"));
    }
}
