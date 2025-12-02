Feature: SIM card activation

  In order to manage SIM card activations
  As a client of the SIM card activation microservice
  I want to activate SIM cards and verify their status

  Scenario: Successful SIM card activation
    When I submit an activation request for ICCID "1255789453849037777" and email "success@example.com"
    Then the activation response should indicate success
    When I query the activation with id 1
    Then the activation record should have ICCID "1255789453849037777"
    And the activation record should have customer email "success@example.com"
    And the activation record should be active

  Scenario: Failed SIM card activation
    When I submit an activation request for ICCID "8944500102198304826" and email "failure@example.com"
    Then the activation response should indicate failure
    When I query the activation with id 2
    Then the activation record should have ICCID "8944500102198304826"
    And the activation record should have customer email "failure@example.com"
    And the activation record should be inactive
