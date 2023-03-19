OpenSSL
===

OpenSSL/Keytool examples.


Types
---
    p12    PKCS#12
    pfx    PKCS#12
    der    DER encoded binary
    pem    ASCII encoded PEM
    crt    certificate: can both be DER and ASCII-PEM
    key    private key: can both be DER and ASCII-PEM


Cygwin
---

IMPORT: Prefix openssl with 'winpty' as follows:
    
    winpty openssl ...


Show/Dump
---

Dump DER, CRT or PEM:

    openssl x509 -inform der -in cacert.der -text

    openssl x509 -in certificate.pem -text -noout

    openssl x509 -in certificate.crt -text -noout

    openssl x509 -inform der -in certificate.crt -text


Dump PRIVATE key: Note prints out *only* RSA modulo info

    openssl rsa -in privkey.key -text

    keytool -list -v -keystore keystore.jks -storepass passwd


List
---

List keystore (keytool)

    keytool -v -list -keystore keystore.p12 -storepass passwd

List trust certificates from p12 (openssl):

    openssl pkcs12 -nokeys -in trusstore.p12 -info
    openssl pkcs12 -nokeys -in truststore.p12 -info -passin pass:passwd
    

Convert DER, PEM, etc.
---

Convert: CRT/PEM -> DER

    openssl x509 -in cert.crt -outform der -out cert.der

Convert: CRT/DER -> PEM

    openssl x509 -in cert.crt -inform der -outform pem -out cert.pem

Convert: DER file (.crt .cer .der) -> PEM

    openssl x509 -inform der -in cert.der -out certificate.pem

Convert: PEM -> DER

    openssl x509 -in certificate.pem -outform der -out certificate.der

Convert: PEM -> CRT (as DER!)

   openssl x509  -in certificate.pem -outform der -out cert-der.crt


SSH RSA Conversions
---

Extract public SSH-RSA key from private RSA (PEM format):

    openssl rsa -in id_rsa -pubout > pubkey.pem

Extract public SSH-RSA key from private SSH-RSA:

    ssh-keygen -y -f id_rsa > id_rsa.pub


Extract from p12 (pfx)
---

Extract: PKCS#12 file (.pfx .p12) containing a private key and certificates to public-private PEM collection:
    
    openssl pkcs12 -in keystore.p12 -out keystore.pem -nodes

You can add `-nocerts` to only output the private key or add `-nokeys` to only output the certificates.

Extract: public cert(s) from private keystore p12: to pubcerts.pem

    openssl pkcs12 -in keystore.p12 -out pubcerts.pem -nodes -nokeys

Extract: private key from private keystore p12: to privkey.pem

    openssl pkcs12 -in keystore.p12 -out privkey.pem -nodes -nocerts


Improt/add into p12 (pfx)
---

Combine/import into PKCS#12: a public PEM certificate file, CA certificate(s) and a private key:

    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in certificate.crt -certfile cacerts.crt
    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in pubcert.pem -certfile cacerts.pem
    openssl pkcs12 -export -out keystore.p12 -inkey privkey.pem -in pubcert.pem

Import/create certificate (pem) into new p12. Will delete previous (openssl):

    openssl pkcs12 -export -nokeys -in certificate.pem -out truststore.p12

    keytool -import -alias root-ca -keystore trusted-ca.ts.p12 -file root-ca.crt
    keytool -import -alias intermediate-ca -keystore trusted-ca.ts.p12 -file intermediate-ca.crt
    keytool -importcert -storetype PKCS12 -keystore root-ca.ts.p12 -storepass password -alias root-ca -file keys/root-ca.crt -noprompt

OpenSSL doesn't work: 
    openssl pkcs12 -export -nokeys -in root-ca.crt -inkey root-ca.key -certfile root-ca.crt -out root-ca.ts.p12 -password pass:password

__NOTE: openssl cannot/willnot import trustcerts without matching privatekey. Above will not work.__

Import/add trust cert (pem) to existing p12 keystore (keytool only):
	
    keytool -import -keystore keystore.jks -alias newca -file cacert.pem
	
	# using p12 as destination (optionally provide passwd):
	keytool -import -keystore truststore.p12 -alias newca -file cacert.pem
	keytool -import -keystore truststore.p12 -alias newca -file cacert.pem -storepass passwd

GSKit
---

Convert .kdb + .sth to p12 (+ v1 stash):

    gsk8capicmd_64 -keydb -convert -db keystore.kdb  -stashed -new_db new.ks.p12 -new_format p12 -v1stash


MISC
===


Change Alias (keytool):

    keytool -changealias -alias "old-alias" -destalias "new-alias" -keystore keystore.p12

Convert p12 <-> jks (keytool):

    keytool -importkeystore -srckeystore keystore.p12 -destkeystore keystore.jks -srcstoretype pkcs12
    keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -deststoretype pkcs12

Change Store and Private key password (keytool), always do BOTH:

    keytool -storepasswd -new new_storepass -keystore keystore.jks

    keytool -keypasswd  -alias <Alias>  -keystore keystore.p12

If you get: 'Keystore password is too short - must be at least 6 characters'
 -> use option `-storepass oldpw`:

    keytool -storepasswd -storepass oldpw -new newpwd -keystore keystore.jks


Create CSR:
===

Create cert.conf:
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

Use openssl:

    openssl req -out cert.csr -newkey rsa:2048 -nodes -keyout cert-private.key -config cert.conf

Dump CSR:

	openssl req -text -noout -verify -in cert.csr
	
Ubuntu
---
 
Update system CAs:

    /usr/share/ca-certificates
    sudo update-ca-certificates

