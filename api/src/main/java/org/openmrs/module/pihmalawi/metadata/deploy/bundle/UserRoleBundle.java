package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.privilege;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

@Component
public class UserRoleBundle extends AbstractMetadataBundle {

    public static final class UserRoles {

        public static final String VIEW_CLINICAL_DATA_PRIVILEGE = "cbbfac84-e94f-42a9-9925-6e99f7a8a1b9";
        public static final String VIEW_CLINICAL_DATA = "9c419a7e-f11f-4d66-8944-aab24b538f9c";

        public static final String VIEW_NUTRITION_PATIENTS = "6e1ec1d3-80bd-4e08-8665-75784f09df02";
        public static final String EDIT_NUTRITION_PATIENTS = "d2742175-fdf4-4d3d-81cc-f8a072df0688";
        public static final String MANAGE_NUTRITION_PROGRAM = "B29C7016-303E-4B95-B31E-D6B89F16F443";
    }

    @Override
    public void install() throws Exception {

        install(privilege("View clinical data", "View patient's clinical data", UserRoles.VIEW_CLINICAL_DATA_PRIVILEGE));
        install(role("Clinical data","Access to clinical data", null, idSet("View clinical data"), UserRoles.VIEW_CLINICAL_DATA));

        install(role("View nutrition patients","View patient records who are enrolled in the nutrition program.",
                null,
                idSet( "View Patient Programs" ,
                "Get Order Types" ,
                "Get Field Types" ,
                "View Orders" ,
                "Get Identifier Types" ,
                "Get Forms" ,
                "Get Orders" ,
                "Patient Dashboard - View Overview Section" ,
                "View Field Types" ,
                "View People" ,
                "Get Patient Programs" ,
                "View Visit Attribute Types" ,
                "Get People" ,
                "Get Person Attribute Types" ,
                "View Observations" ,
                "Get Allergies" ,
                "Patient Overview - View Patient Actions" ,
                "View Problems" ,
                "Get Problems" ,
                "Get Programs" ,
                "View Identifier Types" ,
                "View Patient Identifiers" ,
                "Patient Dashboard - View Demographics Section" ,
                "View Encounter Types" ,
                "Provider Management API" ,
                "Manage Forms" ,
                "View Allergies" ,
                "View Navigation Menu" ,
                "View Visits" ,
                "Get Visit Types" ,
                "Get Visit Attribute Types" ,
                "Get Visits" ,
                "Get Providers" ,
                "View Visit Types" ,
                "View Users" ,
                "View Order Types" ,
                "Patient Dashboard - View Visits Section" ,
                "View Person Attribute Types" ,
                "View Unpublished Forms" ,
                "Patient Overview - View Programs" ,
                "Patient Overview - View Problem List" ,
                "Patient Overview - View Allergies" ,
                "Get Encounters" ,
                "Get Users" ,
                "View Programs" ,
                "Get Locations" ,
                "Get Encounter Types" ,
                "Get Patients" ,
                "View Encounters" ,
                "View Forms" ,
                "Get Observations" ,
                "View Locations" ,
                "Get Patient Identifiers" ,
                "View Providers" ,
                "View Patients" ,
                "Patient Dashboard - View Encounters Section" ,
                "Manage Encounter Roles" ,
                "Patient Dashboard - View Patient Summary" ),
                UserRoles.VIEW_NUTRITION_PATIENTS));

        install(role("Edit nutrition patients","This role allows the user to view and edit patient nutrition data.",
                idSet("View nutrition patients"),
                idSet("Add Allergies",
                        "Add Encounters",
                        "Add Observations",
                        "Add Patient Identifiers",
                        "Add Patient Programs",
                        "Add Patients",
                        "Add People",
                        "Add Visits",
                        "Delete Encounters",
                        "Delete Observations",
                        "Delete Patient Identifiers",
                        "Delete Patient Programs",
                        "Delete Patients",
                        "Delete People",
                        "Delete Visits",
                        "Edit Allergies",
                        "Edit Observations",
                        "Edit Orders",
                        "Edit Patient Identifiers",
                        "Edit Patient Programs",
                        "Edit Patients",
                        "Edit People",
                        "Edit Users",
                        "Edit Visits",
                        "Form Entry",
                        "Get Concepts",
                        "Get Encounter Roles",
                        "Remove Allergies",
                        "Remove Problems",
                        "SQL Level Access",
                        "View Calculations",
                        "View Concepts",
                        "View Encounter Roles"),
                UserRoles.EDIT_NUTRITION_PATIENTS));

        install(role("Manage nutrition program","Access to managing the nutrition program",
                idSet("Anonymous",
                        "Provider",
                        "View nutrition patients",
                        "Edit nutrition patients"),
                null,
                UserRoles.MANAGE_NUTRITION_PROGRAM));
    }
}
