package org.openmrs.module.pihmalawi.metadata.deploy.bundle;


import org.openmrs.Concept;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ProviderService;
import org.openmrs.customdatatype.datatype.DateDatatype;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
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
        public static final String EDUCATION_LEVEL  = "1ff995ad-8bba-4660-a5cd-acc7801c9b51";

        public static final String EDUCATION_LEVEL_CONCEPT  = "655b226e-977f-11e1-8993-905e29aff6c1";

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

        Concept educationLevelConcept = conceptService.getConceptByUuid(ProviderAttributeTypes.EDUCATION_LEVEL_CONCEPT);
        Integer educationLevelConceptId = null;
        if (educationLevelConcept != null) {
            educationLevelConceptId = educationLevelConcept.getConceptId();
        }

        install(providerAttributeType(
                "Education Level",
                "Education Level",
                CodedConceptDatatype.class,
                educationLevelConceptId.toString(),
                0,
                1,
                ProviderAttributeTypes.EDUCATION_LEVEL));
    }
}
