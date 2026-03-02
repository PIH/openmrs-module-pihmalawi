# There are 2 concepts that have no names, but do have a description.  Add a name to each of these based on the description.

insert into concept_name (concept_id, name, locale, concept_name_type, locale_preferred, creator, date_created, uuid)
select
    d.concept_id, d.description, d.locale, 'FULLY_SPECIFIED', 1, 1, now(), uuid()
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

