#!/usr/bin/env bash

# set env
export CLUSTER_NAME=loyalty-cluster
export SERVICE_ACCOUNT_NAME=loyalty-service-account
export export NAMESPACE_POSTGRES=postgres

# setup cluster
kubectl config set-context $CLUSTER_NAME
kubectl config use-context $CLUSTER_NAME

# using minikube
minikube start -p $CLUSTER_NAME --memory='4000mb' --cpus=4 --disk-size=40gb --vm-driver="virtualbox" --insecure-registry=localhost:5000
minikube profille


