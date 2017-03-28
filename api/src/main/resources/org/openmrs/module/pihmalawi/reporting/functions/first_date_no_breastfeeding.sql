/*
	Extract date on which patient first stopped breastfeeding over 6 weeks ago
	For all patients in the register, first recorded breastfeeding status obs date, if the value is stopped over 6 weeks ago,
	and after any other different status is recorded.
	We intentionally do not restrict this by location, as we want Observations to span all locations
*/
DROP FUNCTION IF EXISTS first_date_no_breastfeeding;

CREATE FUNCTION first_date_no_breastfeeding(patientId INT, endDate DATE)
  RETURNS DATE
DETERMINISTIC
  BEGIN
    DECLARE ret DATE;

    DECLARE lastInvalidDate DATE;

    SELECT  max(visit_date) INTO lastInvalidDate
    FROM 		mw_eid_visits
    WHERE   breastfeeding_status != 'Breastfeeding stopped over 6 weeks ago'
    AND     patient_id = patientId
    AND     visit_date <= endDate;

    SELECT  min(visit_date) INTO ret
    FROM 		mw_eid_visits
    WHERE   breastfeeding_status = 'Breastfeeding stopped over 6 weeks ago'
    AND     patient_id = patientId
    AND     (lastInvalidDate is null OR (visit_date <= endDate AND visit_date <= lastInvalidDate));

    return ret;
  END