# OpenMRS will install concepts for "true" and "false" into new, empty instance by default
# Although this is not an issue on existing production servers, in order to support creation of new, empty environments
# we want to remove these concepts if they get created, so that these match the expected production configuration

select concept_id into @defaultTrueConcept from concept where uuid = 'cf82933b-3f3f-45e7-a5ab-5d31aaee3da3';
select concept_id into @defaultFalseConcept from concept where uuid = '488b58ff-64f5-4f8a-8979-fa79940b1594';

delete from concept_name where concept_id in (@defaultTrueConcept, @defaultFalseConcept) and locale != 'en' and concept_name_type != 'FULLY_SPECIFIED';
update concept_name set name = concat(name, ' Default') where neno.concept_name.concept_id in (@defaultTrueConcept, @defaultFalseConcept);
update concept set retired = 1, retired_by = 1, date_retired = now(), retire_reason = 'retiring default true and false concepts' where concept_id in (@defaultTrueConcept, @defaultFalseConcept);