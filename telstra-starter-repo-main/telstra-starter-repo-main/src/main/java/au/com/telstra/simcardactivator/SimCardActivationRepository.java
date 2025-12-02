package au.com.telstra.simcardactivator;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimCardActivationRepository extends JpaRepository<SimCardActivation, Long> {
    // basic CRUD is enough for this task
}
