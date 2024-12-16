/***************************************************************************

PEPFAR TB PREV REPORT
Use OpenMRS data warehouse tables and stored procedures

Proportion of ART patients who started on a standard course of TB Preventive
 Treatment (TPT) in the previous reporting period who completed therapy.
***************************************************************************/

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @ageGroup = "Male";

SET @dayOfEndDate = DAY(@endDate);
SET @startDate =
  (SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 90 DAY), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @sixMonthsStartDate= (SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @sevenMonthsStartDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 7 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);
SET @thirteenMonthsStartDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 13 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);


call create_age_groups();
call create_last_art_outcome_at_facility(@endDate,@location);


DROP TEMPORARY TABLE IF EXISTS pepfar_tb_prev;
CREATE TEMPORARY TABLE pepfar_tb_prev(
    sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    new_start_three_hp int,
    new_start_six_h int,
    previous_start_three_hp int,
    previous_start_six_h int,
	completed_new_start_three_hp int,
	completed_new_start_six_h int,
	completed_old_three_hp int,
	completed_old_six_h  int
    );

call create_pepfar_tb_prev(@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tb_prev_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tb_prev_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tb_prev_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tb_prev_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");


select * from pepfar_tb_prev;