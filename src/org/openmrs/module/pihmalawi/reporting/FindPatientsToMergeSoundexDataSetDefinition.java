package org.openmrs.module.pihmalawi.reporting;

import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class FindPatientsToMergeSoundexDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;

	@ConfigurationProperty
	boolean swapFirstLastName = false;
	
	@ConfigurationProperty
	List<EncounterType> encounterTypesToLookForDuplicates = null;
	
	public FindPatientsToMergeSoundexDataSetDefinition() {
		super();
	}
	
	public FindPatientsToMergeSoundexDataSetDefinition(String name, String description) {
		super(name, description);
	}

	public void setSwapFirstLastName(boolean soundexSwapFirstLastName) {
		this.swapFirstLastName = soundexSwapFirstLastName;
	}

	public boolean isSwapFirstLastName() {
		return swapFirstLastName;
	}

	public boolean getSwapFirstLastName() {
		return swapFirstLastName;
	}

	public List<EncounterType> getEncounterTypesToLookForDuplicates() {
		return encounterTypesToLookForDuplicates;
	}

	public void setEncounterTypesToLookForDuplicates(
			List<EncounterType> encounterTypesToLookForDuplicates) {
		this.encounterTypesToLookForDuplicates = encounterTypesToLookForDuplicates;
	}
}
