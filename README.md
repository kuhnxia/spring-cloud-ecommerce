# spring-cloud-ecommerce

## Docker

Login to docker.io

````powershell
docker login -u "myusername" -p "mypassword" docker.io
````

Push images to docker.io

````powershell
docker push username/image:tag
````

Before build

````powershell
mvn clean install
````

Build docker image example

````powershell
docker build -t alexandersupertramp/serviceregistry:0.0.1 -t alexandersupertramp/serviceregistry:latest .
docker build -t alexandersupertramp/configserver:0.0.1 -t  alexandersupertramp/configserver:latest .
docker build -t alexandersupertramp/cloudgateway:0.0.1 -t  alexandersupertramp/cloudgateway:latest .
````
Run docker image example

````powershell
docker run --name Eureka -d -p8761:8761 alexandersupertramp/serviceregistry:0.0.1
docker run -d --name configserver -p9296:9296 -e EUREKA_SERVER_URL=host.docker.internal alexandersupertramp/configserver:latest
docker run -d --name cloudgateway -p9090:9090 -e CONFIG_SERVER_URL=host.docker.internal -e EUREKA_SERVER_URL=host.docker.internal alexandersupertramp/cloudgateway:latest
````

Run docker-compose example

````powershell
docker-compose -f docker-compose.yaml up 
````

Auto build docker image by Jib Maven Plugin （Try different VPN if connection problem occur.

````powershell
mvn clean install jib:build
````

## Kubernetes

Check information

```powershell
kubectl get all -o wide
```

Create deployment examples

```powershell
kubectl create deployment nginx --image=nginx
```
Get deployment details

```powershell
kubectl describe deployment nginx
```

Inside the pod

```
kubectl exec -it nginx-748c667d99-hrlhf -- /bin/bash
```

