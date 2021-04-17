Feature: Create new repository


  Scenario: create new repo with min requirement and verify repo info
    Given request for creating a new repo
    When request for retrieving the new repo
    Then new repo information matches expected
  @wip
  Scenario: update the new repo created and verify repo info
    Given request for creating a new repo
    When updating this repo
    And request for retrieving the new repo
    Then new repo information matches expected


  Scenario: delete a repo
    Given request for creating a new repo
    When request for deleting the repo
    Then verify the repo is deleted