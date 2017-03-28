
CREATE FUNCTION first_test_result_by_date_entered(patientId INT, testType VARCHAR(100), endDate DATE)
  RETURNS INT
DETERMINISTIC
  BEGIN
    DECLARE ret INT;

    SELECT    t.lab_test_id into ret
    FROM      mw_lab_tests t
    WHERE     t.patient_id = patientId
    AND       t.test_type = testType
    AND       (t.result_numeric is not null or t.result_coded is not null or t.result_exception is not null)
    AND       (endDate is null or t.date_result_entered <= endDate)
    ORDER BY  t.date_result_entered asc
    LIMIT     1;

    return ret;
  END
