/*USE openmrs_warehouse;

SET @location = "Neno district hospital";
SET @startDate = "2025-07-01";
SET @endDate   = "2025-09-30";*/

SET @birthDateDivider = 30;
SET @defaultCutOff = 60;

CALL create_hiv_cohort(@startDate, @endDate, @location, @birthDateDivider);
CALL create_last_eid_outcome_at_facility(@endDate, @location);

SELECT
    hv.location as location,
    CONCAT('Q', QUARTER(@startDate)) as quarter_label,
    YEAR(@startDate) AS year_label,

    #HCC
    eid_newly_total_registered_in_quarter,
    eid_cummulative_ever_total_registered,
    eid_newly_ft,
    eid_cummulative_ft,
    eid_initial_transfer_In,
    eid_cummulative_transfer_In,
    eid_initial_males_all_ages,
    eid_initial_females_all_ages,
    eid_cumulative_males_all_ages,
    eid_cumulative_females_all_ages,
    eid_age_at_initiation_less_2months,
    eid_age_at_initiation_2_24months,
    cumulative_eid_age_at_initiation_less_2months,
    cumulative_eid_age_at_initiation_2_24months,
    quarterly_eid_tx_curr,
    cumulative_eid_tx_curr,


    #ART Clinic
    -- ART Clinic Registrations
    SUM(hv.initial_visit_date BETWEEN @startDate AND @endDate) AS newly_total_registered_in_quarter,
    SUM(hv.initial_visit_date <= @endDate) AS cummulative_ever_total_registered,

    -- First Time (FT) Starts
    SUM(IF(hv.gender='M' AND hv.transfer_in_date IS NULL AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS newly_ft_male,
    SUM(IF(hv.gender='M' AND hv.transfer_in_date IS NULL AND hv.initial_visit_date <= @endDate, 1, 0)) AS cummulative_ft_male,
    SUM(IF(hv.gender='F' AND hv.transfer_in_date IS NULL AND (hv.initial_pregnant_or_lactating != 'Patient pregnant' OR hv.initial_pregnant_or_lactating IS NULL) AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS newly_ft_female_nonpreg,
    SUM(IF(hv.gender='F' AND hv.transfer_in_date IS NULL AND (hv.initial_pregnant_or_lactating != 'Patient pregnant' OR hv.initial_pregnant_or_lactating IS NULL) AND hv.initial_visit_date <= @endDate, 1, 0)) AS cummulative_ft_female_nonpreg,
    SUM(IF(hv.gender='F' AND hv.transfer_in_date IS NULL AND hv.initial_pregnant_or_lactating = 'Patient pregnant' AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS newly_ft_female_preg,
    SUM(IF(hv.gender='F' AND hv.transfer_in_date IS NULL AND hv.initial_pregnant_or_lactating = 'Patient pregnant' AND hv.initial_visit_date <= @endDate, 1, 0)) AS cummulative_ft_female_preg,
    SUM(CASE WHEN hv.transfer_in_date IS NULL AND hv.initial_visit_date BETWEEN @startDate AND @endDate THEN 1 ELSE 0 END) AS total_ft,
    SUM(CASE WHEN hv.transfer_in_date IS NULL AND hv.initial_visit_date <= @endDate THEN 1 ELSE 0 END) AS cummulative_ft,

    -- Transfer-In (TI)
    SUM(IF(hv.transfer_in_date BETWEEN @startDate AND @endDate, 1, 0)) AS initial_transfer_In,
    SUM(IF(hv.transfer_in_date <= @endDate, 1, 0)) AS cummulative_transfer_In,

    -- Age at Initiation
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND hv.gender = 'M', 1, 0)) AS initial_males_all_ages,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND hv.gender = 'F' AND (hv.initial_pregnant_or_lactating != 'Patient pregnant' OR hv.initial_pregnant_or_lactating IS NULL), 1, 0)) AS initial_fnp_all_ages,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND hv.gender = 'F' AND hv.initial_pregnant_or_lactating = 'Patient pregnant', 1, 0)) AS initial_fp_all_ages,
    SUM(IF(hv.age < 24 AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS age_at_initiation_less_24m,
    SUM(IF(hv.age BETWEEN 24 AND 168 AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS age_at_initiation__24m_14yrs,
    SUM(IF(hv.age >= 180 AND hv.initial_visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS age_at_initiation_15plus,

    SUM(IF(initial_visit_date <= @endDate AND gender = 'M', 1, 0)) AS cummulative_males_all_ages,
    SUM(IF(initial_visit_date <= @endDate AND gender = 'F' AND (initial_pregnant_or_lactating != 'Patient pregnant' OR initial_pregnant_or_lactating IS NULL), 1, 0)) AS cummulative_fnp_all_ages,
    SUM(IF(initial_visit_date <= @endDate AND gender = 'F' AND initial_pregnant_or_lactating = 'Patient pregnant', 1, 0)) AS cummulative_fp_all_ages,
    SUM(age < 24 AND initial_visit_date <= @endDate) AS age_at_initiation_less_24m_cummulative,
    SUM(age BETWEEN 24 AND 168 AND initial_visit_date <= @endDate) AS age_at_initiation__24m_14yrs_cummulative,
    SUM(age >= 180 AND initial_visit_date <= @endDate) AS age_at_initiation_15plus_cummulative,

    -- Reasons for Starting (piv join)
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND (piv.age_at_initiation < 1 and piv.presumed_hiv_severe_present = 'yes'), 1, 0)) AS initial_pres_sev_hiv,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.age_at_initiation < 1, 1, 0)) AS initial_infants_less_12_mths,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.age_at_initiation >= 1 AND piv.age_at_initiation < 5, 1, 0)) AS initial_children_12_59_mths,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.pregnant_or_lactating = 'Patient pregnant', 1, 0)) AS intial_preg,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.pregnant_or_lactating = 'Currently breastfeeding child', 1, 0)) AS initial_bf,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.cd4_count < 200, 1, 0)) AS initial_cd4_below_threshold,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.clinical_stage LIKE '%stage III%', 1, 0)) AS initial_who_stage_3,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND (piv.clinical_stage LIKE '%stage IV%' ), 1, 0)) AS initial_who_stage_4,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.who_clinical_conditions REGEXP '^(As|Asy|Assy|Asm|Assm|Asn)' , 1, 0)) AS initial_asymptomatic_mild,
    SUM(IF(hv.initial_visit_date BETWEEN @startDate AND @endDate AND piv.who_clinical_conditions IS NULL AND piv.clinical_stage IS NULL, 1, 0)) AS initial_unknown_reason,

    SUM(IF(hv.initial_visit_date <= @endDate AND (piv.age_at_initiation < 1 and piv.presumed_hiv_severe_present = 'yes'), 1, 0)) AS cummulative_pres_sev_hiv,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.age_at_initiation < 1, 1, 0)) AS cummulative_infants_less_12_mths,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.age_at_initiation >= 1 AND piv.age_at_initiation < 5, 1, 0)) AS cummulative_children_12_59_mths,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.pregnant_or_lactating = 'Patient pregnant', 1, 0)) AS cummulative_preg,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.pregnant_or_lactating = 'Currently breastfeeding child', 1, 0)) AS cummulative_bf,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.cd4_count < 200, 1, 0)) AS cummulative_cd4_below_threshold,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.clinical_stage LIKE '%stage III%', 1, 0)) AS cummulative_who_stage_3,
    SUM(IF(hv.initial_visit_date <= @endDate AND (piv.clinical_stage LIKE '%stage IV%' ), 1, 0)) AS cummulativel_who_stage_4,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.who_clinical_conditions REGEXP '^(As|Asy|Assy|Asm|Assm|Asn)' , 1, 0)) AS cummulative_asymptomatic_mild,
    SUM(IF(hv.initial_visit_date <= @endDate AND piv.who_clinical_conditions IS NULL AND piv.clinical_stage IS NULL, 1, 0)) AS cummulative_unknown_reason,


    #Tb conditions at initiation
    SUM(IF(initial_visit_date BETWEEN @startDate AND @endDate AND
    (piv.tb_treatment_start_date IS NULL OR DATEDIFF(hv.initial_visit_date, piv.tb_treatment_start_date) > 730), 1, 0)
    ) AS initial_never_tb_or_over_2yrs_ago,
    SUM(IF(initial_visit_date <= @endDate AND
    (piv.tb_treatment_start_date IS NULL OR DATEDIFF(hv.initial_visit_date, piv.tb_treatment_start_date) > 730), 1, 0)
    ) AS cummulative_never_tb_or_over_2yrs_ago,
    SUM(IF(initial_visit_date BETWEEN @startDate AND @endDate AND
    (piv.tb_treatment_start_date IS NOT NULL AND DATEDIFF(hv.initial_visit_date, piv.tb_treatment_start_date) <= 730), 1, 0)
    ) AS initial_tb_within_last_2yrs,
    SUM(IF(initial_visit_date <= @endDate AND
    (piv.tb_treatment_start_date IS NOT NULL AND DATEDIFF(hv.initial_visit_date, piv.tb_treatment_start_date) <= 730), 1, 0)
    ) AS cummulative_tb_within_last_2yrs,
    SUM(tb_status_initial='Currently in treatment' AND initial_visit_date BETWEEN @startDate AND @endDate) AS initial_current_episode_tb,
    SUM(tb_status_initial='Currently in treatment' AND initial_visit_date <= @endDate) AS cummulative_current_episode_tb,


    #ks
    SUM(ks_side_effects_worsening_on_arvs='True' AND initial_visit_date BETWEEN @startDate AND @endDate) AS ks_initiation,
    SUM(ks_side_effects_worsening_on_arvs='True' AND initial_visit_date <= @endDate) AS ks_cummulative,

    -- Outcomes
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff, 1, 0)) AS tx_curr,
    SUM(state = 'treatment Stopped') AS stopped,
    SUM(state = 'Patient defaulted') AS defaulted,
    SUM(state = 'patient transferred out') AS transfer_out,
    SUM(state = 'patient died') AS died_total,
    SUM(IF(state = 'patient died' AND DATEDIFF(start_date, initial_visit_date) <= 30, 1, 0)) AS died_month_1,
    SUM(IF(state = 'patient died' AND DATEDIFF(start_date, initial_visit_date) BETWEEN 31 AND 60, 1, 0)) AS died_month_2,
    SUM(IF(state = 'patient died' AND DATEDIFF(start_date, initial_visit_date) BETWEEN 61 AND 90, 1, 0)) AS died_month_3,
    SUM(IF(state = 'patient died' AND DATEDIFF(start_date, initial_visit_date) >= 90, 1, 0)) AS died_after_month_3,
    SUM(IF(state IN ('treatment Stopped', 'patient died', 'patient transferred out', 'Patient defaulted'), 1, 0)) AS  total_adv_outcomes,

    #ART regimens
/* ADULT (A) REGIMENS */
    SUM(IF(state='On antiretrovirals' AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='0A: ABC/3TC + NVP', 1, 0)) AS '0A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='2A: AZT / 3TC / NVP (previous AZT)', 1, 0)) AS '2A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='4A: AZT / 3TC + EFV (previous AZTEFV)', 1, 0)) AS '4A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='5A: TDF / 3TC / EFV', 1, 0)) AS '5A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='6A: TDF / 3TC + NVP', 1, 0)) AS '6A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='7A: TDF / 3TC + ATV/r', 1, 0)) AS '7A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='8A: AZT / 3TC + ATV/r', 1, 0)) AS '8A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='9A: ABC / 3TC + LPV/r', 1, 0)) AS '9A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='10A: TDF / 3TC + LPV/r', 1, 0)) AS '10A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='11A: AZT / 3TC + LPV', 1, 0)) AS '11A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='12A: DRV + r + DTG', 1, 0)) AS '12A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='13A: TDF / 3TC / DTG', 1, 0)) AS '13A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='14A: AZT / 3TC + DTG', 1, 0)) AS '14A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='15A: ABC / 3TC + DTG', 1, 0)) AS '15A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='16A: ABC / 3TC + RAL', 1, 0)) AS '16A',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='17A: ABC / 3TC + EFV', 1, 0)) AS '17A',

    /* PAEDIATRIC (P) REGIMENS */

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='0P: ABC/3TC + NVP', 1, 0)) AS '0P',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='2P: AZT / 3TC / NVP', 1, 0)) AS '2P',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="4PP: AZT 60 / 3TC 30 + EFV 200", 1, 0)) AS '4PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="4PA: AZT 300 / 3TC 150 + EFV 200", 1, 0)) AS '4PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='9PP: ABC 120 / 3TC 60 + LPV/r 100/25', 1, 0)) AS '9PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='9PA: ABC 600 / 3TC 300 + LPV/r 100/25', 1, 0)) AS '9PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="11PP: AZT 60 / 3TC 30 + LPV/r 100/25", 1, 0)) AS '11PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="11PA: AZT 300 / 3TC 150 + LPV/r 100/25", 1, 0)) AS '11PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="12PP: DRV 150 + r 50 + DTG 10 (± NRTIs)", 1, 0)) AS '12PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="12PA: DRV 150 + r 50 + DTG 50", 1, 0)) AS '12PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="14PP: AZT 60 / 3TC 30 + DTG 10", 1, 0)) AS '14PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="14PA: AZT 60 / 3TC 30 + DTG 50", 1, 0)) AS '14PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='15P: ABC / 3TC + DTG', 1, 0)) AS '15P',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen='15PP: ABC / 3TC + DTG', 1, 0)) AS '15PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="15PA: ABC 120 / 3TC 60 + DTG 50", 1, 0)) AS '15PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="16P: ABC / 3TC + RAL", 1, 0)) AS '16P',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="17PP: ABC 120 / 3TC 60 + EFV 200", 1, 0)) AS '17PP',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND current_regimen="17PA: ABC 600 / 3TC 300 + EFV 200", 1, 0)) AS '17PA',

    SUM(IF(state='On antiretrovirals'
    AND FLOOR(DATEDIFF(@endDate,last_appt_date)) <= @defaultCutOff
    AND (current_regimen='Non standard' or current_regimen IS NULL), 1, 0)) AS non_Standard,

    #pregnant/ breastfeeding
    SUM(IF((state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND hv.pregnant_or_lactating = 'Patient pregnant', 1, 0)) AS pregnant_Current,
    SUM(IF((state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND hv.pregnant_or_lactating = 'Currently breastfeeding child', 1, 0)) AS breastfeeding_Current,
    SUM(IF((state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND (hv.pregnant_or_lactating NOT IN ('Patient pregnant', 'Currently breastfeeding child') OR hv.pregnant_or_lactating IS NULL), 1, 0)) AS all_other_Current,


    #current tb_status
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff AND hv.tb_status = 'TB NOT suspected', 1, 0)) AS tb_not_suspected,
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff AND hv.tb_status = 'TB suspected', 1, 0)) AS tb_suspected,
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff AND hv.tb_status = 'Confirmed TB NOT on treatment', 1, 0)) AS tb_confirmed_not_rx,
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff AND hv.tb_status = 'Confirmed TB on treatment', 1, 0)) AS tb_confirmed_rx,
    SUM(IF(state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff AND (hv.tb_status = '' OR hv.tb_status IS NULL), 1, 0)) AS tb_unknown,

    #side effects
    SUM(CASE WHEN (state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND pv.no_side_effect = 'No' THEN 1 ELSE 0 END) AS none_side_effect,
    SUM(CASE WHEN (state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND (pv.peripheral_neuropathy_side_effect IS NOT NULL OR
    pv.hepatitis_side_effect IS NOT NULL OR pv.skin_rash_side_effect IS NOT NULL OR pv.lipodystrophy_side_effect IS NOT NULL OR pv.other_side_effect IS NOT NULL)THEN 1 ELSE 0 END) AS any_side_effect,
    SUM(CASE WHEN (state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff) AND (pv.no_side_effect IS NULL OR pv.no_side_effect != 'No')
    AND pv.peripheral_neuropathy_side_effect IS NULL AND pv.hepatitis_side_effect IS NULL AND pv.skin_rash_side_effect IS NULL AND pv.lipodystrophy_side_effect IS NULL AND pv.other_side_effect IS NULL THEN 1 ELSE 0 END) AS unknown_side_effect,

    #adherence
    sum(case when state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff and doses_missed between 0 and 3 then 1 end) as 0_3_missed_dose,
    sum(case when state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff and doses_missed >= 4 then 1 end) as 4_plus_missed_dose,
    sum(case when state = 'On antiretrovirals' AND FLOOR(DATEDIFF(@endDate, last_appt_date)) <= @defaultCutOff and doses_missed is null then 1 end) as unknown_missed_dose,

/* New Starts on 3HP */
    SUM(IF(((initial_visit_date BETWEEN @startDate AND @endDate AND transfer_in_date IS NULL) OR start_date >= @startDate)
    AND ((first_inh_300 IS NOT NULL AND first_rfp_150 IS NOT NULL) OR first_rfp_inh IS NOT NULL), 1, 0)) AS new_start_three_hp,

/* New Starts on 6H */
    SUM(IF(((initial_visit_date BETWEEN @startDate AND @endDate AND transfer_in_date IS NULL) OR start_date >= @startDate)
    AND (first_inh_300 IS NOT NULL AND first_rfp_150 IS NULL), 1, 0)) AS new_start_six_h,

    #cpt/ipt
    #pifp currently not available

/*bp screen */
    SUM(IF(followup_visit_date BETWEEN @startDate AND @endDate AND (systolic_bp IS NOT NULL and diastolic_bp IS NOT NULL), 1, 0)) AS screened_for_htn


FROM hiv_cohort hv
-- JOIN for EID Subquery
    CROSS JOIN (
    SELECT
    SUM(mei.visit_date BETWEEN @startDate AND @endDate) AS eid_newly_total_registered_in_quarter,
    SUM(mei.visit_date <= @endDate) AS eid_cummulative_ever_total_registered,
    SUM(IF(sub.eid_transfer_in_date IS NULL AND mei.visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS eid_newly_ft,
    SUM(IF(sub.eid_transfer_in_date IS NULL AND mei.visit_date <= @endDate, 1, 0)) AS eid_cummulative_ft,
    SUM(IF(sub.eid_transfer_in_date BETWEEN @startDate AND @endDate, 1, 0)) AS eid_initial_transfer_In,
    SUM(IF(sub.eid_transfer_in_date <= @endDate, 1, 0)) AS eid_cummulative_transfer_In,
    SUM(IF(mei.visit_date BETWEEN @startDate AND @endDate AND lef.gender = 'M', 1, 0)) AS eid_initial_males_all_ages,
    SUM(IF(mei.visit_date BETWEEN @startDate AND @endDate AND lef.gender = 'F', 1, 0)) AS eid_initial_females_all_ages,
    SUM(IF(mei.visit_date <= @endDate AND lef.gender = 'M', 1, 0)) AS eid_cumulative_males_all_ages,
    SUM(IF(mei.visit_date <= @endDate AND lef.gender = 'F', 1, 0)) AS eid_cumulative_females_all_ages,
    SUM(IF(DATEDIFF(mei.visit_date, lef.birthdate) < 60 AND mei.visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS eid_age_at_initiation_less_2months,
    SUM(IF(DATEDIFF(mei.visit_date, lef.birthdate) BETWEEN 60 AND 729 AND mei.visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS eid_age_at_initiation_2_24months,
    SUM(IF(DATEDIFF(mei.visit_date, lef.birthdate) < 60 AND mei.visit_date <= @endDate, 1, 0)) AS cumulative_eid_age_at_initiation_less_2months,
    SUM(IF(DATEDIFF(mei.visit_date, lef.birthdate) BETWEEN 60 AND 729 AND mei.visit_date <= @endDate, 1, 0)) AS cumulative_eid_age_at_initiation_2_24months,
    SUM(IF(lef.state = 'Exposed Child (Continue)' AND DATEDIFF(@endDate, ev.eid_l_date) <= @defaultCutOff AND mei.visit_date BETWEEN @startDate AND @endDate, 1, 0)) AS quarterly_eid_tx_curr,
    SUM(IF(lef.state = 'Exposed Child (Continue)' AND DATEDIFF(@endDate, ev.eid_l_date) <= @defaultCutOff, 1, 0)) AS cumulative_eid_tx_curr
    FROM last_eid_facility_outcome lef
    JOIN mw_eid_initial mei ON mei.patient_id = lef.pat AND mei.location = lef.location
    LEFT JOIN (
    SELECT patient_id, MAX(visit_date) AS eid_l_date
    FROM mw_eid_followup WHERE visit_date <= @endDate GROUP BY patient_id
    ) ev ON ev.patient_id = lef.pat
    LEFT JOIN (
    SELECT patient_id, value_date AS eid_transfer_in_date
    FROM omrs_obs WHERE concept = 'Transfer in date'
    ) sub ON sub.patient_id = lef.pat
    ) eid
-- ART Join Logic
    LEFT join (
    select map.patient_id, map.visit_date as fv_date, map.next_appointment_date as l_appt_date,doses_missed,peripheral_neuropathy_side_effect,
    hepatitis_side_effect, skin_rash_side_effect, lipodystrophy_side_effect, other_side_effect, no_side_effect
    from mw_art_followup map
    join
    (
    select patient_id,MAX(visit_date) as visit_date ,MAX(next_appointment_date) as last_appt_date from mw_art_followup where visit_date <= @endDate
    group by patient_id
    ) map1
    ON map.patient_id = map1.patient_id and map.visit_date = map1.visit_date) pv on pv.patient_id=hv.patient_id
    LEFT join (
    select mar.patient_id, tb_status, ks_side_effects_worsening_on_arvs, tb_status as tb_status_initial, tb_treatment_start_date,
    age_at_initiation, pregnant_or_lactating, who_clinical_conditions, clinical_stage, presumed_hiv_severe_present, cd4_count
    from mw_art_initial mar
    join
    (
    select patient_id,MAX(visit_date) as visit_date  from mw_art_initial where visit_date <= @endDate
    group by patient_id
    ) mar1
    ON mar.patient_id = mar1.patient_id and mar.visit_date = mar1.visit_date) piv on piv.patient_id=hv.patient_id