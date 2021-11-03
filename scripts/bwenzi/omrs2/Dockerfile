FROM tomcat:7-jdk8-openjdk

MAINTAINER cosmin ioan <cioan@pih.org>

ENV JAVA_OPTS="-Xmx3072m -Xms1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:NewSize=128m -Djava.awt.headless=true -Djava.awt.headlesslib=true"
RUN rm -rf /usr/local/tomcat/webapps/*
ADD config/etc-default-tomcat /etc/default/tomcat
ADD config/webapps/omrs2.war /usr/local/tomcat/webapps/omrs2.war
ADD config/modules/*.omod /root/.OpenMRS/modules/
ADD config/openmrs-runtime.properties /root/.OpenMRS/omrs2-runtime.properties
ADD config/warehouse-connection.properties /root/.OpenMRS/warehouse-connection.properties
ADD config/web.xml /usr/local/tomcat/conf/web.xml
ADD config/context.xml /usr/local/tomcat/conf/context.xml
ADD config/server.xml /usr/local/tomcat/conf/server.xml

# Open Ports

EXPOSE 8080

# Start Services
