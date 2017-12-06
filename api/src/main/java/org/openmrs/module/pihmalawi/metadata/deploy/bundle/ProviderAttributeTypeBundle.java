package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype;
import org.openmrs.module.coreapps.customdatatype.LocationDatatype;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.providerAttributeType;

/**
 * Installs VHW provider attributes
 */
@Component
public class ProviderAttributeTypeBundle extends AbstractMetadataBundle {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private ProviderService providerService;

    public static final class ProviderAttributeTypes {
        public static final String PHONE_NUMBER  = "30375A78-FA92-4C5C-A2FD-7E8339EC69CF";
        public static final String NUMBER_OF_HOUSEHOLDS  = "0c267ae8-f793-4cf8-9b27-93accaa45d86";
        public static final String DATE_HIRED  = "c8ef8a16-a8cd-4748-b0ea-e8a1ec503fbb";
        public static final String HH_MODEL_TEST  = "C0E1F105-DD36-4577-B00E-87A08D446A3A";
        public static final String HEALTH_FACILITY  = "94047146-7918-4927-9401-F4284A10C7FD";

        public static final String HH_MODEL_TEST_CONCEPT  = "0E483511-6278-4D1A-881A-6385C223FAC7";


    }
    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {

        install(providerAttributeType(
                "Phone Number",
                "Provider phone number",
                FreeTextDatatype.class,
                null,
                0,
                1,
                ProviderAttributeTypes.PHONE_NUMBER));

        install(providerAttributeType(
                "Households",
                "Number of households monitored by a VHW",
                FreeTextDatatype.class,
                null,
                0,
                1,
                ProviderAttributeTypes.NUMBER_OF_HOUSEHOLDS));

        ProviderAttributeType dateHired = install(providerAttributeType(
                "Date Hired",
                "The date the provider was hired.",
                DateDatatype.class,
                null,
                0,
                1,
                ProviderAttributeTypes.DATE_HIRED));
        if (dateHired != null) {
            dateHired.setPreferredHandlerClassname("org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler");
            providerService.saveProviderAttributeType(dateHired);
        }


        Concept hhTestConcept = conceptService.getConceptByUuid(ProviderAttributeTypes.HH_MODEL_TEST_CONCEPT);
        Integer hhTestConceptId = null;
        if (hhTestConcept != null) {
            hhTestConceptId = hhTestConcept.getConceptId();
            
            install(providerAttributeType(
                    "Passed HH Test",
                    "Passed HH Test",
                    CodedConceptDatatype.class,
                    hhTestConceptId.toString(),
                    0,
                    1,
                    ProviderAttributeTypes.HH_MODEL_TEST));

        }
        install(providerAttributeType(
                PihMalawiConstants.HEALTH_FACILITY_GP_VALUE,
                PihMalawiConstants.HEALTH_FACILITY_GP_VALUE,
                LocationDatatype.class,
                PihMalawiConstants.HEALTH_FACILITY_GP_VALUE,
                0,
                1,
                ProviderAttributeTypes.HEALTH_FACILITY));

    }
}
