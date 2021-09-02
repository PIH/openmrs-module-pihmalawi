#!/bin/bash

function log() {
    sudo docker logs -f neno-openmrs
}

function stop() {
    sudo docker stop neno-openmrs
}

function start() {
    sudo docker start neno-openmrs
    log
}

function rebuild() {
    stop
    sudo docker rm neno-openmrs
    sudo docker rmi pih/malawi-openmrs:latest
    sudo docker build -t pih/malawi-openmrs:latest .
    sudo docker run --restart=on-failure:10 --net="host" -d -p 8080:8080 -p 8009:8009 -v /usr/local/malawi/config/modules:/root/.OpenMRS/modules -v /usr/local/malawi/config/webapps:/usr/local/tomcat/webapps --name neno-openmrs pih/malawi-openmrs
    log
}

case $1 in
    start)
	start
	;;
    log)
	log
	;;
    stop)
	stop
	;;
    rebuild)
	rebuild
	;;
    *)
	echo "USAGE:"
	echo ""
	echo "  start: Starts container"
	echo "  stop: Stops container"
	echo "  rebuild: Rebuilds the image and runs the container"
	echo "  log: Tails the OpenMRS Log File"
	echo ""
	;;
esac
exit 0
