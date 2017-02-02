-- CPT Migration (MLW-510)
-- Triggers Migration (MLW-522)
-- TB Migration (MLW-524)
-- Smoking History Migration (MLW-525)
-- Glucose Migration (MLW-508)
-- Foot Examination Migration (MLW-508)


-- **************************************************
-- CPT Migration (MLW-510)
-- **************************************************

-- Create temporary table to store data-to-be-migrated
create table temp_cpt_migration (
  id INT not null auto_increment primary key,
  obs_id int(11) not NULL,
  encounter_id int(11) not NULL,
  person_id int(11) not NULL,
  location_id int(11) not NULL,
  obs_group_id int(11) default NULL,
  obs_datetime datetime default NULL,
  pills double default NULL,
  creator int(11) not NULL
);

-- Add data-to-be-migrated to temporary table
insert into temp_cpt_migration
  (obs_id, encounter_id, person_id,  
   location_id, obs_datetime, pills, creator)
select o.obs_id, o.encounter_id, o.person_id, o.location_id, o.obs_datetime, o.value_numeric, o.creator
  from obs o
  join encounter e on e.encounter_id = o.encounter_id
  where o.concept_id = 6319
  and encounter_type = 10
  and o.voided = 0
  and e.voided = 0;

-- Create obs group container for obs
insert into obs
  (person_id,concept_id,encounter_id,obs_datetime,location_id,creator,comments,date_created,uuid)
select person_id,8607,encounter_id,obs_datetime,location_id,creator,id,now(),uuid()
from temp_cpt_migration;

-- Add obs_group_id to temp table
update temp_cpt_migration,obs
   set temp_cpt_migration.obs_group_id = obs.obs_id
 where temp_cpt_migration.id = obs.comments
 and obs.concept_id = 8607; 

-- Insert prophylaxis type into obs (all CPT from old observations)
insert into obs
  (person_id,concept_id,encounter_id,obs_datetime,location_id,obs_group_id,value_coded,creator,date_created,uuid)
select person_id,8606,encounter_id,obs_datetime,location_id,obs_group_id,916,creator,now(),uuid()
from temp_cpt_migration;

-- Insert number of pills into obs (from migrated values)
insert into obs
  (person_id,concept_id,encounter_id,obs_datetime,location_id,obs_group_id,value_numeric,creator,date_created,uuid)
select person_id,2834,encounter_id,obs_datetime,location_id,obs_group_id,pills,creator,now(),uuid()
from temp_cpt_migration;

-- CLEANUP 
-- 

-- Remove comments
UPDATE obs 
  set comments = NULL
  where concept_id = 8607;

-- Void old obs
update obs o, temp_cpt_migration t
   set o.voided = 1,
       o.voided_by = 58324,
       o.date_voided = now(),
       o.void_reason = 'Migrated data to obsgroup'  
  where t.obs_id = o.obs_id;

-- Drop temporary table
drop table if exists temp_cpt_migration;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- Triggers Migration (MLW-522)
-- **************************************************

-- Create temp table with obs group ids
create table temp_trigger as
 select obs_group_id from obs
 where concept_id = 1193 
    and value_coded IN (select answer_concept from concept_answer where concept_id = 8538) 
    and voided = 0;

-- Fix the obs and replace drug concept with trigger
update obs
      set concept_id = 8538,
            obs_group_id = NULL,
            comments = 'MLW-522 Fixed trigger on epilepsy form' 
where concept_id = 1193 
    and value_coded IN (select answer_concept from concept_answer where concept_id = 8538) 
    and voided = 0;

-- delete unused obs group
delete from obs
 where obs_id IN (select obs_group_id from temp_trigger);

-- drop temp table
drop table temp_trigger;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- TB Migration (MLW-524)
-- **************************************************

DROP TABLE IF EXISTS temp_tb_migration;

create table temp_tb_migration (
  id INT not null auto_increment primary key,
  obs_id int(11) not NULL
);

INSERT INTO temp_tb_migration
	(obs_id)
select obs_id from obs 
where concept_id = 8395;

UPDATE obs o, temp_tb_migration tb
	set o.concept_id = 7459
	where o.obs_id = tb.obs_id;
	
DROP table temp_tb_migration;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- Smoking History Migration (MLW-525)
-- **************************************************

DROP TABLE IF EXISTS temp_tobacco_migration;

create table temp_tobacco_migration (
  id INT not null auto_increment primary key,
  obs_id int(11) not NULL
);

INSERT INTO temp_tobacco_migration
	(obs_id)
select obs_id from obs 
where concept_id = 2545;

UPDATE obs o, temp_tobacco_migration tb
	set o.concept_id = 1551
	where o.obs_id = tb.obs_id;
	
DROP table temp_tobacco_migration;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- Glucose Migration (MLW-508)
-- **************************************************

-- Create a temporary table to store the old obs
DROP TABLE IF EXISTS temp_glucose_migration;

create table temp_glucose_migration (
  id INT not null auto_increment primary key,
  obs_id int(11) not NULL,
  encounter_id int(11) not NULL,
  person_id int(11) not NULL,
  location_id int(11) not NULL,
  obs_datetime datetime default NULL,
  glucose double default NULL,
  testType int(11) not NULL,
  creator int(11) not NULL
);

-- Store the old obs into temporary table
-- If 8447 -> test type = 6380
-- If 8448 -> test type = 6379
insert into temp_glucose_migration
  (obs_id, encounter_id, person_id, location_id, obs_datetime, glucose, testType, creator)
select 	ob.obs_id, 
		encounter_id, 
		person_id, 
		location_id, 
		obs_datetime, 
		value_numeric as glucose,
		CASE WHEN concept_id = 8447 THEN 6380 WHEN concept_id = 8448 THEN 6379 END AS testType, 		
		creator 
from (select obs_id, concept_id, value_numeric, encounter_id, person_id, location_id, obs_datetime, creator from obs where concept_id in (8447,8448) and voided = 0) ob;

-- Insert numeric values into obs (with concept 887)
INSERT INTO obs 
	(person_id,concept_id,encounter_id,obs_datetime,value_numeric,location_id,creator,date_created,uuid)
select person_id,887,encounter_id,obs_datetime,glucose,location_id,creator,now(),uuid()
	from temp_glucose_migration;
-- Insert test values as coded values into obs (using concept 6381)
INSERT INTO obs 
	(person_id,concept_id,encounter_id,obs_datetime,value_coded,location_id,creator,date_created,uuid)
select person_id,6381,encounter_id,obs_datetime,testType,location_id,creator,now(),uuid()
	from temp_glucose_migration;	

-- Void old obs
UPDATE obs o, temp_glucose_migration tg
	SET o.voided = 1 
	WHERE o.obs_id = tg.obs_id;

-- drop temporary table	
DROP TABLE IF EXISTS temp_glucose_migration;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- Foot Examination Migration (MLW-508)
-- **************************************************

-- Create a temporary table to store the old obs
DROP TABLE IF EXISTS temp_foot_migration;

create table temp_foot_migration (
  id INT not null auto_increment primary key,
  obs_id int(11) not NULL,
  encounter_id int(11) not NULL,
  person_id int(11) not NULL,
  location_id int(11) not NULL,
  obs_datetime datetime default NULL,
  diagnosis int(11) default NULL,
  creator int(11) not NULL
);

-- store old data to migrate
insert into temp_foot_migration
  (obs_id, encounter_id, person_id, location_id, obs_datetime, diagnosis, creator)
select 	obs_id, 
		encounter_id, 
		person_id, 
		location_id, 
		obs_datetime, 
		value_coded, 		
		creator 
from obs
where concept_id = 8457
and voided = 0;

-- insert such that old value-coded diagnosis is now the question concept with answer yes (1065)

INSERT INTO obs 
	(person_id,concept_id,encounter_id,obs_datetime,value_coded,location_id,creator,date_created,uuid)
select person_id,diagnosis,encounter_id,obs_datetime,1065,location_id,creator,now(),uuid()
	from temp_foot_migration;
	
-- void old data
UPDATE obs o, temp_foot_migration tf
	SET o.voided = 1 
	WHERE o.obs_id = tf.obs_id;

-- drop temporary table	
DROP TABLE IF EXISTS temp_foot_migration;

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- MLW-294 Remove Empty Obs
-- **************************************************

delete from obs
where value_group_id is null 
and value_boolean is null 
and value_coded is null 
and value_coded_name_id is null 
and value_drug is null 
and value_datetime is null 
and value_numeric is null 
and value_modifier is null 
and value_text is null 
and voided = 0
and concept_id not in (8574, 2242, 8570, 8573, 8559, 8556, 8445, 8548, 8501, 8546, 8607, 2168, 6785, 2520, 1337, 8445, 8501, 3573, 1325, 991, 1292, 2220, 2171);

-- **************************************************
-- 
-- **************************************************

-- **************************************************
-- MLW-481 Diagnosis Issues in Chronic Care - Epilepsy Diagnoses
-- **************************************************

CREATE TEMPORARY TABLE temp_epilepsy_dx AS 
select patient_id, 3683 as conceptId, e.encounter_id, e.encounter_datetime, e.location_id, value_coded, 58324 as user, now() as obsDate, uuid() as uuid
from (select * from encounter where encounter_type = 122 and voided = 0 group by patient_id) e
left join (select * from obs where concept_id = 3683 and value_coded = 155) o on o.person_id = e.patient_id
having value_coded is NULL;

INSERT INTO obs
  (person_id, concept_id, encounter_id, obs_datetime, location_id, value_coded, creator, date_created, uuid)
select patient_id, conceptId, encounter_id, encounter_datetime, location_id, 155, user, now(), uuid()
from temp_epilepsy_dx;

DROP TABLE IF EXISTS temp_epilepsy_dx;

-- **************************************************
-- 
-- **************************************************





