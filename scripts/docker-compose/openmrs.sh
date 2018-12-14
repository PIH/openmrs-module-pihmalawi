#!/bin/bash

export MYSQL_ROOT_PASSWORD=root
export MYSQL_PASSWORD=openmrs
export MYSQL_PORT=3308
export OPENMRS_SERVER_PORT=8080
export ARTIFACTS_PATH=~/malawi-artifacts
export MYSQL_INITIAL_DB_PATH=$ARTIFACTS_PATH/malawi-initial-db.sql
export OPENMRS_WEBAPPS_PATH=$ARTIFACTS_PATH/webapps
export OPENMRS_MODULES_PATH=$ARTIFACTS_PATH/modules

OPERATION=$1

function start() {
    docker-compose -f ~/openmrs-module-pihmalawi/scripts/docker-compose/docker-compose.yml up -d --build
}

function stop() {
    docker-compose -f ~/openmrs-module-pihmalawi/scripts/docker-compose/docker-compose.yml down
}

function log() {
    docker logs -f dockercompose_openmrs_1
}

case $OPERATION in
    start)
        start
        ;;
    stop)
        stop
        ;;
    log)
        log
        ;;
    update)
        if [ -d "$ARTIFACTS_PATH" ]; then
                echo "Please remove/backup existing malawi-artifacts directory before updating..."
                exit 0
        fi
        stop
        ~/openmrs-module-pihmalawi/scripts/update-artifacts.sh
        start
        ;;
    *)
        echo "USAGE:"
        echo "start:  Starts all services"
        echo "stop:   Stops all services"
        echo "log:    Tails the OpenMRS log"
        echo "update: Stops the server, downloads new artifacts, starts the server"
    ;;
esac