#!/bin/bash

services=""

for f in *; do
    if [ -d "$f" ] && [[ "$f" == *service ]]; then
        services+="$f "
    fi
done

echo -e "\n"Command: docker-compose -f docker-compose.dev.yml stop $services "$@" "\n"
docker-compose -f docker-compose.dev.yml stop $services "$@"
