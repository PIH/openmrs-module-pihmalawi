/*
    Return the concept id for the given uuid
*/
DROP FUNCTION IF EXISTS concept_by_uuid;
CREATE FUNCTION concept_by_uuid(_uuid CHAR(38))
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE ret INT;

    SELECT concept_id into ret
        FROM concept
        WHERE uuid = _uuid;

    return ret;
END
;

/*
    Return the patient_identifier_type id for the given uuid
*/
DROP FUNCTION IF EXISTS identifier_type_by_uuid;
CREATE FUNCTION identifier_type_by_uuid(_uuid CHAR(38))
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE ret INT;

    SELECT patient_identifier_type_id into ret
        FROM patient_identifier_type
        WHERE uuid = _uuid;

    return ret;
END
;

/*
    Return the program id for the given uuid
*/
DROP FUNCTION IF EXISTS program_by_uuid;
CREATE FUNCTION program_by_uuid(_uuid CHAR(38))
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE ret INT;

    SELECT program_id into ret
    FROM program
    WHERE uuid = _uuid;

    return ret;
END
;
