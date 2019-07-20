/************************************************************************
  CREATES A TEMPORARY TABLE OF ALL HIV ENROLLMENTS FOR EACH PATIENT
  EXCLUDES VOIDED, AND ADDS IN THE RELEVANT IDENTIFIERS THAT ARE
  ALSO ASSOCIATED WITH THE LOCATION OF THE ENROLLMENT

  REQUIRES: program_by_uuid, identifier_type_by_uuid
*************************************************************************/
DROP PROCEDURE IF EXISTS create_mw_hiv_enrollment;
CREATE PROCEDURE create_mw_hiv_enrollment(IN _endDate DATE)
BEGIN

    drop temporary table if exists mw_hiv_enrollment;
    create temporary table mw_hiv_enrollment
    (
        enrollment_id  int primary key,
        patient_id     int,
        location_id    int,
        hcc_number     varchar(20),
        art_number     varchar(20),
        date_enrolled  date,
        date_completed date,
        active         boolean
    );

    set @arvNumber = identifier_type_by_uuid('66784d84-977f-11e1-8993-905e29aff6c1');
    set @hccNumber = identifier_type_by_uuid('66786256-977f-11e1-8993-905e29aff6c1');
    set @hivProgram = program_by_uuid('66850b0a-977f-11e1-8993-905e29aff6c1');

    insert into mw_hiv_enrollment (enrollment_id, patient_id, location_id, date_enrolled, date_completed, active)
    select pp.patient_program_id,
           pp.patient_id,
           pp.location_id,
           pp.date_enrolled,
           pp.date_completed,
           (date(pp.date_enrolled) <= @endDate &&
            (pp.date_completed is null or date(pp.date_completed) >= @endDate)) as active
    from patient_program pp
             inner join mw_patient p on pp.patient_id = p.patient_id
    where pp.voided = 0
      and pp.program_id = @hivProgram;

    # IDENTIFIERS ASSOCIATED WITH THE LOCATION OF THE ENROLLMENT

    update mw_hiv_enrollment e
        inner join
        (select patient_id, location_id, identifier
         from patient_identifier pi
         where pi.voided = 0
           and pi.identifier_type = @arvNumber
         order by pi.preferred asc,
                  pi.date_created asc) pi
        on e.patient_id = pi.patient_id and e.location_id = pi.location_id
    set e.art_number = pi.identifier;

    update mw_hiv_enrollment e
        inner join
        (select patient_id, location_id, identifier
         from patient_identifier pi
         where pi.voided = 0
           and pi.identifier_type = @hccNumber
         order by pi.preferred asc,
                  pi.date_created asc) pi
        on e.patient_id = pi.patient_id and e.location_id = pi.location_id
    set e.hcc_number = pi.identifier;

END
