#!/bin/sh

# Dump the database to file
cd $HOME/backup/to_backup
mysqldump -u openmrs -p<password> openmrs > openmrs.sql
7z a openmrs.sql.7z openmrs.sql

# Dump smaller database file without 2 GI-normous tables and without global properties
mysqldump -u openmrs -p<password> --ignore-table=openmrs.formentry_archive --ignore-table=openmrs.hl7_in_archive --ignore-table=openmrs.global_property openmrs > openmrs-no-props.sql
7z a openmrs-no-props.sql.7z openmrs-no-props.sql

# Keep copy of latest files
cd $HOME/backup
rm -f current/*
cp to_backup/openmrs.sql.7z current/.
cp to_backup/openmrs-no-props.sql.7z current/.

# Keep a backup sequence of the files
cd $HOME/backup
cp to_backup/openmrs.sql.7z sequences/`date '+%Y'`-`date '+%b'`-`date '+%d'`-neno-mysql.7z
cp to_backup/openmrs-no-props.sql.7z sequences/`date '+%Y'`-`date '+%b'`-`date '+%d'`-neno-no-props-mysql.7z
rm -f current/*
cp to_backup/* current/.

# Copy database to Boston data storage for safe-keeping
cd $HOME/backup/to_backup
chown emradmin.backup openmrs.7z
chmod 660 openmrs.sql.7z
scp openmrs.sql.7z backup@dev.pih-emr.org:malawi/db/`date '+%Y'`-`date '+%b'`-`date '+%d'`-neno-mysql.7z

# Copy birt reports to Boston data storage
cd /home/tomcat5/.OpenMRS/birt
tar cf - reports/*.rptdesign | 7za a -siy $HOME/backup/to_backup/birt-reports.tar.7z >/dev/null
chown emradmin.backup $HOME/backup/to_backup/birt-reports.tar.7z
chmod 660 $HOME/backup/to_backup/birt-reports.tar.7z
cd $HOME/backup
cp to_backup/birt-reports.tar.7z current/.
cp to_backup/birt-reports.tar.7z sequences/`date '+%Y'`-`date '+%b'`-`date '+%d'`-birt-reports.tar.7z
scp current/birt-reports.tar.7z backup@dev.pih-emr.org:malawi/birt/`date '+%Y'`-`date '+%b'`-`date '+%d'`-birt-report.tar.7z

# Remove today's temporary file
rm -f $HOME/backup/to_backup/*

