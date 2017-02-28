FROM tomcat:7-jre7

MAINTAINER cosmin ioan <cioan@pih.org>

RUN rm -rf /usr/local/tomcat/webapps/*
ADD etc-default-tomcat /etc/default/tomcat
ADD openmrs.war /usr/local/tomcat/webapps/openmrs.war
ADD openmrs-runtime.properties /root/.OpenMRS/openmrs-runtime.properties
ADD modules/*.omod /root/.OpenMRS/modules/

# Open Ports

EXPOSE 8080