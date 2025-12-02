package au.com.telstra.simcardactivator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
public class ActuatorClient {

    private final RestTemplate restTemplate;

    // Can override this in application.properties if needed
    @Value("${actuator.url:http://localhost:8444/actuate}")
    private String actuatorUrl;

    public ActuatorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean activateSim(String iccid) {
        Map<String, String> requestBody = Collections.singletonMap("iccid", iccid);

        ResponseEntity<ActuatorResponse> response =
                restTemplate.postForEntity(actuatorUrl, requestBody, ActuatorResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().isSuccess();
        }

        // Treat non-2xx or missing body as failure
        return false;
    }
}
