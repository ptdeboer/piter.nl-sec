Piter.NL Security Examples
===

(C-Left) 2015-2025 Piter.NL - Free of use.\
See LICENSE.txt for more details.

Security related (Spring) Web & Cryptography code examples.\
The code examples in this repository are for demonstration purposes only.\
No guarantees are given.


PKI
---

Public Key Infrastructere (PKI) examples.\
See pki-demo how to setup your own PKI with custom Root CA, Intermediate CA:

- pki/pki-demo

Some certificate utils/scripts:

- pki/scripts

Web TLS (mTLS)
---

Spring Boot example how to use tls-ma/mTLS between two microservices.

- web/web.t6-tls => nl.piter.web:web.t6-tls


Authentication and Authorization
---

Spring boot with JWT and LDAP example:

- web/web.t7-jwtldap => nl.piter.web:web.t7-jwtldap

The used JWT tokens above are based on server-side secrets. No PKI example (yet).

