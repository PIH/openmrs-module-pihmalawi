
DROP FUNCTION IF EXISTS latest_diabetes_htn_visit;

CREATE FUNCTION latest_diabetes_htn_visit(patientId INT, endDate DATE)
  RETURNS INT
DETERMINISTIC
  BEGIN
    DECLARE ret INT;

    SELECT    v.ncd_visit_id INTO ret
    FROM      mw_ncd_visits v
    WHERE     v.patient_id = patientId
    AND       (v.diabetes_htn_initial = TRUE || v.diabetes_htn_followup = TRUE)
    AND       v.visit_date <= endDate
    order BY  v.visit_date DESC
    LIMIT     1;

    return ret;
  END
