CREATE DEFINER=`root`@`localhost` PROCEDURE `updateNextAppointDates`(IN _recent_nr_of_months INT)
  BEGIN
    DECLARE missing_obs_date DATE;
    DECLARE missing_enc_id INT;
    DECLARE missing_patient_id INT;
    DECLARE finished INTEGER DEFAULT 0;

    SET @next_appt_date_concept_id = 5096;
    set @update_datetime = now();

    DROP TEMPORARY TABLE IF EXISTS enc_with_no_appt;
    create temporary table enc_with_no_appt (
      encounter_id INT not null primary key,
      patient_id INT,
      encounter_datetime DATE,
      encounter_type_id INT,
      encounter_type_name varchar(50),
      form_id INT,
      date_created DATE,
      next_appointment_date DATE,
      from_enc_id INT,
      from_enc_name varchar(50),
      from_enc_date DATE
    );

    insert into enc_with_no_appt(
      encounter_id,
      patient_id,
      encounter_datetime,
      encounter_type_id,
      encounter_type_name,
      form_id,
      date_created,
      next_appointment_date)
      select
        e.encounter_id,
        s.person_id,
        e.encounter_datetime,
        e.encounter_type,
        t.name,
        e.form_id,
        e.date_created,
        o.value_datetime
      from encounter e JOIN person as s on (e.patient_id = s.person_id)
        JOIN encounter_type t ON (e.encounter_type = t.encounter_type_id)
        left outer join obs as o on (e.encounter_id = o.encounter_id and o.concept_id=@next_appt_date_concept_id)
      where e.voided=0
            and t.name in ('PALLIATIVE_FOLLOWUP', 'ASTHMA_FOLLOWUP', 'PART_FOLLOWUP',
                                                  'EXPOSED_CHILD_FOLLOWUP', 'EPILEPSY_FOLLOWUP', 'CHEMOTHERAPY',
                                                  'DIABETES HYPERTENSION FOLLOWUP', 'MENTAL_HEALTH_FOLLOWUP',
                                                  'CHRONIC_CARE_FOLLOWUP', 'ART_FOLLOWUP', 'PDC_FOLLOWUP',
                           'CHF_FOLLOWUP', 'CKD_FOLLOWUP', 'NCD_OTHER_FOLLOWUP')
            and TIMESTAMPDIFF(MONTH, e.encounter_datetime, now()) < _recent_nr_of_months
            and o.value_datetime is null;

    -- update the Next Appointment Date from another followup encounter on the same day
    update enc_with_no_appt e
      inner join
      (  select
           o.value_datetime as appt_date ,
           o.encounter_id as from_enc_id,
           enc.encounter_datetime,
           t.name as from_enc_type,
           o.person_id
         from obs o, encounter enc, encounter_type t
         where o.encounter_id = enc.encounter_id and enc.encounter_type = t.encounter_type_id
               and concept_id = @next_appt_date_concept_id and value_datetime is not null
         order by obs_datetime desc) ob
        on e.patient_id = ob.person_id and Date(e.encounter_datetime)=Date(ob.encounter_datetime)
    set
      e.next_appointment_date = ob.appt_date,
      e.from_enc_id = ob.from_enc_id,
      e.from_enc_name=ob.from_enc_type,
      e.from_enc_date=ob.encounter_datetime;

    -- create new Next Appointment Date obs and attach them to the followup encounter that did not have them
    insert into obs(
      person_id,
      concept_id,
      encounter_id,
      obs_datetime,
      value_datetime,
      comments,
      creator,
      date_created,
      uuid )
      select
        patient_id,
        @next_appt_date_concept_id,
        encounter_id,
        CONVERT(encounter_datetime, DATETIME),
        CONVERT(next_appointment_date, DATETIME),
        concat("from encounter with id=", from_enc_id, "; from encounter: ", from_enc_name, "; from date: ", from_enc_date),
        1,
        @update_datetime,
        uuid()
      from enc_with_no_appt
      where next_appointment_date is not null;
  END
