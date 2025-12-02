package au.com.telstra.simcardactivator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/sim")
public class SimActivationController {

    private final ActuatorClient actuatorClient;
    private final SimCardActivationRepository activationRepository;

    public SimActivationController(ActuatorClient actuatorClient,
                                   SimCardActivationRepository activationRepository) {
        this.actuatorClient = actuatorClient;
        this.activationRepository = activationRepository;
    }

    @PostMapping("/activate")
    public ResponseEntity<ActuatorResponse> activateSim(@RequestBody ActivationRequest request) {
        // 1. Call actuator
        boolean success = actuatorClient.activateSim(request.getIccid());

        // 2. Save record to database
        SimCardActivation activation = new SimCardActivation(
                request.getIccid(),
                request.getCustomerEmail(),
                success
        );
        SimCardActivation saved = activationRepository.save(activation);

        // 3. Print result
        System.out.println("Saved activation with ID " + saved.getId()
                + " for ICCID " + saved.getIccid()
                + " (customer " + saved.getCustomerEmail()
                + ") active=" + saved.isActive());

        // 4. Return success flag (same shape as earlier task)
        return ResponseEntity.ok(new ActuatorResponse(success));
    }

    @GetMapping("/activation")
    public ResponseEntity<ActivationStatusResponse> getActivation(
            @RequestParam("simCardId") long simCardId) {

        Optional<SimCardActivation> maybeActivation = activationRepository.findById(simCardId);

        if (maybeActivation.isEmpty()) {
            // Not strictly specified, but 404 is reasonable
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        SimCardActivation activation = maybeActivation.get();

        ActivationStatusResponse response = new ActivationStatusResponse(
                activation.getIccid(),
                activation.getCustomerEmail(),
                activation.isActive()
        );

        return ResponseEntity.ok(response);
    }
}
