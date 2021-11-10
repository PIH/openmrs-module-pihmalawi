/************************************************************************
  CREATES A TEMPORARY TABLE OF ALL VIRAL LOADS FOR EACH PATIENT

  REQUIRES: concept_by_uuid
*************************************************************************/

DROP PROCEDURE IF EXISTS create_mw_viral_load;
#
CREATE PROCEDURE create_mw_viral_load(IN _endDate DATE)
BEGIN

    drop temporary table if exists mw_viral_load;
    create temporary table mw_viral_load
    (
        encounter_id   int primary key,
        patient_id     int,
        vl_date        date,
        vl_numeric     double,
        vl_ldl         boolean,
        vl_less_than   double,
        value_display  varchar(20),
        num_from_start int,
        num_from_end   int
    );

    -- A VIRAL LOAD CAN HAVE MULTIPLE RESULTS
    -- A NUMERIC, LDL, OR LESS THAN NUMERIC RESULT.  FLATTEN THESE TO A SINGLE ROW.

    set @vlNumeric = concept_by_uuid('654a7694-977f-11e1-8993-905e29aff6c1');
    set @ldl = concept_by_uuid('e97b36a2-16f5-11e6-b6ba-3e1d05defe78');
    set @lessThanNumeric = concept_by_uuid('69e87644-5562-11e9-8647-d663bd873d93');

    insert into mw_viral_load (encounter_id, patient_id, vl_date, vl_numeric)
    select o.encounter_id,
           o.person_id,
           o.obs_datetime,
           max(o.value_numeric)
        from
           obs o
        where
           o.voided = 0 and o.obs_datetime <= _endDate and o.concept_id = @vlNumeric
        group by
           o.encounter_id,
           o.person_id;

    insert into mw_viral_load (encounter_id, patient_id, vl_date, vl_ldl)
    select o.encounter_id,
           o.person_id,
           o.obs_datetime,
           true
        from
           obs o
        where
           o.voided = 0 and o.obs_datetime <= @endDate and o.concept_id = @ldl
        on duplicate key update
           vl_ldl = true;

    insert into mw_viral_load (encounter_id, patient_id, vl_date, vl_less_than)
    select o.encounter_id,
           o.person_id,
           o.obs_datetime,
           o.value_numeric
        from
           obs o
        where
           o.voided = 0 and o.obs_datetime <= @endDate and o.concept_id = @lessThanNumeric
        on duplicate key update
           vl_less_than = o.value_numeric;

    -- NEXT, PROCESS THESE ROWS AND ASSIGN SEQUENCE NUMBERS TO EACH VIRAL LOAD DATE PER PATIENT

    drop temporary table if exists mw_viral_load_seq;
    create temporary table mw_viral_load_seq
    (
        encounter_id   int primary key,
        num_from_start int,
        num_from_end   int
    );

    set @row_number = 0;
    set @patient_id = 0;

    insert into mw_viral_load_seq (encounter_id, num_from_start)
    select s.encounter_id,
           s.num
        from
           (
               select @row_number := if(@patient_id = patient_id, @row_number + 1, 1) as num,
                      @patient_id := patient_id                                       as patient_id,
                      encounter_id
                   from
                      mw_viral_load
                   order by
                      patient_id,
                      vl_date asc
           ) s;

    set @row_number = 0;
    set @patient_id = 0;

    insert into mw_viral_load_seq (encounter_id, num_from_end)
    select s.encounter_id,
           s.num
        from
           (
               select @row_number := if(@patient_id = patient_id, @row_number + 1, 1) as num,
                      @patient_id := patient_id                                       as patient_id,
                      encounter_id
                   from
                      mw_viral_load
                   order by
                      patient_id,
                      vl_date desc
           ) s
        on duplicate key update
           num_from_end = s.num;

    update mw_viral_load v
        inner join mw_viral_load_seq s on v.encounter_id = s.encounter_id
    set v.num_from_start = s.num_from_start,
        v.num_from_end   = s.num_from_end;

    drop temporary table mw_viral_load_seq;

    -- ADD A DISPLAY COLUMN THAT CONSOLIDATES ALL OF THE RESULT TYPES

    update mw_viral_load
        set value_display = if(vl_ldl, 'LDL', if(vl_less_than is not null, concat('<', convert(vl_less_than, char)), convert(vl_numeric, char)));

END
#
