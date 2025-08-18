-- noinspection SqlDialectInspectionForFile

-- You should uncomment this line to check syntax in IDE.  Liquibase handles this internally.
-- DELIMITER #

/*
 get global property value
*/
#
DROP FUNCTION IF EXISTS global_property_value;
#
CREATE FUNCTION global_property_value(
    _property varchar(255),
    _defaultValue text
)
    RETURNS text
    DETERMINISTIC
BEGIN
    DECLARE val text;
    SELECT property_value into val FROM global_property where property = _property;
    SELECT if(val is null || val = '', _defaultValue, val) into val;
    return val;
END
#

/*
get names from the concept_name table
*/
#
DROP FUNCTION IF EXISTS concept_name;
#
CREATE FUNCTION concept_name(
    _conceptID INT,
    _locale varchar(50)
)
    RETURNS VARCHAR(255)
    DETERMINISTIC

BEGIN
    DECLARE conceptName varchar(255);

    SELECT name INTO conceptName
    FROM concept_name
    WHERE voided = 0
      AND concept_id = _conceptID
    order by if(_locale = locale, 0, 1), if(locale = 'en', 0, 1),
             locale_preferred desc, ISNULL(concept_name_type),
             field(concept_name_type,'FULLY_SPECIFIED','SHORT')
    limit 1;

    RETURN conceptName;
END
#

#
/*
  Location name
*/
#
DROP FUNCTION IF EXISTS location_name;
#
CREATE FUNCTION location_name (
    _location_id int
)
    RETURNS TEXT
    DETERMINISTIC
BEGIN
    DECLARE locName TEXT;

    select      name into locName
    from        location
    where       location_id = _location_id;

    RETURN locName;
END