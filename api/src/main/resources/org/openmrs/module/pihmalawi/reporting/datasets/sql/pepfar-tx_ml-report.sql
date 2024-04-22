/* 30 for age in months*/
SET @birthDateDivider = 30;
--SET @location = "Neno District Hospital";
--SET @startDate = "2020-01-01";
--SET @endDate = "2021-06-30";

SET @defaultCutOff = 30;

SET @ageGroup = "FNP";

DROP TEMPORARY TABLE IF EXISTS pepfar_tx_ml;
CREATE TEMPORARY TABLE pepfar_tx_ml(
    sort_value INT PRIMARY KEY auto_increment,
    age_group VARCHAR(100),
    gender varchar(10),
    Died int,
    IIT_3mo_or_less_mo int,
    IIT_3to5_mo int,
    IIT_6plus_mo int,
    Transferred_out int,
    Refused_Stopped int);


call create_pepfar_tx_tml(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_tx_tml_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_pepfar_tx_tml_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_pepfar_tx_tml_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_tx_tml_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");


select * from pepfar_tx_ml;