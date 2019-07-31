-- ## report_uuid = 2fe281be-3ff4-11e6-9d69-0f1641034c73
-- ## design_uuid = 3ed8dbbe-3ff4-11e6-9d69-0f1641034c73
-- ## report_name = HIV - High Viral Load Report
-- ## report_description = Report listing patients with a high viral load from last laboratory test  (revision: July 2019)
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = min_vl|Minimum Viral Load|java.lang.Integer

# SET @endDate = '2019-06-30';
# SET @min_vl = 1000;

# CONSTRUCT TARGET TABLE THAT WE WILL POPULATE TO REPRESENT THE FULL EXPORT

drop temporary table if exists rpt_high_vls;
create temporary table rpt_high_vls
(
    patient_id       int primary key,
    identifier       varchar(50),
    first_name       varchar(50),
    last_name        varchar(50),
    gender           char(1),
    age_yrs          int,
    outcome          varchar(100),
    outcome_date     date,
    last_visit_date  date,
    next_appt_date   date,
    next_appt_loc    varchar(50),
    last_vl_date     date,
    last_vl          varchar(50),
    first_vl_date    date,
    first_vl         varchar(50),
    second_vl_date   date,
    second_vl        varchar(50),
    third_vl_date    date,
    third_vl         varchar(50),
    fourth_vl_date   date,
    fourth_vl        varchar(50),
    first_reg_date   date,
    first_reg        varchar(10),
    second_reg_date  date,
    second_reg       varchar(10),
    third_reg_date   date,
    third_reg        varchar(10),
    current_reg_date date,
    current_reg      varchar(10)
);

call create_mw_patient(@endDate);
call create_mw_viral_load(@endDate);
call create_mw_regimen(@endDate);
call create_mw_hiv_enrollment(@endDate);
call create_mw_hiv_visit(@endDate);

# FIRST POPULATE THE BASE SET OF PATIENTS THAT THIS REPORT WILL CONTAIN
# ACTIVE PATIENTS IN THE HIV PROGRAM WHOSE LATEST VIRAL LOAD IS HIGH

insert into rpt_high_vls (patient_id, identifier, first_name, last_name, gender, age_yrs, last_vl_date, last_vl)
select v.patient_id,
       ifnull(p.art_number, p.hcc_number) as identifier,
       p.first_name,
       p.last_name,
       p.gender,
       p.age_yrs,
       v.vl_date,
       v.vl_numeric
from mw_viral_load v
         inner join mw_patient p on v.patient_id = p.patient_id
where v.patient_id in (select patient_id from mw_hiv_enrollment where active = true)
  and v.num_from_end = 1
  and (v.vl_numeric is not null and v.vl_numeric >= @min_vl)
;

# LOAD IN THE LATEST ENROLLMENT STATUS INFO

update rpt_high_vls h
    inner join mw_hiv_enrollment_status s on h.patient_id = s.patient_id and s.num_from_end = 1
set h.outcome      = s.state_name,
    h.outcome_date = s.start_date;

# LOAD IN THE LATEST VISIT AND OBS INFO

update rpt_high_vls h
    inner join mw_hiv_visit v on h.patient_id = v.patient_id and v.num_from_end = 1
    inner join location l on v.location_id = l.location_id
set h.next_appt_date = v.next_appt_date,
    h.next_appt_loc  = l.name;

# LOAD SPECIFIC VIRAL LOAD RESULTS FOR THE PATIENT IN FROM THE MW_VIRAL_LOAD TABLE

update rpt_high_vls h
    inner join mw_viral_load v on h.patient_id = v.patient_id and v.num_from_start = 1
set h.first_vl_date = v.vl_date,
    h.first_vl      = v.value_display;

update rpt_high_vls h
    inner join mw_viral_load v on h.patient_id = v.patient_id and v.num_from_start = 2
set h.second_vl_date = v.vl_date,
    h.second_vl      = v.value_display;

update rpt_high_vls h
    inner join mw_viral_load v on h.patient_id = v.patient_id and v.num_from_start = 3
set h.third_vl_date = v.vl_date,
    h.third_vl      = v.value_display;

update rpt_high_vls h
    inner join mw_viral_load v on h.patient_id = v.patient_id and v.num_from_start = 4
set h.fourth_vl_date = v.vl_date,
    h.fourth_vl      = v.value_display;

# LOAD SPECIFIC REGIMEN CHANGES IN FROM THE MW_REGIMEN_CHANGE TABLE

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_start = 1
set h.first_reg_date = v.regimen_date,
    h.first_reg      = v.regimen_name;

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_start = 2
set h.second_reg_date = v.regimen_date,
    h.second_reg      = v.regimen_name;

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_start = 3
set h.third_reg_date = v.regimen_date,
    h.third_reg      = v.regimen_name;

update rpt_high_vls h
    inner join mw_regimen_change v on h.patient_id = v.patient_id and v.num_from_end = 1
set h.current_reg_date = v.regimen_date,
    h.current_reg      = v.regimen_name;

# OUTPUT FINAL DATA SET
# THIS SELECTS FROM THE MASTER TABLE, ELIMINATES PATIENTS WHO ARE NO LONGER ACTIVE
# AND PERFORMS SOME FINAL TRANSFORMATIONS

SELECT patient_id       as 'PID',
       identifier       as 'Identifier',
       first_name       as 'First',
       last_name        as 'Last',
       gender           as 'Gender',
       age_yrs          as 'Age (yrs)',
       outcome          as 'Outcome',
       outcome_date     as 'Date of last Outcome',
       next_appt_date   as 'Date of Next appt',
       next_appt_loc    as 'Appointment Location',
       last_vl_date     as 'Date High Viral Load',
       last_vl          as 'High Viral Load Result',
       first_vl_date    as 'First-ever Viral Load Test Date',
       first_vl         as 'First-ever Viral Load Test Result',
       second_vl_date   as 'Second Viral Load Test Date',
       second_vl        as 'Second Viral Load Test Result',
       third_vl_date    as 'Third Viral Load Test Date',
       third_vl         as 'Third Viral Load Test Result',
       fourth_vl_date   as 'Fourth Viral Load Test Date',
       fourth_vl        as 'Fourth Viral Load Test Result',
       first_reg_date   as 'Initial Regimen Start Date',
       first_reg        as 'Initial Regimen',
       second_reg_date  as 'Second Regimen Start date',
       second_reg       as 'Second Regimen',
       third_reg_date   as 'Third Regimen Start Date',
       third_reg        as 'Third Regimen',
       current_reg_date as 'Current Regimen Start Date',
       current_reg      as 'Current Regimen'
FROM rpt_high_vls
ORDER BY identifier
;
