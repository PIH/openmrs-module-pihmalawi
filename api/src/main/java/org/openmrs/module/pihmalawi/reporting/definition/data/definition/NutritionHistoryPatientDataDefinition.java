package org.openmrs.module.pihmalawi.reporting.definition.data.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

import java.util.Date;
import java.util.List;

/**
 * Returns a List of all Weight, Height and MUAC observations
 * with obsDatetime less than or equal to the configured endDate.
 * Includes a calculated, transient "BMI" for each weight as applicable
 * Results will be ordered chronologically from earliest to most recen
 */
@Localized("pihmalawi.NutritionHistoryPatientDataDefinition")
public class NutritionHistoryPatientDataDefinition extends BaseDataDefinition implements PatientDataDefinition {

    public NutritionHistoryPatientDataDefinition() {
        super();
    }

    @ConfigurationProperty
    private Date endDate;

    @Override
    public Class<?> getDataType() {
        return List.class;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
