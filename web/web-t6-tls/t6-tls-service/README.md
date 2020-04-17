TLS Enabled Service
===

Spring Boot micro-service example using Spring TLS (ssl) configuration.

Boot
---

Start using maven:

    mvn spring-boot:run

Plain jar:

    java -jar target/t6-tls-service-0.6.0-SNAPSHOT-exec.jar


API
---

Call unsecured part:

    curl --insecure https://localhost:9443/ping
    curl --insecure https://localhost:9443/info
    
Call using private keys:

    ...