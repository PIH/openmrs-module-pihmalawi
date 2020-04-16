-- This is a second migration code
-- combine coded and text "other" diagnosis fields
--
drop table if exists migrate_ncd_dx_other;

#

create table migrate_ncd_dx_other (
id INT not null auto_increment primary key,
person_id int(11) not NULL,
encounter_id int(11) not NULL,
obs_datetime DATETIME not NULL,
location_id int(11) not NULL,
obs_group_id int(11) DEFAULT NULL,
creator int(11) default NULL,
dx_coded int(11) default NULL,
dx_coded_date DATETIME NULL,
orphaned BOOLEAN default NULL,
obs_group_id_text  int(11) default NULL,
obs_id_text  int(11) default NULL,
dx_text TEXT default NULL,
obs_id_text_date  int(11) default NULL,
dx_text_date DATETIME NULL,
void_date BOOLEAN default 0
);

#

-- Get existing other coded obs (put into temporary table)
insert into migrate_ncd_dx_other
(person_id, encounter_id, obs_datetime, location_id, obs_group_id, creator, dx_coded)
select person_id, e.encounter_id, obs_datetime, o.location_id, obs_group_id, o.creator, value_coded
from obs o
join encounter e on e.encounter_id = o.encounter_id
join encounter_type et on et.encounter_type_id = e.encounter_type
where et.uuid = 'b562295c-e335-11e8-9f32-f2801f1b9fd1'
and o.concept_id = 3683
and o.value_coded = 5622
and o.voided = 0;

#

-- get existing coded obs date values (put into temporary table)
update migrate_ncd_dx_other m, obs o
set m.dx_coded_date = o.value_datetime
where o.concept_id = 6774
and o.obs_group_id = m.obs_group_id
and o.voided = 0;

#

-- Add text values to table (where coded value exists)
update migrate_ncd_dx_other m, obs o
set m.dx_text = o.value_text, m.obs_id_text = o.obs_id, m.obs_group_id_text = o.obs_group_id
where o.concept_id = 7685
and o.encounter_id = m.encounter_id
and o.voided = 0;

#

-- Add text values (from forms that do not have any coded other dx values)
-- We will create obs groups for these values (since some do not have obs groups)
insert into migrate_ncd_dx_other
(person_id, encounter_id, obs_datetime, location_id, creator, orphaned, obs_group_id_text, obs_id_text, dx_text)
select person_id, o.encounter_id, obs_datetime, o.location_id, o.creator, True, obs_group_id, obs_id, value_text
from obs o
join encounter e on e.encounter_id = o.encounter_id
join encounter_type et on et.encounter_type_id = e.encounter_type
where et.uuid = 'b562295c-e335-11e8-9f32-f2801f1b9fd1'
and o.concept_id = 7685
and o.encounter_id not in (select encounter_id from migrate_ncd_dx_other)
and o.voided = 0;

#

-- Add text *date* values to table
update migrate_ncd_dx_other m, obs o
set m.dx_text_date = o.value_datetime, m.obs_id_text_date = o.obs_id
where o.concept_id = 6774
and o.obs_group_id = m.obs_group_id_text
and o.voided = 0;

#

-- Create obs groups for value_text obs
insert into obs
(person_id,encounter_id,obs_datetime,location_id,
concept_id,
creator,date_created,comments,uuid)
select person_id,encounter_id,obs_datetime,location_id,
8445,
creator,now(),id,uuid()
from migrate_ncd_dx_other m
where obs_group_id is NULL;

#

-- add these obs group ids to the migration table
update migrate_ncd_dx_other m, obs o
set m.obs_group_id = o.obs_id
where o.comments = m.id
and o.concept_id = 8445;

#

-- remove comments from all dx obs groups
update obs o, migrate_ncd_dx_other m
set comments = NULL
where o.concept_id = m.obs_group_id;

#

-- Create concept coded values for orphaned text values
insert into obs
(person_id,encounter_id,obs_datetime,location_id,obs_group_id,
concept_id,value_coded,
creator,date_created,uuid)
select person_id,encounter_id,obs_datetime,location_id,obs_group_id,
3683,5622,
creator,now(),uuid()
from migrate_ncd_dx_other m
where orphaned is True;

#

-- update obs_group_id for text fields
update obs o, migrate_ncd_dx_other m
set o.obs_group_id = m.obs_group_id
where m.obs_id_text = o.obs_id;

#

-- update obs_group_id for text date fields
update obs o, migrate_ncd_dx_other m
set o.obs_group_id = m.obs_group_id
where m.obs_id_text_date = o.obs_id;

#

-- mark text date values to be voided if there are two date fields
update migrate_ncd_dx_other
set void_date = 1
where dx_text_date is not NULL
and dx_coded_date is not NULL;

#

-- if coded date field exists, void text date field
update obs o, migrate_ncd_dx_other m
set o.voided = 1, o.voided_by = 1, date_voided = now()
where m.obs_id_text_date = o.obs_id
and m.void_date = 1;

#

-- void old obs group values
update obs o, migrate_ncd_dx_other m
set o.voided = 1, o.voided_by = 1, date_voided = now()
where m.obs_group_id_text = o.obs_id;

#

drop table if exists migrate_ncd_dx_other;

#