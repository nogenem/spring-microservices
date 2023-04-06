#!/bin/bash

echo -e "\n"Command: docker-compose -f docker-compose.dev.yml down "$@" "\n"
docker-compose -f docker-compose.dev.yml down "$@"
