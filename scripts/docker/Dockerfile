FROM tomcat:7-jre7

MAINTAINER cosmin ioan <cioan@pih.org>

ENV JAVA_OPTS="-Xmx3072m -Xms1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:NewSize=128m -Djava.awt.headless=true -Djava.awt.headlesslib=true"
RUN rm -rf /usr/local/tomcat/webapps/*
ADD openmrs.war /usr/local/tomcat/webapps/openmrs.war
ADD openmrs-runtime.properties /root/.OpenMRS/openmrs-runtime.properties
ADD modules/*.omod /root/.OpenMRS/modules/

# Open Ports

EXPOSE 8080
