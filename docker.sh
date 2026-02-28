#!/bin/sh

./gradlew clean build -x check

sh ./lemur-app/docker.sh
sh ./lemur-app/docker.sh
