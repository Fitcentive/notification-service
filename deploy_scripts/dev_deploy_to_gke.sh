#!/bin/bash

kubectl delete -n notification deployment/notification-service

# Delete old 1.0 image from gcr
echo "y" | gcloud container images delete gcr.io/fitcentive-dev-02/notification:1.0 --force-delete-tags

# Build and push image to gcr
sbt docker:publish

kubectl apply -f deployment/gke-dev-env/