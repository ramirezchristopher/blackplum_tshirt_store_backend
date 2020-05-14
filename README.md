# Black Plum Store Backend

This Spring Boot service handles all backend responsibilities for the 'Black Plum Apparel' t-shirt store web application.

#### Purpose
Stores catalog of products for display on frontend. When purchases are received from the frontend, payments are processed, orders are placed for fulfillment, and status updates and confirmations are sent to the customer.

### Getting started

This project was compiled with Java 1.8 (8.0.252-open).
Requires MongoDB setup before running to store product catalog data.

**Clone the project**
   * git clone https://github.com/ramirezchristopher/blackplum_tshirt_store_backend.git

**Add configuration properties**
   * Create 'application.yml' in src/main/resources (file not included in git repo).
   This file supplies configuration properties for connection to MongoDB, Printful API, Mailgun, Braintree Payments, and Swagger.

### METHOD 1: Running with Gradle (Gradle Wrapper 6.1)
  1. cd <PATH_TO_PROJECT>/blackplum_tshirt_store_backend/
  2. ./gradlew bootRun


### METHOD 2: Running from JAR

**Build**
  1. cd <PATH_TO_PROJECT>/blackplum_tshirt_store_backend/
  2. ./gradlew build

**Run Locally**
  * java -jar build/libs/blackplum_tshirt_store_backend-1.0.0.jar


### METHOD 3: Running with Docker

Two different Spring profiles are available: develop or production. To change the profile, edit Dockerfile and supply the argument to the *-Dspring.profiles.active* property.

**Build Image**
  1. cd <PATH_TO_PROJECT>/blackplum_tshirt_store_backend/
  2. sudo docker build -t blackplum_backend .

**Running Container**
  * sudo docker run -it -p 8080:8080 --rm blackplum_backend

**Stopping Container**
  * sudo docker container stop <container id>

**Deleting Image**
  *  sudo docker image rm blackplum_backend


### Accessing the REST API

  * Swagger: [http://localhost:8080/webjars/swagger-ui/3.19.0/index.html?url=http://localhost:8080/api-docs](http://localhost:8080/webjars/swagger-ui/3.19.0/index.html?url=http://localhost:8080/api-docs)
