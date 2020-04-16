-- This is the migration code
-- get table of obs with "other" text in CHF mastercard
--
drop table if exists obs_temp_update;

#

CREATE TEMPORARY TABLE obs_temp_update (
  SELECT 	person_id,
    e.encounter_id,
    obs_datetime,
    o.location_id,
    obs_group_id,
    o.date_created,
    value_text,
    o.creator
  from obs o
    join encounter e on e.encounter_id = o.encounter_id
    join encounter_type et on et.encounter_type_id = e.encounter_type
  where concept_id = 7685
        and obs_group_id is not null
        and et.name = 'CHF_INITIAL'
        and o.voided = 0
);

#

-- add observations of "other" checkbox into form

insert into obs
(person_id, concept_id, obs_datetime, encounter_id, location_id, obs_group_id, value_coded, creator, date_created, voided, uuid)
  select person_id, 3683, obs_datetime, encounter_id, location_id, obs_group_id, 5622, creator, date_created, 0, uuid()
  from obs_temp_update;

#