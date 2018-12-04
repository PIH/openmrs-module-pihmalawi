# Deploying the PIH Malawi Distribution using Docker Compose

This project contains the relevant files to run the PIH Malawi distribution using Docker.  It utilizes docker-compose, which enables connecting several independent
dockerized services together (in this case MySQL and Tomcat), and maintaining configuration for these within a docker-compose.yml file.

If you haven't already done do, you will need to [Install Docker](https://docs.docker.com/) and [Install Docker Compose](https://docs.docker.com/compose/)

### Step 1:  Download the necessary deployment artifacts

This Docker container mounts in several host files/directories to provide the webapps, modules, and initial database script.
These can be 


### Step 1:  Adjust the included configuration files as needed

MYSQL_ROOT_PASSWORD=root
MYSQL_PASSWORD=openmrs
MYSQL_PORT=3308
OPENMRS_SERVER_PORT=8080
MYSQL_INITIAL_DB_PATH=~/environments/nenotest/malawi-initial-db.sql
OPENMRS_WEBAPPS_PATH=~/environments/nenotest/webapps
OPENMRS_MODULES_PATH=~/environments/nenotest/modules

Although this docker-compose project should work without modification, there may be occasions when you would like more control over the
way it operates, given your particular machine configuration.  Most of the included files should be considered defaults that can be changed.  Some common examples:

**The Tomcat port** - Default is 8080.  Changing this requires editing the "tomcat" service settings in docker-compose.yml 

**The Tomcat memory and environment** - Edit the included openboxes-setenv.sh file

**The MySQL database name, port, username, or password** - Changing these requires editing the "db" service settings in docker-compose.yml 
and the dataSource.url, dataSource.username, and/or dataSource.password in openmrs-runtime.properties

**The MySQL root password** - Edit the "db" service settings in docker-compose.yml 

**The MySQL configuration** - Edit the included openmrs-mysql.conf file

### Step 2:  Build the necessary Docker images and attempt initial run

From the base directory (the one that contains your docker-compose.yml file) run:

`docker-compose up --build`

### Step 3:  Attempt to start up the service

After step 1 above, Ctrl-C out of it, and attempt to run again, this time omitting the build flag:

`docker-compose up`

This should successfully start up the application.  If any errors occur, these will generally be configuration related.  Fix and repeat this step

Ctrl-C from the window in which you brought the containers up is sufficient to later stop those services.  You can also stop them explicitly from another
terminal window, by running:

`docker-compose down`

### Working with the running containers

Some useful commands that are helpful to work with these containers while they are running:

**Open up a bash shell for working within a container**: 

`docker exec -it <container-name> /bin/bash`

For example, let's say you want to use the mysql client within the mysql container to look around the database a bit:

1. `docker ps` - will show you the names of which containers are running.
2. `docker exec -it container_name /bin/bash` - this will put you into a bash shell __inside__ the container.  It will look something like `root@d7e7bd809849:/#`
3. `mysql -u openmrs -p openmrs` - Now that you are inside the container, you can run the normal slew of mysql commands as if it is local

**Tail the log file of a particular container**

`docker logs -f <container-name>` (see https://docs.docker.com/engine/reference/commandline/logs/)

For example, let's say you want to tail the Tomcat logs":

1. `docker ps` - will show you the names of which containers are running.
2. `docker logs -f container_name` - will tail the log file.  Hit Ctrl-C to exit.
