Feature: Check User Authorities
    Authorize users and verify their granted authorities.
    These tests use the preconfigured test data from 'sql/data-cctest.sql'

    Scenario: Authorize admin user 'admin' and verify granted authorities.
        Given a valid JWT Token for user 'admin' with password 'admin' using authorization URL '/auth'
        When I request the user authorities using a GET to url '/user/authorities'
        Then the response should be http OK 200
        Then the JWT token subject must be 'admin'
        Then the response must match JSON StringSet '["LOCAL_EDITOR", "LOCAL_ADMIN"]'

    Scenario: Authorize default user 'jan' and verify granted authorities.
        Given a valid JWT Token for user 'jan' with password 'password' using authorization URL '/auth'
        When I request the user authorities using a GET to url '/user/authorities'
        Then the response should be http OK 200
        Then the JWT token subject must be 'jan'
        Then the response must match JSON StringSet '["KAAPVAARDERS_VIEWER","KAAPVAARDERS_ADMIN","KAAPVAARDERS_EDITOR"]'

    Scenario: Authorize default user 'piet' and verify user LDAP roles.
        Given a valid JWT Token for user 'piet' with password 'password' using authorization URL '/auth'
        When I request the user authorities using a GET to url '/user/ldap/roles'
        Then the response should be http OK 200
        Then the JWT token subject must be 'piet'
        Then the response must match JSON StringSet:
            """
            ["cn=g-domain-kaapvaarders-admin,ou=groups,dc=kaapvaarders,dc=com",
             "cn=g-domain-kaapvaarders-viewers,ou=groups,dc=kaapvaarders,dc=com"]
            """