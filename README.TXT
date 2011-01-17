Collection of PIH Malawi customizations

For now mainly Reporting module reports are kept here.

Requirement:s
- OpenMRS 1.5.2
- ProgramLocation Module
- Reporting Module 0.5+

After deploying this module the reports can be registered through
the Administration UI for this module.

Additionally for a local dev env _misused_ Junit tests can register the reports
without the need of deploying a module.

The reports can be invoked through the generic OpenMRS Reporting module UI or
with automated scripts from the scripts directory. The script may require
additional software like a specific version of the commands mysql, date or nail.
Use these crontab entries as an example:
0 23 * * Mon /home/emradmin/script_reports/run_weekly_reports_nno.sh >> /dev/null
0 19 * * * /home/emradmin/script_reports/run_monthly_reports_nno.sh >> /dev/null

