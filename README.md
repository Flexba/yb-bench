# PG Benchmark
## Setup YugabyteDB

```sh
minikube delete --all
minikube start --memory=16384 --cpus=8 --disk-size=40g
minikube addons enable metrics-server
kubectl create namespace yb-demo
helm install yb-demo yugabytedb/yugabyte --version 2.19.0 --set resource.master.requests.cpu=0.5,resource.master.requests.memory=0.5G,resource.tserver.requests.cpu=0.5,resource.tserver.requests.memory=0.5G,replicas.master=1,replicas.tserver=10,resource.master.limits.cpu=0.5,resource.master.limits.memory=0.5G,resource.tserver.limits.cpu=0.5,resource.tserver.limits.memory=0.5G --namespace yb-demo
kubectl --namespace yb-demo port-forward svc/yb-tserver-service 5433:5433

kubectl --namespace yb-demo port-forward svc/yb-master-ui 7000:7000
minikube dashboard
```

## Add and Remove Tablet servers
Servers can be added by simply scaling the yb-tserver Stateful Set.

To Scale down, the Servers must first be blacklisted to avoid data loss. Execute the according commands in one of the Master Pods:
````sh
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-0.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-1.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-2.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-3.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-4.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-5.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-6.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-7.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-8.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist ADD yb-tserver-9.yb-tservers.yb-demo.svc.cluster.local:9100

yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-0.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-1.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-2.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-3.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-4.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-5.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-6.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-7.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-8.yb-tservers.yb-demo.svc.cluster.local:9100
yb-admin -master_addresses yb-master-2.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-1.yb-masters.yb-demo.svc.cluster.local:7100,yb-master-0.yb-masters.yb-demo.svc.cluster.local:7100 change_blacklist REMOVE yb-tserver-9.yb-tservers.yb-demo.svc.cluster.local:9100
````