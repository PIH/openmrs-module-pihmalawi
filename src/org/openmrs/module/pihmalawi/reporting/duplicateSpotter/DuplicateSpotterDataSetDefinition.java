package org.openmrs.module.pihmalawi.reporting.duplicateSpotter;

import java.util.Date;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;

public class DuplicateSpotterDataSetDefinition extends BaseDataSetDefinition {
	
	private static final long serialVersionUID = 6405583324151111487L;

	private boolean soundexCheck = false;
	
	private boolean soundexSwapFirstLastName = false;
	
	private boolean nnoEncounterMatching = false;
	
	private Date onOrAfter = null;
	
	public DuplicateSpotterDataSetDefinition() {
		super();
	}
	
	public DuplicateSpotterDataSetDefinition(String name, String description) {
		super(name, description);
	}

	public boolean isSoundexCheck() {
		return soundexCheck;
	}

	public void setSoundexCheck(boolean soundexCheck) {
		this.soundexCheck = soundexCheck;
	}

	public boolean isNnoEncounterMatching() {
		return nnoEncounterMatching;
	}

	public void setNnoEncounterMatching(boolean nnoEncounterMatching) {
		this.nnoEncounterMatching = nnoEncounterMatching;
	}

	public Date getOnOrAfter() {
		return onOrAfter;
	}

	public void setOnOrAfter(Date onOrAfter) {
		this.onOrAfter = onOrAfter;
	}

	public void setSoundexSwapFirstLastName(boolean soundexSwapFirstLastName) {
		this.soundexSwapFirstLastName = soundexSwapFirstLastName;
	}

	public boolean isSoundexSwapFirstLastName() {
		return soundexSwapFirstLastName;
	}
}
