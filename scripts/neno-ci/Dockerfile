FROM tomcat:7-jdk8-openjdk

MAINTAINER cosmin ioan <cioan@pih.org>

ENV JAVA_OPTS="-Xmx3072m -Xms1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:NewSize=128m -Djava.awt.headless=true -Djava.awt.headlesslib=true"
RUN rm -rf /usr/local/tomcat/webapps/*
ADD config/etc-default-tomcat /etc/default/tomcat
ADD config/webapps/openmrs.war /usr/local/tomcat/webapps/openmrs.war
ADD config/modules/*.omod /root/.OpenMRS/modules/
ADD config/openmrs-runtime.properties /root/.OpenMRS/openmrs-runtime.properties
ADD config/warehouse-connection.properties /root/.OpenMRS/warehouse-connection.properties
ADD config/web.xml /usr/local/tomcat/conf/web.xml
ADD config/context.xml /usr/local/tomcat/conf/context.xml

# Open Ports

EXPOSE 8080

# Start Services
