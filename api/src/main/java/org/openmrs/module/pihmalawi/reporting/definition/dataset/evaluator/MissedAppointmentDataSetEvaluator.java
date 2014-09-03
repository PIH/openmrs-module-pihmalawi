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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientSetService;
import org.openmrs.module.pihmalawi.metadata.ChronicCareMetadata;
import org.openmrs.module.pihmalawi.metadata.group.ChronicCareTreatmentGroup;
import org.openmrs.module.pihmalawi.metadata.group.TreatmentGroup;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.MissedAppointmentDataSetDefinition.Mode;
import org.openmrs.module.pihmalawi.reporting.library.BaseEncounterDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.BasePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.ChronicCarePatientDataLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SortCriteria.SortDirection;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.library.BuiltInEncounterDataLibrary;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataSetDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.StaticValuePatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Handler(supports={MissedAppointmentDataSetDefinition.class})
public class MissedAppointmentDataSetEvaluator implements DataSetEvaluator {

	@Autowired
	private DataFactory df;

	@Autowired
	private ChronicCareMetadata metadata;

	@Autowired
	private ChronicCarePatientDataLibrary ccPatientData;

	@Autowired
	private CohortDefinitionService cohortDefinitionService;

	@Autowired
	private DataSetDefinitionService dataSetDefinitionService;

	@Autowired
	private BuiltInPatientDataLibrary builtInPatientData ;

	@Autowired
	private BasePatientDataLibrary basePatientData ;

	@Autowired
	private BuiltInEncounterDataLibrary builtInEncounterData;

	@Autowired
	private BaseEncounterDataLibrary baseEncounterData;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		MissedAppointmentDataSetDefinition dsd = (MissedAppointmentDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, context);

		TreatmentGroup treatmentGroup = dsd.getTreatmentGroup();
		Map<Location, String> locationCodes = metadata.getLocationShortNames();

		for (Location location : dsd.getLocations()) {
			String locationCode = locationCodes.get(location);

			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("location", "Location", String.class), location.getName());

			// All modes except for patients over 3 weeks late include patients with no appointment
			if (dsd.getMode() != Mode.MORE_THAN_3_WEEKS_LATE) {
				for (int i = 0; i < 3; i++) {
					if (dsd.getMode() == Mode.OVERVIEW || i == 0) {  // Only include previous weeks for overview report
						Date endDate = DateUtil.adjustDate(dsd.getEndDate(), -1 * i, DurationUnit.WEEKS);

						EvaluationContext childContext = context.shallowCopy();
						childContext.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), endDate);
						childContext.addParameterValue(ReportingConstants.LOCATION_PARAMETER.getName(), location);

						CodedObsCohortDefinition noAppt = new CodedObsCohortDefinition();
						noAppt.setTimeModifier(PatientSetService.TimeModifier.NO);
						noAppt.setQuestion(metadata.getAppointmentDateConcept());
						noAppt.setEncounterTypeList(treatmentGroup.getEncounterTypes());
						noAppt.setOnOrBefore(endDate);

						CohortDefinition indicatorCohort = df.getPatientsInAll(dsd.getBaseCohort(), noAppt);

						String key = "noapp" + (i > 0 ? i : "");
						String label = "No appointment" + (i == 1 ? " 1 week ago" : (i > 1 ? " " + i + " weeks ago" : ""));

						if (dsd.getMode() == Mode.OVERVIEW) { // Overview mode only shows the indicator number
							Cohort base = cohortDefinitionService.evaluate(dsd.getBaseCohort(), childContext);
							row.addColumnValue(new DataSetColumn("base" + (i > 0 ? i : ""), "base" + (i > 0 ? i : ""), Cohort.class), base);
							Cohort c = cohortDefinitionService.evaluate(indicatorCohort, childContext);
							row.addColumnValue(new DataSetColumn(key, label, Cohort.class), c);
						}
						else {
							DataSet patientDataSet = getPatientDataSet(indicatorCohort, treatmentGroup, childContext);
							row.addColumnValue(new DataSetColumn(key + locationCode, label + " (" + location.getName() + ")", DataSet.class), patientDataSet);
						}
					}
				}
			}

			List<Integer[]> weekRangesToInclude = new ArrayList<Integer[]>();
			if (dsd.getMode() == Mode.OVERVIEW || dsd.getMode() == Mode.BETWEEN_2_AND_3_WEEKS_LATE) {
				weekRangesToInclude.add(new Integer[]{2, 3});
			}
			if (dsd.getMode() == Mode.MORE_THAN_A_WEEK_LATE) {
				weekRangesToInclude.add(new Integer[]{1, null});
			}
			if (dsd.getMode() == Mode.OVERVIEW || dsd.getMode() == Mode.MORE_THAN_3_WEEKS_LATE) {
				weekRangesToInclude.add(new Integer[]{3, 8});
				weekRangesToInclude.add(new Integer[]{8, 12});
				weekRangesToInclude.add(new Integer[]{12, null});
			}

			// Missed appointment.  Include missed by 2-3 weeks, 3-8 weeks, 8-12 weeks, and 12 weeks+.  Include data as of endDate, 1 week prior, 2 weeks prior
			for (Integer[] weekRange : weekRangesToInclude) {
				for (int i = 0; i < 3; i++) {
					if (dsd.getMode() == Mode.OVERVIEW || i == 0) {  // Only include previous weeks for overview report
						Date endDate = DateUtil.adjustDate(dsd.getEndDate(), -1 * i, DurationUnit.WEEKS);

						EvaluationContext childContext = context.shallowCopy();
						childContext.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), endDate);
						childContext.addParameterValue(ReportingConstants.LOCATION_PARAMETER.getName(), location);

						Concept apptDate = metadata.getAppointmentDateConcept();
						String olderThan = weekRange[0] + "w";
						String onOrPriorTo = weekRange[1] == null ? null : (weekRange[1] + "w");
						CohortDefinition lastApptInRange = df.getPatientsWhoseMostRecentObsDateIsBetweenValuesByEndDate(apptDate, treatmentGroup.getEncounterTypes(), olderThan, onOrPriorTo);

						CohortDefinition indicatorCohort = df.getPatientsInAll(dsd.getBaseCohort(), lastApptInRange);

						String key = olderThan + "msd" + (i > 0 ? i : "");
						String label = "Missed appointment >" + weekRange[0] + (weekRange[1] == null ? "" : " <=" + weekRange[1]) + " weeks " + (i == 1 ? "1 week ago" : (i > 1 ? i + " weeks ago" : ""));

						if (dsd.getMode() == Mode.OVERVIEW) { // Overview mode only shows the indicator number
							Cohort c = cohortDefinitionService.evaluate(indicatorCohort, childContext);
							row.addColumnValue(new DataSetColumn(key, label, Cohort.class), c);
						}
						else {
							DataSet patientDataSet = getPatientDataSet(indicatorCohort, treatmentGroup, childContext);
							row.addColumnValue(new DataSetColumn(key + locationCode, label + " (" + location.getName() + ")", DataSet.class), patientDataSet);
						}
					}
				}
			}

			dataSet.addRow(row);
		}

		return dataSet;
	}

	/**
	 * @return the base patient data set definition to return for all data sets
	 */
	protected DataSet getPatientDataSet(CohortDefinition filter, TreatmentGroup treatmentGroup, EvaluationContext context) throws EvaluationException {
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.addParameter(ReportingConstants.END_DATE_PARAMETER);
		dsd.addParameter(ReportingConstants.LOCATION_PARAMETER);

		dsd.addRowFilter(Mapped.mapStraightThrough(filter));

		dsd.addSortCriteria(treatmentGroup.getIdentifierTypeShortName(), SortDirection.ASC);

		addColumn(dsd, treatmentGroup.getIdentifierTypeShortName(), treatmentGroup.getPreferredIdentifierDefinition());
		addColumn(dsd, "All " + treatmentGroup.getIdentifierTypeShortName() + "s", treatmentGroup.getAllIdentifiersDefinition());

		if (treatmentGroup instanceof ChronicCareTreatmentGroup) {
			addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
			addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
			addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
			addColumn(dsd, "Village", basePatientData.getVillage());
			addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
			addColumn(dsd, "District", basePatientData.getDistrict());
			addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());
			addColumn(dsd, "Diagnosis", ccPatientData.getAllChronicCareDiagnosesByEndDate());
			addColumn(dsd, "High risk", ccPatientData.getIsPatientHighRisk());
			addColumn(dsd, "Time to clinic in hours", ccPatientData.getTimeSpentTravelingToClinicInHours());
			addColumn(dsd, "Time to clinic in minutes", ccPatientData.getTimeSpentTravelingToClinicInMinutes());
			addColumn(dsd, "Last visit date", ccPatientData.getMostRecentEncounterDateByEndDate());
			addColumn(dsd, "Last recorded 'next appt date'", ccPatientData.getMostRecentAppointmentDateByEndDate());
		}
		else {
			addColumn(dsd, "Given name", builtInPatientData.getPreferredGivenName());
			addColumn(dsd, "Last name", builtInPatientData.getPreferredFamilyName());
			addColumn(dsd, "Birthdate", basePatientData.getBirthdate());
			addColumn(dsd, "Current Age (yr)", basePatientData.getAgeAtEndInYears());
			addColumn(dsd, "Current Age (mth)", basePatientData.getAgeAtEndInMonths());
			addColumn(dsd, "M/F", builtInPatientData.getGender());
			addColumn(dsd, "Village", basePatientData.getVillage());
			addColumn(dsd, "TA", basePatientData.getTraditionalAuthority());
			addColumn(dsd, "District", basePatientData.getDistrict());
			addColumn(dsd, "VHW", basePatientData.getChwOrGuardian());

			addColumn(dsd, "Outcome", treatmentGroup.getCurrentStateAtLocationDefinition(df.getStateNameConverter()));
			addColumn(dsd, "Outcome change date", treatmentGroup.getCurrentStateAtLocationDefinition(df.getStateStartDateConverter()));
			addColumn(dsd, "Outcome location", treatmentGroup.getCurrentStateAtLocationDefinition(df.getStateLocationConverter()));
			addColumn(dsd, "Last Outcome in DB (not filtered)", treatmentGroup.getCurrentStateDefinition(df.getStateNameConverter()));
			addColumn(dsd, "Last Outcome change date", treatmentGroup.getCurrentStateDefinition(df.getStateStartDateConverter()));
			addColumn(dsd, "Last Outcome change loc", treatmentGroup.getCurrentStateDefinition(df.getStateLocationConverter()));

			EncounterDataSetDefinition encDsd = new EncounterDataSetDefinition();
			encDsd.addParameters(dsd.getParameters());
			encDsd.addRowFilter(Mapped.mapStraightThrough(treatmentGroup.getAllEncountersDefinition()));
			addColumn(encDsd, "encounterDate", builtInEncounterData.getEncounterDatetime());
			addColumn(encDsd, "locationName", builtInEncounterData.getLocationName());
			addColumn(encDsd, "appointmentDate", baseEncounterData.getNextAppointmentDateObsValue());
			addColumn(encDsd, "encounterTypeName", builtInEncounterData.getEncounterTypeName());
			encDsd.addSortCriteria("encounterDate", SortDirection.ASC);

			PatientDataSetDataDefinition followups = new PatientDataSetDataDefinition(encDsd);
			addColumn(dsd, "Last Visit date in " + treatmentGroup.getName() + " (not filtered)", df.convert(followups, df.getLastDataSetItemConverter("encounterDate", "(no encounter found)")));
			addColumn(dsd, "Last Visit loc", df.convert(followups, df.getLastDataSetItemConverter("locationName", "")));
			addColumn(dsd, "Last Visit appt date", df.convert(followups, df.getLastDataSetItemConverter("appointmentDate", "")));
			addColumn(dsd, "Last Visit type", df.convert(followups, df.getLastDataSetItemConverter("encounterTypeName", "")));
		}

		addColumn(dsd, "Confirmed Missed Appt", new StaticValuePatientDataDefinition(""));
		addColumn(dsd, "Unable to Verify", new StaticValuePatientDataDefinition(""));
		addColumn(dsd, "Missed Data Entry", new StaticValuePatientDataDefinition(""));

		return dataSetDefinitionService.evaluate(dsd, context);
	}

	protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToEncounterDataDefinition(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		dsd.addColumn(columnName, edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","));
	}
}
