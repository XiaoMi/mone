# OzHeraOperator Overview
OzHeraOperator is used to deploy the entire OzHera system with one click.

# Environment Requirements
+ k8s cluster
+ Better to have LB enabled. If in local test environment, NodePort will be used by default.
+ Must have k8s privileges to create ns, crd, service, deployment, pv.

# Operation Steps
Please switch to ozhera-operator/ozhera-operator-server/src/main/resources/operator directory
## Create Account and Namespace
kubectl apply -f ozhera_operator_auth.yaml

## Create ozhera CRD
kubectl apply -f ozhera_operator_crd.yaml

## Deploy Operator
kubectl apply -f ozhera_operator_deployment.yaml
Please note: the operator image. If you need to build the image from the source code yourself, please modify the image address in the yaml file.

## Operator UI Operation to Complete OzHera Deployment
Find the access address of ozhera, the specific service name is in the ozhera_operator_deployment.yaml
kubectl get service -n=ozhera-namespace

After finding it, access the operator UI address in the browser and deploy according to the UI operation.

# Appendix
+ Some k8s operations (for convenient testing)
  + Initialize custom resources
  + kubectl apply -f mone_bootstrap_crd.yaml
  + View created custom resources
  + kubectl get monebootstraps
  + Delete a specific custom resource
  + kubectl delete monebootstraps example-podset
  + Delete a deployment
  + kubectl delete deployment nginx-deployment
  + View all deployments
  + kubectl get deployments
+ In this test case, it is actually installing 2 deployments and two services.
+ Test after installation: curl http://localhost:31080/
+ Pull mysql 5.7 image
+ docker pull mysql:5.7  (for arm64 m1: docker pull mariadb:latest)
+ Execute command in node
+ kubectl exec mysql-deployment-7b65855b66-9qhp7 - mysql -uroot -pMone!123456 -e "select 1"
+ Execute command in sidecar
+ kubectl exec -it redis-deployment-74667fdc89-727kp -c toolbox
