#!/bin/bash
set -e

gcloud auth activate-service-account --key-file=/opt/service-account/key.json
gcloud pubsub topics publish --project=fitcentive-dev-03 flush-stale-notifications --message="{\"topic\":\"flush-stale-notifications\",\"payload\":{\"message\":\"Flush all stale notifications now\"},\"id\":\"607106fc-659b-427a-8bfb-29031cc57df7\"}"

exec "$@"