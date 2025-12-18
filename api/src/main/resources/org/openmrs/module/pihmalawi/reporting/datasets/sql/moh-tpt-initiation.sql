/************************************************************************

MOH TPT NEW INITIATION REPORT
Use OpenMRS data warehouse tables and stored procedures

Query aggregates patients starting TB preventive therapy,
3HP (3 months of Isoniazid and Rifapentine)
and 6H(6 months of daily isoniazid for TB preventive therapy).
*************************************************************************/

/* 30 for age in months*/
SET @birthDateDivider = 30;
SET @defaultOneMonth = 60;
SET @district="Neno";

SET @ageGroup = "Male";

DROP TEMPORARY TABLE IF EXISTS moh_tpt_initiation;
CREATE TEMPORARY TABLE moh_tpt_initiation(
    sort_value INT PRIMARY KEY auto_increment,
    district varchar(10),
    age_group VARCHAR(100),
    gender varchar(10),
    new_start_three_hp int,
    previous_start_three_hp int,
    new_start_six_h int,
    previous_start_six_h int);

call create_moh_tpt_initiation(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_moh_tpt_initiation_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_moh_tpt_initiation_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_moh_tpt_initiation_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_moh_tpt_initiation_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");

SELECT
    *
FROM
    moh_tpt_initiation;