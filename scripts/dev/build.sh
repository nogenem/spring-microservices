#!/bin/bash

echo -e "\n"Command: docker-compose -f docker-compose.dev.yml build "$@" "\n"
docker-compose -f docker-compose.dev.yml build "$@"