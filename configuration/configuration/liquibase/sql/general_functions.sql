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
