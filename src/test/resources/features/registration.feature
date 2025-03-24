Feature: User Registration

  Scenario Outline: Valid and Invalid User Registration Tests
    Given I am on the registration page using "<browser>"
    When I enter user details with "<field_status>"
    And I confirm acceptance of the terms and conditions if "<accept_terms>"
    And I confirm that I am over 18 years of age
    And I agree to the ethics policy
    And I submit the registration form
    Then I should see a "<message_type>" message
    And the account should "<account_status>"

    Examples:
      | browser | field_status         | accept_terms | message_type                 | account_status          |
      | chrome  | all fields completed | yes          | success                      | successfully be created |
      | firefox | all fields completed | yes          | success                      | successfully be created |
      | chrome  | missing last name    | yes          | missing last name            | not be created          |
      | firefox | missing last name    | yes          | missing last name            | not be created          |
      | chrome  | mismatched passwords | yes          | mismatched passwords         | not be created          |
      | firefox | mismatched passwords | yes          | mismatched passwords         | not be created          |
      | chrome  | all fields completed | no           | missing terms and conditions | not be created          |
      | firefox | all fields completed | no           | missing terms and conditions | not be created          |
