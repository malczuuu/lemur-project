#!/bin/sh

./gradlew clean build -x check

./lemur-app/docker.sh
./lemur-app/docker.sh
