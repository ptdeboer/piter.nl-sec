API
===

API examples

JSON Web Tokens
--- 
Authenticate with 'local' admin account:

    curl --request POST \
     --url http://localhost:9001/auth \
     --header 'content-type: application/json' \
     --data '{"username": "admin", "password": "admin"}'

Authenticate user by "uid", where uid = 'jan':

    curl --request POST \
     --url http://localhost:9001/auth \
     --header 'content-type: application/json' \
     --data '{"username": "jan", "password": "password"}'

Authenticate user by "sAMAccountName", where sAMAccountName = 'jan001':

    curl --request POST \
     --url http://localhost:9001/auth \
     --header 'content-type: application/json' \
     --data '{"username": "jan001", "password": "password"}'

Will result in something like:

    {"token":"eyJhbGciO...e1gQ36rb-Ijg"}

export to environment variable:

    export TOKEN=eyJhbGciOiJIUzUxMiJ9...
 
    curl -X GET -H "Authorization: Bearer ${TOKEN}" http://localhost:9001/ping ; echo

or:

    export TOKEN_HEADER="Authorization: Bearer ${TOKEN}"
    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/ping ; echo

user info
---

    export TOKEN_HEADER="Authorization: Bearer ${TOKEN}"

    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user ; echo
         
    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/roles ; echo

    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/authorities ; echo

    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/ldap ; echo

    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/user/ldap/roles ; echo

Note that local 'admin' example doesn't have roles nor ldap memberships as the authorities are set in the properties file.

User db
---
As admin list current stored users:

    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/users ; echo

Authenticated Domain methods
---

Create a new Domain Object:

    curl -X POST -H "${TOKEN_HEADER}" http://localhost:9001/data/ships -H "Content-Type: application/json"\
 	     -d '{"shipName":"Voyager","shipDescription":"Intrepid class NCC-74656","referenceId":"ncc-74656"}' ; echo

List item

    curl -X GET -H "${TOKEN_HEADER}" "http://localhost:9001/data/ships?referenceId=ncc-74656" ; echo

Get created object and format JSON output:

    ID=1
    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/data/ships/${ID} | python -m json.tool  

List created resources and format output:

    ID=1
    curl -X GET -H "${TOKEN_HEADER}" http://localhost:9001/data/ships | python -m json.tool  

Updated existing domain object using PUT:

    ID=1
    curl -X PUT -H "${TOKEN_HEADER}" http://localhost:9001/data/ships/${ID} -H "Content-Type: application/json"\
	     -d '{"shipName":"Voyager 1b","shipDescription":"Intrepid class NCC-74656b (refurbised)"}' ; echo 

Delete:

    ID=1
    curl -X DELETE -H "${TOKEN_HEADER}" "http://localhost:9001/data/ships/{$}" ; echo 

other
---
Authenticate user 'other' to test *authenticated* but *unauthorised* access: 403 Forbidden.

    curl --request POST \
     --url http://localhost:9001/auth \
     --header 'content-type: application/json' \
     --data '{"username": "other", "password": "password"}'

When the 'other' account is used to access the domain data, using the REST examples above,
an 403 error should return.
