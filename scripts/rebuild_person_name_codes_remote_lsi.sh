#!/bin/bash

# as i was unable to install the ruby mysql lib on ubuntu 9.04 i just
# move around it and recalculate the person_name_codes for lsi at nno

ssh -p 8999 emradmin@195.200.93.58 "mysqldump -u openmrs -ppa55ionFruit openmrs person_name person patient | bzip2 > /tmp/pn_lsi.sql.bz2"
scp -P 8999 emradmin@195.200.93.58:/tmp/pn_lsi.sql.bz2 /tmp

bunzip2 -f /tmp/pn_lsi.sql.bz2
mysqldump -u openmrs -ppa55ionFruit openmrs person_name person_name_code person patient >/tmp/pn_nno.sql
mysql -u openmrs -ppa55ionFruit openmrs < /tmp/pn_lsi.sql
/home/emradmin/pihmalawi/scripts/rebuild_person_name_codes.sh 
mysqldump -u openmrs -ppa55ionFruit openmrs person_name person_name_code | bzip2 > /tmp/pn_lsi.sql.bz2
mysql -u openmrs -ppa55ionFruit openmrs < /tmp/pn_nno.sql
/home/emradmin/pihmalawi/scripts/rebuild_person_name_codes.sh 

scp -P 8999  /tmp/pn_lsi.sql.bz2 emradmin@195.200.93.58:/tmp
ssh -p 8999 emradmin@195.200.93.58 "bunzip2 -f /tmp/pn_lsi.sql.bz2"
ssh -p 8999 emradmin@195.200.93.58 "mysql -u openmrs -ppa55ionFruit openmrs < /tmp/pn_lsi.sql"
