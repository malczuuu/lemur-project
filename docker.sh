#!/bin/sh

./gradlew clean build -x check

docker build \
  -t "lemur-app:${LEMUR_VERSION:-snapshot}" \
  -f ./lemur-app/Dockerfile \
  ./lemur-app/

docker build \
  -t "lemur-flyway:${LEMUR_VERSION:-snapshot}" \
  -f ./lemur-flyway/Dockerfile \
  ./lemur-flyway/
