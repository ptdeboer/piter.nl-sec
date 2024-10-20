Spring Boot TLS-ma example
===

Spring-Boot TLS Mutual Authentication (TLS-ma) example.

Modules:

- t6-tls-service      : Example Spring Boot Service with TLS-ma.
- t6-tls-service-test : Example functional and integration testing.

Versions
---

- Java 17, 21
- Spring Boot 3.3.4
- Apache HTTPClient 5

Build
---

    mvn clean package

Service
---

Spring Boot micro-service example using Spring TLS (ssl) configuration.

Start using maven:

    (cd t6-tls-service; mvn spring-boot:run) 

Executable jar:

    java -jar t6-tls-service/target/t6-tls-service-1.0.1-SNAPSHOT-exec.jar

API
---

Call insecure part (no certificate verification):

    curl --insecure https://localhost:9443/ping
    curl --insecure https://localhost:9443/info

Call using private key as follows:
Curl only supports PEM, so the private key + cert needs to be extracted (use 'password'):

    openssl pkcs12 -in t6-tls-service/src/main/resources/keystores/customer.p12 -out keystore.pem -nodes

Use keystore.pem with curl for *client side* authentication only and skip remote server verification as follows:

     curl --insecure -v -GET -E keystore.pem  https://localhost:9443/domain ; echo

__NOTE__: the _--insecure_ means the remote server certificate is not verified, but the client side certificate is provided.
This is useful to test client side keys, ignoring potential host certificate issues or in this case
connect to a 'demo' service which runs at 'localhost'.

Full example using both client-side and server-side certificate verification, for that the CA root needs 
to be supplied in case of self-signed certificates, which is the case for this demo:

    curl -v -GET -E keystore.pem  --cacert t6-tls-service/src/main/resources/keystores/root-ca.crt.pem\
          https://localhost:9443/domain ; echo 
