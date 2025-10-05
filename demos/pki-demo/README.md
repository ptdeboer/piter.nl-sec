PKI Demo
===

Example to setup PKI with custom Root CA, Intermediate CA, and two (leaf) entities: Company and Customer.
This example could be useful when setting up a mock PKI structure.


Setup
---

Generate keys:

    cd setupkeys
    ./setupkeys.sh

Fetch keys, certificates, keystores and truststores from:

    setupkeys/generated_keys/

