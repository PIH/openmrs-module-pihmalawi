package org.openmrs.module.pihmalawi;


public class PihMalawiConstants {

    public static final String MODULE_ID = "pihmalawi";
    public static final String OPENMRS_WAREHOUSE_CONNECTION_PROPERTIES_FILE_NAME = "warehouse-connection.properties";
    public static final String PATIENT_LAB_TESTS_SQL_DATA_SET = "org/openmrs/module/pihmalawi/reporting/datasets/sql/get-patient-tests.sql";
    public static final String PRIV_CHW_MANAGEMENT_APP = "CHW Management App";
    public static final String HEALTH_FACILITY_GP_NAME = "providermanagement.locationTag";
    public static final String HEALTH_FACILITY_GP_VALUE = "Health Facility";
    public static final String DASHBOARD_IDENTIFIERS_GP_NAME = "dashboard.identifiers";
    public static final String DASHBOARD_IDENTIFIERS_GP_VALUE = "{\"9\":[\"ARV Number\",\"HCC Number\",\"KS Number\",\"Chronic Care Number\",\"Palliative Care Number\",\"PDC Identifier\"]}";
    public static final String PATIENT_IDENTIFIER_IMPORTANT_TYPES_GP_NAME = "patient_identifier.importantTypes";
    public static final String PATIENT_IDENTIFIER_IMPORTANT_TYPES_GP_VALUE = "ARV Number,HCC Number,Chronic Care Number,Palliative Care Number,PDC Identifier";
    public static final String TASK_CLOSE_STALE_VISITS_NAME = "PIH Malawi module - Close Stale Visits";
    public static final String TASK_CLOSE_STALE_VISITS_DESCRIPTION = "Closes any open visits that are no longer active";
    public static final long TASK_CLOSE_STALE_VISITS_REPEAT_INTERVAL = 300L;

    public static final String TASK_MIGRATE_EID_TEST_RESULTS = "Migrate EID Test Results";
    public static final String TASK_MIGRATE_EID_TEST_RESULTS_DESCRIPTION = "Migrates EID Test Results on the Exposed Child Initial Form to the EID Screening Form";
    public static final String MEDIC_MOBILE_FACILITY = "Yendanafe Catchment";

    public static final String TRANSFERRED_OUT_PROGRAM_ATTRIBUTE_TYPE = "738D25EB-79E2-4BB4-8E7D-CB954908BB27";
}
