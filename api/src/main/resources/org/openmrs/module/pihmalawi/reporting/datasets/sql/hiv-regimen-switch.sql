/************************************************************************

HIV REGIMEN SWITCH REPORT
Use OpenMRS data warehouse tables and stored procedures

Query get all patients that have changed regimen the past three months per given location
and the provided start date

*************************************************************************/

DROP TEMPORARY TABLE IF EXISTS regimen_switch;
     CREATE TEMPORARY TABLE regimen_switch(
         id INT PRIMARY KEY auto_increment,
         identifier varchar(50),
         gender varchar(10),
         dob varchar(50),
         art_start_date varchar(50),
         weight decimal(12,0),
         previous_regimen varchar(100),
         current_regimen varchar(100),
         dispense_date varchar(50),
         arvs varchar(100),
         arvs_given varchar(150),
         location varchar(150)
     );
call create_changed_regimen_list(@startDate,@location);
call create_changed_regimen_list(DATE_ADD(@startDate, INTERVAL 1 month),@location);
call create_changed_regimen_list(DATE_ADD(@startDate, INTERVAL 2 month),@location);

select * from regimen_switch;