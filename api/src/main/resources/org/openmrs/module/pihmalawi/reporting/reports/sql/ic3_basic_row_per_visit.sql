-- ## report_uuid = f6488a88-830d-11e9-bc42-526af7764f64
-- ## design_uuid = e748905a-830d-11e9-bc42-526af7764f64
-- ## report_name = IC3 - Basic visit report
-- ## report_description = Report indicating which patients had an IC3 visit
-- ## parameter = startDate|Start Date|java.util.Date

select encounter_type_id into @checkinEnc from encounter_type where uuid = '55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b'; -- 141
select encounter_type_id into @bloodPressureScreening from encounter_type where uuid = '0C36F6FB-660E-485F-AF04-249579C9EAC9'; -- 142
select encounter_type_id into @nutritionScreening from encounter_type where uuid = '6265F6BC-EBC0-4181-91F3-28B70BBFDB61'; -- 143
select encounter_type_id into @htcScreening from encounter_type where uuid = '5B7238C1-23C6-4214-957F-7912A5BE87A9'; -- 145
select encounter_type_id into @vlScreening from encounter_type where uuid = '9959A261-2122-4AE1-A89D-1CA444B712EA'; -- 146
select encounter_type_id into @eidScreening from encounter_type where uuid = '8383DE35-5145-4953-A018-34876B797F3E'; -- 147
select encounter_type_id into @adherenceCounseling from encounter_type where uuid = '7D801495-3857-422F-BE2A-A4EEB3F36278'; -- 148
select encounter_type_id into @tbScreen from encounter_type where uuid = '45F221B9-7254-4B15-811B-5B8C8912F245'; -- 154
select encounter_type_id into @tbTestResult from encounter_type where uuid = 'C770232A-4847-42D9-8F70-B01B5BA0EED8'; -- 155
select encounter_type_id into @clinicalPlan from encounter_type where uuid = '04E668BA-E24F-43FF-A135-A085EC3DBE40'; -- 156
select patient_identifier_type_id into @arv_number from patient_identifier_type where uuid = '66784d84-977f-11e1-8993-905e29aff6c1'; -- ARV Number
select patient_identifier_type_id into @hcc_number from patient_identifier_type where uuid = '66786256-977f-11e1-8993-905e29aff6c1'; -- HCC Number
select patient_identifier_type_id into @cc_number from patient_identifier_type where uuid = '11a76c3e-1db8-4d16-9252-9a18b5ed1843'; -- Chronic Care Number
select patient_identifier_type_id into @ic3_id from patient_identifier_type where uuid = 'f51dfa3a-95de-4040-b4eb-52d2de718a74'; -- IC3 Identifier

SELECT
    arv.identifier 'ARV Number',
    hcc.identifier 'HCC Number',
    cc.identifier 'Chronic Care Number',
    ic3.identifier 'IC3 ID',
	CONCAT_WS(" ", given_name, middle_name, family_name) 'Patient names',
	p.birthdate as 'Date of birth',
	IF(p.birthdate_estimated = 1, "Yes", "No") 'Date of birth estimated',
	CAST(CONCAT(timestampdiff(YEAR, p.birthdate, NOW()), '.', MOD(timestampdiff(MONTH, p.birthdate, NOW()), 12) ) as CHAR) as Age,
	pa.state_province as Distict,
	pa.county_district AS 'Traditional Authority',
	pa.city_village as 'Village' ,
	DATE(v.date_started) 'Visit start date',
	DATE(v.date_stopped) 'Visit stop date',
    l.name 'location',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and chk.valueCoded = concept_id) 'Source of referral',
    chk_linkage.valueText 'Linkage to care ID',
    bps.valueNumeric 'Systolic blood pressure',
    bpsd.valueNumeric 'Diastolic blood pressure',
    nsw.valueNumeric 'Weight',
    nsh.valueNumeric 'Height',
    ROUND(nsw.valueNumeric/((nsh.valueNumeric/100)*(nsh.valueNumeric/100)),1) BMI,
    nsm.valueNumeric 'MUAC',
	(select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and nsp.valueCoded = concept_id) 'Patient pregnant',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = vlbled.valueCoded) 'Bled',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and vlr.valueCoded = concept_id) 'Reason for testing',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and vllab.valueCoded = concept_id) 'Location of laboratory',
    (
    SELECT IF(vlbled.valueCoded IS NOT NULL, (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = (select value_coded from obs where concept_id =
    (select concept_id from concept where UUID = "0e447d92-a180-11e8-98d0-529269fb1459") AND obs.encounter_id = vlbled.encounter_id)
    ), NULL )) 'Reason for no VL sample',
    vl.valueNumeric 'Hiv viral load',
    lessvl.valueNumeric 'Hiv Viral load less than value',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = ldlvl.valueCoded) 'Hiv Viral load lower than detection limit',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = htc.valueCoded) 'HTC Results',
    (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "FULLY_SPECIFIED" and concept_id = eid.valueCoded)  'EID Breastfeeding',
    -- (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "FULLY_SPECIFIED" and concept_id = eid_hiv_resultA.valueCoded),
    (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "SHORT" and concept_id = eid_hiv_resultB.valueCoded) 'EID HIV Test Type',
    IF(eid_hiv_resultB.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1040'),
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = eid_hiv_resultA.valueCoded),
    NULL)'EID HIV Rapid Test Results',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and eid_hiv_sample.valueCoded = concept_id) 'EID Bled',
    (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "FULLY_SPECIFIED" and eid_hiv_testres.valueCoded = concept_id)'EID Reason for testing',
    (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "FULLY_SPECIFIED" and eid_res_nosample.valueCoded = concept_id) 'EID Reason for no sample',
    (select name from concept_name where voided = 0 and locale = 'en' and concept_name_type = "FULLY_SPECIFIED" and eid_lablocation.valueCoded = concept_id) 'EID laboratory',
	IF(eid_hiv_resultB.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '844'),
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = eid_hiv_resultA.valueCoded), NULL) 'EID DNA PCR Result',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and eid_dna_nores.ValueCoded = concept_id) 'EID DNA PCR Reason for No Result',
    IF(tb_screen_sweats.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_sweats.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Night sweat',
    IF(tb_screen_fev.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_fev.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Fever',
    IF(tb_screen_cough.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_cough.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Cough',
    IF(tb_screen_wloss.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_wloss.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Weight loss',
    IF(tb_screen_active.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_active.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Recent contact with active TB',
    IF(tb_screen_lymph.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1293')
    , "Yes",
    (IF(tb_screen_lymph.conceptID = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1734')
    , "No", " "))) 'Painful cervical and axillary lymph nodes',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = tb_test_sample.valueCoded) 'Sputum received',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = tb_test_lablocation.valueCoded) 'TB Test laboratory location',
    IF(tb_test_type.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '3052'), "Smear",
	(IF(tb_test_type.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '162202'), "GeneXpert",
    " "))) 'TB Test Type',
    IF(tb_test_smearmicroscopy.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '703'), "Positive",
	(IF(tb_test_smearmicroscopy.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '664'), "Negative",
    (IF(tb_test_smearmicroscopy.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1107'), "No result",
    " "))))) 'TB Smear Result',
    IF(tb_test_generesult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '1301'), "Detected",
	(IF(tb_test_generesult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '1302'), "Undetected",
    (IF(tb_test_generesult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6112'), "No result",
    " "))))) 'GeneXpert Result',
    IF(tb_test_rifresult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '164104'), "Indeterminate",
	(IF(tb_test_rifresult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '162203'), "Positive",
    (IF(tb_test_rifresult.valueCoded = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '162204'), "Negative",
    " "))))) 'Rifampin Resistance',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = tb_test_noresultres.valueCoded) 'Reason for No TB Result',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = adh_no.valueCoded) 'Adherence Number',
    adh_support_provider.valueText 'Name of counselor',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = adh_pill_count.valueCoded) 'Counseled on pill counts',
    adh_med_percent.valueNumeric 'Drug adherence percentage (%)',
    adh_missed_dose.valueNumeric 'Missed doses in the last 7 days (%)',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = adh_vl_counseling.valueCoded) 'Counseled on viral load',
    cl_cmnts.valueText 'Clinical notes',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = cl_outcome.valueCoded) 'Clinical outcome',
    -- PUT in condition basing on cl.valueCoded
    DATE(cl_followup.valueDateTime) 'Next appointment date',
    (select name from concept_name where voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED" and concept_id = cl_daytime.valueCoded) 'AM or PM',
    cl_transferout.valueText 'Transfer Facility (Transfer out to location)',
    cl_resstopcare.valueText 'Reason to stop care',
    GROUP_CONCAT(cl_station.name) 'Refer to station'
FROM
-- Checkin
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @checkinEnc )  and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '3509')
) chk -- checkin
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_text valueText, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @checkinEnc ) and concept_id = (select concept_id from concept where uuid = 'B9E98A62-8437-4807-9DF8-37F0046FD0E8')
) chk_linkage -- Linkage
ON chk_linkage.personID = chk.personID AND chk_linkage.obsDate = chk.obsDate
-- Blood Pressure Screening
LEFT JOIN
(
select
personID, encounterID, obsDate, valueNumeric
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @bloodPressureScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5085'
)) bps where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @bloodPressureScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5085'
) group by personID, obsDate) bps
)
) bps -- Blood pressure screening
ON bps.personID = chk.personID AND chk.obsDate = bps.obsDate
LEFT JOIN
(
select
personID, encounterID, obsDate, valueNumeric
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @bloodPressureScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5086'
)) bpsd where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @bloodPressureScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5086'
) group by personID, obsDate) bpsd
)
)  bpsd -- Blood pressure screening
ON bpsd.personID = chk.personID AND chk.obsDate = bpsd.obsDate
-- Nutrition Screening
LEFT JOIN
(
select
personID, encounterID, obsDate, valueNumeric
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5089'
)) nsw where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5089'
) group by personID, obsDate) nsw
)
) nsw -- Nutrition screening, Weight
ON nsw.personID = chk.personID AND nsw.obsDate = chk.obsDate
LEFT JOIN
(
select
personID, encounterID, obsDate, valueNumeric
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5090'
)) nsh where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5090'
) group by personID, obsDate) nsh
)
) nsh -- Nutrition screening, Height
ON nsh.personID = chk.personID AND nsh.obsDate = chk.obsDate
LEFT JOIN
(
select
personID, encounterID, obsDate, valueNumeric
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1290'
)) nsm where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1290'
) group by personID, obsDate) nsm
)
) nsm -- Nutrition screening, MUAC
ON nsm.personID = chk.personID AND nsm.obsDate = chk.obsDate
LEFT JOIN
(
select
personID, encounterID, obsDate, valueCoded
from
(
select person_id personID, encounter_id encounterID, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6131'
)) nsp where encounterID IN (
select
encounterID
from
(
select person_id personID, max(encounter_id) encounterID, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6131'
) group by personID, obsDate) nsp
)
) nsp -- Nutrition screening, Pregnant
ON nsp.personID = chk.personID AND nsp.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @nutritionScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6131')
) nsd -- Nutrition screening, Diabetes screening recommended
ON nsd.personID = chk.personID AND nsd.obsDate = chk.obsDate
-- Viral Load Screening
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '856')
) vl ON -- Hiv viral load
vl.personID = chk.personID AND vl.obsDate=chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_numeric valueNumeric from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from concept where UUID = "69e87644-5562-11e9-8647-d663bd873d93")
) lessvl -- Hiv viral load Less than limit
ON lessvl.personID = chk.personID AND lessvl.obsDate=chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from concept where UUID = "e97b36a2-16f5-11e6-b6ba-3e1d05defe78")
) ldlvl -- Hiv viral Lower than Detection Limit
ON ldlvl.personID = chk.personID AND ldlvl.obsDate=chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '8421')
) vlbled ON -- Hiv bled
vlbled.personID = chk.personID AND vlbled.obsDate=chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from concept where UUID = "164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
) vlr -- Hiv Reason for testing (coded)
ON vlr.personID = chk.personID AND vlr.obsDate=chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @vlScreening) and concept_id = (select concept_id from concept where UUID = "6fc0ab50-9492-11e7-abc4-cec278b6b50a")
) vllab -- Hiv vl Location of laboratory
ON vllab.personID = chk.personID AND vllab.obsDate=chk.obsDate
-- HTC Screening
LEFT JOIN
(
select person_id personID, encounter_id, Date(obs_datetime) obsDate, value_coded valueCoded from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @htcScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '2169')
) htc ON -- HTC
htc.personID = chk.personID AND htc.obsDate=chk.obsDate
-- TB Screening
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6029')
) tb_screen_sweats -- Night sweats
ON tb_screen_sweats.personID = chk.personID AND tb_screen_sweats.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from concept where uuid = 'a6c1cd1c-b4a2-405a-930c-f11c914d50c5')
) tb_screen_active -- Active TB
ON tb_screen_active.personID = chk.personID AND tb_screen_active.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from concept where uuid = '974d5caf-2db6-4d5d-b509-11c6f5340ea5')
) tb_screen_lymph -- Painful cervical and axillary lymph nodes
ON tb_screen_lymph.personID = chk.personID AND tb_screen_lymph.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '832')
) tb_screen_wloss -- Weight loss
ON tb_screen_wloss.personID = chk.personID AND tb_screen_wloss.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '107')
) tb_screen_cough -- Cough
ON tb_screen_cough.personID = chk.personID AND tb_screen_cough.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbScreen) and value_coded = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5945')
) tb_screen_fev -- Painful cervical and axillary lymph nodes
ON tb_screen_fev.personID = chk.personID AND tb_screen_fev.obsDate = chk.obsDate
-- TB Test Results
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '165252')
) tb_test_sample -- Sample collected
ON tb_test_sample.personID = chk.personID AND tb_test_sample.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from concept where uuid = '6fc0ab50-9492-11e7-abc4-cec278b6b50a')
) tb_test_lablocation -- Location of laboratory
ON tb_test_lablocation.personID = chk.personID AND tb_test_lablocation.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '165254')
) tb_test_type -- Tuberculosis smear microscopy method
ON tb_test_type.personID = chk.personID AND tb_test_type.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '3052')
) tb_test_smearmicroscopy -- Tuberculosis smear microscopy method
ON tb_test_smearmicroscopy.personID = chk.personID AND tb_test_smearmicroscopy.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '162202')
) tb_test_generesult -- Tuberculosis smear microscopy method
ON tb_test_generesult.personID = chk.personID AND tb_test_generesult.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '164937')
) tb_test_rifresult -- Rifampin Resistance
ON tb_test_rifresult.personID = chk.personID AND tb_test_rifresult.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @tbTestResult) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6112')
) tb_test_noresultres -- Reaseon for no result
ON tb_test_noresultres.personID = chk.personID AND tb_test_noresultres.obsDate = chk.obsDate
-- Adherence Counseling
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from concept where uuid = '06b1f7d8-b6cc-11e8-96f8-529269fb1459')
) adh_no -- Adherence number
ON adh_no.personID = chk.personID AND adh_no.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_text valueText, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '3098')
) adh_support_provider -- Name of support provider
ON adh_support_provider.personID = chk.personID AND adh_support_provider.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from concept where uuid = '06b2005c-b6cc-11e8-96f8-529269fb1459')
) adh_pill_count -- Name of support provider
ON adh_pill_count.personID = chk.personID AND adh_pill_count.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_numeric valueNumeric, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from concept where uuid = '20E91F16-BA4F-4058-B17A-998A82F4B803')
) adh_med_percent -- Medication Adherence percent
ON adh_med_percent.personID = chk.personID AND adh_med_percent.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_numeric valueNumeric, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from concept where uuid = '290c1601-a1a7-4a4c-8dc7-d18a17f059a2')
) adh_missed_dose -- Missed dose
ON adh_missed_dose.personID = chk.personID AND adh_missed_dose.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @adherenceCounseling) and concept_id = (select concept_id from concept where uuid = '06b20a2a-b6cc-11e8-96f8-529269fb1459')
) adh_vl_counseling -- VL Counseling
ON adh_vl_counseling.personID = chk.personID AND adh_vl_counseling.obsDate = chk.obsDate
-- Clinical Counseling
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_text valueText, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1364')
) cl_cmnts -- Clinical impression comments
ON cl_cmnts.personID = chk.personID AND cl_cmnts.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6538')
) cl_outcome -- Clinical outcomes
ON cl_outcome.personID = chk.personID AND cl_outcome.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_datetime valueDateTime, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '5096')
) cl_followup -- Clinical followup
ON cl_followup.personID = chk.personID AND cl_followup.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from concept where uuid = '4c923fca-28d6-11e9-b210-d663bd873d93')
) cl_daytime -- Qualitative time
ON cl_daytime.personID = chk.personID AND cl_daytime.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_text valueText, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '3003')
) cl_transferout -- Transfer out to
ON cl_transferout.personID = chk.personID AND cl_transferout.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_text valueText, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and concept_id = (select concept_id from concept where uuid = '558a783a-2990-11e9-b210-d663bd873d93')
) cl_resstopcare -- Reason to exit care
ON cl_resstopcare.personID = chk.personID AND cl_resstopcare.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, name, DATE(obs_datetime) obsDate, value_coded valueCoded, o.concept_id conceptID, obs_group_id obsGID from obs o join concept_name cn on
cn.concept_id = value_coded and cn.voided = 0 and locale = "en" and concept_name_type = "FULLY_SPECIFIED"
and  o.voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @clinicalPlan) and o.concept_id = (select concept_id from concept where uuid = '191b3cdc-ec5b-4447-aeb5-4c985e336779')
) cl_station -- Refer to screening station
ON cl_station.personID = chk.personID AND cl_station.obsDate = chk.obsDate
-- EID
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '8039')
) eid -- Breastfeeding
ON eid.personID = chk.personID AND eid.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '2169')
) eid_hiv_resultA -- eid HIV Test Result
ON eid_hiv_resultA.personID = chk.personID AND eid_hiv_resultA.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '1867')
) eid_hiv_resultB -- eid HIV Test Result
ON eid_hiv_resultB.personID = chk.personID AND eid_hiv_resultB.obsDate = chk.obsDate

-- EID bled
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'CIEL' and rm.code = '165252')
) eid_hiv_sample -- Sample collected
ON eid_hiv_sample.personID = chk.personID AND eid_hiv_sample.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from concept where uuid = '164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA')
) eid_hiv_testres -- Testing Reason
ON eid_hiv_testres.personID = chk.personID AND eid_hiv_testres.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from concept where uuid = "0e447d92-a180-11e8-98d0-529269fb1459")
) eid_res_nosample -- Sample collected
ON eid_res_nosample.personID = chk.personID AND eid_res_nosample.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from concept where uuid = '6fc0ab50-9492-11e7-abc4-cec278b6b50a')
) eid_lablocation -- EID laboratory
ON eid_lablocation.personID = chk.personID AND eid_lablocation.obsDate = chk.obsDate
LEFT JOIN
(
select person_id personID, encounter_id, DATE(obs_datetime) obsDate, value_coded valueCoded, concept_id conceptID, obs_group_id obsGID from obs o where voided = 0 and encounter_id IN (select encounter_id from encounter
where voided = 0 and encounter_type = @eidScreening) and concept_id = (select concept_id from report_mapping rm where rm.source = 'PIH Malawi' and rm.code = '6112')
) eid_dna_nores -- EID DNA Reason for No Result
ON eid_dna_nores.personID = chk.personID AND eid_dna_nores.obsDate = chk.obsDate
JOIN
encounter e on chk.encounter_id = e.encounter_id and e.voided = 0 and chk.personID = e.patient_id
JOIN
visit v ON v.patient_id = e.patient_id and e.visit_id = v.visit_id
JOIN
location l ON e.location_id = l.location_id
JOIN
person p on p.person_id = e.patient_id and p.voided = 0
JOIN
person_name pn ON e.patient_id = pn.person_id and pn.voided = 0
LEFT JOIN
(select patient_id, identifier, max(date_created) from patient_identifier pi where pi.voided = 0 and identifier_type = @arv_number group by patient_id) arv ON e.patient_id = arv.patient_id
LEFT JOIN
(select patient_id, identifier, max(date_created) from patient_identifier pi where pi.voided = 0 and identifier_type = @hcc_number group by patient_id) hcc ON e.patient_id = hcc.patient_id
LEFT JOIN
(select patient_id, identifier, max(date_created) from patient_identifier pi where pi.voided = 0 and identifier_type = @cc_number group by patient_id) cc ON e.patient_id = cc.patient_id
LEFT JOIN
(select patient_id, identifier, max(date_created) from patient_identifier pi where pi.voided = 0 and identifier_type = @ic3_id group by patient_id) ic3 ON e.patient_id = ic3.patient_id
LEFT JOIN
person_address pa on pa.person_id = e.patient_id and pa.voided = 0
where v.date_started >= @startDate
group by chk.encounter_id;
