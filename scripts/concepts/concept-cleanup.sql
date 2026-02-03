
# There are a handful of concepts that have no non-voided names.  This is invalid.  Delete since these have no data associated with them.

select * from concept c where c.concept_id not in (select concept_id from concept_name where voided = 0);
# 1580, 6451, 5498
select * from obs where concept_id in (1580, 6451, 5498);
# 0
select * from orders where concept_id in (1580, 6451, 5498);
# 0
delete from concept_name where concept_id in (1580, 6451, 5498);
# 1
delete from concept_description where concept_id in (1580, 6451, 5498);
# 3
delete from concept_answer where concept_id in (1580, 6451, 5498) or answer_concept in (1580, 6451, 5498);
# 3
delete from concept_set where concept_id in (1580, 6451, 5498) or concept_set in (1580, 6451, 5498);
# 0
delete from concept_reference_map where concept_id in (1580, 6451, 5498);
# 0
delete from concept where concept_id in (1580, 6451, 5498);
# 3

# There are a handful of concepts whose uuids appear to have been copied from ciel incorrectly and are invalid for openmrs
# These are getting set up in MasterCardConcepts.java with these invalid uuids - those need to be removed/changed
# Also, there are some commented out references to these uuids in htn_dm_visit.xml, which should be updated or removed
update concept set uuid = '72247AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '72247AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Bisoprolol
update concept set uuid = '82411AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '82411AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Pravastatin
update concept set uuid = '83936AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA' where uuid = '83936AAAAAAAAAAAAAAAAAAAAAAAAAAA'; # Simvastatin

# There are a handful of concepts that have duplicate concept names in the same locale

# Most are SHORT names that duplicate FULLY_SPECIFIED names
update concept_name n
inner join concept_name fsn
on n.concept_id = fsn.concept_id and n.locale = fsn.locale and n.name = fsn.name
set n.voided = 1, n.date_voided = now(), n.void_reason = 'Duplicate name in locale'
where n.concept_name_type != 'FULLY_SPECIFIED'
  and fsn.concept_name_type = 'FULLY_SPECIFIED'
  and fsn.voided = 0
  and n.voided = 0;

# Two are index terms that duplicate SHORT names
update concept_name n
inner join concept_name sn
on n.concept_id = sn.concept_id and n.locale = sn.locale and n.name = sn.name
set n.voided = 1, n.date_voided = now(), n.void_reason = 'Duplicate name in locale'
where sn.concept_name_type = 'SHORT'
and n.concept_name_type is null
and sn.voided = 0
and n.voided = 0;

# There are a few dozen concepts that have names that match other concepts.  These need to be dealt with one by one
