docker build -t subscription-microservice .
minikube image load subscription-microservice:latest
kubectl delete secret subscription-service-secret
kubectl create secret generic subscription-service-secret --from-env-file=local.env
kubectl delete deployment subscription-service-deployment
kubectl apply -f local-deployment.yaml