#!/bin/bash

echo -e "\n"Command: docker-compose -f docker-compose.dev.yml stop "$@" "\n"
docker-compose -f docker-compose.dev.yml stop "$@"
