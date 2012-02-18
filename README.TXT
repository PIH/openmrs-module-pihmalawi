Collection of PIH Malawi customizations

For now mainly Reporting module reports are kept here.

Requirement:s
- OpenMRS 1.5.2
- ProgramLocation Module
- Reporting Module 0.5+

After deploying this module the reports can be registered through
the Administration UI for this module. Some reports make the serialized_object
table go ...; for this case run this SQL statement:
  alter table serialized_object modify serialized_data longtext;

To remove the reports either use the Administration UI, or run this SQL. But be
careful, it clears out everything!
delete from reporting_report_design_resource; delete from reporting_report_design; delete from serialized_object;

Additionally for a local dev env _misused_ Junit tests can register the reports
without the need of deploying a module.

The reports can be invoked through the generic OpenMRS Reporting module UI or
with automated scripts from the scripts directory. The script may require
additional software like a specific version of the commands mysql, date or nail.
Use these crontab entries as an example:
0 23 * * Sun /home/emradmin/script_reports/run_weekly_reports_nno.sh >> /dev/null
0 22 1 * * /home/emradmin/script_reports/run_monthly_reports_nno.sh >> /dev/null

Mail-Setup for gmail via mailx and msmtp (sudo apt-get install msmtp heirloom-mailx)
~/.mailrc 
# set smtp for mailx

# gmail account (default)
# $ mailx -s "subject line" -a /path/attachment recipient@email.com < /path/body.txt
set from="apzunet@gmail.com (apzunet)"
set sendmail="/usr/bin/msmtp"
set message-sendmail-extra-arguments="-a gmail"

~/.msmtprc
# config options: http://msmtp.sourceforge.net/doc/msmtp.html
defaults
logfile /tmp/msmtp.log

# gmail account
account gmail
auth on
host smtp.gmail.com
port 587
user apzunet@gmail.com
password <password>
from apzunet@gmail.com
tls on
tls_trust_file /usr/share/ca-certificates/mozilla/Equifax_Secure_CA.crt

# set default account to use (not necessary with single account)
account default : gmail

Person name code tables::
DROP TABLE IF EXISTS `person_name_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_name_code` (
  `person_name_code_id` int(11) NOT NULL AUTO_INCREMENT,
  `person_name_id` int(11) DEFAULT NULL,
  `given_name_code` varchar(50) DEFAULT NULL,
  `middle_name_code` varchar(50) DEFAULT NULL,
  `family_name_code` varchar(50) DEFAULT NULL,
  `family_name2_code` varchar(50) DEFAULT NULL,
  `family_name_suffix_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`person_name_code_id`),
  KEY `name_for_patient` (`person_name_id`),
  KEY `given_name_code` (`given_name_code`),
  KEY `middle_name_code` (`middle_name_code`),
  KEY `family_name_code` (`family_name_code`),
  KEY `given_family_name_code` (`given_name_code`,`family_name_code`),
  CONSTRAINT `code for name` FOREIGN KEY (`person_name_id`) REFERENCES `person_name` (`person_name_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=135857 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
