package stepDefinitions;

import au.com.telstra.simcardactivator.ActivationRequest;
import au.com.telstra.simcardactivator.ActivationStatusResponse;
import au.com.telstra.simcardactivator.ActuatorResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SimCardActivatorStepDefinitions {

    // Provided in the skeleton; if it's already here, just reuse it
    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<ActuatorResponse> activationResponse;
    private ResponseEntity<ActivationStatusResponse> queryResponse;

    private static final String BASE_URL = "http://localhost:8080";

    @When("I submit an activation request for ICCID {string} and email {string}")
    public void i_submit_an_activation_request_for_iccid_and_email(String iccid, String email) {
        ActivationRequest request = new ActivationRequest(iccid, email);

        activationResponse = restTemplate.postForEntity(
                BASE_URL + "/sim/activate",
                request,
                ActuatorResponse.class
        );
    }

    @Then("the activation response should indicate success")
    public void the_activation_response_should_indicate_success() {
        Assertions.assertNotNull(activationResponse, "Activation response should not be null");
        Assertions.assertTrue(
                activationResponse.getStatusCode().is2xxSuccessful(),
                "HTTP status should be 2xx for a successful activation"
        );
        Assertions.assertNotNull(activationResponse.getBody(), "Activation response body should not be null");
        Assertions.assertTrue(
                activationResponse.getBody().isSuccess(),
                "Activation success flag should be true"
        );
    }

    @Then("the activation response should indicate failure")
    public void the_activation_response_should_indicate_failure() {
        Assertions.assertNotNull(activationResponse, "Activation response should not be null");
        Assertions.assertTrue(
                activationResponse.getStatusCode().is2xxSuccessful(),
                "HTTP status should still be 2xx even when activation fails"
        );
        Assertions.assertNotNull(activationResponse.getBody(), "Activation response body should not be null");
        Assertions.assertFalse(
                activationResponse.getBody().isSuccess(),
                "Activation success flag should be false"
        );
    }

    @When("I query the activation with id {long}")
    public void i_query_the_activation_with_id(Long id) {
        queryResponse = restTemplate.getForEntity(
                BASE_URL + "/sim/activation?simCardId=" + id,
                ActivationStatusResponse.class
        );
    }

    @Then("the activation record should have ICCID {string}")
    public void the_activation_record_should_have_iccid(String expectedIccid) {
        Assertions.assertNotNull(queryResponse, "Query response should not be null");
        Assertions.assertTrue(
                queryResponse.getStatusCode().is2xxSuccessful(),
                "HTTP status from query should be 2xx"
        );
        ActivationStatusResponse body = queryResponse.getBody();
        Assertions.assertNotNull(body, "Query response body should not be null");
        Assertions.assertEquals(expectedIccid, body.getIccid(), "ICCID in DB should match expected");
    }

    @Then("the activation record should have customer email {string}")
    public void the_activation_record_should_have_customer_email(String expectedEmail) {
        Assertions.assertNotNull(queryResponse, "Query response should not be null");
        ActivationStatusResponse body = queryResponse.getBody();
        Assertions.assertNotNull(body, "Query response body should not be null");
        Assertions.assertEquals(expectedEmail, body.getCustomerEmail(), "Customer email in DB should match expected");
    }

    @Then("the activation record should be active")
    public void the_activation_record_should_be_active() {
        Assertions.assertNotNull(queryResponse, "Query response should not be null");
        ActivationStatusResponse body = queryResponse.getBody();
        Assertions.assertNotNull(body, "Query response body should not be null");
        Assertions.assertTrue(body.isActive(), "Activation record should be marked active");
    }

    @Then("the activation record should be inactive")
    public void the_activation_record_should_be_inactive() {
        Assertions.assertNotNull(queryResponse, "Query response should not be null");
        ActivationStatusResponse body = queryResponse.getBody();
        Assertions.assertNotNull(body, "Query response body should not be null");
        Assertions.assertFalse(body.isActive(), "Activation record should be marked inactive");
    }
}
