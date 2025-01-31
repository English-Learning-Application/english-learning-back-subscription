aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com
docker build -t subscription-microservice .
docker tag subscription-microservice:latest 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com/subscription-microservice:latest
docker push 761018889743.dkr.ecr.ap-southeast-2.amazonaws.com/subscription-microservice:latest
kubectl delete deployment subscription-service-deployment
kubectl apply -f deployment.yaml