package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.CoreConstructors;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationDescriptor;
import org.openmrs.module.metadatadeploy.descriptor.LocationTagDescriptor;
import org.openmrs.module.pihmalawi.metadata.Locations;
import org.springframework.stereotype.Component;

@Component
public class LocationBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        install(Locations.UNKNOWN);

        install(Locations.NENO_DISTRICT_HOSPITAL);
        install(Locations.DAMBE);
        install(Locations.LIGOWE);
        install(Locations.LIGOWE_HTC_OUTREACH);
        install(Locations.LUWANI_PRISON);
        install(Locations.LUWANI);
        install(Locations.MAGALETA);
        install(Locations.MAGALETA_OUTREACH);
        install(Locations.MATANDANI);
        install(Locations.NENO_INWARD);
        install(Locations.NENO_MISSION);
        install(Locations.NENO_PARISH);
        install(Locations.NSAMBE);

        install(Locations.NENO_HTC_OUTREACH);
        install(Locations.BINJE_OUTREACH);
        install(Locations.FPAM);
        install(Locations.GOLDEN_OUTREACH);
        install(Locations.NTAJA_OUTREACH);
        install(Locations.TEDZANI);

        install(Locations.CHIFUNGA);
        install(Locations.CHIFUNGA_HTC_OUTREACH);
        install(Locations.FELEMU_OUTREACH);
        install(Locations.KASAMBA_OUTREACH);
        install(Locations.LISUNGWI);
        install(Locations.LISUNGWI_HTC_OUTREACH);
        install(Locations.LISUNGWI_INWARD);
        install(Locations.MATOPE);
        install(Locations.MIDZEMBA);
        install(Locations.MIDZEMBA_HTC_OUTREACH);
        install(Locations.NKHULA_FALLS);
        install(Locations.ZALEWA);
        install(Locations.ZALEWA_OUTREACH);

        install(Locations.HIV_CLINICIAN_STATION);
        install(Locations.HIV_NURSE_STATION);
        install(Locations.HIV_RECEPTION);
        install(Locations.KCH);
        install(Locations.KUNTUMANJI);
        install(Locations.MALAWI);
        install(Locations.MLAMBE_HOSPITAL);
        install(Locations.MULANJE_DISTRICT_HOSPITAL);
        install(Locations.MWANZA_DISTRICT_HOSPITAL);
        install(Locations.OUTPATIENT_LOCATION);
        install(Locations.QECH);
        install(Locations.REGISTRATION_LOCATION);
        install(Locations.TB_RECEPTION);
        install(Locations.TB_SPUTUM_SUBMISSION_STATION);
        install(Locations.THYOLLO);
        install(Locations.VITALS_LOCATION);
    }

    protected void install(LocationDescriptor d) {
        Location location = CoreConstructors.location(d.name(), d.description(), d.uuid());
        if (d.parent() != null) {
            location.setParentLocation(MetadataUtils.existing(Location.class, d.parent().uuid()));
        }
        if (d.tags() != null) {
            for (LocationTagDescriptor td : d.tags()) {
                location.addTag(MetadataUtils.existing(LocationTag.class, td.uuid()));
            }
        }
        install(location);
        if (d.attributes() != null) {
            for (LocationAttributeDescriptor lad : d.attributes()) {
                install(CoreConstructors.locationAttribute(lad.location().uuid(), lad.type().uuid(), lad.value(), lad.uuid()));
            }
        }
    }

}
