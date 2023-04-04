#!/bin/sh

echo "\n"Command: docker-compose -f docker-compose.dev.yml up -d "$@" "\n"
docker-compose -f docker-compose.dev.yml up -d "$@"
