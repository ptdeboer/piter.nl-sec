#!/bin/bash
##
# (C) Piter.nl 2015-2023
##
#
# Example for PKI demo, creates PKI structure:
# - Self signed Root CA
# - Intermediate CA
# - Two leaf nodes: Customer and Company.
#
# This file is only provided for informational purposes.
# X509v3: Setup has been enhanced with X509v3 attributes: see .conf files.
#

PASSWORD=password
KEYDIR=generated_keys
#
ROOT_CA_SUBJECT_DN="/CN=Root-CA/O=Piter.NL/OU=Crypto"
INTERMEDIATE_SUBJECT_DN="/CN=Intermediate-CA/O=Piter.NL/OU=crypto"
COMPANY_SUBJECT_DN="/CN=Big HyperCorp Ltd./O=hypercorp.comp/OU=Acquisition"
CUSTOMER_SUBJECT_DN="/CN=Sum Customer/O=customer.comp/OU=Red Tape dep."

# No cygwin path conversion as options contain forward slashes:
export "MSYS_NO_PATHCONV=1"

mkdir -pv ${KEYDIR}

error() {
  echo " ***ERROR: $*" 1>&2
}

warn() {
  echo " WARN: $*" 1>&2
}

info() {
  echo " INFO: $*"
}

fatal() {
  error "$@"
  exit 1
}

# Loop over arguments and find prefixed option.
filter() {
  if [ -z "$1" ] || [ -z "$2" ]; then
    return 1
  fi

  local OPT="$1"
  shift

  for ARG in "$@"; do
    local PREFIX=$(echo $ARG | sed "s/^\($OPT\)\(.*\)/\1/")
    if [ "${PREFIX}" == "${OPT}" ]; then
      # output to stdout is captured:
      echo "$ARG" | sed "s/^$OPT//"
      return 0
    fi
  done
  # Response is captured: do not echo to stdout for logging.
  warn "filter(): Failed to find option:$OPT"
  return 2
}

doOpenssl() {
  info " OPENSSL: $*"
  openssl "$@"
}

createRootRA() {
  info " ### Creating Root CA ###"
  doOpenssl req -new -x509 -nodes -days 10000 -out "${KEYDIR}/root-ca.crt" -keyout "${KEYDIR}/root-ca.key" -subj "${ROOT_CA_SUBJECT_DN}" -newkey rsa:4096 -sha256
  openssl x509 -in ${KEYDIR}/root-ca.crt -text | tee "${KEYDIR}/root-ca.crt.pem"
}

opensslReq() {
  local REQ_OUT=$(filter '-reqout:' "$@") || fatal "opensslReq(): No option '-reqout'"
  local KEY_OUT=$(filter '-keyout:' "$@") || fatal "opensslReq(): No option '-keyout'"
  local SUBJECT_DN=$(filter -subject: "$@") || fatal "openssReq(): No option '-subject'"

  info " - Request out : $REQ_OUT"
  info " - Key out     : $KEY_OUT"
  info " - SubjectDN   : $SUBJECT_DN"

  doOpenssl req -new -nodes -out "${REQ_OUT}" -keyout "${KEY_OUT}" -subj "${SUBJECT_DN}" -newkey rsa:4096 -sha256
  ret=$?
  if [ $ret != "0" ]; then
    error "openssl error=$ret"
    exit 1
  fi

}

opensslSign() {
  local REQ_IN=$(filter '-reqin:' "$@") || fatal "opensslSign(): No option '-reqin'"
  local CA_KEY=$(filter '-cakey:' "$@") || fatal "opensslSign(): No option '-cakey'"
  local CA_CERT=$(filter '-cacert:' "$@") || fatal "opensslSign(): No option '-cacert'"
  local CERT_SERIAL=$(filter '-serialno:' "$@") || fatal "opensslSign(): No option '-serialno'"
  local CERT_OUT=$(filter '-certout:' "$@") || fatal "opensslSign(): No option '-certout'"
  local EXTCONF=$(filter '-conf:' "$@") || warn "opensslSign(): No option '-conf'"

  info " - Request in  : $REQ_IN"
  info " - CA key      : $CA_KEY"
  info " - CA cert     : $CA_CERT"
  info " - Cert out    : $CERT_OUT"
  info " - X509v3 conf : $EXTCONF"

  local EXTOPTIONS=""
  if [ -n "${EXTCONF}" ]; then
    EXTOPTIONS="-extfile ${EXTCONF} -extensions v3_ext"
  fi

  doOpenssl x509 -req -in "${REQ_IN}" -CAkey "${CA_KEY}" -CA "${CA_CERT}" -days 10000 -set_serial ${CERT_SERIAL} -sha256 -out "${CERT_OUT}" ${EXTOPTIONS}
  ret=$?
  if [ $ret != "0" ]; then
    error "openssl error=$ret"
    exit 1
  fi
  openssl x509 -in "${CERT_OUT}" -text | tee "${CERT_OUT}.pem"
}

###
### Main
###

info " ### Creating keys and certs ###"
createRootRA

info " ### Creating Intermediate CA Request ###"
#opensslReq -reqout:"${KEYDIR}/Intermediate_CA.req" -keyout:"${KEYDIR}/intermediate-ca.key" -subject:"/CN=Intermediate-CA/O=Piter.NL"
opensslReq -subject:"${INTERMEDIATE_SUBJECT_DN}" -keyout:"${KEYDIR}/intermediate-ca.key" -reqout:"${KEYDIR}/Intermediate-CA.req"

info " ### Signing Intermediate CA Request with Root CA ###"
opensslSign -reqin:"${KEYDIR}/Intermediate-CA.req" -cakey:"${KEYDIR}/root-ca.key" -cacert:"${KEYDIR}/root-ca.crt" -serialno:123 -conf:intermediate.conf -certout:"${KEYDIR}/intermediate-ca.crt"

info " ### Creating Company Cert Request ###"
opensslReq -subject:"${COMPANY_SUBJECT_DN}" -reqout:"${KEYDIR}/Company.req" -keyout:"${KEYDIR}/company.key"

# Note: using SAN from csr doesn't work here (todo check 'copy attributes' config using 'ca' command not 'x509').
info " ### Signing Company Cert Request with Intermediate CA ###"
opensslSign -reqin:"${KEYDIR}/Company.req" -cakey:"${KEYDIR}/intermediate-ca.key" -cacert:"${KEYDIR}/intermediate-ca.crt" -serialno:345 -conf:company.conf -certout:"${KEYDIR}/company.crt"

info " ### Creating Customer Cert Request ###"
opensslReq -subject:"${CUSTOMER_SUBJECT_DN}" -reqout:"${KEYDIR}/Customer.req" -keyout:"${KEYDIR}/customer.key"
#openssl req -text -noout -verify -in "${KEYDIR}/Customer.req"

# Note: SAN names from CSRs are ignored because of security reasons. Create custom config (customer.conf) with SAN values and use that:
info " ### Signing Customer Cert Request with Intermediate CA ###"
opensslSign -reqin:"${KEYDIR}/Customer.req" -cakey:"${KEYDIR}/intermediate-ca.key" -cacert:"${KEYDIR}/intermediate-ca.crt" -serialno:678 -conf:customer.conf -certout:"${KEYDIR}/customer.crt"
# Test: no X509 V3 attributes. Skip optional configuration.
opensslSign -reqin:"${KEYDIR}/Customer.req" -cakey:"${KEYDIR}/intermediate-ca.key" -cacert:"${KEYDIR}/intermediate-ca.crt" -serialno:912 -certout:"${KEYDIR}/customer-nox509v3.crt"

###
### For PKI Demo: create p12 keystores including complete trust chain for explicit certificate verification.
###

cat ${KEYDIR}/intermediate-ca.crt ${KEYDIR}/root-ca.crt >${KEYDIR}/intermediate-ca-chain.pem
cat ${KEYDIR}/customer.crt ${KEYDIR}/intermediate-ca.crt ${KEYDIR}/root-ca.crt >${KEYDIR}/customer-chain.pem
cat ${KEYDIR}/company.crt ${KEYDIR}/intermediate-ca.crt ${KEYDIR}/root-ca.crt >${KEYDIR}/company-chain.pem

# Dos copy example:
# copy /B customer\${KEYDIR}/customer.crt + "intermediate CA\${KEYDIR}/Intermediate CA.crt" + "root CA\${KEYDIR}/Root CA.crt" ${KEYDIR}/Customerchain.pem

info " ### Creating keystores + trust chain ###"

#Root CA
openssl pkcs12 -export -out ${KEYDIR}/root-ca.p12 -in ${KEYDIR}/root-ca.crt -inkey ${KEYDIR}/root-ca.key -chain -CAfile ${KEYDIR}/root-ca.crt -password pass:password

#Create keystores with trusted chain for Intermediate CA
openssl pkcs12 -export -out ${KEYDIR}/intermediate-ca.p12 -in ${KEYDIR}/intermediate-ca.crt -inkey ${KEYDIR}/intermediate-ca.key -chain -CAfile ${KEYDIR}/intermediate-ca-chain.pem -password pass:password

#Create keystores with trusted chain for Company:
openssl pkcs12 -export -out ${KEYDIR}/company.p12 -in ${KEYDIR}/company.crt -inkey ${KEYDIR}/company.key -chain -CAfile ${KEYDIR}/company-chain.pem -password pass:password

#Create keystores with trusted chain for Customer:
openssl pkcs12 -export -out ${KEYDIR}/customer.p12 -in ${KEYDIR}/customer.crt -inkey ${KEYDIR}/customer.key -chain -CAfile ${KEYDIR}/customer-chain.pem -password pass:password

info " ### Creating truststores (keytool) ###"

rm -vf ${KEYDIR}/root-ca.ts.p12
rm -vf ${KEYDIR}/trusted-ca.ts.p12

# Use keytool for compatible trust-stores:
keytool -importcert -storetype PKCS12 -keystore ${KEYDIR}/root-ca.ts.p12 -storepass password -alias root-ca -file ${KEYDIR}/root-ca.crt -noprompt
keytool -importcert -storetype PKCS12 -keystore ${KEYDIR}/trusted-ca.ts.p12 -storepass password -alias root-ca -file ${KEYDIR}/root-ca.crt -noprompt
keytool -importcert -storetype PKCS12 -keystore ${KEYDIR}/trusted-ca.ts.p12 -storepass password -alias intermediate-ca -file ${KEYDIR}/intermediate-ca.crt -noprompt
