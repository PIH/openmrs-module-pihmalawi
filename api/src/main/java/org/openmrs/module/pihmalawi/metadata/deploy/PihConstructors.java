package org.openmrs.module.pihmalawi.metadata.deploy;


import org.openmrs.ProgramAttributeType;
import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.ProviderRole;

import java.util.Set;

public class PihConstructors {

    public static ProviderRole providerRole(
            String name,
            Set<ProviderRole> superviseeProviderRoles,
            Set<RelationshipType> relationshipTypes,
            Set<ProviderAttributeType> providerAttributeTypes,
            String uuid) {

        ProviderRole obj = new ProviderRole();
        obj.setName(name);
        obj.setSuperviseeProviderRoles(superviseeProviderRoles);
        obj.setRelationshipTypes(relationshipTypes);
        obj.setProviderAttributeTypes(providerAttributeTypes);
        obj.setUuid(uuid);
        return obj;
    }

    public static ProgramAttributeType programAttributeType(
            String name,
            String description,
            String datatypeClassname,
            String datatypeConfig,
            Integer minOccurs,
            Integer maxOccurs,
            String uuid) {

        ProgramAttributeType obj = new ProgramAttributeType();
        obj.setName(name);
        obj.setDescription(description);
        obj.setDatatypeClassname(datatypeClassname);
        obj.setDatatypeConfig(datatypeConfig);
        obj.setMinOccurs(minOccurs);
        obj.setMaxOccurs(maxOccurs);
        obj.setUuid(uuid);
        return obj;
    }
}
