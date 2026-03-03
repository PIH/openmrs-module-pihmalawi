# There are 2 concepts that have no names, but do have a description.  Add a name to each of these based on the description.

insert into concept_name (concept_id, name, locale, concept_name_type, creator, date_created, uuid)
select
    d.concept_id, d.description, 'en', 'FULLY_SPECIFIED', 1, now(), uuid()
from concept_description d
inner join
(
    select c.concept_id, count(n.concept_name_id) as num_names
    from concept c
    left join concept_name n on c.concept_id = n.concept_id
    group by c.concept_id
    having num_names = 0
) c on d.concept_id = c.concept_id
;

# There is 1 concept that only has a voided name.  Un-void it.
update concept_name n
    inner join
    (
        select c.concept_id, count(n1.concept_name_id) as num
        from concept c
        left join concept_name n1 on c.concept_id = n1.concept_id
        group by c.concept_id
    ) names on n.concept_id = names.concept_id
    inner join
    (
        select c.concept_id, count(n2.concept_name_id) as num
        from concept c
        left join concept_name n2 on c.concept_id = n2.concept_id
        where voided = 1
        group by c.concept_id
    ) voided_names on n.concept_id = voided_names.concept_id
set n.voided = 0, n.voided_by = null, n.date_voided = null, n.void_reason = null, n.date_changed = now(), n.changed_by = 1
where names.num = 1 and voided_names.num = 1
;

# There are 4 concepts that have english, preferred names, but none that are fully-specified
create temporary table tmp_fsn_to_create as
select concept_id from concept c
where c.concept_id not in (
    select concept_id from concept_name n where n.voided = 0 and n.locale = 'en' and n.concept_name_type = 'FULLY_SPECIFIED'
);

update concept_name n
inner join tmp_fsn_to_create t on n.concept_id = t.concept_id
set n.concept_name_type = 'FULLY_SPECIFIED'
where n.locale = 'en' and n.locale_preferred = 1;

drop temporary table tmp_fsn_to_create;

# There are 3 concepts whose uuids appear to have been copied from ciel incorrectly and are invalid for openmrs
# These are getting set up in MasterCardConcepts.java and have references in htn_dm_visit.xml (commented out), both of which need a corresponding fix.
# Also, there are some commented out references to these uuids in , which should be updated or removed
update concept set uuid = '72247AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '72247AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Bisoprolol
update concept set uuid = '82411AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '82411AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Pravastatin
update concept set uuid = '83936AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '83936AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Simvastatin

# There are a handful of concepts that have duplicate concept names in the same locale

# 6 are SHORT names that duplicate FULLY_SPECIFIED names
update concept_name n
    inner join concept_name fsn
    on n.concept_id = fsn.concept_id and n.locale = fsn.locale and n.name = fsn.name
set n.voided = 1, n.date_voided = now(), n.void_reason = 'Duplicate name in locale'
where n.concept_name_type != 'FULLY_SPECIFIED'
  and fsn.concept_name_type = 'FULLY_SPECIFIED'
  and fsn.voided = 0
  and n.voided = 0;

# 2 are index terms that duplicate SHORT names
update concept_name n
    inner join concept_name sn
    on n.concept_id = sn.concept_id and n.locale = sn.locale and n.name = sn.name
set n.voided = 1, n.date_voided = now(), n.void_reason = 'Duplicate name in locale'
where sn.concept_name_type = 'SHORT'
  and n.concept_name_type is null
  and sn.voided = 0
  and n.voided = 0;

# There are a few dozen concepts that have names that match other concepts.  These need to be dealt with one by one

# Following BB
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6561ab7a-977f-11e1-8993-905e29aff6c1' where uuid = '65631ef6-977f-11e1-8993-905e29aff6c1';

# Aspirin
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6545efde-977f-11e1-8993-905e29aff6c1' where uuid = '656360f0-977f-11e1-8993-905e29aff6c1';

# Community volunteer
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6566ef36-977f-11e1-8993-905e29aff6c1' where uuid = '657d1cca-977f-11e1-8993-905e29aff6c1';

# Decreased sensation
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6569f5aa-977f-11e1-8993-905e29aff6c1' where uuid = '654886a4-977f-11e1-8993-905e29aff6c1';

# Lymphocyte count datetime
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 657492da-977f-11e1-8993-905e29aff6c1' where uuid = '65777a4a-977f-11e1-8993-905e29aff6c1';

# Second line tuberculosis drugs
update concept_name n inner join concept c on n.concept_id = c.concept_id
set n.name = 'Has the patient used second-line tuberculosis drugs'
where n.name = 'Second line tuberculosis drugs' and c.uuid = '65605f86-977f-11e1-8993-905e29aff6c1';

# Extremity exam findings
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6557aea4-977f-11e1-8993-905e29aff6c1' where uuid = '65680efc-977f-11e1-8993-905e29aff6c1';

# Orphan
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 656fc57a-977f-11e1-8993-905e29aff6c1' where uuid = '65583cb6-977f-11e1-8993-905e29aff6c1';

# Pulmonary tuberculosis
update concept_name n inner join concept c on n.concept_id = c.concept_id
set n.name = 'Does the patient have pulmonary tuberculosis'
where n.name = 'Pulmonary tuberculosis' and c.uuid = '655a4bbe-977f-11e1-8993-905e29aff6c1';

# Patient hospitalized
update concept_name n inner join concept c on n.concept_id = c.concept_id
set n.name = 'Patient hospitalized location'
where n.name = 'Patient hospitalized' and c.uuid = '6558bb6e-977f-11e1-8993-905e29aff6c1';

# Tinea cruris
select concept_id into @tineaToRetire from concept where uuid = '6574208e-977f-11e1-8993-905e29aff6c1';
select concept_id into @tineaToKeep from concept where uuid = '6567c5be-977f-11e1-8993-905e29aff6c1';
update obs set value_coded = @tineaToKeep where value_coded = @tineaToRetire and voided = 0 and @tineaToRetire is not null and @tineaToKeep is not null;
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6567c5be-977f-11e1-8993-905e29aff6c1' where concept_id = @tineaToRetire;

# Aortic regurgitation
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 65632ce8-977f-11e1-8993-905e29aff6c1' where uuid = '6568d21a-977f-11e1-8993-905e29aff6c1';

# Carotid findings
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6568c176-977f-11e1-8993-905e29aff6c1' where uuid = '656fe578-977f-11e1-8993-905e29aff6c1';

# Sulfadoxine and Pyrimethamine
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6545eed0-977f-11e1-8993-905e29aff6c1' where uuid = '654b0b72-977f-11e1-8993-905e29aff6c1';

# Postpartum hemorrhage
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6545c7d4-977f-11e1-8993-905e29aff6c1' where uuid = '6546ea42-977f-11e1-8993-905e29aff6c1';

# Urinalysis
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 6547572a-977f-11e1-8993-905e29aff6c1' where uuid = '655a447a-977f-11e1-8993-905e29aff6c1';

# Erythrocyte sedimentation rate
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'Duplicate of 654a754a-977f-11e1-8993-905e29aff6c1' where uuid = '65659f28-977f-11e1-8993-905e29aff6c1';

# People with HIV avoid friendships rather than worry about telling people about their HIV
update concept_name n inner join concept c on n.concept_id = c.concept_id
set n.name = 'Community Stigma 14 - People with HIV avoid friendships rather than worry about telling people about their HIV'
where n.name = 'People with HIV avoid friendships rather than worry about telling people about their HIV' and c.uuid = '655c7150-977f-11e1-8993-905e29aff6c1';

# Parotitis
update concept_name n inner join concept c on n.concept_id = c.concept_id
set n.name = 'Parotitis (finding)'
where n.name = 'Parotitis' and c.uuid = '65696040-977f-11e1-8993-905e29aff6c1';
