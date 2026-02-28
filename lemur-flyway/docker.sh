#!/bin/sh

docker build \
  -t "lemur-flyway:${LEMUR_VERSION:-snapshot}" \
  -f lemur-flyway/Dockerfile \
  lemur-flyway
