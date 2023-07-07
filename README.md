# spring-cloud-ecommerce

## Docker

Build docker image example

````powershell
docker build -t alexandersupertramp/serviceregistry:0.0.1 .
````
Run docker image example

````powershell
docker run --name Eureka -d -p8761:8761 alexandersupertramp/serviceregistry:0.0.1
````

