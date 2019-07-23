/************************************************************************
  CREATE A TEMPORARY TABLE THAT CAN BE USED TO RETRIEVE ALL BASIC PATIENT
  DATA, INCLUDING PRIMARY IDENTIFIER, NAME, ADDRESS, ACCOMPAGNATEUR, ETC.
  THIS EXCLUDES ALL VOIDED PATIENTS SO CAN BE USED SAFELY AS THE PRIMARY
  TABLE FOR ALL REPORTS.

  THE endDate PARAMETER IS USED PRIMARILY FOR AGE AND OTHER DATE-BASED
  CALCULATATIONS THAT MIGHT BE NEEDED

  REQUIRES: identifier_type_by_uuid
*************************************************************************/

DROP PROCEDURE IF EXISTS create_mw_patient;
#
CREATE PROCEDURE create_mw_patient(IN _endDate DATE)
BEGIN

    drop temporary table if exists mw_patient;
    create temporary table mw_patient
    (
        patient_id            int primary key,
        hcc_number            varchar(20),
        art_number            varchar(20),
        ccc_number            varchar(20),
        first_name            varchar(100),
        last_name             varchar(100),
        gender                char(1),
        birthdate             date,
        age_yrs               int,
        district              varchar(100),
        traditional_authority varchar(100),
        village               varchar(100)
    );

    -- NON-VOIDED WITH BASE DATA

    insert into mw_patient (patient_id, gender, birthdate, age_yrs)
    select p.patient_id,
           n.gender,
           n.birthdate,
           TIMESTAMPDIFF(YEAR, n.birthdate, _endDate)
        from
           patient p
               inner join person n on p.patient_id = n.person_id
        where
           p.voided = 0
               and n.voided = 0;

    -- FIRST NAME AND LAST NAME

    update mw_patient p
        inner join
        (
            select p.patient_id,
                   n.given_name,
                   n.family_name
                from
                   patient p
                       left join person_name n on p.patient_id = n.person_id
                where
                   n.person_name_id = (
                       select pn.person_name_id
                           from
                              person_name pn
                           where
                              pn.voided = 0
                                  and pn.person_id = p.patient_id
                           order by
                              pn.preferred desc,
                              pn.date_created desc
                           limit
                              1
                   )
        ) pi on pi.patient_id = p.patient_id
    set p.first_name = pi.given_name,
        p.last_name  = pi.family_name;

    -- ADDRESS DATA

    update mw_patient p
        inner join
        (
            select p.patient_id,
                   a.state_province,  -- District
                   a.county_district, -- Traditional Authority
                   a.city_village     -- Village
                from
                   patient p
                       left join person_address a on p.patient_id = a.person_id
                where
                   a.person_address_id = (
                       select pa.person_address_id
                           from
                              person_address pa
                           where
                              pa.voided = 0
                                  and pa.person_id = p.patient_id
                           order by
                              pa.preferred desc,
                              pa.date_created desc
                           limit
                              1
                   )
        ) pi on pi.patient_id = p.patient_id
    set p.district              = pi.state_province,
        p.traditional_authority = pi.county_district,
        p.village               = pi.city_village;

    -- IDENTIFIERS

    set @arvNumber = identifier_type_by_uuid('66784d84-977f-11e1-8993-905e29aff6c1');
    set @hccNumber = identifier_type_by_uuid('66786256-977f-11e1-8993-905e29aff6c1');
    set @ccNumber = identifier_type_by_uuid('11a76c3e-1db8-4d16-9252-9a18b5ed1843');

    update mw_patient mp
        inner join
        (
            select p.patient_id,
                   i.identifier
                from
                   patient p
                       left join patient_identifier i on p.patient_id = i.patient_id
                where
                   i.patient_identifier_id = (
                       select pi.patient_identifier_id
                           from
                              patient_identifier pi
                           where
                              pi.voided = 0
                                  and pi.identifier_type = @arvNumber
                                  and pi.patient_id = p.patient_id
                           order by
                              pi.preferred desc,
                              pi.date_created desc
                           limit
                              1
                   )
        ) pi on pi.patient_id = mp.patient_id
    set mp.art_number = pi.identifier;

    update mw_patient mp
        inner join
        (
            select p.patient_id,
                   i.identifier
                from
                   patient p
                       left join patient_identifier i on p.patient_id = i.patient_id
                where
                   i.patient_identifier_id = (
                       select pi.patient_identifier_id
                           from
                              patient_identifier pi
                           where
                              pi.voided = 0
                                  and pi.identifier_type = @hccNumber
                                  and pi.patient_id = p.patient_id
                           order by
                              pi.preferred desc,
                              pi.date_created desc
                           limit
                              1
                   )
        ) pi on pi.patient_id = mp.patient_id
    set mp.hcc_number = pi.identifier;

    update mw_patient mp
        inner join
        (
            select p.patient_id,
                   i.identifier
                from
                   patient p
                       left join patient_identifier i on p.patient_id = i.patient_id
                where
                   i.patient_identifier_id = (
                       select pi.patient_identifier_id
                           from
                              patient_identifier pi
                           where
                              pi.voided = 0
                                  and pi.identifier_type = @ccNumber
                                  and pi.patient_id = p.patient_id
                           order by
                              pi.preferred desc,
                              pi.date_created desc
                           limit
                              1
                   )
        ) pi on pi.patient_id = mp.patient_id
    set mp.ccc_number = pi.identifier;

END
#
