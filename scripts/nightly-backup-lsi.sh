#!/bin/sh

LOGFILE=$$.log
exec > $LOGFILE 2>&1

# Dump the database to a file
cd $HOME/backup/to_backup
mysqldump -u openmrs -ppa55ionFruit openmrs > openmrs.sql
7z a openmrs.sql.7z openmrs.sql

# Keep a backup sequence of the files
cd $HOME/backup
cp to_backup/openmrs.sql.7z sequences/`date '+%Y'`-`date '+%b'`-`date '+%d'`-openmrs.sql.7z
rm -f current/openmrs.*
cp to_backup/openmrs.sql.7z current/.

# Copy database to Boston data storage for safe-keeping
scp -P 8999 $HOME/backup/sequences/`date '+%Y'`-`date '+%b'`-`date '+%d'`-openmrs.sql.7z emradmin@195.200.93.242:backup_lisungwi/

# Remove today's temporary file
rm -f $HOME/backup/to_backup/*

MAIL=apzu-emr@apzu.pih.org
PATH=$PATH:/bin:/usr/bin:/home/emradmin/pihmalawi/scripts
TODAY=`date +%Y%m%d`
mailx -s "emr: Lower Neno nightly backup done $TODAY" "$MAIL" < $LOGFILE


