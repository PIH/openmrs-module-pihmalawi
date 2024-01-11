/* 30 for age in months*/
SET @birthDateDivider = 30;
SET @defaultCutOff = 60;
SET @ageGroup = "FNP";

DROP TEMPORARY TABLE IF EXISTS moh_tx_new;
     CREATE TEMPORARY TABLE moh_tx_new(
         sort_value INT PRIMARY KEY auto_increment,
         age_group VARCHAR(100),
         gender varchar(10),
         tx_new_cd4_less_than_two_hundred int,
         tx_new_cd4_equal_to_or_greater_than_two_hundred int,
         tx_new_cd4_equal_unknown_or_not_done int,
         transfer_ins int
     );


call create_moh_tx_new(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_moh_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_moh_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_moh_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_moh_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");



select * from moh_tx_new;