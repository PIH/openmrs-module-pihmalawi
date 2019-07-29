-- ## report_uuid = f6488a88-830d-11e9-bc42-526af7764f64
-- ## design_uuid = e748905a-830d-11e9-bc42-526af7764f64
-- ## report_name = IC3 - Basic visit report
-- ## report_description = Report indicating which patients had an IC3 visit
-- ## parameter = startDate|Start Date|java.util.Date
-- ## parameter = endDate|End Date|java.util.Date

select
e.patient_id,
   pi_hiv.identifier 'ARV Number',
   pi_hcc.identifier 'HCC Number',
   pi_ccc.identifier 'Chronic Care Number',
   pi_ic3.identifier 'IC3 ID',
	CONCAT_WS(" ", given_name, middle_name, family_name) 'Patient name',
	p.birthdate as 'Date of birth',
	IF(p.birthdate_estimated = 1, "Yes", "No") 'Date of birth estimated',
	CAST(CONCAT(timestampdiff(YEAR, p.birthdate, NOW()), '.', MOD(timestampdiff(MONTH, p.birthdate, NOW()), 12) ) as CHAR) as Age,
	pa.state_province as Distict,
	pa.county_district AS 'Traditional Authority',
	pa.city_village as 'Village' ,
 	DATE(v.date_started) 'Visit start date',
	DATE(v.date_stopped) 'Visit stop date',
  l.name 'Encounter location',
-- The below code works by retrieving all of the observations for the specified encounter types in IC3 for any visit within the start and end date of the report
-- The aggregate functions below transpose the rows into columns based on the concepts for the observations
-- External mappings are used when they exist, otherwide the UUID is used
-- The rows are grouped by visit id so there will be one row per visit
-- check-in information:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '3509' then cna.name end) "Referred from",
max(CASE when cq.UUID = 'B9E98A62-8437-4807-9DF8-37F0046FD0E8' then o.value_text end) "Linkage to care ID",
max(CASE when cq.UUID = 'd0d91980-6788-4325-80d3-3bd7b54e705a' then cna.name end) "Person at Visit",
-- blood pressure information:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5085' then o.value_numeric end) "Systolic blood pressure",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5086' then o.value_numeric end) "Diastolic blood pressure",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5087' then o.value_numeric end) "Pulse",
-- nutrition information:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5089' then o.value_numeric end) "Weight",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5090' then o.value_numeric end) "Height",
-- BMI below: weight/(height/100) * (height/100)
ROUND(max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5089' then o.value_numeric end)/((max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5090' then o.value_numeric end)/100)*(max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5090' then o.value_numeric end)/100)),1) "BMI",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1290' then o.value_numeric end) "MUAC",
-- Viral Load results:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '8421' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then cna.name end) "VL Bled",
max(CASE when cq.UUID = '164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then cna.name end) "Reason for VL Testing",
max(CASE when cq.UUID = '6fc0ab50-9492-11e7-abc4-cec278b6b50a' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then cna.name end) "Location of laboratory for VL",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '6112' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then cna.name end) "Reason for no VL sample",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '856'  and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then o.value_numeric end) "HIV viral load",
max(CASE when cq.UUID = '69e87644-5562-11e9-8647-d663bd873d93' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then o.value_numeric end) "HIV Viral load less than value",
max(CASE when cq.UUID = 'e97b36a2-16f5-11e6-b6ba-3e1d05defe78' and cg.UUID = '83931c6d-0e5a-4302-b8ce-a31175b6475e' then cna.name end) "HIV Viral load lower than detection limit",
-- HTC information:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '2169'  and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "HTC Results",
-- EID information:
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '8039'  and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID Breastfeeding",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1867'  and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID HIV Test Type",
max(CASE when rmq.source = 'CIEL' and rmq.code = '165252'  and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID Bled",
max(CASE when cq.UUID = '0e447d92-a180-11e8-98d0-529269fb1459' and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID Reason for no sample",
max(CASE when cq.UUID = '164126AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID Reason for testing",
max(CASE when cq.UUID = '6fc0ab50-9492-11e7-abc4-cec278b6b50a' and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID laboratory",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '2169' and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID DNA PCR Result",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '6112'  and rmg.source = 'PIH Malawi' and rmg.code = '2168' then cna.name end) "EID DNA PCR Reason for No Result",
-- TB Symptons - these are a little different because the questions of the obs are symptoms present/absent symptoms and the symptoms are the answers
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and rma.source = 'PIH Malawi' and rma.code = '107' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and rma.source = 'PIH Malawi' and rma.code = '107' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Cough",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and rma.source = 'PIH Malawi' and rma.code = '5945' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and rma.source = 'PIH Malawi' and rma.code = '5945' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Fever",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and rma.source = 'PIH Malawi' and rma.code = '6029' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and rma.source = 'PIH Malawi' and rma.code = '6029' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Night Sweats",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and rma.source = 'PIH Malawi' and rma.code = '832' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and rma.source = 'PIH Malawi' and rma.code = '832' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Weight Loss",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and ca.UUID = 'a6c1cd1c-b4a2-405a-930c-f11c914d50c5' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and ca.UUID = 'a6c1cd1c-b4a2-405a-930c-f11c914d50c5' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Recent contact with TB",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1293' and ca.UUID = '974d5caf-2db6-4d5d-b509-11c6f5340ea5' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'Yes'
         when rmq.source = 'PIH Malawi' and rmq.code = '1734' and ca.UUID = '974d5caf-2db6-4d5d-b509-11c6f5340ea5' and cg.UUID = '6000c2f8-4eb5-4fd9-ac83-a9a9d6bd8478' then 'No' end) "Painful Neck/Armpit nodes",
-- TB Test Results information:
max(CASE when cq.UUID = '165252AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "Sputum Received",
max(CASE when cq.UUID = '6fc0ab50-9492-11e7-abc4-cec278b6b50a' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Lab Location",
max(CASE when rmq.source = 'CIEL' and rmq.code = '165254' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Test Type",
max(CASE when rmq.source = 'CIEL' and rmq.code = '162202' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Gene Expert Result",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '3052' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Smears Result",
max(CASE when rmq.source = 'CIEL' and rmq.code = '164937' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "Rifampin Resistance",
max(CASE when cq.UUID = '0e447d92-a180-11e8-98d0-529269fb1459' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Reason for no Sample",
max(CASE when cq.UUID = '3ee10e6b-ee68-4238-99ee-1f132551e70e' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Recommended Steps",
max(CASE when cq.UUID = '0e447d92-a180-11e8-98d0-529269fb1459' and cg.UUID = '4c92373c-28d6-11e9-b210-d663bd873d93' then cna.name end) "TB Reason for No Sample",
-- adherence counseling information:
max(CASE when cq.UUID = '06b1f7d8-b6cc-11e8-96f8-529269fb1459' then cna.name end) "Adherence Session Number",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '3098' then o.value_text end) "Counselor Name",
max(CASE when cq.UUID = '06b1f7d8-b6cc-11e8-96f8-529269fb1459' then cna.name end) "Adherence Session Number",
max(CASE when cq.UUID = '06b2005c-b6cc-11e8-96f8-529269fb1459' then cna.name end) "Counseled on Pill Counts",
max(CASE when cq.UUID = '20E91F16-BA4F-4058-B17A-998A82F4B803' then o.value_numeric end) "Drug adherence percentage",
max(CASE when cq.UUID = '290c1601-a1a7-4a4c-8dc7-d18a17f059a2' then o.value_numeric end) "Missed doses in the last 7 days",
max(CASE when cq.UUID = '06b20a2a-b6cc-11e8-96f8-529269fb1459' then cna.name end) "Counseled on Viral Load",
-- diabetes station information
max(CASE when cq.UUID = '65711e3e-977f-11e1-8993-905e29aff6c1' and rmg.source = 'PIH Malawi' and rmg.code = '6382' then cna.name end) "Blood Sugar Test Type",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '887' and rmg.source = 'PIH Malawi' and rmg.code = '6382' then o.value_numeric end) "Blood Sugar Level",
-- cervical cancer screening
max(CASE when cq.UUID = '162816AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' and cg.UUID = '4508D9EC-1355-461A-AB1D-74CF5A9C6F6F' then cna.name end) "Cervical Cancer Screening Result",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1364' and cg.UUID = '4508D9EC-1355-461A-AB1D-74CF5A9C6F6F' then o.value_text end) "Other gyn notes",
max(CASE when cq.UUID = '1BCB4919-3FD2-4A2F-8F60-684CA797E0A2' and cg.UUID = '4508D9EC-1355-461A-AB1D-74CF5A9C6F6F' then cna.name end) "Biopsy Done",
-- lab Test Station
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '6422' then o.value_numeric end) "HbA1c",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '6445' then o.value_numeric end) "Creatinine",
-- clinician station information:  note that clinician station is not currently using an obs group
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '1364' and cg.UUID is null then o.value_text end) "Clinical Notes",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '6538' then cna.name end) "Clinical Outcome",
max(CASE when rmq.source = 'PIH Malawi' and rmq.code = '5096' then date_format(o.value_datetime,"%d-%m-%Y") end) "Next appointment date",
max(CASE when cq.UUID = '4c923fca-28d6-11e9-b210-d663bd873d93' then cna.name end) "Next appointment time",
group_concat(CASE when cq.UUID = '191b3cdc-ec5b-4447-aeb5-4c985e336779' then cna.name end) "Referred to Station"
from visit v
INNER JOIN encounter e on e.visit_id = v.visit_id and v.voided = 0
   and e.encounter_type in (select et.encounter_type_id from encounter_type et where et.uuid in
    ('55a0d3ea-a4d7-4e88-8f01-5aceb2d3c61b',  -- check in
    '0C36F6FB-660E-485F-AF04-249579C9EAC9',   -- bp screening
    '6265F6BC-EBC0-4181-91F3-28B70BBFDB61',   -- nutrition screening
    '5B7238C1-23C6-4214-957F-7912A5BE87A9',   -- htc testing
    '9959A261-2122-4AE1-A89D-1CA444B712EA',   -- vl testing
    '8383DE35-5145-4953-A018-34876B797F3E',   -- EID testing
    '7D801495-3857-422F-BE2A-A4EEB3F36278',   -- adherence counseling
    '45F221B9-7254-4B15-811B-5B8C8912F245',   -- TB symptoms
    'C770232A-4847-42D9-8F70-B01B5BA0EED8',   -- TB testing
    '04E668BA-E24F-43FF-A135-A085EC3DBE40',   -- clinical plan
    'AA2C2B86-1A59-49A3-905B-41D318E94FFE',   -- cervical cancer screening
    'D8D67095-0AAC-4B61-87B3-A2B32B7E1FEE',   -- blood sugar testing
    '6D29EEC4-6FEE-497C-9352-CA8081543FD6'))   -- lab testing station
INNER JOIN obs o on o.encounter_id = e.encounter_id and o.voided = 0
LEFT OUTER JOIN report_mapping rmq on rmq.concept_id = o.concept_id
LEFT OUTER JOIN report_mapping rma on rma.concept_id = o.value_coded
INNER JOIN concept cq on o.concept_id = cq.concept_id and cq.retired = 0
LEFT OUTER JOIN concept ca on o.value_coded = ca.concept_id and ca.retired = 0
LEFT OUTER JOIN concept_name cna on cna.concept_name_id = (select concept_name_id from concept_name cn where cn.concept_id = o.value_coded and cn.voided = 0 and cn.locale = 'en' order by cn.locale_preferred desc limit 1)
-- pulling in information about obs group
LEFT OUTER JOIN obs og on og.obs_id = o.obs_group_id and og.voided = 0
LEFT OUTER JOIN report_mapping rmg on rmg.concept_id = og.concept_id
LEFT OUTER JOIN concept cg on cg.concept_id = og.concept_id and cg.retired = 0
-- end clinical data joins
-- Identifier information:
LEFT OUTER JOIN patient_identifier pi_hiv on pi_hiv.patient_identifier_id =
   (select patient_identifier_id from patient_identifier pid
    where pid.patient_id = e.patient_id
    and pid.voided = 0
    and pid.identifier_type =
    (select  patient_identifier_type_id from patient_identifier_type where uuid = '66784d84-977f-11e1-8993-905e29aff6c1')
    order by pid.preferred desc, pid.date_created desc
    limit 1)
LEFT OUTER JOIN patient_identifier pi_hcc on pi_hcc.patient_identifier_id =
   (select patient_identifier_id from patient_identifier pid
    where pid.patient_id = e.patient_id
    and pid.voided = 0
    and pid.identifier_type =
    (select  patient_identifier_type_id from patient_identifier_type where uuid = '66786256-977f-11e1-8993-905e29aff6c1')
    order by pid.preferred desc, pid.date_created desc
    limit 1)
LEFT OUTER JOIN patient_identifier pi_ccc on pi_ccc.patient_identifier_id =
   (select patient_identifier_id from patient_identifier pid
    where pid.patient_id = e.patient_id
    and pid.voided = 0
    and pid.identifier_type =
    (select  patient_identifier_type_id from patient_identifier_type where uuid = '11a76c3e-1db8-4d16-9252-9a18b5ed1843')
    order by pid.preferred desc, pid.date_created desc
    limit 1)
LEFT OUTER JOIN patient_identifier pi_ic3 on pi_ic3.patient_identifier_id =
   (select patient_identifier_id from patient_identifier pid
    where pid.patient_id = e.patient_id
    and pid.voided = 0
    and pid.identifier_type =
    (select  patient_identifier_type_id from patient_identifier_type where uuid = 'f51dfa3a-95de-4040-b4eb-52d2de718a74')
    order by pid.preferred desc, pid.date_created desc
    limit 1)
INNER JOIN person p on p.person_id = e.patient_id and p.voided = 0
INNER JOIN person_name pn ON pn.person_name_id =
        (select person_name_id from person_name pn2 where e.patient_id = pn2.person_id and pn2.voided = 0 order by pn2.preferred desc, pn2.date_created desc limit 1)
INNER JOIN person_address pa on pa.person_address_id =
        (select person_address_id from person_address pa2 where e.patient_id = pa2.person_id and pa2.voided = 0 order by pa2.preferred desc, pa2.date_created desc limit 1)
INNER JOIN location l ON e.location_id = l.location_id
where v.date_started >= @startDate
and v.date_started <= @endDate
group by v.visit_id
order by v.date_started
;
