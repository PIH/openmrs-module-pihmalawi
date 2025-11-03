SET @defaultOneMonth = 60;
SET @birthDateDivider = 30;

call create_last_tb_outcome_at_facility(@endDate, @location);
call create_tb_age_groups();

SET @dayOfEndDate = DAY(@endDate);
set @startDate=(SELECT
    CASE
      WHEN @dayOfEndDate = 28 THEN date_add(date_sub(@endDate, INTERVAL 3 MONTH), INTERVAL 4 DAY)
      WHEN @dayOfEndDate = 29 THEN date_add(date_sub(@endDate, INTERVAL 3 MONTH), INTERVAL 3 DAY)
      WHEN @dayOfEndDate = 31 THEN date_add(date_sub(@endDate, INTERVAL 3 MONTH), INTERVAL 1 DAY)
      WHEN @dayOfEndDate = 30 THEN date_add(date_sub(@endDate, INTERVAL 3 MONTH), INTERVAL 2 DAY)
      ELSE @endDate
    END);
    
 select sort_value,x.age_group as age_group, CASE WHEN x.gender="F" then 'Female' else 'Male' end as gender,
CASE WHEN tx_curr is null then 0 else tx_curr end as tx_curr,
CASE WHEN new_smear_positive is null then 0 else new_smear_positive end as new_smear_positive,
CASE WHEN new_mtb_detected_xpert is null then 0 else new_mtb_detected_xpert end as new_mtb_detected_xpert,
CASE WHEN new_clinically_diagnosed_pulmonary is null then 0 else new_clinically_diagnosed_pulmonary end as new_clinically_diagnosed_pulmonary,
CASE WHEN new_eptb is null then 0 else new_eptb end as new_eptb,
CASE WHEN relapse_bact_confirmed is null then 0 else relapse_bact_confirmed end as relapse_bact_confirmed,
CASE WHEN relapse_clinically_pulmonary is null then 0 else relapse_clinically_pulmonary end as relapse_clinically_pulmonary,
CASE WHEN relapse_eptb  is null then 0 else relapse_eptb  end as relapse_eptb,
CASE WHEN treatment_ltf_bact_confirmed  is null then 0 else treatment_ltf_bact_confirmed  end as treatment_ltf_bact_confirmed,
CASE WHEN treatment_ltf_clinically_diagnosed_pulmonary  is null then 0 else treatment_ltf_clinically_diagnosed_pulmonary  end as treatment_ltf_clinically_diagnosed_pulmonary,
CASE WHEN treatment_ltf_eptb  is null then 0 else treatment_ltf_eptb  end as treatment_ltf_eptb,
CASE WHEN treatment_failure_bact_conf  is null then 0 else treatment_failure_bact_conf  end as treatment_failure_bact_conf,
CASE WHEN other_bact_pulmonary  is null then 0 else other_bact_pulmonary  end as other_bact_pulmonary,
CASE WHEN other_clinically  is null then 0 else other_clinically  end as other_clinically,
CASE WHEN other_eptb  is null then 0 else other_eptb  end as other_eptb,
CASE WHEN unknown_bact  is null then 0 else unknown_bact  end as unknown_bact,
CASE WHEN unknown_clinically_pulmonary  is null then 0 else unknown_clinically_pulmonary  end as unknown_clinically_pulmonary,
CASE WHEN unknown_eptb  is null then 0 else unknown_eptb  end as unknown_eptb

 from
tb_age_groups as x
LEFT OUTER JOIN
(
SELECT CASE
	WHEN age <= 59 and gender = "M" THEN "0-4 years"
	WHEN age <= 59 and gender = "F" THEN "0-4 years"
	WHEN age >=60 and age <= 179 and gender = "M" THEN "5-14 years"
	WHEN age >=60 and age <= 179 and gender = "F" THEN "5-14 years"
	WHEN age >=180 and age <= 299 and gender = "M" THEN "15-24 years"
	WHEN age >=180 and age <= 299 and gender = "F" THEN "15-24 years"
    WHEN age >=300 and age <= 419 and gender = "M" THEN "25-34 years"
	WHEN age >=300 and age <= 419 and gender = "F" THEN "25-34 years"
	WHEN age >=420 and age <= 539 and gender = "M" THEN "35-44 years"
	WHEN age >=420 and age <= 539 and gender = "F" THEN "35-44 years"
	WHEN age >=540 and age <= 659 and gender = "M" THEN "45-54  years"
	WHEN age >=540 and age <= 659 and gender = "F" THEN "45-54 years"
	WHEN age >=660 and age <= 779 and gender = "M" THEN "55-64 years"
	WHEN age >=660 and age <= 779 and gender = "F" THEN "55-64 years"
	WHEN age >=780 and gender = "M" THEN "65 +> years"
	WHEN age >=780 and gender = "F" THEN "65 +> years"
END as age_group,gender,
    COUNT(IF((state = 'On treatment'), 1, NULL)) as tx_curr,
    COUNT(IF((initiation_month_smear_test = 'Tuberculosis smear microscopy method'), 1, NULL)) as new_smear_positive,
    COUNT(IF((initiation_month_xpert_test = 'Xpert'), 1, NULL)) as new_mtb_detected_xpert,
    COUNT(IF((disease_classification='Pulmonary' and diagnosis='TB Clinically Diagnosed'), 1, NULL)) as new_clinically_diagnosed_pulmonary,
    COUNT(IF((patient_category='New' and disease_classification='Extrapulmonary tuberculosis (EPTB)'), 1, NULL)) as new_eptb,
    COUNT(IF((patient_category='Relapse' and diagnosis='TB Bacteriologically Confirmed'), 1, NULL)) as relapse_bact_confirmed,
    COUNT(IF((patient_category='Relapse' and diagnosis='TB Clinically Diagnosed' and disease_classification='Pulmonary'), 1, NULL)) as relapse_clinically_pulmonary,
    COUNT(IF((patient_category='Relapse' and disease_classification='Extrapulmonary tuberculosis (EPTB)'), 1, NULL)) as relapse_eptb,
    COUNT(IF((patient_category='RALFT' and diagnosis='TB Bacteriologically Confirmed'), 1, NULL)) as treatment_ltf_bact_confirmed,
    COUNT(IF((patient_category='RALFT' and diagnosis='TB Clinically Diagnosed' and disease_classification='Pulmonary'), 1, NULL)) as treatment_ltf_clinically_diagnosed_pulmonary,
    COUNT(IF((patient_category='RALFT' and disease_classification='Extrapulmonary tuberculosis (EPTB)'), 1, NULL)) as treatment_ltf_eptb,
    COUNT(IF((patient_category='Fail' and diagnosis='TB Bacteriologically Confirmed'), 1, NULL)) as treatment_failure_bact_conf,
    COUNT(IF((patient_category='Other' and diagnosis='TB Bacteriologically Confirmed' and disease_classification='Pulmonary'), 1, NULL)) as other_bact_pulmonary,
    COUNT(IF((patient_category='Other' and disease_classification='Extrapulmonary tuberculosis (EPTB)'), 1, NULL)) as other_eptb,
    COUNT(IF((patient_category='Other' and diagnosis='TB Bacteriologically Confirmed'), 1, NULL)) as other_clinically,
    COUNT(IF((patient_category='Unk' and diagnosis='TB Bacteriologically Confirmed'), 1, NULL)) as unknown_bact,
    COUNT(IF((patient_category='Unk' and diagnosis='TB Clinically Diagnosed' and disease_classification='Pulmonary'), 1, NULL)) as unknown_clinically_pulmonary,
    COUNT(IF((patient_category='Unk' and disease_classification='Extrapulmonary tuberculosis (EPTB)'), 1, NULL)) as unknown_eptb

from
(   

select mti.patient_id, opi.identifier, mwp.first_name, mwp.last_name, ops.program, ops.state,ops.start_date,program_state_id,  mwp.gender,
FLOOR((DATEDIFF(@endDate, mwp.birthdate))/30.4375) as age,
 #If(ops.state = "On treatment",floor(datediff(@endDate,mwp.birthdate)/@birthDateDivider),floor(datediff(ops.start_date,mwp.birthdate)/@birthDateDivider)) as age, 
 ops.location, initiation_month_smear_test, initiation_month_xpert_test, disease_classification, diagnosis, patient_category
from mw_tb_initial mti 
join mw_tb_test_results mtr on mti.patient_id=mtr.patient_id and mtr.location=mti.location
join mw_patient mwp  on mwp.patient_id=mti.patient_id
join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id
JOIN
         last_tb_facility_outcome as ops
            on opi.patient_id = ops.pat and opi.location = ops.location
            where opi.type = "TB program identifier"
and mti.visit_date between @startDate and @endDate
)sub1
 group by age_group,gender, location
 order by gender,age_group,location, state
 ) cohort on x.age_group = cohort.age_group
 and x.gender = cohort.gender
 order by sort_value;
