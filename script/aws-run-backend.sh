#!/usr/bin/env bash

#cd to compose folder
cd /docker_compose/;

#pull newest images
docker-compose -f docker-compose-loyalty.yaml pull;

#re run compose with newest image
docker-compose -f docker-compose-loyalty.yaml up;