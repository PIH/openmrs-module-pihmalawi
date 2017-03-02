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

import org.openmrs.Location;
import org.openmrs.module.pihmalawi.common.TraceConstants;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;

public class HighViralLoadNeedingTraceCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private TraceConstants.TraceType traceType;

    @ConfigurationProperty
    private Location location;

	public HighViralLoadNeedingTraceCohortDefinition() {
		super();
	}

    public TraceConstants.TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceConstants.TraceType traceType) {
        this.traceType = traceType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}