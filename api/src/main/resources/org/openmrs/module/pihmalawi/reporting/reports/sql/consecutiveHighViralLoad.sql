-- ## report_uuid = f2659c14-651f-11e9-9c3a-dbb5f9c0d8f7
-- ## design_uuid = 1c1ff582-6510-11e9-9c3a-dbb5f9c0d8f7
-- ## report_name = HIV - Consecutive High Viral Load Report
-- ## report_description = Report listing patients whose last two viral loads are above a minimum threshold (revision: July 2019)
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = min_vl|Minimum Viral Load|java.lang.Integer

-- SET @endDate = '2019-06-30';
-- SET @min_vl = 1000;

# CONSTRUCT TARGET TABLE THAT WE WILL POPULATE TO REPRESENT THE FULL EXPORT

drop temporary table if exists rpt_high_vls;
create temporary table rpt_high_vls
(
    patient_id            int primary key,
    identifier            varchar(50),
    first_name            varchar(50),
    last_name             varchar(50),
    gender                char(1),
    age_yrs               int,
    traditional_authority varchar(100),
    village               varchar(100),
    previous_reg          varchar(10),
    previous_reg_date     date,
    current_reg           varchar(10),
    current_reg_date      date,
    previous_vl_date      date,
    previous_vl           varchar(50),
    last_vl_date          date,
    last_vl               varchar(50),
    last_visit_date       date,
    last_visit_location   varchar(50),
    next_appt_date        date
);

call create_mw_patient(@endDate);
call create_mw_viral_load(@endDate);
call create_mw_regimen(@endDate);
call create_mw_hiv_enrollment(@endDate);
call create_mw_hiv_visit(@endDate);

# FIRST POPULATE THE BASE SET OF PATIENTS AS THOSE ACTIVE IN THE HIV PROGRAM

insert into rpt_high_vls (patient_id, identifier, first_name, last_name, gender, age_yrs, traditional_authority,
                          village)
select p.patient_id,
       ifnull(p.art_number, p.hcc_number) as identifier,
       p.first_name,
       p.last_name,
       p.gender,
       p.age_yrs,
       p.traditional_authority,
       p.village
from mw_patient p
where p.patient_id in (select patient_id from mw_hiv_enrollment where active = true)
;

# LOAD IN THE LATEST AND PREVIOUS VIRAL LOAD

update rpt_high_vls v
    inner join mw_viral_load lvl on v.patient_id = lvl.patient_id and lvl.num_from_end = 1 and lvl.vl_numeric is not null
set v.last_vl      = lvl.vl_numeric,
    v.last_vl_date = lvl.vl_date;

update rpt_high_vls v
    inner join mw_viral_load pvl on v.patient_id = pvl.patient_id and pvl.num_from_end = 2 and pvl.vl_numeric is not null
set v.previous_vl      = pvl.vl_numeric,
    v.previous_vl_date = pvl.vl_date;

# LOAD IN THE LATEST VISIT AND OBS INFO

update rpt_high_vls h
    inner join mw_hiv_visit v on h.patient_id = v.patient_id and v.num_from_end = 1
    inner join location l on v.location_id = l.location_id
set h.last_visit_date     = v.encounter_date,
    h.last_visit_location = l.name,
    h.next_appt_date      = v.next_appt_date
;

# LOAD SPECIFIC REGIMEN CHANGES IN FROM THE MW_REGIMEN_CHANGE TABLE

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_end = 1
set h.current_reg_date = v.regimen_date,
    h.current_reg      = v.regimen_name;

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_end = 2
set h.previous_reg_date = v.regimen_date,
    h.previous_reg      = v.regimen_name;

# OUTPUT FINAL DATA SET
# THIS SELECTS FROM THE MASTER TABLE FOR PATIENTS WHOSE VIRAL LOADS ARE ABOVE THE THRESHOLD

SELECT patient_id            as 'PID',
       identifier            as 'Identifier',
       last_name             as 'Last name',
       first_name            as 'Given name',
       gender                as 'M/F',
       age_yrs               as 'Current Age (yr)',
       traditional_authority as 'TA',
       village               as 'Village',
       previous_reg          as 'Previous Regimen',
       previous_reg_date     as 'Previous Regimen Start Date',
       current_reg           as 'Current Regimen',
       current_reg_date      as 'Current Regimen Start Date',
       previous_vl           as 'Previous Viral Load',
       previous_vl_date      as 'Previous Viral Load Date',
       last_vl               as 'Last Viral Load',
       last_vl_date          as 'Last Viral Load Date',
       last_visit_date       as 'Last Visit Date',
       last_visit_location   as 'Last Visit Location',
       next_appt_date        as 'Next Appointment Date'
FROM rpt_high_vls
WHERE previous_vl is not null and previous_vl >= @min_vl and last_vl is not null and last_vl >= @min_vl
ORDER BY identifier
;
