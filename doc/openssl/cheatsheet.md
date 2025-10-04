OpenSSL
===

OpenSSL/Keytool cheatsheet.


Types
---
    p12     PKCS#12 key and/or certificate store
    pfx     PKCS#12 key and/or certificate store - Microsoft legacy: Do Not Use.
    jks     Java Keystore, needs 'keytool'. Preferably use p12.
    der     DER encoded binary: not ASCII!
    pem     ASCII encoded PEM
    crt     certificate: can both be DER and ASCII-PEM
    cer     certificate: can both be DER and ASCII-PEM
    key     private key: can both be DER and ASCII-PEM

Note: ASCII-PEM can be recognised with the starting lines:

    ----BEGIN PRIVATE KEY----
or:

    ----BEGIN CERTIFICATE----


Cygwin
---

Prefix openssl with 'winpty' to avoid blocking command line as follows:
    
    winpty openssl ...


MacOs
---

MacOs may use LibreSSL, if certain openssl commands do not work, install brew + brew's openssl and use binary from:

    /opt/homebrew/bin/openssl

You can check the version with:

    openssl version


Show/Dump
---

Dump DER, CER, CRT or PEM from ASCII format:

    openssl x509 -in certificate.pem -text -noout
    openssl x509 -in certificate.crt -text -noout
    openssl x509 -in certificate.cer -text -noout

Dump DER, CER, CRT from binary DER format:

    openssl x509 -inform der -in cert.der -text
    openssl x509 -inform der -in cert.crt -text
    openssl x509 -inform der -in cert.cer -text

Dump PRIVATE key: Note prints out *only* RSA modulo info, no metadata is contained inside private key:

    openssl rsa -in privkey.key -text
    openssl rsa -in privkey.pem -text

Show selection of certificate attributes:

    openssl x509 -noout -subject -issuer -dates -serial -in cert.pem

Use ISO-8601 date option (needs openssl-3):

    openssl-3 x509 -noout -subject -issuer -dates -dateopt iso_8601 -serial -in cert.pem


List/Manage Keystore
---

List keystore (keytool)

    keytool -v -list -keystore keystore.p12 -storepass passwd

List trusted certificates from p12 (openssl):

    openssl pkcs12 -nokeys -in trusstore.p12 -info
    openssl pkcs12 -nokeys -in truststore.p12 -info -passin pass:passwd

List certs using keytool:

    keytool -list -v  -keystore truststore.p12 -storepass changeit -storetype PKCS12
    
Change password (keytool):

    keytool -keystore truststore.p12 -storepasswd


Convert DER, PEM, etc.
---

Convert: CRT/PEM &rarr; DER

    openssl x509 -in cert.crt -outform der -out cert.der

Convert: CRT/DER &rarr; PEM

    openssl x509 -in cert.crt -inform der -outform pem -out cert.pem

Convert: DER file (.crt .cer .der) &rarr; PEM

    openssl x509 -inform der -in cert.der -out certificate.pem

Convert: PEM &rarr; DER

    openssl x509 -in certificate.pem -outform der -out certificate.der

Convert: PEM &rarr; CRT (as DER!)

   openssl x509  -in certificate.pem -outform der -out cert-der.crt


SSH RSA Conversions
---

Extract _public_ SSH-RSA key from _private_ RSA (PEM format):

    openssl rsa -in id_rsa -pubout > pubkey.pem

Or use ssh-keygen: Extract _public_ SSH-RSA key from _private_ SSH-RSA:

    ssh-keygen -y -f id_rsa > id_rsa.pub

Extract public key only from certificate:

    openssl x509 -pubkey -noout -in cert.pem  > pubkey.pem


Extract private key + certs from p12 (pfx)
---

Extract: PKCS#12 file (.pfx or .p12) containing a private key and certificates to public-private PEM collection:
    
    openssl pkcs12 -in keystore.p12 -out keystore.pem -nodes

You can add `-nocerts` to only output the private key or add `-nokeys` to only output the certificates.

Extract: public cert(s) from private keystore p12: to pubcerts.pem

    openssl pkcs12 -in keystore.p12 -out pubcerts.pem -nodes -nokeys

Extract: private key only from private keystore p12: to privkey.pem

    openssl pkcs12 -in keystore.p12 -out privkey.pem -nodes -nocerts


Import/add into p12 (pfx)
---

Combine/import into PKCS#12: a public PEM certificate file, CA certificate(s) and a private key:

    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in certificate.crt -certfile cacerts.crt
    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in pubcert.pem -certfile cacerts.pem
    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in pubcert.pem

Import/create certificate (pem) only into new p12 using openssl:

    openssl pkcs12 -export -nokeys -in certificate.pem -out truststore.p12 -password pass:password
    openssl pkcs12 -export -nokeys -in certificate.pem -out truststore.p12 -name cert-alias -password pass:password

Combine mutiple cert files (pem) into single truststore using openssl:

    cat *.pem > allcerts.pems
    openssl pkcs12 -export -nokeys -in allcerts.pems -out truststore.p12 -password pass:password

Keytool import certificate only into keystore/add (single) certificate to p12 truststore:

    keytool -import -alias root-ca -keystore trusted-ca.ts.p12 -file root-ca.crt
    keytool -import -alias intermediate-ca -keystore trusted-ca.ts.p12 -file intermediate-ca.crt
    keytool -importcert -storetype PKCS12 -keystore root-ca.ts.p12 -storepass password -alias root-ca -file keys/root-ca.crt -noprompt

OpenSSL doesn't work:

    ***openssl pkcs12 -export -nokeys -in root-ca.crt -inkey root-ca.key -certfile root-ca.crt -out root-ca.ts.p12 -password pass:password

__NOTE: openssl cannot/willnot import trustcerts without matching privatekey. Above will not work.__


Add list of certifictes into a *new* truststore:

    cat *.pem > allcerts.pems
    openssl pkcs12 -export -nokeys -in allcerts.pems  -out truststore-new.p12 -password pass:password


Import/add trust cert (pem) to existing p12 keystore (keytool only):
	
    keytool -import -keystore keystore.jks -alias newca -file cacert.pem
	
    # using p12 as destination (optionally provide passwd):
    keytool -import -keystore truststore.p12 -alias newca -file cacert.pem
    keytool -import -keystore truststore.p12 -alias newca -file cacert.pem -storepass passwd


Verify/Compare
---

Match private key with certificate using modulus:

Certificate:

    openssl x509 -noout -modulus -in cert.crt | openssl md5

RSA private key:

    openssl rsa -noout -modulus -in privkey.key | openssl md5

CSR:

    openssl req -noout -modulus -in signreq.csr | openssl md5


GSKit
---

Convert .kdb + .sth to p12 (+ v1 stash):

    gsk8capicmd_64 -keydb -convert -db keystore.kdb  -stashed -new_db new.ks.p12 -new_format p12 -v1stash


MISC
===


Change Alias (keytool):

    keytool -changealias -alias "old-alias" -destalias "new-alias" -keystore keystore.p12

Convert p12 -> jks (keytool):

    keytool -importkeystore -srckeystore keystore.p12 -destkeystore keystore.jks -srcstoretype pkcs12

Convert p12 <- jks (keytool):

    keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -deststoretype pkcs12

Change Store and Private key password (keytool), always do BOTH:

    keytool -storepasswd -new new_storepass -keystore keystore.jks

    keytool -keypasswd  -alias <Alias>  -keystore keystore.p12

If you get: 'Keystore password is too short - must be at least 6 characters'
 -> use option `-storepass oldpw`:

    keytool -storepasswd -storepass oldpw -new newpwd -keystore keystore.jks


Create new private key + CSR:
===

Create cert.conf example, you can add/change fields as long as there is at least a 'commonName' field:

    [ req ]
	default_bits        = 4096
	distinguished_name  = req_distinguished_name
	req_extensions      = req_ext
	prompt = no
	[ req_distinguished_name ]
	countryName         = NL
	stateOrProvinceName = Noord-Holland
	organizationName    = Red Tape Inc.
	commonName          = server.domain.local
	[ req_ext ]
	subjectAltName = @alt_names
	[alt_names]
	DNS.1   = server.domain.local
	DNS.2   = srv1.domain.local

Create new private key AND create certificate signing request (CSR) using openssl:

    openssl req -out cert.csr -newkey rsa:2048 -nodes -keyout cert-private.key -config cert.conf

Dump CSR and show as plain text:

	openssl req -text -noout -verify -in cert.csr
	

ASN1 Parse
---
ASN1Parse option. Note: cert file may not have text before "-----BEGIN..."

    openssl asn1parse cert.pem
    openssl asn1parse -inform DER -in cert.der


OS configuration
===

Ubuntu
---
 
Update system CAs:

    /usr/share/ca-certificates
    sudo update-ca-certificates

