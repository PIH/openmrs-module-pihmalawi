SET sql_safe_updates = 0;
set @endDate = '2024-12-31';
set @location = 'Matandani Rural Health Center';

-- create temporary table to hold all data
drop table if exists temp_art_register;
create table temp_art_register
(
    pid                                  integer not null primary key,
    last_enrollment_start_at_location    date,
    arv_number                           varchar(255),
    all_arv_numbers                      varchar(255),
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

-- The rows in the table should be all patients who have ever been enrolled in ART at the given location by the given end date
insert into temp_art_register (pid)
select distinct patient_id from mw_art_register
where (
    (@endDate is null or @endDate >= start_date) and
    (@location is null or @location = location)
);

-- Populate from mw_art_register

update temp_art_register r
    inner join (
        select patient_id, max(start_date) as latest_start_date
        from mw_art_register
        where start_date <= @endDate
          and location = @location
        group by patient_id
    ) a on r.pid = a.patient_id
set r.last_enrollment_start_at_location = a.latest_start_date
;

update temp_art_register r
    inner join (
        select patient_id, group_concat(art_number separator ', ') as all_art_numbers
        from mw_art_register
        where start_date <= @endDate
        group by patient_id
    ) a on r.pid = a.patient_id
set r.all_arv_numbers = a.all_art_numbers
;

update temp_art_register r
inner join mw_art_register a on r.pid = a.patient_id and r.last_enrollment_start_at_location = a.start_date
set r.arv_number = a.art_number
;

update temp_art_register r
inner join (select patient_id, min(start_date) as first_start_date from mw_art_register where start_date <= @endDate group by patient_id) a on r.pid = a.patient_id
set r.art_initial_date = a.first_start_date
;

update temp_art_register r inner join mw_art_register a on r.pid = a.patient_id and r.art_initial_date = a.start_date
set r.art_initial_location = a.location
;

-- Populate from mw_patient

update temp_art_register r inner join mw_patient p on r.pid = p.patient_id
set r.given_name = p.first_name,
    r.last_name = p.last_name,
    r.birthdate = p.birthdate,
    r.current_age_yrs = TIMESTAMPDIFF(YEAR, p.birthdate, @endDate),
    r.current_age_months = TIMESTAMPDIFF(MONTH, p.birthdate, @endDate),
    r.gender = p.gender,
    r.village = p.village,
    r.traditional_authority = p.traditional_authority,
    r.district = p.district
;


-- Select out
select pid as PID,
       arv_number as 'ARV #',
       all_arv_numbers as 'All ARV #s (not filtered)',
       art_initial_date as 'ART initial date',
       art_initial_location as 'ART initial location',
       given_name as 'Given name',
       last_name as 'Last name',
       birthdate as 'Birthdate'
from temp_art_register
;
