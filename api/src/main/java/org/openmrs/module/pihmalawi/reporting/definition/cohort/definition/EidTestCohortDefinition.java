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

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.module.pihmalawi.common.TraceConstants;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class EidTestCohortDefinition extends BaseCohortDefinition {

    @ConfigurationProperty
    private Location location;

    @ConfigurationProperty
    private TraceConstants.TraceType traceType;

	@ConfigurationProperty
	private boolean noTestResultsAfter12MonthsOld;

    @ConfigurationProperty
    private boolean noTestResultsAfterBreastfeeding;

    @ConfigurationProperty
    private Integer lastTestResultWithinLastDays;

    @ConfigurationProperty
    private boolean noVisitSinceLastTestResult;

    @ConfigurationProperty
    private Concept lastTestResult;

    @ConfigurationProperty
    private Concept secondToLastTestResult;

    @ConfigurationProperty
    private Integer maximumNumberOfTestResults;

	public EidTestCohortDefinition() {
		super();
	}

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TraceConstants.TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceConstants.TraceType traceType) {
        this.traceType = traceType;
    }

    public boolean isNoTestResultsAfter12MonthsOld() {
        return noTestResultsAfter12MonthsOld;
    }

    public void setNoTestResultsAfter12MonthsOld(boolean noTestResultsAfter12MonthsOld) {
        this.noTestResultsAfter12MonthsOld = noTestResultsAfter12MonthsOld;
    }

    public boolean isNoTestResultsAfterBreastfeeding() {
        return noTestResultsAfterBreastfeeding;
    }

    public void setNoTestResultsAfterBreastfeeding(boolean noTestResultsAfterBreastfeeding) {
        this.noTestResultsAfterBreastfeeding = noTestResultsAfterBreastfeeding;
    }

    public Integer getLastTestResultWithinLastDays() {
        return lastTestResultWithinLastDays;
    }

    public void setLastTestResultWithinLastDays(Integer lastTestResultWithinLastDays) {
        this.lastTestResultWithinLastDays = lastTestResultWithinLastDays;
    }

    public boolean isNoVisitSinceLastTestResult() {
        return noVisitSinceLastTestResult;
    }

    public void setNoVisitSinceLastTestResult(boolean noVisitSinceLastTestResult) {
        this.noVisitSinceLastTestResult = noVisitSinceLastTestResult;
    }

    public Concept getLastTestResult() {
        return lastTestResult;
    }

    public void setLastTestResult(Concept lastTestResult) {
        this.lastTestResult = lastTestResult;
    }

    public Concept getSecondToLastTestResult() {
        return secondToLastTestResult;
    }

    public void setSecondToLastTestResult(Concept secondToLastTestResult) {
        this.secondToLastTestResult = secondToLastTestResult;
    }

    public Integer getMaximumNumberOfTestResults() {
        return maximumNumberOfTestResults;
    }

    public void setMaximumNumberOfTestResults(Integer maximumNumberOfTestResults) {
        this.maximumNumberOfTestResults = maximumNumberOfTestResults;
    }
}