# identifierTypes.csv previously had Location behavior=REQUIRED hardcoded for nearly every
# identifier type; corrected in the CSV to match each type's real production value (MLW-1839). But
# PatientIdentifierTypeLineProcessor.fill() only calls setLocationBehavior() when the CSV cell is
# non-blank - a blank cell means "leave whatever's already there", not "clear it". So on any
# database that already has the old, wrong REQUIRED value saved (from before this CSV fix), the
# corrected CSV can never retroactively clear it - the identifiertypes domain would reload forever
# without ever fixing this field. Apply the correct values directly here instead, before that
# domain loads - a no-op on a database that's never seen the old bug (a fresh install, or a real
# production copy that's never had this branch's earlier, buggy CSV applied to it), since
# Initializer will simply leave these already-correct values alone.

update patient_identifier_type set location_behavior = null where uuid in (
    '66784d84-977f-11e1-8993-905e29aff6c1', -- ARV Number
    '11a76c3e-1db8-4d16-9252-9a18b5ed1843', -- Chronic Care Number
    '667852ac-977f-11e1-8993-905e29aff6c1', -- Dummy ID
    '66786256-977f-11e1-8993-905e29aff6c1', -- HCC Number
    '667857b6-977f-11e1-8993-905e29aff6c1', -- KS Number
    '66785018-977f-11e1-8993-905e29aff6c1', -- VHW ID
    '66784c3a-977f-11e1-8993-905e29aff6c1', -- National id
    '66784ece-977f-11e1-8993-905e29aff6c1', -- z_deprecated Pre ART Number (Old format)
    '66785158-977f-11e1-8993-905e29aff6c1', -- District TB Number
    '667853f6-977f-11e1-8993-905e29aff6c1', -- z_deprecated EID Number
    '66785536-977f-11e1-8993-905e29aff6c1', -- Unknown ID
    '66785676-977f-11e1-8993-905e29aff6c1', -- MDR-TB Program Identifier
    '667858f6-977f-11e1-8993-905e29aff6c1', -- z_deprecated PART Number
    '66785a54-977f-11e1-8993-905e29aff6c1', -- Diabetes Number
    '66785b94-977f-11e1-8993-905e29aff6c1', -- LAB IDENTIFIER
    '66785e64-977f-11e1-8993-905e29aff6c1', -- DS Number
    '66785fc2-977f-11e1-8993-905e29aff6c1', -- Filing number
    '6678610c-977f-11e1-8993-905e29aff6c1', -- Archived filing number
    '667863a0-977f-11e1-8993-905e29aff6c1'  -- Radiology Study Number
);

update patient_identifier_type set location_behavior = 'NOT_USED'
where uuid = 'a5d38e09-efcb-4d91-a526-50ce1ba5011a' -- OpenEMPI ID
and (location_behavior is null or location_behavior != 'NOT_USED');
