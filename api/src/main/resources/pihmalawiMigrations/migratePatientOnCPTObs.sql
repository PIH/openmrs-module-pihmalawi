SET @concept_coded_type = (select concept_datatype_id from concept_datatype where uuid =  '8d4a48b6-c2cc-11de-8d13-0010c6dffd0f');
SET @cpt_concept_id = (select concept_id from concept where uuid = '655a9e34-977f-11e1-8993-905e29aff6c1');
SET @yes_concept_id = (select concept_id from concept where uuid = '65576354-977f-11e1-8993-905e29aff6c1');
SET @no_concept_id = (select concept_id from concept where uuid = '6557646c-977f-11e1-8993-905e29aff6c1');

update obs b
set b.value_coded = @no_concept_id, b.value_numeric=null
where b.obs_id in ( select * from (
  select o.obs_id from obs o where o.concept_id=@cpt_concept_id and o.value_coded is null and o.value_numeric=0 and o.voided=0
) tblTmp);


update obs b
set b.value_coded = @yes_concept_id, b.value_numeric=null
where b.obs_id in ( select * from (
  select o.obs_id from obs o where o.concept_id=@cpt_concept_id and o.value_coded is null and o.value_numeric=1 and o.voided=0
) tblTmp);
