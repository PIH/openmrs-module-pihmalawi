/*
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
package org.openmrs.module.pihmalawi.reporting.reports;

import org.openmrs.module.pihmalawi.reporting.library.HivEncounterQueryLibrary;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Deprecated
public class ArtEncounterExport extends ApzuDataExportManager {

	@Autowired
	private HivEncounterQueryLibrary encounterQueries;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	public ArtEncounterExport() {}

	@Override
	public String getUuid() {
		return "cfae870d-5041-4127-b989-17ff9b407155";
	}

	@Override
	public String getName() {
		return "ART Encounter Export";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		SqlDataSetDefinition dsd = new SqlDataSetDefinition();
		dsd.addParameters(getParameters());
		StringBuilder q = new StringBuilder();
		q.append("select e.patient_id, e.encounter_id, t.name as encounter_type, ");
		q.append("		 e.encounter_datetime, l.name as location ");
		q.append("from	 encounter e ");
		q.append("inner join patient p on e.patient_id = p.patient_id ");
		q.append("inner join encounter_type t on e.encounter_type = t.encounter_type_id ");
		q.append("left outer join location l on e.location_id = l.location_id ");
		q.append("where t.name in ('ART_INITIAL','ART_FOLLOWUP') ");
		q.append("and e.voided = 0 and p.voided = 0 ");
		dsd.setSqlQuery(q.toString());
		rd.addDataSetDefinition("encounters", Mapped.mapStraightThrough(dsd));

		/*
		TODO: Replace the SQL with this when we can get it to perform reasonably
		EncounterDataSetDefinition dsd = new EncounterDataSetDefinition();
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("patient_id", SortDirection.ASC);
		dsd.addSortCriteria("encounter_id", SortDirection.ASC);
		rd.addDataSetDefinition("encounters", Mapped.mapStraightThrough(dsd));

		dsd.addRowFilter(Mapped.mapStraightThrough(encounterQueries.getArtEncounters()));
		addColumn(dsd, "patient_id", builtInEncounterData.getPatientId());
		addColumn(dsd, "encounter_id", builtInEncounterData.getEncounterId());
		addColumn(dsd, "encounter_type", builtInEncounterData.getEncounterTypeName());
		addColumn(dsd, "encounter_datetime", builtInEncounterData.getEncounterDatetime());
		addColumn(dsd, "location", builtInEncounterData.getLocationName());

		*/

		return rd;
	}

    @Override
    public String getExcelDesignUuid() {
        return "304f9500-e3b0-454e-80ca-a1be12112b47";
    }
}
