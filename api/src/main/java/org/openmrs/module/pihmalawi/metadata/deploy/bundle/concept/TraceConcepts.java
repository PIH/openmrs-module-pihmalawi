package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;


import org.openmrs.Concept;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.VersionedPihConceptBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Requires({CoreConceptMetadataBundle.class})
public class TraceConcepts extends VersionedPihConceptBundle {


    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    protected void installNewVersion() throws Exception {

        Concept yes = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.YES);
        Concept no = MetadataUtils.existing(Concept.class, CommonConcepts.Concepts.NO);


        install(new ConceptBuilder("C0730C1B-E6D3-43DD-A11B-99E9D22C5048")
                .datatype(text)
                .conceptClass(question)
                .name("D16240B6-25E4-4A97-86E8-4C65BD3C094D", "Tracker", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("60A8AD9D-9A59-45D2-BD3D-4830EC02782E")
                .datatype(date)
                .conceptClass(question)
                .name("2934DEDA-7FCB-4785-86F9-C11DFCED3C23", "Date of Next Tracking Attempt", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("6356CBD9-8256-44A2-99F7-8C13DCBBB715")
                .datatype(coded)
                .conceptClass(question)
                .name("48E7408F-CB79-40E7-A4C9-2F8E4DAF5400", "Trace patient found",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        install(new ConceptBuilder("D6635E53-F5F6-44A9-9DE2-960A8C097ACF")
                .datatype(text)
                .conceptClass(question)
                .name("EF92C3D0-CFF6-4EA5-9388-DBE549312E90","Reason for missing appointment",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("DBEA54BD-DC98-45E3-B457-4A7FBF690CB8")
                .datatype(coded)
                .conceptClass(question)
                .name("E3F36866-810E-423E-91A3-673762B7740A", "Agreed to return to clinic",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .answers(yes,no)
                .build());

        install(new ConceptBuilder("B5BC12B7-C769-45CC-8BC5-6E53252B019E")
                .datatype(text)
                .conceptClass(question)
                .name("2C0E401B-D23A-46C6-A90F-0F0C326C4D55","Reasons for returning or not returning to the clinic",
                        Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());

        install(new ConceptBuilder("DA7E403E-EC7F-445B-B8CF-65F4E818A11A")
                .datatype(date)
                .conceptClass(question)
                .name("13B551BD-D480-4434-8916-110B2738E37C", "Trace, Date given", Locale.ENGLISH, ConceptNameType.FULLY_SPECIFIED)
                .build());
    }
}
