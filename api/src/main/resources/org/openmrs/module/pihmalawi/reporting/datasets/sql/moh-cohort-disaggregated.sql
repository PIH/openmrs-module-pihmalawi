/************************************************************************

MOH COHORT DISAGGREGATED REPORT
Use OpenMRS data warehouse tables and stored procedures

Query aggregates active patients on ART.
Patients whose last appointment exceeds 60 days without visiting a heath facility
are considered to be defaulters
*************************************************************************/

SET @birthDateDivider = 30;
SET @defaultCutOff = 60;
SET @ageGroup = "FNP";

DROP TEMPORARY TABLE IF EXISTS moh_cohort_disaggregated;
     CREATE TEMPORARY TABLE moh_cohort_disaggregated(
         sort_value INT PRIMARY KEY auto_increment,
         age_group VARCHAR(100),
         gender varchar(10),
         tx_curr int,
         0A int,
         2A int,
         4A int,
         5A int,
         6A int,
         7A int,
         8A int,
         9A int,
         10A int,
         11A int,
         12A int,
         13A int,
         14A int,
         15A int,
         16A int,
         17A int,
         0P int,
         2P int,
         4PP int,
         4PA int,
         9PP int,
         9PA int,
         11PP int,
         11PA int,
         12PP int,
         12PA int,
         14PP int,
         14PA int,
         15P int,
         15PP int,
         15PA int,
         16P int,
         17PP int,
         17PA int,
         non_standard int
     );

call create_moh_cohort_disaggregated(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_moh_cohort_disaggregated_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Male");
call create_moh_cohort_disaggregated_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FP");
call create_moh_cohort_disaggregated_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_moh_cohort_disaggregated_generic(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FBF");

select * from moh_cohort_disaggregated;