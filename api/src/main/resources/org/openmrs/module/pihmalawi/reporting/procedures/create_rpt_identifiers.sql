DELIMITER $$

/************************************************************************
Get identifiers for the patient at the given location and overall
*************************************************************************/
DROP PROCEDURE IF EXISTS create_rpt_identifiers;
$$
CREATE PROCEDURE create_rpt_identifiers(IN _location VARCHAR(255)) BEGIN

  DROP TEMPORARY TABLE IF EXISTS rpt_identifiers;
  CREATE TEMPORARY TABLE rpt_identifiers (
    patient_id        INT NOT NULL PRIMARY KEY,
    art_number        VARCHAR(50),
    all_art_numbers   VARCHAR(1000),
    eid_number        VARCHAR(50),
    all_eid_numbers   VARCHAR(1000),
    ncd_number        VARCHAR(50),
    all_ncd_numbers   VARCHAR(1000)
  );
  CREATE INDEX rpt_identifiers_patient_id_idx ON rpt_identifiers(patient_id);

  INSERT INTO rpt_identifiers (patient_id, art_number)
    SELECT r.patient_id, r.art_number from mw_art_register r where r.location = _location
  ON DUPLICATE KEY UPDATE art_number = values(art_number);

  INSERT INTO rpt_identifiers (patient_id, all_art_numbers)
    SELECT r.patient_id, group_concat(DISTINCT art_number ORDER BY art_number asc SEPARATOR ', ') FROM mw_art_register r GROUP BY r.patient_id
  ON DUPLICATE KEY UPDATE all_art_numbers = values(all_art_numbers);

  INSERT INTO rpt_identifiers (patient_id, eid_number)
    SELECT r.patient_id, r.eid_number from mw_eid_register r where r.location = _location
  ON DUPLICATE KEY UPDATE eid_number = values(eid_number);

  INSERT INTO rpt_identifiers (patient_id, all_eid_numbers)
    SELECT r.patient_id, group_concat(DISTINCT eid_number ORDER BY eid_number asc SEPARATOR ', ') FROM mw_eid_register r GROUP BY r.patient_id
  ON DUPLICATE KEY UPDATE all_eid_numbers = values(all_eid_numbers);

  INSERT INTO rpt_identifiers (patient_id, ncd_number)
    SELECT r.patient_id, r.ncd_number from mw_ncd_register r where r.location = _location
  ON DUPLICATE KEY UPDATE ncd_number = values(ncd_number);

  INSERT INTO rpt_identifiers (patient_id, all_ncd_numbers)
    SELECT r.patient_id, group_concat(DISTINCT ncd_number ORDER BY ncd_number asc SEPARATOR ', ') FROM mw_ncd_register r GROUP BY r.patient_id
  ON DUPLICATE KEY UPDATE all_ncd_numbers = values(all_ncd_numbers);

END
$$

DELIMITER ;