JWT-LDAP
===

Info
---
Example combining Jason Web Tokens (JWT) using LDAP and stored User Roles from DB.

- Use LDAP for *Authentication*.
- Query LDPA for SSO (LDAP) Memberships and (LDAP) Roles.
- Resolve Memberships and Roles against local *Authorization* DB.

Dev
---
Architecture:
- SpringBoot 2.4.13.
- Java 11.
- Uses In memory 'h2' database for demo purposes only.
- JWT 0.10.8.
- Testing LDAP server from unboundid-ldapsdk.
 
Build
---

    mvn clean package

Service
--- 
Start service:
     
    java -jar t7-jwtldap-service/target/t7-jwtldap-service-0.1.0-SNAPSHOT-exec.jar

API
---
See doc/API.md for details.
