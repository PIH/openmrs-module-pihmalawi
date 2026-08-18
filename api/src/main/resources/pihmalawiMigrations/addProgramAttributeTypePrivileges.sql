-- OpenMRS core guards the ProgramWorkflowService program-attribute-type methods with @Authorized string
-- literals -- "Get/Manage/Purge Patient Program Attribute Types" -- but never inserts those privileges in
-- its own liquibase core-data.  Core creates every other "* Attribute Types" privilege (person, visit,
-- location, concept, provider, order set, diagnoses); these three are the exception.

-- Any code path that reads a ProgramAttributeType therefore fails with
-- "Privilege Get Patient Program Attribute Types does not exist in the database".  For Malawi that path is
-- QuickProgramsFormController (/module/quickprograms/*.form), which reads the transferred-out program
-- attribute type by uuid when recording a transfer out.
-- Create the three privileges, then grant them the way the corresponding patient program privileges are
-- already granted in this database: read access to every role that can already 'Get Patient Programs',
-- manage to the two full-privilege roles, and purge to the full-privilege role only.

insert ignore into privilege (privilege, description, uuid) values
    ('Get Patient Program Attribute Types', 'Able to get patient program attribute types', uuid()),
    ('Manage Patient Program Attribute Types', 'Able to add/edit/retire patient program attribute types', uuid()),
    ('Purge Patient Program Attribute Types', 'Able to purge patient program attribute types', uuid());

-- Read access follows 'Get Patient Programs' -- a role that may read a patient's programs may read the
-- attribute types describing them.  Held in a temp table so the insert does not read its own target.

create temporary table tmp_program_reader_roles as
select distinct role from role_privilege where privilege = 'Get Patient Programs';

insert ignore into role_privilege (role, privilege)
select role, 'Get Patient Program Attribute Types' from tmp_program_reader_roles;

drop temporary table tmp_program_reader_roles;

-- Manage is metadata administration, so it goes to both full-privilege roles.  Purge is destructive and
-- stays with 'Privilege Level: Full' only.

insert ignore into role_privilege (role, privilege)
select r.role, 'Manage Patient Program Attribute Types'
from role r where r.role in ('Privilege Level: Full', 'Privilege Level: High');

insert ignore into role_privilege (role, privilege)
select r.role, 'Purge Patient Program Attribute Types'
from role r where r.role = 'Privilege Level: Full';
