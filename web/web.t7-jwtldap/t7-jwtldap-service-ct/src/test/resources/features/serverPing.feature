Feature: Ping the server
    Test if server is up by performing a ping to "/ping" endpoint and test the get/post
    methods themselves including error detection.

    Scenario: Performing server ping
        When I perform a simple PING call using url "/ping"
        Then the response should be http OK 200
        Then the response value should be "pong"

    Scenario: Performing GET call using '/ping'
        When I perform a simple GET call using url "/ping"
        Then the response should be http OK 200
        Then the response value should be "pong"

    # Feature to test whether error handling works for: 404 Not Found
    Scenario: Testing GET call using invalid URL must result in proper detected 404 error response.
        When I perform a simple GET call using url "/ping-does-not-exist"
        Then the response should be http ERROR 404
