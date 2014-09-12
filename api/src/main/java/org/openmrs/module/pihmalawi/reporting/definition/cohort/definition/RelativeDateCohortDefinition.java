/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.reporting.definition.cohort.definition;

import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;

/**
 * Able to return patients who have two date attributes are a certain time apart
 */
public class RelativeDateCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty(required = true)
	private Mapped<? extends PatientDataDefinition> earlierDateDefinition;

	@ConfigurationProperty(required = true)
	private Mapped<? extends PatientDataDefinition> laterDateDefinition;

	@ConfigurationProperty(required = true)
	private RangeComparator differenceOperator;

	@ConfigurationProperty
	private Integer differenceNumber;

	@ConfigurationProperty
	private DurationUnit differenceUnit;

	@ConfigurationProperty
	private boolean passIfDate1Null = false;

	@ConfigurationProperty
	private boolean passIfDate2Null = false;

	public RelativeDateCohortDefinition() {
		super();
	}

	public Mapped<? extends PatientDataDefinition> getEarlierDateDefinition() {
		return earlierDateDefinition;
	}

	public void setEarlierDateDefinition(Mapped<? extends PatientDataDefinition> earlierDateDefinition) {
		this.earlierDateDefinition = earlierDateDefinition;
	}

	public Mapped<? extends PatientDataDefinition> getLaterDateDefinition() {
		return laterDateDefinition;
	}

	public void setLaterDateDefinition(Mapped<? extends PatientDataDefinition> laterDateDefinition) {
		this.laterDateDefinition = laterDateDefinition;
	}

	public RangeComparator getDifferenceOperator() {
		return differenceOperator;
	}

	public void setDifferenceOperator(RangeComparator differenceOperator) {
		this.differenceOperator = differenceOperator;
	}

	public Integer getDifferenceNumber() {
		return differenceNumber;
	}

	public void setDifferenceNumber(Integer differenceNumber) {
		this.differenceNumber = differenceNumber;
	}

	public DurationUnit getDifferenceUnit() {
		return differenceUnit;
	}

	public void setDifferenceUnit(DurationUnit differenceUnit) {
		this.differenceUnit = differenceUnit;
	}

	public boolean isPassIfDate1Null() {
		return passIfDate1Null;
	}

	public void setPassIfDate1Null(boolean passIfDate1Null) {
		this.passIfDate1Null = passIfDate1Null;
	}

	public boolean isPassIfDate2Null() {
		return passIfDate2Null;
	}

	public void setPassIfDate2Null(boolean passIfDate2Null) {
		this.passIfDate2Null = passIfDate2Null;
	}
}