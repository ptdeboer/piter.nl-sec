#!/bin/bash
##
# Script to test the REST api.
#

log() { 
  echo "$@" >&2
}

doAuth() {
 local name="$1"
 local pwd="$2"
 local result=$(curl --request POST --url http://localhost:9001/auth --header 'content-type: application/json' --data "{\"username\": \"${name}\", \"password\": \"${pwd}\"}")
 log $result
 TOKEN=$(echo $result | sed -e 's/[{}"]*//g' -e 's/.*:\([a-zA-Z0-9]*\)/\1/' ) 
 log "TOKEN=$TOKEN"
 export TOKEN
 export TOKEN_HEADER="Authorization: Bearer ${TOKEN}"
 echo   "TOKEN_HEADER=${TOKEN_HEADER}"
}

getUserInfo() {

 echo "> USER:"
 curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user ; echo

 echo "> ROLES:"
 curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/roles ; echo

 echo "> AUTHORITIES:"
 curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/authorities ; echo

 echo "> LDAP:"
 curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/ldap ; echo

 echo "> LDAP ROLES:"
 curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/ldap/roles ; echo

}

doAuth admin admin;

#Use admin access to list current users:
echo "> USERS:"
curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user ; echo

getUserInfo;

doAuth jan password;

getUserInfo;

echo "===                        === "
echo "=== Pinging Service        ==="
echo "===                        === "
curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/ping ; echo 

echo "===                        === "
echo "=== Checking Ship Domain   ==="
echo "===                        === "

echo "> Query ships"
curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/data/ships ; echo

# put update get and delete
echo "> Creating ship"
curl -X POST -H "${TOKEN_HEADER}" http://localhost:9001/data/ships -H "Content-Type: application/json"\
	-d '{"shipName":"Voyager","shipDescription":"Intrepid class NCC-74656","referenceId":"ncc-74656"}' ; echo

echo "> Query Ship with id=1" 
#Assume ID=1
ID=1
curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/data/ships/${ID} ; echo 

echo "> Update ship (id=1)"
curl -X PUT -H "${TOKEN_HEADER}" http://localhost:9001/data/ships/${ID} -H "Content-Type: application/json"\
	-d '{"shipName":"Voyager 1b","shipDescription":"Intrepid class NCC-74656b (refurbised)"}' ; echo 

echo "> Query ship by reference Id"
curl -X GET -H "${TOKEN_HEADER}" "http://localhost:9001/data/ships?referenceId=ncc-74656" ; echo 

echo "> Deleting ship with Id=1"
ID=1
curl -X DELETE -H "${TOKEN_HEADER}" "http://localhost:9001/data/ships/${ID}" ; echo

echo "> Query remaining ships"
curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/data/ships ; echo 

echo  "===                        === "
echo  "=== Testing 'other' access === "
echo  "===                        === "
doAuth other password;

echo "> Test acces by *authenticated* but not *authorized* user 'other'"
curl -X POST -H "${TOKEN_HEADER}" http://localhost:9001/data/ships -H "Content-Type: application/json"\
	-d '{"shipName":"Voyager","shipDescription":"Intrepid class NCC-74656","referenceId":"ncc-74656"}' ; echo
