/* Pepfar Cut-off = 30, MoH cut-off = 60 */

SET @defaultOneMonth = 30;
SET @birthDateDivider = 30;

SET @ageGroup = "FNP";

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


DROP TEMPORARY TABLE IF EXISTS pepfar_tx_mmd;
CREATE TEMPORARY TABLE pepfar_tx_mmd(
    sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    less_than_three_months int,
    three_to_five_months int,
    six_months_plus int
    );

call create_pepfar_tx_mmd(@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_mmd_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_mmd_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_mmd_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_mmd_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");


select * from pepfar_tx_mmd;