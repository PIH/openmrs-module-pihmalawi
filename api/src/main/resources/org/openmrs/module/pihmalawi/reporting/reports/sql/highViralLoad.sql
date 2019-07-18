-- ## report_uuid = 2fe281be-3ff4-11e6-9d69-0f1641034c73
-- ## design_uuid = 3ed8dbbe-3ff4-11e6-9d69-0f1641034c73
-- ## report_name = HIV - High Viral Load Report
-- ## report_description = Report listing patients with a high viral load from last laboratory test.
-- ## parameter = endDate|End Date|java.util.Date
-- ## parameter = min_vl|Minimum Viral Load|java.lang.Integer

-- Report lists patients who have a high viral load based on the last available
-- viral load observation (simply searches obs and joins demographic information).

-- SET @endDate = '2019-06-30';
-- SET @min_vl = 1000;

# CONSTRUCT TARGET TABLE THAT WE WILL POPULATE TO REPRESENT THE FULL EXPORT

drop table if exists temp_high_vls;
create temporary table temp_high_vls
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

# IN ORDER TO POPULATE THIS TABLE, THE FIRST THING WE NEED TO DO IS TO IDENTIFY PATIENTS WHOSE MOST RECENT
# VIRAL LOAD IS HIGH.  WE ALSO NEED VIRAL LOAD DATA FOR OTHER SPECIFIC TESTS IN A PATIENT'S HISTORY (2ND, 3RD, ETC)
# SO THE NEXT THING WE WILL DO IS TO BUILD A TABLE OF VIRAL LOADS FOR EACH PATIENT, AND A SEQUENCE NUMBER FOR EACH
# THAT WILL ALLOW US TO EASILY SELECT SPECIFICALLY NUMBERED VIRAL LOADS FROM THE START OR END OF A PATIENT'S HISTORY

drop temporary table if exists temp_vl;
create temporary table temp_vl
(
    encounter_id   int primary key,
    patient_id     int,
    vl_date        date,
    vl_numeric     double,
    vl_ldl         boolean,
    vl_less_than   double,
    num_from_start int,
    num_from_end   int
);

# A VIRAL LOAD CAN HAVE MULTIPLE RESULTS - A NUMERIC, LDL, OR LESS THAN NUMERIC RESULT.  FLATTEN THESE TO A SINGLE ROW.

insert into temp_vl (encounter_id, patient_id, vl_date, vl_numeric)
select o.encounter_id, o.person_id, o.obs_datetime, max(o.value_numeric)
from obs o
         inner join concept c on o.concept_id = c.concept_id
where o.voided = 0
  and o.obs_datetime <= @endDate
  and c.uuid = '654a7694-977f-11e1-8993-905e29aff6c1' # VL Numeric Result
group by o.encounter_id, o.person_id, o.obs_datetime;

insert into temp_vl (encounter_id, patient_id, vl_date, vl_ldl)
select o.encounter_id, o.person_id, o.obs_datetime, true
from obs o
         inner join concept c on o.concept_id = c.concept_id
where o.voided = 0
  and o.obs_datetime <= @endDate
  and c.uuid = 'e97b36a2-16f5-11e6-b6ba-3e1d05defe78' # Lower than detectable limit
on duplicate key update vl_ldl = true;

insert into temp_vl (encounter_id, patient_id, vl_date, vl_less_than)
select o.encounter_id, o.person_id, o.obs_datetime, o.value_numeric
from obs o
         inner join concept c on o.concept_id = c.concept_id
where o.voided = 0
  and o.obs_datetime <= @endDate
  and c.uuid = '69e87644-5562-11e9-8647-d663bd873d93' # Less than numeric value
on duplicate key update vl_less_than = o.value_numeric;

# NEXT, PROCESS THESE ROWS AND ASSIGN SEQUENCE NUMBERS TO EACH VIRAL LOAD DATE PER PATIENT

drop temporary table if exists temp_vl_seq;
create temporary table temp_vl_seq
(
    encounter_id   int primary key,
    num_from_start int,
    num_from_end   int
);

set @row_number = 0;
set @patient_id = 0;

insert into temp_vl_seq (encounter_id, num_from_start)
select s.encounter_id, s.num
from (
         select @row_number :=
                        CASE WHEN @patient_id = patient_id THEN @row_number + 1 ELSE 1 END as num,
                @patient_id := patient_id                                                  as patient_id,
                encounter_id
         from temp_vl
         order by patient_id, vl_date asc
     ) s
;

set @row_number = 0;
set @patient_id = 0;

insert into temp_vl_seq (encounter_id, num_from_end)
select s.encounter_id, s.num
from (
         select @row_number :=
                        CASE WHEN @patient_id = patient_id THEN @row_number + 1 ELSE 1 END as num,
                @patient_id := patient_id                                                  as patient_id,
                encounter_id
         from temp_vl
         order by patient_id, vl_date desc
     ) s
on duplicate key update num_from_end = s.num
;

update temp_vl v
    inner join temp_vl_seq s on v.encounter_id = s.encounter_id
set v.num_from_start = s.num_from_start,
    v.num_from_end   = s.num_from_end;

drop temporary table temp_vl_seq;

# NOW THAT WE HAVE FLATTENED AND SEQUENCED VIRAL LOAD DATA FOR EACH PATIENT, WE CAN INITIALIZE THE EXPORT
# TABLE WITH THOSE PATIENTS WHOSE MOST RECENT VIRAL LOAD IS ABOVE THE MINIMUM REQUESTED

insert into temp_high_vls (patient_id, last_vl_date, last_vl)
select v.patient_id, v.vl_date, v.vl_numeric
from temp_vl v
         inner join patient p on v.patient_id = p.patient_id
where p.voided = 0
  and v.num_from_end = 1
  and (v.vl_numeric is not null and v.vl_numeric >= @min_vl)
;

# WE CAN ALSO USE THIS VIRAL LOAD TABLE TO POPULATE EACH OF THE SEQUENTIAL VL DATA

update temp_high_vls h
    inner join temp_vl v on h.patient_id = v.patient_id and v.num_from_start = 1
set h.first_vl_date = v.vl_date,
    h.first_vl      = if(v.vl_ldl, 'LDL', if(v.vl_less_than is not null, concat('<', convert(v.vl_less_than, char)),
                                             convert(v.vl_numeric, char)));

update temp_high_vls h
    inner join temp_vl v on h.patient_id = v.patient_id and v.num_from_start = 2
set h.second_vl_date = v.vl_date,
    h.second_vl      = if(v.vl_ldl, 'LDL', if(v.vl_less_than is not null, concat('<', convert(v.vl_less_than, char)),
                                              convert(v.vl_numeric, char)));

update temp_high_vls h
    inner join temp_vl v on h.patient_id = v.patient_id and v.num_from_start = 3
set h.third_vl_date = v.vl_date,
    h.third_vl      = if(v.vl_ldl, 'LDL', if(v.vl_less_than is not null, concat('<', convert(v.vl_less_than, char)),
                                             convert(v.vl_numeric, char)));

update temp_high_vls h
    inner join temp_vl v on h.patient_id = v.patient_id and v.num_from_start = 4
set h.fourth_vl_date = v.vl_date,
    h.fourth_vl      = if(v.vl_ldl, 'LDL', if(v.vl_less_than is not null, concat('<', convert(v.vl_less_than, char)),
                                              convert(v.vl_numeric, char)));

# NEXT IS TO POPULATE THE REST OF THE DESIRED COLUMNS.
# WE START WITH THE DEMOGRAPHIC AND PROGRAM DATA

# IDENTIFIER

# GET A SINGLE IDENTIFIER PER PATIENT, PREFERRING ARV NUMBER OVER HCC NUMBER, PREFERRED, MOST RECENTLY CREATED

update temp_high_vls v
    inner join
    (
        select p.patient_id, i.identifier
        from patient p
                 left join patient_identifier i on p.patient_id = i.patient_id
        where i.patient_identifier_id = (
            select pi.patient_identifier_id
            from patient_identifier pi
                     inner join patient_identifier_type pit on pi.identifier_type = pit.patient_identifier_type_id
            where pi.voided = 0
              and pit.name in ('ARV Number', 'HCC Number')
              and pi.patient_id = p.patient_id
            order by if(pit.name = 'ARV Number', 0, 1) asc, pi.preferred desc, pi.date_created desc
            limit 1
        )
    ) pi on pi.patient_id = v.patient_id
set v.identifier = pi.identifier;

# FIRST NAME AND LAST NAME

update temp_high_vls v
    inner join
    (
        select p.patient_id, n.given_name, n.family_name
        from patient p
                 left join person_name n on p.patient_id = n.person_id
        where n.person_name_id = (
            select pn.person_name_id
            from person_name pn
            where pn.voided = 0
              and pn.person_id = p.patient_id
            order by pn.preferred desc, pn.date_created desc
            limit 1
        )
    ) pi on pi.patient_id = v.patient_id
set v.first_name = pi.given_name,
    v.last_name  = pi.family_name
;

# GENDER AND AGE IN YEARS
# TODO: Determine if age should be "now" or at end date of report or at last vl date, etc.

update temp_high_vls v
    inner join person p on v.patient_id = p.person_id
set v.gender  = p.gender,
    v.age_yrs = TIMESTAMPDIFF(YEAR, p.birthdate, @endDate)
;

# PROGRAM STATUS AND DATE

# HERE WE GET THE MOST RECENT STATUS WITHIN THE HIV PROGRAM FOR THE PATIENT, AND THE DATE THEY ENTERED THIS STATUS

update temp_high_vls v
    inner join
    (
        select p.patient_id, ps.start_date, cn.name as state_name
        from patient p
                 inner join patient_program pp on p.patient_id = pp.patient_id
                 inner join patient_state ps on ps.patient_program_id = pp.patient_program_id
                 inner join program_workflow_state pws on ps.state = pws.program_workflow_state_id
                 left join (select n.concept_id, max(n.name) as name from concept_name n where n.locale = 'en' and n.concept_name_type = 'FULLY_SPECIFIED' AND n.voided = 0 group by n.concept_id) cn on cn.concept_id = pws.concept_id
        where ps.patient_state_id = (
            select psi.patient_state_id
            from patient_state psi
                     inner join patient_program ppi on psi.patient_program_id = ppi.patient_program_id
                     inner join program p on ppi.program_id = p.program_id
            where p.name = 'HIV Program'
              and psi.voided = 0
              and ppi.voided = 0
              and ppi.patient_id = p.patient_id
            order by psi.start_date desc, ifnull(psi.end_date, now()) desc, psi.patient_state_id desc
            limit 1
        )
    ) pi on pi.patient_id = v.patient_id
set v.outcome = pi.state_name,
    v.outcome_date  = pi.start_date
;

# LAST VISIT DATE AND NEXT APPOINTMENT DATE AND LOCATION

drop temporary table if exists temp_patient_data;
create temporary table temp_patient_data
(
    patient_id      int primary key,
    last_visit_date date
);

insert into temp_patient_data (patient_id, last_visit_date)
select e.patient_id,
       max(date(e.encounter_datetime)) as last_visit_date
    from
       encounter e
           inner join encounter_type et on e.encounter_type = et.encounter_type_id
    where
       e.voided = 0
           and et.name in ('ART_FOLLOWUP', 'EID_FOLLOWUP')
    group by
       e.patient_id;

update temp_high_vls v
    inner join temp_patient_data d on d.patient_id = v.patient_id
set v.last_visit_date = d.last_visit_date;

update temp_high_vls v
    inner join (
        select o.person_id,
               date(o.value_datetime) as next_appt_date,
               l.name as next_appt_loc
            from
               obs o
                   inner join temp_patient_data d on o.person_id = d.patient_id
                   inner join concept c on o.concept_id = c.concept_id
                   inner join encounter e on o.encounter_id = e.encounter_id
                   inner join location l on e.location_id = l.location_id
            where
               c.uuid = '6569cbd4-977f-11e1-8993-905e29aff6c1' # Appointment Date
                   and o.voided = 0
                   and date(e.encounter_datetime) = date(d.last_visit_date)
            order by o.value_datetime desc
    ) a on v.patient_id = a.person_id
set v.next_appt_date = a.next_appt_date,
    v.next_appt_loc = a.next_appt_loc
;

drop temporary table temp_patient_data;

# OUTPUT FINAL DATA SET
# THIS SELECTS FROM THE MASTER TABLE, ELIMINATES PATIENTS WHO ARE NO LONGER ACTIVE
# AND PERFORMS SOME FINAL TRANSFORMATIONS

SELECT identifier     as 'Identifier',
       first_name     as 'First',
       last_name      as 'Last',
       gender         as 'Gender',
       age_yrs        as 'Age (yrs)',
       outcome        as 'Outcome',
       outcome_date   as 'Date of last Outcome',
       next_appt_date as 'Date of Next appt',
       next_appt_loc  as 'Appointment Location',
       last_vl_date   as 'Date High Viral Load',
       last_vl        as 'High Viral Load Result',
       first_vl_date  as 'First-ever Viral Load Test Date',
       first_vl       as 'First-ever Viral Load Test Result',
       second_vl_date as 'Second Viral Load Test Date',
       second_vl      as 'Second Viral Load Test Result',
       third_vl_date  as 'Third Viral Load Test Date',
       third_vl       as 'Third Viral Load Test Result',
       fourth_vl_date as 'Fourth Viral Load Test Date',
       fourth_vl      as 'Fourth Viral Load Test Result'
    FROM
       temp_high_vls
WHERE
    outcome not in ('Patient Died', 'Patient defaulted', 'Patient transferred out')
;
