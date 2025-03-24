Feature: User Registration

  Scenario: Successful User Registration on Chrome
    Given I am on the registration page using "chrome"
    When I enter user details with "all fields completed"
    And I confirm acceptance of the terms and conditions: "yes"
    And I confirm that I am over 18 years of age
    And I agree to the ethics policy
    And I submit the registration form
    Then I should see a "success" message
    And the account should "be created"

  Scenario: Successful User Registration on Firefox
    Given I am on the registration page using "firefox"
    When I enter user details with "all fields completed"
    And I confirm acceptance of the terms and conditions: "yes"
    And I confirm that I am over 18 years of age
    And I agree to the ethics policy
    And I submit the registration form
    Then I should see a "success" message
    And the account should "be created"


  Scenario Outline: Failed User Registration Tests
    Given I am on the registration page using "<browser>"
    When I enter user details with "<field_status>"
    And I confirm acceptance of the terms and conditions: "<accept_terms>"
    And I confirm that I am over 18 years of age
    And I agree to the ethics policy
    And I submit the registration form
    Then I should see a "<message_type>" message
    And the account should "not be created"

    Examples:
      | browser | field_status         | accept_terms | message_type                 |
      | chrome  | missing last name    | yes          | missing last name            |
      | firefox | missing last name    | yes          | missing last name            |
      | chrome  | mismatched passwords | yes          | mismatched passwords         |
      | firefox | mismatched passwords | yes          | mismatched passwords         |
      | chrome  | all fields completed | no           | missing terms and conditions |
      | firefox | all fields completed | no           | missing terms and conditions |
