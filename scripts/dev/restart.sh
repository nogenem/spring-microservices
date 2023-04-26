#!/bin/bash

echo -e "\n"Command: docker-compose -f docker-compose.dev.yml restart "$@" "\n"
docker-compose -f docker-compose.dev.yml restart "$@"
