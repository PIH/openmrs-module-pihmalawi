package org.openmrs.module.pihmalawi.metadata.deploy;


import org.openmrs.ProgramAttributeType;

public class PihConstructors {

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
