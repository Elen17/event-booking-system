#!/usr/bin/env bash

set -euo pipefail
which docker > /dev/null || (echoerr "Please ensure that docker is in your PATH" && exit 1)

mkdir -p $HOME/docker/volumes/postgres
rm -rf $HOME/docker/volumes/postgres/data

docker run --rm --name pg-docker \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=event_booking \
    -d -p 5434:5432 \
    -v $HOME/docker/volumes/postgres:/var/lib/postgresql postgres:17

sleep 3
export PGPASSWORD=postgres

docker cp ./schema.sql pg-docker:/docker-entrypoint-initdb.d/schema.sql
docker cp ./data.sql pg-docker:/docker-entrypoint-initdb.d/data.sql
docker exec -u postgres pg-docker psql event_booking postgres -f docker-entrypoint-initdb.d/schema.sql
docker exec -u postgres pg-docker psql event_booking postgres -f docker-entrypoint-initdb.d/data.sql