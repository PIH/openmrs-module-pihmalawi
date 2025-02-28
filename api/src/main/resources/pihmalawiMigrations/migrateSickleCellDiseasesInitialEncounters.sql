set @ncd_other_initial = (select encounter_type_id from encounter_type where uuid = 'b562295c-e335-11e8-9f32-f2801f1b9fd1');
set @sickle_cell_initial = (select encounter_type_id from encounter_type where uuid = '56C2D952-DB11-4B47-B248-79C1B2A88E88');
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
from obs o inner join sickle_cell_initial_enc e on o.obs_id = e.source_obs_group_id;

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
where o.encounter_id = e.source_encounter_id and o.obs_group_id = e.source_obs_group_id and o.voided=0;

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

-- insert Diagnosis Construct member obs: the diagnosis and the diagnosis date
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
    o.source_date_created,
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
    o.source_date_created,
    o.source_voided,
    uuid(),
    'migrated from NCD_OTHER_INITIAL encounter'
from sickle_cell_disease_obs o, sickle_cell_initial_enc e
where o.source_encounter_id = e.source_encounter_id
  and o.source_concept_id in (@phone_number,  @guardian, @next_of_kin_phone, @relationship, @fup, @hiv_status,@hiv_test_date,@art_start_date) ;

-- void the obs that were migrated from the NCD_OTHER_INITIAL encounter
update obs o inner join sickle_cell_disease_obs s on o.obs_id = s.source_obs_id
set o.voided = 1, o.voided_by = 1, o.date_voided = current_timestamp(), o.void_reason = 'migrated to SICKLE_CELL_DISEASE_INITIAL encounter';
