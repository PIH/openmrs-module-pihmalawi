SET sql_safe_updates = 0;
SET group_concat_max_len = 100000;

set @endDate = '2024-12-31';
set @location = 5;

select program_id into @hivProgram from program where name = 'HIV PROGRAM';
set @txStatusConcept = lookup_concept('Treatment Status');
select program_workflow_id into @txStatusWorkflow from program_workflow where program_id = @hivProgram and concept_id = @txStatusConcept;
select program_workflow_state_id into @onArvsState from program_workflow_state where uuid = '6687fa7c-977f-11e1-8993-905e29aff6c1';
select program_workflow_state_id into @exposedChildState from program_workflow_state where uuid = '668847a2-977f-11e1-8993-905e29aff6c1';
select patient_identifier_type_id into @arvNumber from patient_identifier_type where name = 'ARV Number';
select encounter_type_id into @artInitial from encounter_type where name = 'ART_INITIAL';
select encounter_type_id into @artFollowup from encounter_type where name = 'ART_FOLLOWUP';
select encounter_type_id into @preArtFollowup from encounter_type where name = 'PART_FOLLOWUP';
select encounter_type_id into @exposedChildFollowup from encounter_type where name = 'EXPOSED_CHILD_FOLLOWUP';

-- create temporary table to hold all data
drop table if exists temp_art_register;
create table temp_art_register
(
    pid                                  integer not null primary key,
    first_art_start_date_at_location     date,
    first_art_state_id_at_location       integer,
    first_art_enrollment_id_at_location  integer,
    last_art_start_date_at_location      date,
    last_art_state_id_at_location        integer,
    last_art_enrollment_id_at_location   integer,
    first_art_start_date                 date,
    first_art_state_id                   integer,
    first_exposed_child_start_date       date,
    first_exposed_child_state_id         integer,
    arv_number                           varchar(255),
    all_arv_numbers                      varchar(255),
    art_initial_encounter_id             integer,
    art_initial_date                     date,
    art_initial_location                 varchar(255),
    given_name                           varchar(255),
    last_name                            varchar(255),
    birthdate                            date,
    current_age_yrs                      integer,
    current_age_months                   integer,
    gender                               char(1),
    village                              varchar(255),
    traditional_authority                varchar(255),
    district                             varchar(255),
    art_outcome_state_id                 integer,
    art_outcome                          varchar(255),
    art_outcome_date                     date,
    art_outcome_location                 varchar(255),
    first_art_enrollment_date            date,
    first_art_enrollment_location        varchar(255),
    first_exposed_child_date             date,
    first_exposed_child_location         varchar(255),
    arv_start_reasons                    text,
    last_first_line_art_start_date       date,
    last_lab_cd4_count                   double,
    last_lab_cd4_date                    date,
    last_clinician_reported_cd4          double,
    last_clinician_reported_cd4_date     date,
    last_cd4_count                       double,
    last_cd4_date                        date,
    last_viral_load                      varchar(255),
    last_viral_load_date                 date,
    last_viral_load_numeric              double,
    last_viral_load_numeric_date         date,
    last_viral_load_ldl_date             date,
    last_viral_load_ldl_limit            double,
    last_viral_load_ldl_limit_date       date,
    vhw                                  varchar(255),
    last_hiv_visit_date                  date,
    last_hiv_visit_encounter_id          integer,
    last_hiv_visit_location              varchar(255),
    last_hiv_visit_next_appointment_date date,
    last_arvs_received                   varchar(255),
    last_arvs_received_date              date,
    last_tb_status                       varchar(255),
    last_tb_status_date                  date,
    last_art_side_effects                varchar(1000),
    last_art_side_effects_date           date,
    last_height_cm                       double,
    last_weight_kg                       double,
    last_weight_date                     date,
    all_enrollments                      varchar(1000)
);

-- Collect all patients who were in the On ARVs state at the given location on or before the given end date
drop temporary table if exists temp_status;
create temporary table temp_status (
    patient_state_id int not null primary key,
    patient_program_id int,
    patient_id int,
    location_id int,
    state_id int,
    state_concept_id int,
    start_date date,
    end_date date
);

insert into temp_status
select s.patient_state_id, p.patient_program_id, p.patient_id, p.location_id, s.state, pws.concept_id, s.start_date, s.end_date
from patient_state s
inner join patient_program p on s.patient_program_id = p.patient_program_id
inner join program_workflow_state pws on s.state = pws.program_workflow_state_id
where p.voided = 0 and s.voided = 0
  and pws.program_workflow_id = @txStatusWorkflow
  and (@endDate is null or date(s.start_date) <= @endDate)
;
create index temp_status_patient_start_idx on temp_status(patient_id, start_date);
create index temp_status_state_idx on temp_status(state_id);
create index temp_status_location_idx on temp_status(location_id);

-- The rows in the main table should be all patients who have ever been enrolled in ART at the given location by the given end date
-- Add these and identifier the specific states that represent the first and last art states at the given location, and the first overall

insert into temp_art_register (pid,
                               first_art_start_date_at_location,
                               last_art_start_date_at_location)
select patient_id, min(start_date), max(start_date)
from temp_status
where state_id = @onArvsState
and (@location is null or location_id = @location)
group by patient_id;

update temp_art_register t
inner join (select patient_id, min(start_date) as first_art_start from temp_status where state_id = @onArvsState group by patient_id) s on t.pid = s.patient_id
set t.first_art_start_date = s.first_art_start;

update temp_art_register t
inner join (select patient_id, min(start_date) as first_art_start from temp_status where state_id = @onArvsState group by patient_id) s on t.pid = s.patient_id
set t.first_art_start_date = s.first_art_start;

update temp_art_register t
inner join (select patient_id, min(start_date) as first_exposed_child_start from temp_status where state_id = @exposedChildState group by patient_id) s on t.pid = s.patient_id
set t.first_exposed_child_start_date = s.first_exposed_child_start;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.first_art_start_date_at_location = s.start_date and s.state_id = @onArvsState
set t.first_art_state_id_at_location = s.patient_state_id, t.first_art_enrollment_id_at_location = s.patient_program_id;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.last_art_start_date_at_location = s.start_date and s.state_id = @onArvsState
set t.last_art_state_id_at_location = s.patient_state_id, t.last_art_enrollment_id_at_location = s.patient_program_id;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.first_art_start_date = s.start_date and s.state_id = @onArvsState
set t.first_art_state_id = s.patient_state_id;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.first_exposed_child_start_date = s.start_date and s.state_id = @exposedChildState
set t.first_exposed_child_state_id = s.patient_state_id;

-- Identifiers
update temp_art_register set arv_number = patient_identifier(pid, @arvNumber, @location);
update temp_art_register set all_arv_numbers = patient_identifiers_of_type(pid, @arvNumber);

-- ART Initial Encounter
update temp_art_register set art_initial_encounter_id = first_encounter_of_type(pid, @artInitial, @endDate);
update temp_art_register set art_initial_date = (select encounter_datetime from encounter where encounter_id = art_initial_encounter_id);
update temp_art_register set art_initial_location = (select location_name(location_id) from encounter where encounter_id = art_initial_encounter_id);

-- Demographics
update temp_art_register set given_name = person_given_name(pid);
update temp_art_register set last_name = person_family_name(pid);
update temp_art_register set birthdate = (select birthdate from person where person_id = pid);
update temp_art_register set current_age_yrs = timestampdiff(YEAR, birthdate, @endDate);
update temp_art_register set current_age_months = timestampdiff(MONTH, birthdate, @endDate);
update temp_art_register set gender = (select person.gender from person where person_id = pid);
update temp_art_register set village = person_address_village(pid);
update temp_art_register set traditional_authority = person_address_traditional_authority(pid);
update temp_art_register set district = person_address_district(pid);

-- Most recent Treatment Status state at the given location
update temp_art_register set art_outcome_state_id = latest_state_in_workflow(last_art_enrollment_id_at_location, @txStatusWorkflow, @location, @endDate);
update temp_art_register set art_outcome = state_name(art_outcome_state_id);
update temp_art_register set art_outcome_date = (select start_date from patient_state where patient_state_id = art_outcome_state_id);
update temp_art_register set art_outcome_location = (select location_name(location_id) from temp_status where patient_state_id = art_outcome_state_id);

update temp_art_register set first_art_enrollment_date = first_art_start_date;
update temp_art_register set first_art_enrollment_location = (select location_name(location_id) from temp_status where patient_state_id = first_art_state_id);
update temp_art_register set first_exposed_child_date = first_exposed_child_start_date;
update temp_art_register set first_exposed_child_location = (select location_name(location_id) from temp_status where patient_state_id = first_exposed_child_state_id);

-- Reasons for starting ART

drop table if exists temp_art_initial_obs;
create temporary table temp_art_initial_obs as
select o.encounter_id, o.concept_id, o.value_coded, o.value_numeric, o.value_text
from obs o
inner join temp_art_register r on o.encounter_id = r.art_initial_encounter_id
where o.voided = 0;
create index temp_art_initial_obs_enc_idx on temp_art_initial_obs(encounter_id, concept_id);

drop table if exists temp_start_reasons;
create temporary table temp_start_reasons (
    encounter_id int,
    type varchar(20),
    reason text
);

set @cd4Concept = lookup_concept('CD4 count');
set @ksWorseningConcept = lookup_concept('Kaposis sarcoma side effects worsening while on ARVs?');
set @tbTxStatusConcept = lookup_concept('Tuberculosis treatment status');
set @whoStageConcept = lookup_concept('WHO stage');
set @cd4PercentConcept = lookup_concept('Cd4%');
set @pshdConcept = lookup_concept('Presumed severe HIV criteria present');
set @conditionsConcept = lookup_concept('Clinical Conditions Text');
set @pregLacConcept = lookup_concept('Pregnant/Lactating');

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'CD4', convert(value_numeric, char) from temp_art_initial_obs where concept_id = @cd4Concept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'KS',
       case when value_coded = 2257 then 'Yes' when value_coded = 2258 then 'No' end
from temp_art_initial_obs where concept_id = @ksWorseningConcept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'TB',
       case when value_coded = 1067 then 'Never' when value_coded = 1714 then 'Last' when value_coded = 1432 then 'Curr' end
from temp_art_initial_obs where concept_id = @tbTxStatusConcept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'TLC', convert(value_numeric, char) from temp_art_initial_obs where concept_id = @cd4PercentConcept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'PSHD', concept_name(value_coded) from temp_art_initial_obs where concept_id = @pshdConcept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'CONDITIONS', value_text from temp_art_initial_obs where concept_id = @conditionsConcept;

insert into temp_start_reasons (encounter_id, type, reason)
select encounter_id, 'PREG',
       case when value_coded = 1066 then 'No' when value_coded = 1755 then 'Pregnant' when value_coded = 5632 then 'Lactating' end
from temp_art_initial_obs where concept_id = @pregLacConcept;

delete from temp_start_reasons where reason is null or trim(reason) = '';
create index temp_start_reasons_encounter_idx on temp_start_reasons(encounter_id);

update temp_art_register r
inner join (
    select encounter_id, group_concat(concat(type, ': ', reason) separator ', ') as reason
    from temp_start_reasons
    group by encounter_id
) t on r.art_initial_encounter_id = t.encounter_id
set r.arv_start_reasons = t.reason;

-- Latest obs values

drop table if exists temp_obs;
create temporary table temp_obs as
select o.obs_id, o.person_id, o.encounter_id, o.obs_datetime, o.concept_id, o.value_coded, o.value_numeric, o.value_text, o.value_datetime
from obs o
inner join temp_art_register r on o.person_id = r.pid
where o.voided = 0 and date(o.obs_datetime) <= @endDate;

create index temp_obs_encounter_idx on temp_obs(encounter_id);
create index temp_obs_person_concept_idx on temp_obs(person_id, concept_id);

drop table if exists temp_latest_obs_dates;
create table temp_latest_obs_dates as
select o.person_id, o.concept_id, max(o.obs_datetime) as obs_datetime
from temp_obs o
group by o.person_id, o.concept_id;

create index temp_latest_obs_person_concept_date_idx on temp_latest_obs_dates(person_id, concept_id, obs_datetime);

alter table temp_obs add column latest boolean;
update temp_obs o inner join temp_latest_obs_dates d on o.person_id = d.person_id and o.concept_id = d.concept_id and o.obs_datetime = d.obs_datetime
set latest = true;

create index temp_obs_person_concept_latest_idx on temp_obs(person_id, concept_id, latest);

-- Last Date of starting first line ARVs

set @dateFirstLineArvsConcept = lookup_concept('656fbe36-977f-11e1-8993-905e29aff6c1');
update temp_art_register r set r.last_first_line_art_start_date = (select value_datetime from temp_obs where person_id = r.pid and concept_id = @dateFirstLineArvsConcept and latest = true order by obs_id desc limit 1);

-- Last CD4

update temp_art_register r set r.last_lab_cd4_count = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @cd4Concept and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_lab_cd4_date = (select obs_datetime from temp_obs  where person_id = r.pid and concept_id = @cd4Concept and latest = true order by obs_id desc limit 1);

set @clinicanReportedCd4Concept = lookup_concept('Clinician reported to CD4');
update temp_art_register r set r.last_clinician_reported_cd4 = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @clinicanReportedCd4Concept and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_clinician_reported_cd4_date = (select obs_datetime from temp_obs  where person_id = r.pid and concept_id = @clinicanReportedCd4Concept and latest = true order by obs_id desc limit 1);

update temp_art_register set last_cd4_count = last_lab_cd4_count, last_cd4_date = last_lab_cd4_date;
update temp_art_register set last_cd4_count = last_clinician_reported_cd4, last_cd4_date = last_clinician_reported_cd4_date where last_clinician_reported_cd4_date > last_lab_cd4_date;

-- Last Viral Load

set @viralLoadNumericConcept = lookup_concept('654a7694-977f-11e1-8993-905e29aff6c1');
update temp_art_register r set r.last_viral_load_numeric = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @viralLoadNumericConcept and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_viral_load_numeric_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @viralLoadNumericConcept and latest = true order by obs_id desc limit 1);

set @viralLoadLdlConcept = lookup_concept('e97b36a2-16f5-11e6-b6ba-3e1d05defe78');
update temp_art_register r set r.last_viral_load_ldl_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @viralLoadNumericConcept and latest = true and value_coded = 2257 order by obs_id desc limit 1);

set @viralLoadLdlLimitConcept = lookup_concept('69e87644-5562-11e9-8647-d663bd873d93');
update temp_art_register r set r.last_viral_load_ldl_limit = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @viralLoadLdlLimitConcept and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_viral_load_ldl_limit_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @viralLoadLdlLimitConcept and latest = true order by obs_id desc limit 1);

update temp_art_register set last_viral_load = cast(last_viral_load_numeric as char), last_viral_load_date = last_viral_load_numeric_date;
update temp_art_register set last_viral_load = 'LDL', last_viral_load_date = last_viral_load_ldl_date where last_viral_load_ldl_date > last_viral_load_date;
update temp_art_register set last_viral_load = concat('<', last_viral_load_ldl_limit), last_viral_load_date = last_viral_load_ldl_limit_date where last_viral_load_ldl_limit_date > last_viral_load_date;

-- vhw TODO

-- Last HIV Visit
update temp_art_register r set r.last_hiv_visit_date = (select max(encounter_datetime) from encounter where patient_id = r.pid and voided = 0 and encounter_type in (@artFollowup, @preArtFollowup, @exposedChildFollowup) and date(encounter_datetime) <= @endDate);
update temp_art_register r set r.last_hiv_visit_encounter_id = (select encounter_id from encounter where patient_id = r.pid and voided = 0 and encounter_type in (@artFollowup, @preArtFollowup, @exposedChildFollowup) and encounter_datetime = r.last_hiv_visit_date);
update temp_art_register r set r.last_hiv_visit_location = (select location_name(location_id) from encounter where encounter_id = last_hiv_visit_encounter_id);
update temp_art_register r set r.last_hiv_visit_next_appointment_date = (select value_datetime from temp_obs o where o.encounter_id = r.last_hiv_visit_encounter_id order by obs_id desc limit 1);

-- Last Obs
set @arvsReceived = lookup_concept('Malawi Antiretroviral drugs received');
update temp_art_register r set r.last_arvs_received = (select concept_name(value_coded) from temp_obs where person_id = r.pid and concept_id = @arvsReceived and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_arvs_received_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @arvsReceived and latest = true order by obs_id desc limit 1);

set @tbStatus = lookup_concept('TB status');
update temp_art_register r set r.last_tb_status = (select concept_name(value_coded) from temp_obs where person_id = r.pid and concept_id = @tbStatus and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_tb_status_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @tbStatus and latest = true order by obs_id desc limit 1);

set @artSideEffects = lookup_concept('Malawi ART side effects');
update temp_art_register r set r.last_art_side_effects = (select concept_name(value_coded) from temp_obs where person_id = r.pid and concept_id = @artSideEffects and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_art_side_effects_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @artSideEffects and latest = true order by obs_id desc limit 1);

set @height = lookup_concept('Height (cm)');
update temp_art_register r set r.last_height_cm = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @height and latest = true order by obs_id desc limit 1);

set @weight = lookup_concept('Weight (kg)');
update temp_art_register r set r.last_weight_kg = (select value_numeric from temp_obs where person_id = r.pid and concept_id = @weight and latest = true order by obs_id desc limit 1);
update temp_art_register r set r.last_weight_date = (select obs_datetime from temp_obs where person_id = r.pid and concept_id = @weight and latest = true order by obs_id desc limit 1);

-- all_enrollments TODO
-- df.getAllActiveStatesOnEndDate(df.getActiveStatesAsStringConverter()))

-- Extract out

select * from temp_art_register;
