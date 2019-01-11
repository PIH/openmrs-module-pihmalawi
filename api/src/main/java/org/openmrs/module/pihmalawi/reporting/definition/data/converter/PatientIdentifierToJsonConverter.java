package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.openmrs.PatientIdentifier;
import org.openmrs.module.pihmalawi.common.JsonObject;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class PatientIdentifierToJsonConverter implements DataConverter {

    public PatientIdentifierToJsonConverter() {};

    @Override
    public Object convert(Object o) {
        PatientIdentifier identifier = (PatientIdentifier) o;
        JsonObject result = new JsonObject();
        result.put("location", identifier.getLocation());
        result.put("identifierType", identifier.getIdentifierType());
        result.put("identifier", new PatientIdentifierConverter().convert(identifier));
        result.put("raw_identifier", identifier.getIdentifier());
        result.put("preferred", identifier.getPreferred());
        return result;
    }

    @Override
    public Class<?> getInputDataType() {
        return PatientIdentifier.class;
    }

    @Override
    public Class<?> getDataType() {
        return JsonObject.class;
    }
}
