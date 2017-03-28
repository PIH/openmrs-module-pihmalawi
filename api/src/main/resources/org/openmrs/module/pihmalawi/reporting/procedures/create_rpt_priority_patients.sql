DELIMITER $$

/************************************************************************
  Get priority conditions for each patient
*************************************************************************/

DROP PROCEDURE IF EXISTS create_rpt_priority_patients;
$$
CREATE PROCEDURE create_rpt_priority_patients(IN _endDate DATE) BEGIN

  DROP TEMPORARY TABLE IF EXISTS rpt_priority_patients;
  CREATE TEMPORARY TABLE rpt_priority_patients (
    patient_id  INT NOT NULL,
    priority   VARCHAR(50)
  );
  CREATE INDEX rpt_priority_patients_patient_id_idx ON rpt_priority_patients(patient_id);

  -- HIV:  HIV patients (all)

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT DISTINCT p.patient_id, 'HIV'
    FROM
      (
        SELECT patient_id FROM mw_pre_art_register WHERE start_date <= _endDate
        UNION
        SELECT patient_id FROM mw_art_register WHERE start_date <= _endDate
      ) p
  ;

  -- BP > 180/110:  Hypertension patients with BP ever greater than 180/110 (both systolic and diastolic should exceed threshold)

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT  DISTINCT p.patient_id, 'BP > 180/110'
    FROM
      (
        SELECT  v.patient_id
        FROM    mw_ncd_visits v
        WHERE   v.systolic_bp > 180 and v.diastolic_bp > 110
      ) p
  ;

  -- ON INSULIN:  Diabetes patients on insulin

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT  DISTINCT p.patient_id, 'ON INSULIN'
    FROM
      (
        SELECT  v.patient_id
        FROM    mw_ncd_visits v
        WHERE   v.on_insulin = TRUE
      ) p
  ;

  -- SEVERE PERSISTENT ASTHMA:  Asthma patients with severity of “severe persistent” at last visit

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT  DISTINCT p.patient_id, 'SEVERE PERSISTENT ASTHMA'
    FROM
      (
        SELECT  v.patient_id
        FROM    mw_ncd_visits v
        WHERE   v.asthma_classification = 'Severe persistent'
        AND     v.ncd_visit_id = latest_asthma_visit(v.patient_id, _endDate)
      ) p
  ;

  -- "> 5 SEIZURES / MONTH":  Epilepsy patients reporting over 5 seizures per month at last visit

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT  DISTINCT p.patient_id, '> 5 SEIZURES / MONTH'
    FROM
      (
        SELECT  v.patient_id
        FROM    mw_ncd_visits v
        WHERE   v.num_seizures > 5
        AND     v.ncd_visit_id = latest_epilepsy_visit(v.patient_id, _endDate)
      ) p
  ;

  -- Sickle cell disease patients (all)
  -- Chronic kidney disease patients (all)
  -- Rheumatic Heart Disease patients (all)
  -- Congestive Heart Failure patients (all)

  INSERT INTO rpt_priority_patients(patient_id, priority)
    SELECT  DISTINCT p.patient_id, p.diagnosis
    FROM
      (
        SELECT    d.patient_id, d.diagnosis
        FROM      mw_ncd_diagnoses d
        WHERE     d.diagnosis in ('Sickle cell disease' , 'Chronic kidney disease', 'Rheumatic heart disease', 'Congestive heart failure')
        AND       d.diagnosis_date <= _endDate
        GROUP BY  d.patient_id, d.diagnosis
      ) p
  ;

END
$$