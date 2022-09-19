/************************************************************************

PEPFAR COHORT DISAGGREGATED REPORT
Use OpenMRS data warehouse tables and stored procedures

Query aggregates active patients on ART.
Patients whose last appointment exceeds 30 days without visiting a heath facility
are considered to be defaulters
*************************************************************************/

/* 30 for age in months*/
SET @birthDateDivider = 30;
SET @defaultCutOff = 30;

DROP TEMPORARY TABLE IF EXISTS pepfar_cohort_disaggregated;
CREATE TEMPORARY TABLE pepfar_cohort_disaggregated(
	 sort_value INT PRIMARY KEY auto_increment,
	 age_group VARCHAR(100),
	 gender varchar(10),
	 tx_new int,
	 tx_curr int,
	 tx_curr_ipt int,
	 tx_curr_screened_tb int
);

call create_pepfar_cohort_disaggregated(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_cohort_disaggregated_male_aggregation(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider);
call create_pepfar_cohort_disaggregated_female_aggregation(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Patient Pregnant","FP");
call create_pepfar_fnp_aggregation(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"FNP");
call create_pepfar_cohort_disaggregated_female_aggregation(@startDate,@endDate,@location,@defaultCutOff,@birthDateDivider,"Currently breastfeeding child","Fbf");

select * from pepfar_cohort_disaggregated;

