/*********************************************************************

PEPFAR TX_PVLS REPORT
Percentage of ART patients with a suppressed viral load (VL) result (<1000 copies/ml) documented in
the medical or laboratory records/laboratory information systems (LIS) within the past 12 months
**********************************************************************/

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @ageGroup = "FNP";

set @dayOfEndDate = DAY(@endDate);

set @startOfTheYear=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 12 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);

DROP TEMPORARY TABLE IF EXISTS pepfar_tx_pvls;
CREATE TEMPORARY TABLE pepfar_tx_pvls(
	sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    tx_curr int,
	due_for_vl int,
    routine_samples_drawn int,
    target_samples_drawn int,
    routine_low_vl_less_than_1000_copies int,
    routine_high_vl_more_than_1000_copies int,
    targeted_low_vl_less_than_1000_copies int,
    targeted_high_vl_more_than_1000_copies int);

call create_pepfar_tx_pvls(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_pvls_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_pvls_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_pvls_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_pvls_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");

select * from pepfar_tx_pvls;