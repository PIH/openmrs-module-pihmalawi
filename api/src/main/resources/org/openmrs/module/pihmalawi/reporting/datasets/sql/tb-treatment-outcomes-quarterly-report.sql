CALL create_last_tb_outcome_at_facility(@endDate, @location);

SET @dayOfEndDate = DAY(@endDate);
SET @startDate = (
  SELECT CASE
    WHEN @dayOfEndDate = 28 THEN DATE_ADD(DATE_SUB(@endDate, INTERVAL 3 MONTH), INTERVAL 4 DAY)
    WHEN @dayOfEndDate = 29 THEN DATE_ADD(DATE_SUB(@endDate, INTERVAL 3 MONTH), INTERVAL 3 DAY)
    WHEN @dayOfEndDate = 31 THEN DATE_ADD(DATE_SUB(@endDate, INTERVAL 3 MONTH), INTERVAL 1 DAY)
    WHEN @dayOfEndDate = 30 THEN DATE_ADD(DATE_SUB(@endDate, INTERVAL 3 MONTH), INTERVAL 2 DAY)
    ELSE @endDate
  END
);

-- Drop summary table if it exists
DROP TEMPORARY TABLE IF EXISTS tb_registered_cases_summary;

-- Create summary table
CREATE TEMPORARY TABLE tb_registered_cases_summary (
  category VARCHAR(255),
  registered_cases INT
);

-- Populate each category
INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'New smear positive', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
JOIN mw_tb_test_results tbt ON tbi.patient_id = tbt.patient_id
WHERE tbi.patient_category = 'New'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbt.initiation_month_smear_result = 'Positive';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'New MTB detected (Xpert)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
JOIN mw_tb_test_results tbt ON tbi.patient_id = tbt.patient_id
WHERE tbi.patient_category = 'New'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbt.initiation_month_xpert_result = 'MTB DETECTED';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'New pulmonary clinically diagnosed', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category = 'New'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.diagnosis = 'TB Clinically Diagnosed'
  AND tbi.disease_classification = 'Pulmonary';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'New EPTB', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category = 'New'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.disease_classification = 'Extrapulmonary tuberculosis (EPTB)';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Relapse (Bact confirmed)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
JOIN mw_tb_test_results tbt ON tbi.patient_id = tbt.patient_id
WHERE tbi.patient_category = 'Relapse'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.diagnosis = 'TB Bacteriologically Confirmed';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Relapse (clinical Pul)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category = 'Relapse'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.diagnosis = 'TB Clinically Diagnosed'
  AND tbi.disease_classification = 'Pulmonary';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Relapse (EPTB)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category = 'Relapse'
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.disease_classification = 'Extrapulmonary tuberculosis (EPTB)';

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Retreatment (excluding relapse)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category IN ('RALFT', 'Fail', 'Other')
  AND tbi.visit_date BETWEEN @startDate AND @endDate;

INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'HIV positive new and relapse', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
WHERE tbi.patient_category IN ('New', 'Relapse')
  AND tbi.visit_date BETWEEN @startDate AND @endDate
  AND tbi.hiv_test_history IN ('Past Positive','New positive');

-- Children under 5 years
INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Children (0-4 Years)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
JOIN mw_patient p ON tbi.patient_id = p.patient_id
WHERE tbi.visit_date BETWEEN @startDate AND @endDate
  AND FLOOR((DATEDIFF(@endDate, p.birthdate) / 365.25)) < 5;

-- Children 5–14 years
INSERT INTO tb_registered_cases_summary (category, registered_cases)
SELECT 'Children (5-14yrs)', COUNT(DISTINCT tbi.patient_id)
FROM mw_tb_initial tbi
JOIN mw_patient p ON tbi.patient_id = p.patient_id
WHERE tbi.visit_date BETWEEN @startDate AND @endDate
  AND FLOOR((DATEDIFF(@endDate, p.birthdate) / 365.25)) BETWEEN 5 AND 14;

-- Final summary with outcomes
SELECT
  r.category as category,
  r.registered_cases as registered_cases,
  SUM(CASE WHEN l.state = 'Cured' THEN 1 ELSE 0 END) AS Cured,
  SUM(CASE WHEN l.state = 'Treatment completed' THEN 1 ELSE 0 END) AS Treatment_completed,
  SUM(CASE WHEN l.state = 'Treatment failed' THEN 1 ELSE 0 END) AS Treatment_failed,
  SUM(CASE WHEN l.state = 'Died' THEN 1 ELSE 0 END) AS Died,
  SUM(CASE WHEN l.state = 'Lost to follow up' THEN 1 ELSE 0 END) AS Lost_to_follow_up,
  SUM(CASE WHEN l.state = 'Not evaluated' THEN 1 ELSE 0 END) AS Not_Evaluated
FROM tb_registered_cases_summary r
LEFT JOIN mw_tb_initial tbi ON tbi.patient_id IN (
  SELECT patient_id
  FROM mw_tb_initial
  WHERE visit_date BETWEEN @startDate AND @endDate
)
LEFT JOIN last_tb_facility_outcome l ON l.pat = tbi.patient_id
GROUP BY r.category, r.registered_cases;
