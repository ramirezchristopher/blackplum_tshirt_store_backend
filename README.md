# Black Plum Store Backend

This Spring Boot service handles all backend responsibilities for the 'Black Plum Apparel' t-shirt store web application.

### Purpose
Stores catalog of products for display on frontend. When purchases are received from the frontend, payments are processed, orders are placed for fulfillment, and status updates and confirmations are sent to the customer.

### MongoDB Setup

**Run mongo-org 4.2.7-bionic Docker image**
  <pre>  sudo docker run -p 27017:27017 --name mongodb mongo:4.2.7-bionic</pre>
  
**Setup Catalog Database**
  + Install and run Mongo Shell (https://docs.mongodb.com/mongodb-shell/):
    <pre>  mongosh</pre>
 
  + Create 'catalog' database:
    <pre>  use catalog</pre>
    
  + Create service user:
    <pre>
      db.runCommand(
        {
          createUser: "my_service_user",
          pwd: "my_service_password", 
          roles: [
            { role: "readWrite", db: "catalog" }
          ],
          mechanisms:["SCRAM-SHA-1"], 
          digestPassword: true
        }
      )
    </pre>
    
  + Verify created user:
  <pre>
      db.runCommand(
        {
          usersInfo: "my_service_user",
          showCredentials: true,
          showPrivileges: true,
          showAuthenticationRestrictions: true
        }
      )
  </pre>

  + Catalog data can be loaded into the database by using the /v1/catalog/init REST endpoint
  
  
**Cleanup**

+ Remove User: 
<pre>
  db.runCommand(
    {
      dropUser: "my_service_user"
    }
  )
</pre>

**Remove MongoDB Container**
  * <tt>sudo docker container stop container_id</tt>
  * <tt>sudo docker container rm container_id</tt>

**Delete MongoDB Image**
  *  <tt>sudo docker image rm image_id</tt>


### Service Setup

This project was compiled with Java 1.8 (8.0.252-open).
Requires MongoDB setup before running to store product catalog data.

**Clone the project**
   * <tt>git clone https://github.com/ramirezchristopher/blackplum_tshirt_store_backend.git</tt>

**Add configuration properties**
   * Create 'application.yml' in src/main/resources (file not included in git repo).
   This file supplies configuration properties for connection to MongoDB, Printful API, Mailgun, Braintree Payments, and Swagger.

### METHOD 1: Running with Spring Boot Gradle Plugin
  * <tt>cd PATH_TO_PROJECT/blackplum_tshirt_store_backend/</tt>
  * <tt>./gradlew bootRun</tt>


### METHOD 2: Running from JAR

**Build**
   * <tt>cd PATH_TO_PROJECT/blackplum_tshirt_store_backend/</tt>
   * <tt>./gradlew build</tt>>
        

**Run**
  * <tt>java -jar build/libs/blackplum_tshirt_store_backend-1.0.0.jar</tt>


### METHOD 3: Running with Docker

Two different Spring profiles are available: develop or production. To change the profile, edit Dockerfile and supply the argument to the *-Dspring.profiles.active* property.

**Build Image**
  * <tt>cd PATH_TO_PROJECT/blackplum_tshirt_store_backend/</tt>
  * <tt>sudo docker build -t blackplum_backend .</tt>

**Run Container**
  * <tt>sudo docker run -it -p 8080:8080 --rm blackplum_backend</tt>

#### Cleanup
**Remove Container**
  * <tt>sudo docker container stop container_id</tt>
  * <tt>sudo docker container rm container_id</tt>

**Delete Image**
  *  <tt>sudo docker image rm image_id</tt>


### Accessing the REST API

  * Swagger: [http://localhost:8080/webjars/swagger-ui/3.19.0/index.html?url=http://localhost:8080/api-docs](http://localhost:8080/webjars/swagger-ui/3.19.0/index.html?url=http://localhost:8080/api-docs)
