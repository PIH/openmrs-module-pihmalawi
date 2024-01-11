/* defaultCutOff of 30 days */

SET @birthDateDivider = 30;
SET @defaultCutOff = 30;
SET @ageGroup = "FNP";

DROP TEMPORARY TABLE IF EXISTS pepfar_tx_new;
     CREATE TEMPORARY TABLE pepfar_tx_new(
         sort_value INT PRIMARY KEY auto_increment,
         age_group VARCHAR(100),
         gender varchar(10),
         tx_new_cd4_less_than_two_hundred int,
         tx_new_cd4_equal_to_or_greater_than_two_hundred int,
         tx_new_cd4_equal_unknown_or_not_done int,
         transfer_ins int
     );

call create_pepfar_tx_new(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_new_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");

select * from pepfar_tx_new;
