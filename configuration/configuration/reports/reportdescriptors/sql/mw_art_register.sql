SET sql_safe_updates = 0;
set @endDate = '2024-12-31';
set @location = 5;

select program_id into @hivProgram from program where name = 'HIV PROGRAM';
select concept_id into @txStatusConcept from concept_name where name = 'Treatment Status' and locale = 'en' and concept_name_type = 'FULLY_SPECIFIED';
select program_workflow_id into @txStatusWorkflow from program_workflow where program_id = @hivProgram and concept_id = @txStatusConcept;
select program_workflow_state_id into @onArvsState from program_workflow_state where uuid = '6687fa7c-977f-11e1-8993-905e29aff6c1';
select patient_identifier_type_id into @arvNumber from patient_identifier_type where name = 'ARV Number';
select encounter_type_id into @artInitial from encounter_type where name = 'ART_INITIAL';

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
    arv_start_reasons                    varchar(1000),
    last_first_line_art_start_date       date,
    last_cd4_count                       double,
    last_cd4_date                        date,
    last_viral_load                      double,
    last_viral_load_date                 date,
    vhw                                  varchar(255),
    last_hiv_visit_date                  date,
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
inner join (select patient_id, min(start_date) as first_art_start from temp_status group by patient_id) s on t.pid = s.patient_id
set t.first_art_start_date = s.first_art_start;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.first_art_start_date_at_location = s.start_date
set t.first_art_state_id_at_location = s.patient_state_id, t.first_art_enrollment_id_at_location = s.patient_program_id;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.last_art_start_date_at_location = s.start_date
set t.last_art_state_id_at_location = s.patient_state_id, t.last_art_enrollment_id_at_location = s.patient_program_id;

update temp_art_register t
inner join temp_status s on t.pid = s.patient_id and t.first_art_start_date = s.start_date
set t.first_art_state_id = s.patient_state_id;

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


-- Extract out

select * from temp_art_register;
