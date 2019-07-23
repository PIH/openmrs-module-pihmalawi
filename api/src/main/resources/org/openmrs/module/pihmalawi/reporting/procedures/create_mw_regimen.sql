/************************************************************************
  CREATES TWO TEMPORARY TABLES OF ALL REGIMENS FOR EACH PATIENT
  GROUPS REGIMEN OBS AND REGIMEN DATE OBS INTO A SINGLE ROW
  ADDS A SEQUENCE NUMBER FROM START AND END TO EASILY DETERMINE
  REGIMEN HISTORY AND TO PULL OUT SPECIFIC REGIMENS WITHIN THE PATIENT HISTORY

  REQUIRES: concept_by_uuid
*************************************************************************/

DROP PROCEDURE IF EXISTS create_mw_regimen;
#
CREATE PROCEDURE create_mw_regimen(IN _endDate DATE)
BEGIN

    drop temporary table if exists mw_regimen;
    create temporary table mw_regimen
    (
        regimen_obs_id      int primary key,
        encounter_id        int,
        patient_id          int,
        regimen_question    int,
        regimen_answer      int,
        regimen_date        datetime,
        regimen_date_obs_id int
    );

    drop temporary table if exists mw_regimen_change;
    create temporary table mw_regimen_change
    (
        patient_id      int,
        regimen_concept int,
        regimen_date    date,
        num_from_start  int,
        num_from_end    int,
        regimen_name    varchar(10),
        regimen_line    int
    );

    -- DEFINE VARIABLES FOR SPECIFIC CONCEPT QUESTIONS TO LOOK UP

    set @arvReceived = concept_by_uuid('657ac57e-977f-11e1-8993-905e29aff6c1');
    set @arvChange1 = concept_by_uuid('657ac678-977f-11e1-8993-905e29aff6c1');
    set @arvChange2 = concept_by_uuid('657ac7c2-977f-11e1-8993-905e29aff6c1');
    set @arvChange3 = concept_by_uuid('657ac8d0-977f-11e1-8993-905e29aff6c1');
    set @arvChangeDate1 = concept_by_uuid('656fbe36-977f-11e1-8993-905e29aff6c1');
    set @arvChangeDate2 = concept_by_uuid('655fabfe-977f-11e1-8993-905e29aff6c1');
    set @arvChangeDate3 = concept_by_uuid('655fad02-977f-11e1-8993-905e29aff6c1');

    -- ADD ALL OBSERVATIONS THE RECORD THE PATIENT'S ART REGIMEN

    insert into mw_regimen (regimen_obs_id, encounter_id, patient_id, regimen_question, regimen_answer, regimen_date)
    select o.obs_id,
           o.encounter_id,
           o.person_id,
           o.concept_id,
           o.value_coded,
           o.obs_datetime
        from
           obs o
        where
           o.voided = 0
               and o.obs_datetime <= _endDate
               and o.concept_id in (@arvReceived, @arvChange1, @arvChange2, @arvChange3);

    -- FOR THOSE THAT HAVE SPECIFIC REGIMEN DATE OBS THAT MIGHT BE FILLED OUT, PULL THESE IN IF THEY EXIST

    update mw_regimen r
        inner join obs o on r.encounter_id = o.encounter_id
    set r.regimen_date_obs_id = o.obs_id,
        r.regimen_date        = o.value_datetime
    where o.voided = 0
      and o.concept_id = @arvChangeDate1
      and r.regimen_question = @arvChange1;

    update mw_regimen r
        inner join obs o on r.encounter_id = o.encounter_id
    set r.regimen_date_obs_id = o.obs_id,
        r.regimen_date        = o.value_datetime
    where o.voided = 0
      and o.concept_id = @arvChangeDate2
      and r.regimen_question = @arvChange2;

    update mw_regimen r
        inner join obs o on r.encounter_id = o.encounter_id
    set r.regimen_date_obs_id = o.obs_id,
        r.regimen_date        = o.value_datetime
    where o.voided = 0
      and o.concept_id = @arvChangeDate3
      and r.regimen_question = @arvChange3;

    -- ITERATE OVER EACH REGIMEN, AND SEQUENTIALLY NUMBER THOSE THAT REPRESENT CHANGES FOR A PATIENT

    drop temporary table if exists mw_regimen_seq;
    create temporary table mw_regimen_seq
    (
        regimen_obs_id int primary key,
        patient_id     int,
        regimen        int,
        regimen_date   date,
        num_from_start int,
        num_from_end   int
    );

    set @row_number = 0;
    set @patient_id = 0;
    set @last_regimen = null;

    insert into mw_regimen_seq (regimen_obs_id, patient_id, regimen, regimen_date, num_from_start)
    select s.regimen_obs_id,
           s.patient_id,
           s.regimen,
           s.regimen_date,
           s.num
        from
           (
               select @row_number :=
                              if(@patient_id = patient_id,
                                 if(@last_regimen = regimen_answer, @row_number, @row_number + 1),
                                 1)                   as num,
                      @patient_id := patient_id       as patient_id,
                      @last_regimen := regimen_answer as regimen,
                      regimen_date,
                      regimen_obs_id
                   from
                      mw_regimen
                   order by
                      patient_id,
                      regimen_date asc
           ) s;

    set @row_number = 0;
    set @patient_id = 0;
    set @last_regimen = null;

    update mw_regimen_seq r
        inner join (
            select s.regimen_obs_id,
                   s.num
                from
                   (
                       select @row_number := if(@patient_id = patient_id,
                                                if(@last_regimen = regimen_answer, @row_number, @row_number + 1),
                                                1)      as num,
                              @patient_id := patient_id as patient_id,
                              @last_regimen := regimen_answer,
                              regimen_obs_id
                           from
                              mw_regimen
                           order by
                              patient_id,
                              regimen_date desc
                   ) s
        ) ri on r.regimen_obs_id = ri.regimen_obs_id
    set r.num_from_end = ri.num;

    -- POPULATE REGIMEN CHANGE TABLE WITH
    -- ROWS THE REPRESENT THE FIRST DATE OF EACH REGIMEN CHANGE
    -- AND POPULATE WITH THE NAME AND LINE OF EACH REGIMEN

    insert into mw_regimen_change (patient_id, regimen_concept, regimen_date, num_from_start, num_from_end)
    select s.patient_id,
           s.regimen,
           min(s.regimen_date) as regimen_date,
           s.num_from_start,
           s.num_from_end
        from
           mw_regimen_seq s
        group by
           s.patient_id,
           s.regimen,
           s.num_from_start,
           s.num_from_end;

    -- DEFINE VARIABLES THE SPECIFIC REGIMENS

    update mw_regimen_change set regimen_name = '0A', regimen_line = 1 where regimen_concept = concept_by_uuid('d5930c3a-cb57-11e5-9956-625662870761');
    update mw_regimen_change set regimen_name = '0P', regimen_line = 1 where regimen_concept = concept_by_uuid('d59315a4-cb57-11e5-9956-625662870761');
    update mw_regimen_change set regimen_name = '1A', regimen_line = 1 where regimen_concept = concept_by_uuid('657ab1ba-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '1P', regimen_line = 1 where regimen_concept = concept_by_uuid('657ab372-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '2A', regimen_line = 1 where regimen_concept = concept_by_uuid('657ab520-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '2P', regimen_line = 1 where regimen_concept = concept_by_uuid('657ab6d8-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '3A', regimen_line = 1 where regimen_concept = concept_by_uuid('657ab886-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '3P', regimen_line = 1 where regimen_concept = concept_by_uuid('657aba3e-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '4A', regimen_line = 1 where regimen_concept = concept_by_uuid('657abd9a-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '4P', regimen_line = 1 where regimen_concept = concept_by_uuid('657abf48-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '5A', regimen_line = 1 where regimen_concept = concept_by_uuid('657ac056-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '6A', regimen_line = 1 where regimen_concept = concept_by_uuid('657ac164-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '7A', regimen_line = 2 where regimen_concept = concept_by_uuid('657ac268-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '8A', regimen_line = 2 where regimen_concept = concept_by_uuid('657ac36c-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '9A', regimen_line = 2 where regimen_concept = concept_by_uuid('8a795372-ba39-11e6-91a8-5622a9e78e10');
    update mw_regimen_change set regimen_name = '9P', regimen_line = 2 where regimen_concept = concept_by_uuid('657ac470-977f-11e1-8993-905e29aff6c1');
    update mw_regimen_change set regimen_name = '10A', regimen_line = 2 where regimen_concept = concept_by_uuid('7ebc782a-baa2-11e6-91a8-5622a9e78e10');
    update mw_regimen_change set regimen_name = '11A', regimen_line = 2 where regimen_concept = concept_by_uuid('8bb7294e-baa2-11e6-91a8-5622a9e78e10');
    update mw_regimen_change set regimen_name = '11P', regimen_line = 2 where regimen_concept = concept_by_uuid('91bcdad2-baa2-11e6-91a8-5622a9e78e10');
    update mw_regimen_change set regimen_name = '12A', regimen_line = 3 where regimen_concept = concept_by_uuid('43b86ce6-dc3f-11e8-9f8b-f2801f1b9fd1');
    update mw_regimen_change set regimen_name = '13A', regimen_line = 1 where regimen_concept = concept_by_uuid('53009e3a-dc3f-11e8-9f8b-f2801f1b9fd1');
    update mw_regimen_change set regimen_name = '14A', regimen_line = 1 where regimen_concept = concept_by_uuid('5e16f0b2-dc3f-11e8-9f8b-f2801f1b9fd1');
    update mw_regimen_change set regimen_name = '15A', regimen_line = 1 where regimen_concept = concept_by_uuid('6764fc5e-dc3f-11e8-9f8b-f2801f1b9fd1');
    update mw_regimen_change set regimen_name = 'NS' where regimen_name is null;

    drop temporary table mw_regimen_seq;

END
#
