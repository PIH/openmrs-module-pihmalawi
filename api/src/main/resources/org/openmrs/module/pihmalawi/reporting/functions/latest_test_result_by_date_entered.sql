
DROP FUNCTION IF EXISTS latest_test_result_by_date_entered;

CREATE FUNCTION latest_test_result_by_date_entered(patientId INT, testType VARCHAR(100), fromDate DATE, toDate DATE, offsetNum INT)
  RETURNS INT
DETERMINISTIC
  BEGIN
    DECLARE ret INT;

    SELECT    t.lab_test_id into ret
    FROM      mw_lab_tests t
    WHERE     t.patient_id = patientId
    AND       t.test_type = testType
    AND       (t.result_numeric is not null or t.result_coded is not null or t.result_exception is not null)
    AND       (fromDate is null or t.date_result_entered >= fromDate)
    AND       (toDate is null or t.date_result_entered <= toDate)
    ORDER BY  t.date_result_entered desc
    LIMIT     1
    OFFSET    offsetNum;

    return ret;
  END
