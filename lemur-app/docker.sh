#!/bin/sh

docker build \
  -t "lemur-app:${LEMUR_VERSION:-snapshot}" \
  -f lemur-app/Dockerfile \
  lemur-app
