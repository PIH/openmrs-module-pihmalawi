-- Migrate CHF non-coded meds to dispensing construct
--
--

SET @chf_visit_enc_type=(select encounter_type_id from encounter_type where uuid = '1f6ad830-6e94-4819-b1fd-8c4146e77280');
SET @otherCHFdrugs = (select concept_id from concept where uuid = '51f56e0e-0038-42fd-88d5-d0d7d47b56b6'); -- Other chronic heart failure drugs
SET @previous_other_non_coded = (select concept_id from concept where uuid = '656cce7e-977f-11e1-8993-905e29aff6c1'); -- Other non-coded
SET @new_non_coded_answer = (select concept_id from concept where uuid = '657140f8-977f-11e1-8993-905e29aff6c1'); -- generic non-coded answer
SET @new_non_coded_text = (select concept_id from concept where uuid = 'd57e3a20-5802-11e6-8b77-86f30ca893d3'); -- non-coded text
SET @new_med_construct_id = (select concept_id from concept where uuid = '3269F65B-1A28-42EE-8578-B9658387AA00');
SET @med_name = (select concept_id from concept where uuid = '65585192-977f-11e1-8993-905e29aff6c1');

#

DROP TABLE IF EXISTS chf_visit_meds_migration;

#
create table chf_visit_meds_migration (
                                             obs_id int PRIMARY KEY AUTO_INCREMENT,
                                             source_encounter_id int,
                                             source_person_id int,
                                             source_obs_id int,
                                             source_obs_datetime DATETIME,
                                             source_location_id int,
                                             source_concept_id int,
                                             source_value_coded int,
                                             source_value_text varchar(255),
                                             source_creator int,
                                             source_date_created DATETIME,
                                             source_voided tinyint(1) NOT NULL DEFAULT '0',
                                             source_uuid char(38)
);

set @max_obs=(select MAX(obs_id) + 1 from obs);
SET @stmt = CONCAT('ALTER TABLE chf_visit_meds_migration AUTO_INCREMENT=', @max_obs);
PREPARE yourStatementName FROM @stmt;
execute yourStatementName;

#

insert into chf_visit_meds_migration(
    source_encounter_id,
    source_person_id,
    source_obs_id,
    source_obs_datetime,
    source_location_id,
    source_concept_id,
    source_value_coded,
    source_value_text,
    source_creator,
    source_date_created,
    source_voided,
    source_uuid
)
select
    encounter_id,
    person_id,
    obs_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    value_text,
    creator,
    date_created,
    voided,
    uuid
from obs where encounter_id in (select encounter_id from encounter where encounter_type=@chf_visit_enc_type)
           and concept_id = @otherCHFdrugs and value_text is not null
           and voided=0;

#

DROP TABLE IF EXISTS tmp_obs;

#

CREATE TABLE tmp_obs (
                         obs_id INT PRIMARY KEY AUTO_INCREMENT,
                         obs_group_id INT,
                         obs_datetime DATETIME,
                         encounter_id INT,
                         concept_id int,
                         value_coded_id int,
                         value_text varchar(255),
                         patient_id INT,
                         location_id int,
                         creator int,
                         date_created DATETIME,
                         voided tinyint(1) NOT NULL DEFAULT '0'
);

#

insert into tmp_obs (
    obs_id,
    encounter_id,
    obs_datetime,
    concept_id,
    patient_id,
    location_id,
    creator,
    date_created,
    voided
) select   obs_id,
           source_encounter_id,
           source_obs_datetime,
           @new_med_construct_id,
           source_person_id,
           source_location_id,
           source_creator,
           source_date_created,
           source_voided
from    chf_visit_meds_migration
where source_concept_id = @otherCHFdrugs and source_value_text is not null;

#

INSERT INTO tmp_obs (
    obs_group_id,
    encounter_id,
    obs_datetime,
    concept_id,
    value_coded_id,
    patient_id,
    location_id,
    creator,
    date_created,
    voided
) select obs_id,
         source_encounter_id,
         source_obs_datetime,
         @med_name,
         @new_non_coded_answer,
         source_person_id,
         source_location_id,
         source_creator,
         source_date_created,
         source_voided
from    chf_visit_meds_migration
where  source_concept_id = @otherCHFdrugs and source_value_text is not null;

#

INSERT INTO tmp_obs (
    obs_group_id,
    encounter_id,
    obs_datetime,
    concept_id,
    value_text,
    patient_id,
    location_id,
    creator,
    date_created,
    voided
) select obs_id,
         source_encounter_id,
         source_obs_datetime,
         @new_non_coded_text,
         source_value_text,
         source_person_id,
         source_location_id,
         source_creator,
         source_date_created,
         source_voided
from    chf_visit_meds_migration
where  source_concept_id = @otherCHFdrugs and source_value_text is not null;

#

INSERT INTO obs (
    obs_id,
    person_id,
    encounter_id,
    obs_group_id,
    obs_datetime,
    location_id,
    concept_id,
    value_coded,
    value_text,
    creator,
    date_created,
    voided,
    uuid
)
SELECT
    o.obs_id,
    o.patient_id,
    o.encounter_id,
    o.obs_group_id,
    o.obs_datetime,
    ifnull(o.location_id, 1),
    o.concept_id,
    o.value_coded_id,
    o.value_text,
    ifnull(o.creator, 1),
    o.date_created,
    ifnull(o.voided, 0),
    uuid()
FROM tmp_obs o;

#

update obs o
    inner join chf_visit_meds_migration a on o.uuid = a.source_uuid
set o.voided = 1, o.voided_by = 1, o.date_voided =  current_timestamp(), o.void_reason = 'migrated to dispensing construct';

#
