#!/bin/bash

# Beware: mailx is a bitch to set up, in particular with gmail, check README.TXT for some hints

MAIL=cneumann@pih.org
PATH=$PATH:/bin:/usr/bin # make sure wget and mailx commands are in the crontab path
BASEDIR=$(dirname $0)

# copy these 3 lines into a file openmrs.config and make sure it never gets checked in/published anywhere
# BASE_URL=http://localhost:8080/openmrs
# USER=admin
# PW=<change me>

#source $BASEDIR/openmrs.config
source $BASEDIR/run_report.config

wget --quiet --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet

if [ $? -ne 0 ]; then
  # do it again to make sure this system wasn't just too busy in this moment
  sleep 300
  wget --quiet --post-data "uname=$USER&pw=$PW" $BASE_URL/loginServlet
  if [ $? -ne 0 ]; then
    # stop and start tomcat, might/will require root privileges
    # run script from root crontab or give sudo permissions to user (sudo visudo)
    sudo /etc/init.d/tomcat5.5 stop
    sleep 300
    sudo /etc/init.d/tomcat5.5 start
    mailx -s "emr: OpenMRS did not respond. Restarted at `date +%Y%m%d-%H%M`" "$MAIL" <<EOF
`tail -n 250 /var/log/tomcat5.5/catalina.out`
EOF
  fi
fi

rm -f index.htm*
