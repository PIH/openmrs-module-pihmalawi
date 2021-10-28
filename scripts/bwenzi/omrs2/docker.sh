#!/bin/bash

function log() {
    sudo docker logs -f omrs2
}

function stop() {
    sudo docker stop omrs2
}

function start() {
    sudo docker start omrs2
    log
}

function rebuild() {
    stop
    sudo docker rm omrs2
    sudo docker rmi pih/malawi-omrs2:latest
    sudo docker build -t pih/malawi-omrs2:latest .
    sudo docker run --restart=on-failure:10 --net="host" -d -p 8081:8080 -p 8209:8009 -p 8205:8205 -v /usr/local/malawi/omrs2/config/modules:/root/.OpenMRS/modules -v /usr/local/malawi/omrs2/config/webapps:/usr/local/tomcat/webapps --name omrs2 pih/malawi-omrs2
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

