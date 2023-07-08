# spring-cloud-ecommerce

## Docker

Build docker image example

````powershell
docker build -t alexandersupertramp/serviceregistry:0.0.1 -t alexandersupertramp/serviceregistry:latest .
docker build -t alexandersupertramp/configserver:0.0.1 -t  alexandersupertramp/configserver:latest .
````
Run docker image example

````powershell
docker run --name Eureka -d -p8761:8761 alexandersupertramp/serviceregistry:0.0.1
docker run -d --name configserver -p9296:9296 -e EUREKA_SERVER_ADDRESS=http://host.docker.internal:8761/eureka alexandersupertramp/configserver:latest
````

