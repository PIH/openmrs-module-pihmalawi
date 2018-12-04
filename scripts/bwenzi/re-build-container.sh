#!/bin/bash

sudo docker stop neno-openmrs
sudo docker rm neno-openmrs
sudo docker rmi pih/malawi-openmrs:latest
sudo docker build -t pih/malawi-openmrs:latest .
sudo docker run --net="host" -d -p 8080:8080 --name neno-openmrs pih/malawi-openmrs
sudo docker exec -it neno-openmrs tail -f /root/.OpenMRS/openmrs.log