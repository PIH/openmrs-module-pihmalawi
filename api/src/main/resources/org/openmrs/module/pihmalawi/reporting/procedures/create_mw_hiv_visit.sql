/************************************************************************
  CREATES A TEMPORARY TABLE OF ALL HIV VISITS FOR EACH PATIENT
  EXCLUDES VOIDED.  THESE REPRESENT ROWS IN THE ART MASTERCARD AND
  EXPOSED CHILD MASTERCARD

  REQUIRES: encounter_type_by_uuid
*************************************************************************/
DROP PROCEDURE IF EXISTS create_mw_hiv_visit;
CREATE PROCEDURE create_mw_hiv_visit(IN _endDate DATE)
BEGIN

    drop temporary table if exists mw_hiv_visit;
    create temporary table mw_hiv_visit
    (
        encounter_id   int primary key,
        patient_id     int,
        location_id    int,
        encounter_date date,
        visit_type     varchar(20),
        next_appt_date date,
        num_from_start int,
        num_from_end   int
    );

    set @artFollowup = encounter_type_by_uuid('664b8650-977f-11e1-8993-905e29aff6c1');
    set @eidFollowup = encounter_type_by_uuid('664b8d44-977f-11e1-8993-905e29aff6c1');
    set @preArtFollowup = encounter_type_by_uuid('664b8812-977f-11e1-8993-905e29aff6c1');
    set @nextApptDate = concept_by_uuid('6569cbd4-977f-11e1-8993-905e29aff6c1');

    # VISIT TABLE POPULATION

    insert into mw_hiv_visit (encounter_id, patient_id, location_id, encounter_date, visit_type)
    select e.encounter_id,
           e.patient_id,
           e.location_id,
           date(e.encounter_datetime),
           if(e.encounter_type = @artFollowup, 'ART', 'EID')
    from encounter e
             inner join mw_patient p on e.patient_id = p.patient_id
    where e.voided = 0
      and e.encounter_type in (@artFollowup, @eidFollowup, @preArtFollowup);

    # ADD SPECIFIC OBS THAT WE WANT TO QUERY FOR EACH ENCOUNTER

    update mw_hiv_visit v
    inner join (
            select e.patient_id, date(e.encounter_datetime) as encounter_date, date(o.value_datetime) as appt_date
            from encounter e
                     inner join obs o
                                on o.encounter_id = e.encounter_id and o.concept_id = @nextApptDate and e.voided = 0 and
                                   o.voided = 0
        ) vo on v.patient_id = vo.patient_id and v.encounter_date = vo.encounter_date
    set v.next_appt_date = vo.appt_date;

    # SEQUENCE NUMBERS THAT ALLOW US TO EASILY RETRIEVE SPECIFIC OCCURANCES (EG. CURRENT)

    drop temporary table if exists mw_enc_seq;
    create temporary table mw_enc_seq
    (
        encounter_id   int primary key,
        patient_id     int,
        num_from_start int,
        num_from_end   int
    );

    set @row_number = 0;
    set @patient_id = 0;

    insert into mw_enc_seq (encounter_id, patient_id, num_from_start)
    select s.encounter_id,
           s.patient_id,
           s.num
    from (
             select @row_number := if(@patient_id = patient_id, @row_number + 1, 1) as num,
                    @patient_id := patient_id                                       as patient_id,
                    encounter_id
             from mw_hiv_visit
             order by patient_id,
                      encounter_date asc,
                      encounter_id asc
         ) s;

    set @row_number = 0;
    set @patient_id = 0;

    update mw_enc_seq r
        inner join (
            select s.encounter_id,
                   s.num
            from (
                     select @row_number := if(@patient_id = patient_id, @row_number + 1, 1) as num,
                            @patient_id := patient_id                                       as patient_id,
                            encounter_id
                     from mw_hiv_visit
                     order by patient_id,
                              encounter_date desc,
                              encounter_id desc
                 ) s
        ) ri on r.encounter_id = ri.encounter_id
    set r.num_from_end = ri.num;

    update mw_hiv_visit s
        inner join mw_enc_seq r on s.encounter_id = r.encounter_id
    set s.num_from_start = r.num_from_start,
        s.num_from_end   = r.num_from_end;

    drop table mw_enc_seq;

END
