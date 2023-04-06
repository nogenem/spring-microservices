#!/bin/bash

for f in *; do
    if [ -d "$f" ] && [[ "$f" == *service ]]; then
        echo -e "\n"Command: docker-compose -f docker-compose.dev.yml build "$f" "$@" "\n"
        docker-compose -f docker-compose.dev.yml build "$f" "$@"
    fi
done