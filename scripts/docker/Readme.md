Dockerfile inclduded in this folder is used to run a container on the [bwenzi](https://bwenzi.pih-emr.org/openmrs) server.


####  Steps to build and run the docker image ####
1. Directory stucture
<pre>
|-- Dockerfile  
|  
|-- etc-default-tomcat  
|  
|-- modules     
|     
|-- openmrs-runtime.properties     
|  	    
|-- openmrs.war     
</pre>
2. Build the image  
**`sudo docker build -t pih/malawi-openmrs:latest .`**
3. Run the container  
**`sudo docker run --net="host" -d -p 8080:8080 --name neno-openmrs pih/malawi-openmrs`**
4. Tail OpenMRS log  
**`sudo docker exec -it neno-openmrs tail -f /root/.OpenMRS/openmrs.log`**
5. Run interactive bash terminal  
**`sudo docker exec -it neno-openmrs bash`**
6. Stop the container  
**`sudo docker stop neno-openmrs`**
7. Start the stopped container  
**`sudo docker start neno-openmrs`**
8. Remove the container  
**`sudo docker rm neno-openmrs`**
9. Remove the image  
**`sudo docker rmi pih/malawi-openmrs:latest`**
