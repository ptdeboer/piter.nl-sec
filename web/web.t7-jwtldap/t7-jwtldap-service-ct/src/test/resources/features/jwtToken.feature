Feature: Create/Update JWT Tokens
  Test creating and updating JWT Tokens.

  Scenario: Request JWT Token Authentication with user + password
    When I request an JWT Token with user 'admin' and password 'admin' at url '/auth'
    Then the response should be http OK 200
    Then the JWT token must be valid
    Then the JWT token user name must be 'admin'
    Then print the JWT token to the INFO log.

    # 401 Unauthorized => means UNAUTHENTICATED aka 'login failure' or invalid user+password combination.
  Scenario: Authentication with invalid username for JWT Token
    When I request an JWT Token with user 'admin-not' and password 'admin' at url '/auth'
    Then the response should be http ERROR 401

  Scenario: Authentication with invalid password for JWT Token
    When I request an JWT Token with user 'admin' and password 'invalid-password' at url '/auth'
    Then the response should be http ERROR 401
