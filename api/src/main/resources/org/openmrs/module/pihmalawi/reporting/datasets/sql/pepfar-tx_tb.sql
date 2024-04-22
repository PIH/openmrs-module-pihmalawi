/***************************************************************************

PEPFAR TX_TB REPORT
Use OpenMRS data warehouse tables and stored procedures

Proportion of ART patients screened for TB in the semiannual reporting period who start TB
treatment.
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

DROP TEMPORARY TABLE IF EXISTS pepfar_tx_tb;
CREATE TEMPORARY TABLE pepfar_tx_tb(
    sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    tx_curr int,
    symptom_screen_alone int,
    cxr_screen int,
    mwrd_screen int,
    screened_for_tb_tx_new_pos int,
    screened_for_tb_tx_new_neg int,
    screened_for_tb_tx_prev_pos int,
    screened_for_tb_tx_prev_neg int,
    tb_rx_new int,
    tb_rx_prev int);

call create_pepfar_tx_tb(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_tb_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_tb_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_tb_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_tb_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");


select * from pepfar_tx_tb;