
/************************************************************************

  TRACE Report Dataset
  Requires Pentaho Warehouse tables
  Expected parameters, which will be passed in via the Evaluation Context, are:

  -- set @endDate = now();
  -- set @location = 'Matandani Rural Health Center';

*************************************************************************/

CALL create_rpt_identifiers(@location);
CALL create_rpt_active_eid(@endDate, @location);
CALL create_rpt_active_art(@endDate, @location);
CALL create_rpt_active_ncd(@endDate, @location);
CALL create_rpt_active_pdc(@endDate, @location);
CALL create_rpt_priority_patients(@endDate);

drop TEMPORARY table if exists rpt_trace_criteria;
create TEMPORARY table rpt_trace_criteria
(
    patient_id int not null,
    criteria   varchar(50)
);
create index rpt_trace_criteria_patient_id_idx on rpt_trace_criteria(patient_id);

-- Late ART
insert into rpt_trace_criteria(patient_id, criteria)
select  patient_id, 'LATE_ART'
from    rpt_active_art
where   days_late_appt is not null
and     days_late_appt >= 14
and     days_late_appt < 56
;

-- Late EID
insert into rpt_trace_criteria(patient_id, criteria)
select  patient_id, 'LATE_EID'
from    rpt_active_eid
where   days_late_appt is not null
and     days_late_appt >= 14
and     days_late_appt < 56
;

-- Late NCD
insert into rpt_trace_criteria(patient_id, criteria)
select  patient_id, 'LATE_NCD'
from    rpt_active_ncd
where   days_late_appt is not null
and     days_late_appt >= 14
and     days_late_appt < 56
;

-- Late PDC
insert into rpt_trace_criteria(patient_id, criteria)
select  patient_id, 'LATE_PDC'
from    rpt_active_pdc
where   days_late_appt is not null
and     days_late_appt >= 14
and     days_late_appt < 56
;

drop TEMPORARY table if exists rpt_active_mh;
create TEMPORARY table rpt_active_mh
(
    patient_id int not null primary key,
    mh_number  varchar(50)
);

insert into rpt_active_mh (patient_id)
select distinct patient_id
from    omrs_program_enrollment p
where   p.program = 'Mental Health Care Program'
and     p.location = @location
and     (p.completion_date is null or p.completion_date > @endDate);

update rpt_active_mh mh
inner join  (
        select  i.patient_id, i.identifier
        from    omrs_patient_identifier i
        where   i.patient_identifier_id = (
            select      i1.patient_identifier_id
            from        omrs_patient_identifier i1
            where       i1.patient_id = i.patient_id
            and         i1.location = @location
            and         i1.type = 'Chronic Care Number'
            order by    i1.date_created desc
            limit 1
        )
) i on mh.patient_id = i.patient_id
set mh.mh_number = i.identifier;

SELECT        t.patient_id,
              p.village,
              p.traditional_authority,
              p.chw,
              p.first_name,
              p.last_name,
              p.birthdate,
              i.eid_number,
              i.art_number,
              i.ncd_number,
              i.pdc_number,
              mh.mh_number,
              art.last_visit_date as art_last_visit_date,
              art.last_appt_date as art_last_appt_date,
              round(art.days_late_appt / 7, 1) as art_weeks_out_of_care,
              eid.last_visit_date as eid_last_visit_date,
              eid.last_appt_date as eid_last_appt_date,
              round(eid.days_late_appt / 7, 1) as eid_weeks_out_of_care,
              ncd.last_visit_date as ncd_last_visit_date,
              ncd.last_appt_date as ncd_last_appt_date,
              ncd.last_visit_type as ncd_last_visit_type,
              round(ncd.days_late_appt / 7, 1) as ncd_weeks_out_of_care,
              pdc.last_visit_date as pdc_last_visit_date,
              pdc.last_appt_date as pdc_last_appt_date,
              pdc.last_visit_type as pdc_last_visit_type,
              round(pdc.days_late_appt / 7, 1) as pdc_weeks_out_of_care,
              TRIM(TRAILING ',' FROM concat(if(art.patient_id is null, '', 'HIV, '), if(eid.patient_id is null, '', 'EID, '), d.diagnoses)) as diagnoses,
              y.pdc_conditions as pdc_conditions,
              z.pdc_non_coded_conditions as pdc_non_coded_conditions,
              c.priority_criteria,
              group_concat(t.criteria ORDER BY t.criteria asc SEPARATOR ', ') as trace_criteria
FROM          rpt_trace_criteria t
INNER JOIN    mw_patient p on t.patient_id = p.patient_id
LEFT JOIN     rpt_identifiers i on i.patient_id = p.patient_id
LEFT JOIN     rpt_active_art art on art.patient_id = p.patient_id
LEFT JOIN     rpt_active_eid eid on eid.patient_id = p.patient_id
LEFT JOIN     rpt_active_ncd ncd on ncd.patient_id = p.patient_id
LEFT JOIN     rpt_active_pdc pdc on pdc.patient_id = p.patient_id
LEFT JOIN     rpt_active_mh mh on mh.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, group_concat(priority ORDER BY priority asc SEPARATOR ', ') as priority_criteria from rpt_priority_patients GROUP BY patient_id) c on c.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, group_concat(diagnosis ORDER BY diagnosis asc SEPARATOR ', ') as diagnoses from mw_ncd_diagnoses where diagnosis_date <= @endDate GROUP BY patient_id) d on d.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, group_concat(diagnosis ORDER BY diagnosis asc SEPARATOR ', ') as pdc_conditions from mw_pdc_diagnoses where diagnosis != 'Diagnosis, non-coded' and visit_date <= @endDate GROUP BY patient_id) y on y.patient_id = p.patient_id
LEFT JOIN     ( select patient_id, comments as pdc_non_coded_conditions from mw_pdc_diagnoses where diagnosis = 'Diagnosis, non-coded' and comments is not null and visit_date <= @endDate GROUP BY patient_id) z on z.patient_id = p.patient_id
GROUP BY      t.patient_id
ORDER BY      if(p.chw is null, 1, 0), p.chw, p.village, p.last_name
;
