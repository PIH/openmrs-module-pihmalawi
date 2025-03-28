
set @ncd_other_initial = (select encounter_type_id from encounter_type where uuid =  'b562295c-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_initial = (select encounter_type_id from encounter_type where uuid =  '56C2D952-DB11-4B47-B248-79C1B2A88E88');
set @cc_dg_cstr = (select concept_id from concept where uuid = '6db168f1-0f38-42d9-9f0e-90946a3d8e72');
set @cc_dx_id = (select concept_id from concept where uuid = '65671c9a-977f-11e1-8993-905e29aff6c1');
set @sickle_cell_disease = (select concept_id from concept where uuid = '65774b06-977f-11e1-8993-905e29aff6c1');
set @sickle_cell_form_id = (select form_id from form where uuid='7AFEC71B-15D3-4E2D-8C42-D8CB2B75BC54');
set @phone_number = (select concept_id from concept where uuid = '6559ba14-977f-11e1-8993-905e29aff6c1');
set @guardian = (select concept_id from concept where uuid = '655bbc74-977f-11e1-8993-905e29aff6c1');
set @next_of_kin_phone = (select concept_id from concept where uuid = '65600cd4-977f-11e1-8993-905e29aff6c1');
set @relationship = (select concept_id from concept where uuid = '6558fd0e-977f-11e1-8993-905e29aff6c1');
set @fup = (select concept_id from concept where uuid = '655fc526-977f-11e1-8993-905e29aff6c1');
set @hiv_status = (select concept_id from concept where uuid = '6567ae62-977f-11e1-8993-905e29aff6c1');
set @hiv_test_date = (select concept_id from concept where uuid = '655bc9da-977f-11e1-8993-905e29aff6c1');
set @art_start_date = (select concept_id from concept where uuid = '655f9eb6-977f-11e1-8993-905e29aff6c1');

set @ncd_other_followup = (select encounter_type_id from encounter_type where uuid =  'b5622bf0-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_followup = (select encounter_type_id from encounter_type where uuid = 'D4073EB7-60B1-4586-B062-13FCE4CBC9E8');
set @ncd_other_qs = (select encounter_type_id from encounter_type where uuid =  'b5622d4e-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_qs = (select encounter_type_id from encounter_type where uuid = '119A7061-ABC7-4759-88BF-086ADC8EBB80');
set @sickle_cell_visit_form_id = (select form_id from form where uuid='E68275D4-C300-46B0-8754-4C2CF2598B78');

set @ncd_other_as = (select encounter_type_id from encounter_type where uuid =  'b5623082-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_as = (select encounter_type_id from encounter_type where uuid = '0C0CFAD6-0F92-4B19-8B6A-675005BE7F86');
set @sickle_cell_as_form_id = (select form_id from form where uuid='37744503-6E86-4F2C-9941-F9944AB6F69D');
set @ncd_other_hosp = (select encounter_type_id from encounter_type where uuid =  'b56231e0-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_hosp = (select encounter_type_id from encounter_type where uuid = 'AD864EB5-7042-4080-AE81-E0A4CF8CED43');
set @sickle_cell_hosp_form_id = (select form_id from form where uuid='9FD4B1D3-FFFB-4A4A-B626-976BE1EE0519');

set @sickle_cell_qs_form_id = (select form_id from form where uuid='66CB607A-85A3-4939-897E-8B3473ABE674');
set @height = (select concept_id from concept where uuid = '6569c562-977f-11e1-8993-905e29aff6c1');
set @weight = (select concept_id from concept where uuid = '6569c44a-977f-11e1-8993-905e29aff6c1');
set @systolicBP = (select concept_id from concept where uuid = '6569bffe-977f-11e1-8993-905e29aff6c1');
set @diastolicBP = (select concept_id from concept where uuid = '6569c116-977f-11e1-8993-905e29aff6c1');
set @heartRate = (select concept_id from concept where uuid = '6569c224-977f-11e1-8993-905e29aff6c1');
set @bloodOxygenSaturation = (select concept_id from concept where uuid = '6569c792-977f-11e1-8993-905e29aff6c1');
set @$hospitalSince =  (select concept_id from concept where uuid = '655b3dc6-977f-11e1-8993-905e29aff6c1');
set @creatinine =  (select concept_id from concept where uuid = '657170a0-977f-11e1-8993-905e29aff6c1');
set @hospitalDays =  (select concept_id from concept where uuid = '656170f6-977f-11e1-8993-905e29aff6c1');
set @admissionReason =  (select concept_id from concept where uuid = '162879AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA');
set @dischargeDiagnosis =  (select concept_id from concept where uuid = '0ed9abe4-b982-11e5-9912-ba0be0483c18');
set @dischargeMeds =  (select concept_id from concept where uuid = '316bc014-ba1f-11e5-9912-ba0be0483c18');

drop table if exists sickle_cell_initial_enc;

create table sickle_cell_initial_enc (
         encounter_id int PRIMARY KEY AUTO_INCREMENT,
         source_encounter_id int,
         source_encounter_type int,
         source_obs_group_id int,
         patient_id int,
         location_id int,
         encounter_datetime DATETIME,
         source_form_id int,
         creator int,
         date_created DATETIME,
         voided tinyint(1)
);

set @max_enc=(select max(encounter_id) + 1 from encounter);
SET @stmt = CONCAT('ALTER TABLE sickle_cell_initial_enc AUTO_INCREMENT=', @max_enc);
PREPARE yourStatementName FROM @stmt;
execute yourStatementName;

-- migrate only the patients who don't already have a SICKLE_CELL_DISEASE_INITIAL encounter
insert into sickle_cell_initial_enc(
    source_encounter_id, source_encounter_type, source_obs_group_id, patient_id, location_id, encounter_datetime, source_form_id, creator, date_created, voided)
select e.encounter_id, e.encounter_type, o.obs_group_id, e.patient_id, e.location_id, e.encounter_datetime, e.form_id,e.creator,  e.date_created, e.voided
from encounter e,  obs o
where e.encounter_type = @ncd_other_initial and e.encounter_id = o.encounter_id
  and e.voided = 0 and o.voided = 0
  and o.concept_id = @cc_dx_id and o.value_coded = @sickle_cell_disease
  and e.patient_id not in (select patient_id from encounter where encounter_type=@sickle_cell_initial and voided=0);

drop table if exists sickle_cell_disease_obs;

create table sickle_cell_disease_obs (
         obs_id int PRIMARY KEY AUTO_INCREMENT,
         source_encounter_id int,
         source_person_id int,
         source_obs_id int,
         source_obs_group_id int,
         source_obs_datetime DATETIME,
         source_location_id int,
         source_concept_id int,
         source_value_coded int,
         source_value_datetime DATETIME,
         source_value_numeric DOUBLE,
         source_value_text text,
         source_creator int,
         source_date_created DATETIME,
         source_voided tinyint(1) NOT NULL DEFAULT '0'
);

set @max_obs=(select max(obs_id) + 1 from obs);
SET @stmt = CONCAT('ALTER TABLE sickle_cell_disease_obs AUTO_INCREMENT=', @max_obs);
PREPARE yourStatementName FROM @stmt;
execute yourStatementName;

-- add the Diagnosis Construct obs
insert into sickle_cell_disease_obs(
     source_encounter_id, source_person_id, source_obs_id, source_obs_group_id, source_obs_datetime, source_location_id, source_concept_id
   , source_value_coded, source_creator, source_date_created, source_voided)
select
    o.encounter_id,
    o.person_id,
    o.obs_id,
    o.obs_group_id,
    o.obs_datetime,
    o.location_id,
    o.concept_id,
    o.value_coded,
    o.creator,
    o.date_created,
    o.voided
from obs o
         inner join sickle_cell_initial_enc e on o.obs_id = e.source_obs_group_id;

-- add Dx Construct members: the coded diagnosis and the diagnosis date
insert into sickle_cell_disease_obs(
    source_encounter_id,
    source_person_id,
    source_obs_id,
    source_obs_group_id,
    source_obs_datetime,
    source_location_id,
    source_concept_id,
    source_value_coded,
    source_value_datetime,
    source_creator,
    source_date_created,
    source_voided)
select
    o.encounter_id,
    o.person_id,
    o.obs_id,
    o.obs_group_id,
    o.obs_datetime,
    o.location_id,
    o.concept_id,
    o.value_coded,
    o.value_datetime,
    o.creator,
    o.date_created,
    o.voided
from obs o, sickle_cell_initial_enc e
where o.encounter_id = e.source_encounter_id and o.obs_group_id = e.source_obs_group_id;

-- add all other simple obs from the NCD_OTHER_INITIAL
insert into sickle_cell_disease_obs(
    source_encounter_id,
    source_person_id,
    source_obs_id,
    source_obs_group_id,
    source_obs_datetime,
    source_location_id,
    source_concept_id,
    source_value_coded,
    source_value_datetime,
    source_value_numeric,
    source_value_text,
    source_creator,
    source_date_created,
    source_voided)
select
    o.encounter_id,
    o.person_id,
    o.obs_id,
    o.obs_group_id,
    o.obs_datetime,
    o.location_id,
    o.concept_id,
    o.value_coded,
    o.value_datetime,
    o.value_numeric,
    o.value_text,
    o.creator,
    o.date_created,
    o.voided
from obs o, sickle_cell_initial_enc e
where o.encounter_id = e.source_encounter_id
  and o.concept_id in (@phone_number,  @guardian, @next_of_kin_phone, @relationship, @fup, @hiv_status,@hiv_test_date,@art_start_date)
  and o.voided=0;

-- Create SICKLE_CELL_DISEASE_INITIAL encounters
insert into encounter(
    encounter_id,
    uuid,
    encounter_datetime,
    date_created,
    encounter_type,
    form_id,
    patient_id,
    creator,
    location_id)
select
    e.encounter_id,
    uuid(),
    e.encounter_datetime,
    now(),
    @sickle_cell_initial,
    @sickle_cell_form_id,
    e.patient_id,
    e.creator,
    e.location_id
from sickle_cell_initial_enc e;

-- insert Diagnosis Cosntruct obs
insert into obs(
    obs_id,
    person_id,
    encounter_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    creator,
    date_created,
    voided,
    uuid,
    comments)
select
    o.obs_id,
    o.source_person_id,
    e.encounter_id,
    o.source_obs_datetime,
    o.source_location_id,
    o.source_concept_id,
    o.source_value_coded,
    o.source_creator,
    now(),
    o.source_voided,
    uuid(),
    'migrated from NCD_OTHER_INITIAL encounter'
from sickle_cell_disease_obs o, sickle_cell_initial_enc e
where o.source_encounter_id = e.source_encounter_id and o.source_concept_id=@cc_dg_cstr and o.source_obs_group_id is null;

-- insert Diagnosis Cosntruct member obs: the diagnosis and the diagnosis date
insert into obs(
    obs_id,
    person_id,
    encounter_id,
    obs_group_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    value_datetime,
    creator,
    date_created,
    voided,
    uuid,
    comments)
select
    o.obs_id,
    o.source_person_id,
    e.encounter_id,
    g.obs_id,
    o.source_obs_datetime,
    o.source_location_id,
    o.source_concept_id,
    o.source_value_coded,
    o.source_value_datetime,
    o.source_creator,
    now(),
    o.source_voided,
    uuid(),
    'migrated from NCD_OTHER_INITIAL encounter'
from sickle_cell_disease_obs o, sickle_cell_initial_enc e, sickle_cell_disease_obs g
where o.source_encounter_id = e.source_encounter_id and o.source_obs_group_id is not null
  and o.source_obs_group_id = g.source_obs_id and g.source_concept_id=@cc_dg_cstr;

-- add all other simple top level obs
insert into obs(
    obs_id,
    person_id,
    encounter_id,
    obs_group_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    value_datetime,
    value_numeric,
    value_text,
    creator,
    date_created,
    voided,
    uuid,
    comments)
select
    o.obs_id,
    o.source_person_id,
    e.encounter_id,
    o.source_obs_group_id,
    o.source_obs_datetime,
    o.source_location_id,
    o.source_concept_id,
    o.source_value_coded,
    o.source_value_datetime,
    o.source_value_numeric,
    o.source_value_text,
    o.source_creator,
    now(),
    o.source_voided,
    uuid(),
    'migrated from NCD_OTHER_INITIAL encounter'
from sickle_cell_disease_obs o, sickle_cell_initial_enc e
where o.source_encounter_id = e.source_encounter_id
  and o.source_concept_id in (@phone_number,  @guardian, @next_of_kin_phone, @relationship, @fup, @hiv_status,@hiv_test_date,@art_start_date) ;

SET SQL_SAFE_UPDATES = 0;

update obs o inner join sickle_cell_disease_obs s on o.obs_id = s.source_obs_id
    inner join sickle_cell_initial_enc e on s.source_obs_id = e.source_obs_group_id or s.source_obs_group_id = e.source_obs_group_id
set o.voided = 1, o.voided_by = 1, o.date_voided = current_timestamp(), o.void_reason = 'migrated to SICKLE_CELL_DISEASE_INITIAL encounter';

SET SQL_SAFE_UPDATES = 1;

drop table if exists sickle_cell_visit_enc;

create table sickle_cell_visit_enc (
       encounter_id int PRIMARY KEY AUTO_INCREMENT,
       source_encounter_id int,
       source_encounter_type int,
       patient_id int,
       location_id int,
       encounter_datetime DATETIME,
       source_form_id int,
       creator int,
       date_created DATETIME,
       voided tinyint(1)
);

set @max_enc=(select max(encounter_id) + 1 from encounter);
SET @stmt = CONCAT('ALTER TABLE sickle_cell_visit_enc AUTO_INCREMENT=', @max_enc);
PREPARE yourStatementName FROM @stmt;
execute yourStatementName;

insert into sickle_cell_visit_enc(
    source_encounter_id,
    source_encounter_type,
    patient_id,
    location_id,
    encounter_datetime,
    source_form_id,
    creator,
    date_created,
    voided)
select
    e.encounter_id,
    e.encounter_type,
    e.patient_id,
    e.location_id,
    e.encounter_datetime,
    e.form_id,
    e.creator,
    e.date_created,
    e.voided
from encounter e, sickle_cell_initial_enc s, person p
where e.patient_id = s.patient_id and e.encounter_type in (@ncd_other_followup, @ncd_other_qs, @ncd_other_as, @ncd_other_hosp ) and e.voided = 0
  and e.patient_id = p.person_id and p.dead = 0;

drop table if exists ncd_other_visit_obs;

create table ncd_other_visit_obs (
         obs_id int PRIMARY KEY AUTO_INCREMENT,
         source_encounter_id int,
         source_person_id int,
         source_obs_id int,
         source_obs_group_id int,
         source_obs_datetime DATETIME,
         source_location_id int,
         source_concept_id int,
         source_value_coded int,
         source_value_datetime DATETIME,
         source_value_numeric DOUBLE,
         source_value_text text,
         source_creator int,
         source_date_created DATETIME,
         source_voided tinyint(1) NOT NULL DEFAULT '0'
);

set @max_obs=(select max(obs_id) + 1 from obs);
SET @stmt = CONCAT('ALTER TABLE ncd_other_visit_obs AUTO_INCREMENT=', @max_obs);
PREPARE yourStatementName FROM @stmt;
execute yourStatementName;

insert into ncd_other_visit_obs(
    source_encounter_id,
    source_person_id,
    source_obs_id,
    source_obs_group_id,
    source_obs_datetime,
    source_location_id,
    source_concept_id,
    source_value_coded,
    source_value_datetime,
    source_value_numeric,
    source_value_text,
    source_creator,
    source_date_created,
    source_voided)
select
    o.encounter_id,
    o.person_id,
    o.obs_id,
    o.obs_group_id,
    o.obs_datetime,
    o.location_id,
    o.concept_id,
    o.value_coded,
    o.value_datetime,
    o.value_numeric,
    o.value_text,
    o.creator,
    o.date_created,
    o.voided
from obs o, sickle_cell_visit_enc e
where o.encounter_id = e.source_encounter_id
  and o.concept_id in (@height,  @weight, @systolicBP, @diastolicBP, @heartRate, @bloodOxygenSaturation, @$hospitalSince,@hiv_status, @creatinine,@hospitalDays, @admissionReason, @dischargeDiagnosis, @dischargeMeds)
  and o.voided=0;

-- Create SICKLE_CELL_DISEASE_FOLLOWUP encounters
insert into encounter(
    encounter_id,
    uuid,
    encounter_datetime,
    date_created,
    encounter_type,
    form_id,
    patient_id,
    creator,
    location_id)
select
    e.encounter_id,
    uuid(),
    e.encounter_datetime,
    now(),
    case e.source_encounter_type
        when @ncd_other_followup then @sickle_cell_followup
        when @ncd_other_qs then @sickle_cell_qs
        when @ncd_other_as then @sickle_cell_as
        when @ncd_other_hosp then @sickle_cell_hosp
        else null
        end,
    case e.source_encounter_type
        when @ncd_other_followup then @sickle_cell_visit_form_id
        when @ncd_other_qs then @sickle_cell_qs_form_id
        when @ncd_other_as then @sickle_cell_as_form_id
        when @ncd_other_hosp then @sickle_cell_hosp_form_id
        else null
        end,
    e.patient_id,
    e.creator,
    e.location_id
from sickle_cell_visit_enc e
where e.source_encounter_type in (@ncd_other_followup, @ncd_other_qs, @ncd_other_as, @ncd_other_hosp);

-- Migrate obs from NCD_FOLLOWUP encounters into the new created SICKLE_CELL_DISEASE_FOLLOWUP encounters
insert into obs(
    obs_id,
    person_id,
    encounter_id,
    obs_group_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    value_datetime,
    value_numeric,
    value_text,
    creator,
    date_created,
    voided,
    uuid,
    comments)
select
    o.obs_id,
    o.source_person_id,
    e.encounter_id,
    o.source_obs_group_id,
    o.source_obs_datetime,
    o.source_location_id,
    o.source_concept_id,
    o.source_value_coded,
    o.source_value_datetime,
    o.source_value_numeric,
    o.source_value_text,
    o.source_creator,
    now(),
    o.source_voided,
    uuid(),
    'migrated from NCD_OTHER_ encounter'
from ncd_other_visit_obs o, sickle_cell_visit_enc e
where o.source_encounter_id = e.source_encounter_id
  and o.source_concept_id in (@height,  @weight, @systolicBP, @diastolicBP, @heartRate, @bloodOxygenSaturation, @$hospitalSince, @hiv_status, @creatinine, @hospitalDays, @admissionReason, @dischargeDiagnosis, @dischargeMeds);

