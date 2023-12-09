JWT-LDAP
===

Info
---
Example combining Jason Web Tokens (JWT) using LDAP and stored User Roles from DB.

- Uses LDAP for *Authentication*.
- Query LDAP for SSO (LDAP) memberships and (LDAP) roles.
- Resolve memberships and roles against local *Authorization* DB.

Dev
---
Architecture:

- SpringBoot 3.1.4.
- Java 17, 21
- Uses In memory 'H2' database for demo purposes only.
- JWT 0.10.8 using encrypted tokens.
- Testing LDAP server from unboundid-ldapsdk.
- Junit 5, Cucumber 7.14 (demo)


Build
---

    mvn clean package verify


Service
--- 
Start service:

    java -jar t7-jwtldap-service/target/t7-jwtldap-service-0.1.0-SNAPSHOT-exec.jar


API
---
See doc/API.md for details.
