#!/usr/bin/env bash
#
# Sample usage:
#   ./test_all.bash start stop
#   start and stop are optional
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
# When not in Docker
#: ${HOST=localhost}
#: ${PORT=7000}

# When in Docker
: ${HOST=localhost}
: ${PORT=8080}

#array to hold all our test data ids
allTestAdoptionPaperIds=()
allTestLocationsIds=()
allTestVolunteerIds=()
allTestAdopterIds=()

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

#have all the microservices come up yet?
function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

#prepare the test data that will be passed in the curl commands for posts and puts
function setupTestdata() {

#CREATE SOME CUSTOMER TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
body=\
'{
     "fName" : "jeremy",
     "lName" : "Roos",
 "address": {
             "streetAddress": "123 Maple St",
             "city": "Toronto",
             "province": "Ontario",
             "country": "Canada",
             "postalCode": "M5A 1A1"
         },
 "phoneNumber": {
             "type": "MOBILE",
             "phoneNumber": "416-123-4567"
         },
 "contactMethodPreference": "PHONE"
 }'
    recreateAdopterAggregate 1 "$body"

#CREATE SOME EMPLOYEE TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
body=\
'{
     "fName": "Nathan",
     "lName": "Doe",
     "email": "johndoe@example.com",
     "salary": "50000.0",
     "title": "MANAGER",
         "volunteerAddress": {
         "streetAddress": "123 Maple St",
         "city": "Toronto",
         "province": "Ontario",
         "country": "Canada",
         "postalCode": "M5A 1A1"
     },
     "volunteerPhoneNumber": {
         "type": "WORK",
         "phoneNumber": "416-123-4567"
     }
 }'
    recreateAdopterAggregate 1 "$body"


#CREATE SOME INVENTORY TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
body=\
'{
     "name": "Happy Tails Shelter",
     "shelterType": "SHELTER",
     "streetAddress": "123 Bark St",
     "city": "New York",
     "province": "New York",
     "country": "USA",
     "postalCode": "10001",
     "capacity": 50,
     "availableSpace": 10
 }'
    recreateLocationAggregate 1 "$body"




#CREATE SOME PURCHASE REQUEST TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
#all use adopterId cc9c2c7f-afc9-46fb-8119-17158e54d02f

body=\
'{
     "adopterId": "fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1",
     "dogId": "2cfa25c5-1d13-4a9c-ae2a-55e2a5ae2481",
     "locationId": "3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6",
     "volunteerId": "6a8aeaec-cff9-4ace-a8f0-146f8ed180e5"
 }'
    recreateAdoptionPaperAggregate 1 "$body" "${allTestAdopterIds[1]}"



} #end setupTestdata


#USING EMPLOYEE TEST DATA - EXECUTE POST REQUEST
function recreateVolunteerAggregate() {
    local testId=$1
    local aggregate=$2

    #create the employee aggregates and record the generated volunteerIds
    volunteerId=$(curl -X POST http://$HOST:$PORT/api/v1/volunteers -H "Content-Type:
    application/json" --data "$aggregate" | jq '.volunteerId')
    allTestVolunteerIds[$testId]=$volunteerId
    echo "Added Volunteer Aggregate with volunteerId: ${allTestVolunteerIds[$testId]}"
}

function recreateAdopterAggregate() {
    local testId=$1
    local aggregate=$2

    #create the customer aggregate and record the generated adopterIds
    adopterId=$(curl -X POST http://$HOST:$PORT/api/v1/adopters -H "Content-Type:
    application/json" --data "$aggregate" | jq '.adopterId')
    allTestAdopterIds[$testId]=$adopterId
    echo "Added Adopter Aggregate with adopterId: ${allTestAdopterIds[$testId]}"
}

#USING PURCHASE TEST DATA - EXECUTE POST REQUEST
function recreateAdoptionPaperAggregate() {
    local testId=$1
    local aggregate=$2
    local adopterId=$3

    #create the purchase aggregates and record the generated adoptionpaperIds
    adoptionpaperId=$(curl -X POST http://$HOST:$PORT/api/v1/adopters/${adopterId}/adoptionpapers -H "Content-Type:
    application/json" --data "$aggregate" | jq '.adoptionpaperId')
    allTestAdoptionPaperIds[$testId]=$adoptionpaperId
    echo "Added Adoption Paper Aggregate with adoptionpaperId: ${allTestAdoptionPaperIds[$testId]}"
}

#USING INVENTORY TEST DATA - EXECUTE POST REQUEST
function recreateLocationAggregate() {
    local testId=$1
    local aggregate=$2

    #create the inventory aggregates and record the generated locationIds
    locationId=$(curl -X POST http://$HOST:$PORT/api/v1/locations -H "Content-Type:
    application/json" --data "$aggregate" | jq '.locationId')
    allTestLocationsIds[$testId]=$locationId
    echo "Added Location Aggregate with locationId: ${allTestLocationsIds[$testId]}"
}



#don't start testing until all the microservices are up and running
function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

#start of test script
set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

#try to delete an entity/aggregate that you've set up but that you don't need. This will confirm that things are working
#I've set up an inventory with no vehicles in it
#waitForService curl -X DELETE http://$HOST:$PORT/api/v1/locations/7890abcd-ef12-3456-7890-abcdef123456

#try to delete a resource that you won't be needing. This will confirm that our microservices architecture is up and running.
waitForService curl -X DELETE http://$HOST:$PORT/api/v1/volunteers/b139a9b1-6c37-486b-80b3-5e2e0b3d90fa

setupTestdata

#EXECUTE EXPLICIT TESTS AND VALIDATE RESPONSES
#
#EMPLOYEES
#
##verify that a get all volunteers works
echo -e "\nTest 1: Verify that a GET ALL volunteers works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/volunteers -s"
assertEqual 10 $(echo $RESPONSE | jq ". | length")
#Note: data-mysql.sql adds 6, we deleted 1 during the init phase, and then we posted 1, so now we have 6.
#
#
## Verify that a normal get by id of earlier posted employee works
echo -e "\nTest 2: Verify that a normal GET by volunteerId of earlier posted volunteer works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/volunteers/${allTestVolunteerIds[1]} -s"
assertEqual ${allTestVolunteerIds[1]} $(echo $RESPONSE | jq .volunteerId)
assertEqual "\"Nathan\"" $(echo $RESPONSE | jq ".fName")
#
#
# Verify that a 404 (Not Found) HTTP Status is returned for a GET by volunteerId with a non existing volunteerId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 3: Verify that a 404 (Not Found) HTTP Status is returned for a GET by volunteerId request with a non existing volunteerId"
assertCurl 404 "curl http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff96708db4d6 -s"
#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a GET by volunteerId with an invalid (c3540a89-cb47-4c96-888e-ff9670)
echo -e "\nTest 4: Verify that a 422 (Unprocessable Entity) HTTP Status for a GET by volunteerId request with an invalid volunteerId"
assertCurl 422 "curl http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff9670 -s"
#
#
## Verify that the post of an employee works
echo -e "\nTest 5: Verify that an post of an volunteer works"
body=\
'{
     "fName": "John",
     "lName": "Doe",
     "email": "johndoe@example.com",
     "salary": "50000.0",
     "title": "MANAGER",
         "volunteerAddress": {
         "streetAddress": "123 Maple St",
         "city": "Toronto",
         "province": "Ontario",
         "country": "Canada",
         "postalCode": "M5A 1A1"
     },
     "volunteerPhoneNumber": {
         "type": "WORK",
         "phoneNumber": "416-123-4567"
     }
 }'
assertCurl 201 "curl -X POST http://$HOST:$PORT/api/v1/volunteers -H \"Content-Type: application/json\" -d '${body}' -s"
assertEqual "\"John\"" $(echo $RESPONSE | jq ".fName")
  assertEqual "\"johndoe@example.com\"" $(echo $RESPONSE | jq ".email")
#
#
## Verify that an update of an earlier posted employee works
echo -e "\nTest 6: Verify that an update of an earlier posted volunteer works"
body=\
'{
     "fName": "John",
     "lName": "Doe",
     "email": "johndoe@example.com",
     "salary": "50000.0",
     "title": "MANAGER",
     "positionTitle": "MANAGER",
     "volunteerAddress": {
         "streetAddress": "123 Maple St",
         "city": "Toronto",
         "province": "Ontario",
         "country": "Canada",
         "postalCode": "M5A 1A1"
     },
     "volunteerPhoneNumber": {
         "type": "WORK",
         "phoneNumber": "416-123-4567"
     }
 }'
assertCurl 200 "curl -X PUT http://$HOST:$PORT/api/v1/volunteers/${allTestVolunteerIds[1]} -H \"Content-Type: application/json\" -d '${body}' -s"
assertEqual ${allTestVolunteerIds[1]} $(echo $RESPONSE | jq .volunteerId)
assertEqual "\"John\"" $(echo $RESPONSE | jq ".fName")
assertEqual "\"Doe\"" $(echo $RESPONSE | jq ".lName")
#
#
# Verify that a 404 (Not Found) HTTP Status is returned for a PUT with a non existing volunteerId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 7: Verify that a 404 (Not Found) HTTP Status is returned for a PUT request with a non existing volunteerId"
assertCurl 404 "curl -X PUT http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff96708db4d7 -H \"Content-Type: application/json\" -d '${body}' -s"
#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a PUT with an volunteerId that is invalid (c3540a89-cb47-4c96-888e-ff9670)
echo -e "\nTest 8: Verify that a 422 (Unprocessable Entity) HTTP Status for a PUT request with an invalid volunteerId"
assertCurl 422 "curl -X PUT http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff9670 -H \"Content-Type: application/json\" -d '${body}' -s"
#
#
# Verify that a delete of an earlier posted employee works
echo -e "\nTest 9: Verify that the delete of earlier posted volunteer works"
assertCurl 204 "curl -X DELETE http://$HOST:$PORT/api/v1/volunteers/${allTestVolunteerIds[1]} -s"
#
#
# Verify that 404 (Not Found) HTTP Status is returned for a DELETE with a non existing volunteerId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 10: Verify that that a 404 (Not Found) HTTP Status is returned for a DELETE request with a non existing volunteerId"
assertCurl 404 "curl -X DELETE http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff96708db4d7 -s"
#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a DELETE with an volunteerId that is invalid (c3540a89-cb47-4c96-888e-ff9670)
echo -e "\nTest 11: Verify that a 422 (Unprocessable Entity) HTTP Status for a DELETE request with an invalid volunteerId"
assertCurl 422 "curl -X DELETE http://$HOST:$PORT/api/v1/volunteers/c3540a89-cb47-4c96-888e-ff9670 -H \"Content-Type: application/json\" -d '${body}' -s"
#
#
##INVENTORIES
#
##verify that a get all locations works

#
#
## Verify that a normal get by id of earlier posted inventory works

#
#
# Verify that 404 (Not Found) HTTP Status is returned for a GET with a non existing locationId (c3540a89-cb47-4c96-888e-ff96708db4d7)

#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a GET with an inventoryID that is invalid (c3540a89-cb47-4c96-888e-ff9670)

#
#
##CUSTOMERS
#
#
##verify that a GET all adopters works

#
#
## Verify that a normal get by id of earlier posted customer works

#
#
# Verify that 404 (Not Found) HTTP Status is returned for a GET with a non existing adopterId (c3540a89-cb47-4c96-888e-ff96708db4d7)

#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a GET with an adopterId that is invalid (c3540a89-cb47-4c96-888e-ff9670)

#
#
# Verify post of a customer

#
#
##CUSTOMER PURCHASES
#
#
##verify that a GET all customer PURCHASES works
echo -e "\nTest 21: Verify that a GET all customer purchases works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/adopters/fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1/adoptionpapers -s"
assertEqual 1 $(echo $RESPONSE | jq ". | length")
#
#
## Verify that a normal GET by id of earlier posted purchase works
echo -e "\nTest 22: Verify that a normal get by id of earlier posted purchase works"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/adopters/fcbf86b1-8a76-4d2b-a352-75b10a8fd4a2/adoptionpapers/${allTestAdoptionPaperIds[1]} -s"
assertEqual ${allTestAdoptionPaperIds[1]} $(echo $RESPONSE | jq .adoptionpaperId)
assertEqual "\"John\"" $(echo $RESPONSE | jq ".adopterFName")
#
#
# Verify that a 404 (Not Found) error is returned for a GET purchase request with a non-existing adopterId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 23: Verify that a 404 (Not Found) HTTP Status is returned for a get purchase request with a non existing adopterId"
assertCurl 404 "curl http://$HOST:$PORT/api/v1/adopters/fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1/adoptionpapers/${allTestAdoptionPaperIds[1]} -s"
#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a GET purchase with an adopterId that is invalid (c3540a89-cb47-4c96-888e-ff9670)
echo -e "\nTest 24 Verify that a 422 (Unprocessable Entity) HTTP Status for a GET purchase request with an invalid adopterId"
assertCurl 422 "curl http://$HOST:$PORT/api/v1/adopters/fcbf86b1-8a76-4d2b-a352-75b10a8f/adoptionpapers/${allTestAdoptionPaperIds[1]} -s"
#
#
# Verify that a 404 (Not Found) error is returned for a GET purchase request with a non-existing adoptionpaperId (c3540a89-cb47-4c96-888e-ff96708db4d7)
echo -e "\nTest 25: Verify that a 404 (Not Found) HTTP Status is returned for a get purchase request adoptionpaperId a non existing adoptionpaperId"
assertCurl 404 "curl http://$HOST:$PORT/api/v1/clients/fcbf86b1-8a76-4d2b-a352-75b10a8fd4a2/adoptionpapers/c3540a89-cb47-4c96-888e-ff96708db4d7 -s"
#
#
# Verify that a 422 (Unprocessable Entity) HTTP Status is returned for a GET purchase with an adoptionpaperId that is invalid (c3540a89-cb47-4c96-888e-ff9670)
echo -e "\nTest 26: Verify that a 422 (Unprocessable Entity) HTTP Status for a GET purchase request with an invalid adopterId"
assertCurl 422 "curl http://$HOST:$PORT/api/v1/adopters/c3540a89-cb47-4c96-888e-ff9670/purchases/c3540a89-cb47-4c96-888e-ff9670 -s"
#
#
# Verify that aggregate invariant worked for the previous purchase post and that the vehicle status was changed to SALE_PENDING
# make sure it was AVAILABLE in the test data
echo -e "\nTest 27: AGGREGATE INVARIANT: Verify using a vehicle get by locationId and vin that the vehicle status was updated upon post of the customer purchase"
assertCurl 200 "curl http://$HOST:$PORT/api/v1/locations/3fe5c169-c1ef-42ea-9e5e-870f30ba9dd0/vehicles/5YJ3E1EA7KF654321 -s"
assertEqual "\"SALE_PENDING\"" $(echo $RESPONSE | jq ".status")
#
#
#Verify that a PUT for a previous purchase post works. Change the purchase status to PURCHASE_COMPLETED, then verify vehicle status is SOLD
#
#
#Verify that a DELETE for a previous post works. Verify that the status of the sale is now PURCHASE_CANCELLED and that the vehicle status is AVAILABLE
#
#


#cleanup docker

if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test environment..."
    echo "$ docker-compose down"
    docker-compose down
fi

API_FIELDS["http://localhost:8080/api/v1/adopters"]="fName lName"
API_FIELDS["http://localhost:8080/api/v1/volunteers"]="fName lName"
API_FIELDS["http://localhost:8080/api/v1/locations"]="name"
API_FIELDS["http://localhost:8080/api/v1/locations/3e6c62a3-b1e7-4eb7-9642-8cdcb3ac74e6/dogs"]="name"
API_FIELDS["http://localhost:8080/api/v1/adopters/fcbf86b1-8a76-4d2b-a352-75b10a8fd4a1/adoptionpapers"]="adopterFName"
