/***************************************************************************
PEPFAR TX HTN REPORT

The number of people living with HIV (PLHIV) on ART who are diagnosed
and controlled for hypertension (HTN) during the reporting period
***************************************************************************/

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @ageGroup = "Male";

SET @dayOfEndDate = DAY(@endDate);
set @startDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 6 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);


DROP TEMPORARY TABLE IF EXISTS pepfar_tx_hiv_htn;
CREATE TEMPORARY TABLE pepfar_tx_hiv_htn(
    sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    tx_curr int,
    diagnosed_htn int,
    screened_for_htn int,
	newly_diagnosed int,
	controlled_htn int
    );

call create_pepfar_tx_hiv_htn(@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_hiv_htn_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_hiv_htn_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_hiv_htn_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_hiv_htn_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");


select * from pepfar_tx_hiv_htn;
