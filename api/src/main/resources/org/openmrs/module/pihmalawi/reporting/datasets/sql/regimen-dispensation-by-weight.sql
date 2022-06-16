/************************************************************************

MOH REGIMEN DISTRIBUTION BY WEIGHT REPORT
Use OpenMRS data warehouse tables and stored procedures

Query aggregates active patients on ART with their last regimen and weight
during the reporting period.
Patients whose last appointment exceeds 60 days without visiting a heath facility
are considered to be defaulters
*************************************************************************/

SET @defaultCutOff = 60;

call create_weight_groups();
call create_last_art_outcome_at_facility(@endDate,@location);

select wg.weight_group, wg.gender_full as gender,
CASE WHEN 0A is null then 0 else 0A end as 0A,
CASE WHEN 1A is null then 0 else 1A end as 1A,
CASE WHEN 2A is null then 0 else 2A end as 2A,
CASE WHEN 3A is null then 0 else 3A end as 3A,
CASE WHEN 4A is null then 0 else 4A end as 4A,
CASE WHEN 5A is null then 0 else 5A end as 5A,
CASE WHEN 6A is null then 0 else 6A end as 6A,
CASE WHEN 7A is null then 0 else 7A end as 7A,
CASE WHEN 8A is null then 0 else 8A end as 8A,
CASE WHEN 9A is null then 0 else 9A end as 9A,
CASE WHEN 10A is null then 0 else 10A end as 10A,
CASE WHEN 11A is null then 0 else 11A end as 11A,
CASE WHEN 12A is null then 0 else 12A end as 12A,
CASE WHEN 13A is null then 0 else 13A end as 13A,
CASE WHEN 14A is null then 0 else 14A end as 14A,
CASE WHEN 15A is null then 0 else 15A end as 15A,
CASE WHEN 16A is null then 0 else 16A end as 16A,
CASE WHEN 17A is null then 0 else 17A end as 17A,
CASE WHEN 0P is null then 0 else 0P end as 0P,
CASE WHEN 1P is null then 0 else 1P end as 1P,
CASE WHEN 2P is null then 0 else 2P end as 2P,
CASE WHEN 3P is null then 0 else 3P end as 3P,
CASE WHEN 4P is null then 0 else 4P end as 4P,
CASE WHEN 4PP is null then 0 else 4PP end as 4PP,
CASE WHEN 4PA is null then 0 else 4PA end as 4PA,
CASE WHEN 9P is null then 0 else 9P end as 9P,
CASE WHEN 9PP is null then 0 else 9PP end as 9PP,
CASE WHEN 9PA is null then 0 else 9PA end as 9PA,
CASE WHEN 11P is null then 0 else 11P end as 11P,
CASE WHEN 11PP is null then 0 else 11PP end as 11PP,
CASE WHEN 11PA is null then 0 else 11PA end as 11PA,
CASE WHEN 12PP is null then 0 else 12PP end as 12PP,
CASE WHEN 12PA is null then 0 else 12PA end as 12PA,
CASE WHEN 14P is null then 0 else 14P end as 14P,
CASE WHEN 14PP is null then 0 else 14PP end as 14PP,
CASE WHEN 14PA is null then 0 else 14PA end as 14PA,
CASE WHEN 15P is null then 0 else 15P end as 15P,
CASE WHEN 15PP is null then 0 else 15PP end as 15PP,
CASE WHEN 15PA is null then 0 else 15PA end as 15PA,
CASE WHEN 16P is null then 0 else 16P end as 16P,
CASE WHEN 17P is null then 0 else 17P end as 17P,
CASE WHEN 17PP is null then 0 else 17PP end as 17PP,
CASE WHEN 17PA is null then 0 else 17PA end as 17PA,
CASE WHEN non_standard is null then 0 else non_standard end as non_standard
 from weight_groups as wg
left join
(SELECT CASE

WHEN convert(weight,DECIMAL) >=10 and convert(weight,DECIMAL) <= 13.9 and gender = "M"  THEN "10 - 13.9 Kg"
WHEN convert(weight,DECIMAL) >=10 and convert(weight,DECIMAL) <= 13.9 and gender = "F"  THEN "10 - 13.9 Kg"
WHEN convert(weight,DECIMAL) >=14 and convert(weight,DECIMAL) <= 19.9 and gender = "M"  THEN "14 - 19.9 Kg"
WHEN convert(weight,DECIMAL) >=14 and convert(weight,DECIMAL) <= 19.9 and gender = "F"  THEN "14 - 19.9 Kg"
WHEN convert(weight,DECIMAL) >=20 and convert(weight,DECIMAL) <= 24.9 and gender = "F"  THEN "20 - 24.9 Kg"
WHEN convert(weight,DECIMAL) >=20 and convert(weight,DECIMAL) <= 24.9 and gender = "M"  THEN "20 - 24.9 Kg"
WHEN convert(weight,DECIMAL) >=25 and convert(weight,DECIMAL) <= 29.9 and gender = "M"  THEN "25 - 29.9 Kg"
WHEN convert(weight,DECIMAL) >=25 and convert(weight,DECIMAL) <= 29.9 and gender = "F"  THEN "25 - 29.9 Kg"
WHEN convert(weight,DECIMAL) >=3 and convert(weight,DECIMAL) <= 3.9 and gender = "M"    THEN "3 - 3.9 Kg"
WHEN convert(weight,DECIMAL) >=3 and convert(weight,DECIMAL) <= 3.9 and gender = "F"    THEN "3 - 3.9 Kg"
WHEN convert(weight,DECIMAL) >=30 and convert(weight,DECIMAL) <= 34.9 and gender = "M"  THEN "30 - 34.9 Kg"
WHEN convert(weight,DECIMAL) >=30 and convert(weight,DECIMAL) <= 34.9 and gender = "F" THEN "30 - 34.9 Kg"
WHEN convert(weight,DECIMAL) >=35 and convert(weight,DECIMAL) <= 39.9 and gender = "M" THEN "35 - 39.9 Kg"
WHEN convert(weight,DECIMAL) >=35 and convert(weight,DECIMAL) <= 39.9 and gender = "F" THEN "35 - 39.9 Kg"
WHEN convert(weight,DECIMAL) >=4 and convert(weight,DECIMAL) <= 4.9 and gender = "M"   THEN "4 - 4.9 Kg"
WHEN convert(weight,DECIMAL) >=4 and convert(weight,DECIMAL) <= 4.9 and gender = "F"   THEN "4 - 4.9 Kg"
WHEN convert(weight,DECIMAL) >=40 and convert(weight,DECIMAL) <= 49.9 and gender = "M"   THEN "40 - 49.9 Kg"
WHEN convert(weight,DECIMAL) >=40 and convert(weight,DECIMAL) <= 49.9 and gender = "F"   THEN "40 - 49.9 Kg"
WHEN convert(weight,DECIMAL) >=50 and gender = "M" THEN "50 Kg +"
WHEN convert(weight,DECIMAL) >=50 and gender = "F" THEN "50 Kg +"
WHEN convert(weight,DECIMAL) >=6 and convert(weight,DECIMAL) <= 9.9 and gender = "M"   THEN "6 - 9.9 Kg"
WHEN convert(weight,DECIMAL) >=6 and convert(weight,DECIMAL) <= 9.9 and gender = "F"   THEN "6 - 9.9 Kg"
WHEN weight is null and gender = "M" THEN "Unknown"
WHEN weight is null and gender = "F" THEN "Unknown"

END as weight_group,gender,
COUNT(IF((current_regimen = '0A: ABC/3TC + NVP'), 1, NULL)) as 0A,
COUNT(IF((current_regimen = '1A: d4T / 3TC / NVP (previous 1L)'), 1, NULL)) as 1A,
COUNT(IF((current_regimen = '2A: AZT / 3TC / NVP (previous AZT)'), 1, NULL)) as 2A,
COUNT(IF((current_regimen = '3A: d4T / 3TC + EFV (previous EFV)'), 1, NULL)) as 3A,
COUNT(IF((current_regimen = '4A: AZT / 3TC + EFV (previous AZTEFV)'), 1, NULL)) as 4A,
COUNT(IF((current_regimen = '5A: TDF / 3TC / EFV'), 1, NULL)) as 5A,
COUNT(IF((current_regimen = '6A: TDF / 3TC + NVP'), 1, NULL)) as 6A,
COUNT(IF((current_regimen = '7A: TDF / 3TC + ATV/r'), 1, NULL)) as 7A,
COUNT(IF((current_regimen = '8A: AZT / 3TC + ATV/r'), 1, NULL)) as 8A,
COUNT(IF((current_regimen = '9A: ABC / 3TC + LPV/r'), 1, NULL)) as 9A,
COUNT(IF((current_regimen = '10A: TDF / 3TC + LPV/r'), 1, NULL)) as 10A,
COUNT(IF((current_regimen = '11A: AZT / 3TC + LPV'), 1, NULL)) as 11A,
COUNT(IF((current_regimen = '12A: DRV + r + DTG'), 1, NULL)) as 12A,
COUNT(IF((current_regimen = '13A: TDF / 3TC / DTG'), 1, NULL)) as 13A,
COUNT(IF((current_regimen = '14A: AZT / 3TC + DTG'), 1, NULL)) as 14A,
COUNT(IF((current_regimen = '15A: ABC / 3TC + DTG'), 1, NULL)) as 15A,
COUNT(IF((current_regimen = '16A: ABC / 3TC + RAL'), 1, NULL)) as 16A,
COUNT(IF((current_regimen = '17A: ABC / 3TC + EFV'), 1, NULL)) as 17A,
COUNT(IF((current_regimen = '0P: ABC/3TC + NVP'), 1, NULL)) as 0P,
COUNT(IF((current_regimen = '1P: d4T / 3TC / NVP'), 1, NULL)) as 1P,
COUNT(IF((current_regimen = '2P: AZT / 3TC / NVP'), 1, NULL)) as 2P,
COUNT(IF((current_regimen = '3P: d4T / 3TC + EFV'), 1, NULL)) as 3P,
COUNT(IF((current_regimen = '4P: AZT / 3TC + EFV'), 1, NULL)) as 4P,
COUNT(IF((current_regimen = "4PP: AZT 60 / 3TC 30 + EFV 200"), 1, NULL)) as 4PP,
COUNT(IF((current_regimen = "4PA: AZT 300 / 3TC 150 + EFV 200"), 1, NULL)) as 4PA,
COUNT(IF((current_regimen = '9P: ABC / 3TC + LPV/r'), 1, NULL)) as 9P,
COUNT(IF((current_regimen = "9PP: ABC 120 / 3TC 60 + LPV/r 100/25"), 1, NULL)) as 9PP,
COUNT(IF((current_regimen = "9PA: ABC 600 / 3TC 300 + LPV/r 100/25"), 1, NULL)) as 9PA,
COUNT(IF((current_regimen = '11P: AZT / 3TC + LPV/r (previous AZT3TCLPV)'), 1, NULL)) as 11P,
COUNT(IF((current_regimen = "11PP: AZT 60 / 3TC 30 + LPV/r 100/25"), 1, NULL)) as 11PP,
COUNT(IF((current_regimen = "11PA: AZT 300 / 3TC 150 + LPV/r 100/25"), 1, NULL)) as 11PA,
COUNT(IF((current_regimen = "12PP: DRV 150 + r 50 + DTG 10 (± NRTIs)"), 1, NULL)) as 12PP,
COUNT(IF((current_regimen = "12PA: DRV 150 + r 50 + DTG 50"), 1, NULL)) as 12PA,
COUNT(IF((current_regimen = '14P: AZT / 3TC + DTG'), 1, NULL)) as 14P,
COUNT(IF((current_regimen = "14PP: AZT 60 / 3TC 30 + DTG 10"), 1, NULL)) as 14PP,
COUNT(IF((current_regimen = "14PA: AZT 60 / 3TC 30 + DTG 50"), 1, NULL)) as 14PA,
COUNT(IF((current_regimen = '15P: ABC / 3TC + DTG'), 1, NULL)) as 15P,
COUNT(IF((current_regimen = '15PP: ABC / 3TC + DTG'), 1, NULL)) as 15PP,
COUNT(IF((current_regimen = "15PA: ABC 120 / 3TC 60 + DTG 50"), 1, NULL)) as 15PA,
COUNT(IF((current_regimen = '16P: ABC / 3TC + RAL'), 1, NULL)) as 16P,
COUNT(IF((current_regimen = '17P: ABC / 3TC + EFV'), 1, NULL)) as 17P,
COUNT(IF((current_regimen = "17PP: ABC 120 / 3TC 60 + EFV 200"), 1, NULL)) as 17PP,
COUNT(IF((current_regimen = "17PA: ABC 600 / 3TC 300 + EFV 200"), 1, NULL)) as 17PA,
COUNT(IF((current_regimen = 'Non standard'), 1, NULL)) as non_standard
from
(
select opi.identifier,mwp.gender,mwp.birthdate as dob,
CASE WHEN (map.weight is null or map.weight = "")  THEN (SELECT weight FROM mw_art_followup where patient_id = map.patient_id  and weight is not null order by patient_id DESC limit 1) ELSE map.weight END as weight,
 map.art_regimen as current_regimen, map.arvs_given, map.visit_date as dispense_date, opi.location
    from mw_art_followup map
join
(
	select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date from mw_art_followup where visit_date <= @endDate
	group by patient_id
	) map1
ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date
join mw_patient mwp
on mwp.patient_id = map.patient_id
 join omrs_patient_identifier opi
on mwp.patient_id = opi.patient_id and opi.location = @location and opi.type = "ARV number"
where map.patient_id in (select pat from last_facility_outcome where state = "On antiretrovirals")
and floor(datediff(@endDate,map.next_appointment_date)) <=  @defaultCutOff

)AS sub1
group by weight_group,gender
order by gender, weight_group
 ) AS sub22  on sub22.weight_group = wg.weight_group and sub22.gender = wg.gender
order by sort_value;