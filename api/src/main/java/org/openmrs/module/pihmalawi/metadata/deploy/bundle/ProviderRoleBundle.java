package org.openmrs.module.pihmalawi.metadata.deploy.bundle;

import org.openmrs.PersonAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.impl.PersonServiceImpl;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.pihmalawi.metadata.deploy.PihConstructors.providerRole;

/**
 * Install VHW Provider Roles
 */
@Component
@Requires( {ProviderAttributeTypeBundle.class, RelationshipTypeBundle.class})
public class ProviderRoleBundle extends AbstractMetadataBundle{

    @Autowired
    private PersonService personService;

    @Autowired
    private ProviderService providerService;

    public static final class ProviderRoles {
        public static final String VHW = "68624C4C-9E10-473B-A849-204820D16C45";
        public static final String VHW_SUPERVISOR = "11C1A56D-82F7-4269-95E8-2B67B9A3D837";
    }

    /**
     * Performs the installation of the metadata items
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {

        Set<RelationshipType> relationshipTypes = null;
        RelationshipType vhwToPatient = personService.getRelationshipTypeByUuid(RelationshipTypeBundle.RelationshipTypes.VHW_TO_PATIENT);
        if (vhwToPatient != null) {
            relationshipTypes = new HashSet<RelationshipType>();
            relationshipTypes.add(vhwToPatient);
        }

        Set<ProviderAttributeType> providerAttributes = new HashSet<ProviderAttributeType>();
        ProviderAttributeType providerAttributeType = providerService.getProviderAttributeTypeByUuid(ProviderAttributeTypeBundle.ProviderAttributeTypes.DATE_HIRED);
        if (providerAttributeType !=null ) {
            providerAttributes.add(providerAttributeType);
        }
        providerAttributeType = providerService.getProviderAttributeTypeByUuid(ProviderAttributeTypeBundle.ProviderAttributeTypes.NUMBER_OF_HOUSEHOLDS);
        if (providerAttributeType !=null ) {
            providerAttributes.add(providerAttributeType);
        }
        providerAttributeType = providerService.getProviderAttributeTypeByUuid(ProviderAttributeTypeBundle.ProviderAttributeTypes.EDUCATION_LEVEL);
        if (providerAttributeType !=null ) {
            providerAttributes.add(providerAttributeType);
        }

        ProviderRole vhwSupervisor = install(providerRole("VHW Supervisor", null, relationshipTypes, providerAttributes, ProviderRoles.VHW_SUPERVISOR));

        Set<ProviderRole> supervisorRoles = null;
        if (vhwSupervisor != null ) {
            supervisorRoles = new HashSet<ProviderRole>();
            supervisorRoles.add(vhwSupervisor);
        }
        install(providerRole("VHW", supervisorRoles, relationshipTypes, providerAttributes, ProviderRoles.VHW));

    }
}
