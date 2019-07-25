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
package org.openmrs.module.pihmalawi.reporting.definition.data.evaluator;

import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.ObsValueBooleanYesNoConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.PregnantLactatingConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.TbStatusConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.converter.WhoStageConverter;
import org.openmrs.module.pihmalawi.reporting.definition.data.definition.ReasonForStartingArvsPatientDataDefinition;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.encounter.definition.ObsOnSameDateEncounterDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.encounter.definition.BasicEncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evaluates a ReasonForStartingArvsPatientDataDefinition to produce a PatientData
 */
@Handler(supports = ReasonForStartingArvsPatientDataDefinition.class, order = 50)
public class ReasonForStartingArvsPatientDataDefinitionEvaluator implements PatientDataEvaluator {

	@Autowired
	private DataFactory df;

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private DataSetDefinitionService dataSetDefinitionService;
	
	@Override
	public EvaluatedPatientData evaluate(PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

		ReasonForStartingArvsPatientDataDefinition d = (ReasonForStartingArvsPatientDataDefinition)definition;
		EvaluatedPatientData pd = new EvaluatedPatientData(definition, context);

		EncounterDataSetDefinition firstArtInitial = new EncounterDataSetDefinition();

		BasicEncounterQuery q = new BasicEncounterQuery();
		q.setWhich(TimeQualifier.FIRST);
		q.setWhichNumber(1);
		q.addEncounterType(hivMetadata.getArtInitialEncounterType());
		q.setOnOrBefore(d.getEndDate());
		firstArtInitial.addRowFilter(Mapped.noMappings(q));

		firstArtInitial.addColumn("PID", new PatientIdDataDefinition(), "");
		addColumn(firstArtInitial, "CD4", hivMetadata.getCd4CountConcept(), df.getObsValueNumericConverter());
		addColumn(firstArtInitial, "KS", hivMetadata.getKsSideEffectsWorseningOnArvsConcept(), new ObsValueBooleanYesNoConverter());
		addColumn(firstArtInitial, "TB", hivMetadata.getTbTreatmentStatusConcept(), new TbStatusConverter());
		addColumn(firstArtInitial, "STAGE", hivMetadata.getWhoStageConcept(), new WhoStageConverter());
		addColumn(firstArtInitial, "TLC", hivMetadata.getCd4PercentConcept(), df.getObjectFormatter());
		addColumn(firstArtInitial, "PSHD", hivMetadata.getPresumedSevereHivCriteriaPresentConcept(), df.getObjectFormatter());
		addColumn(firstArtInitial, "CONDITIONS", hivMetadata.getWhoClinicalConditionsConcept(), df.getObsValueTextConverter());
		addColumn(firstArtInitial, "PREG", hivMetadata.getPregnantOrLactatingConcept(), new PregnantLactatingConverter());

		SimpleDataSet ds = (SimpleDataSet) dataSetDefinitionService.evaluate(firstArtInitial, context);
		for (DataSetRow row : ds.getRows()) {
			Integer pId = (Integer)row.getColumnValue("PID");
			Map<String, Object> reasons = new LinkedHashMap<String, Object>();
			for (int i=1; i<firstArtInitial.getColumnDefinitions().size(); i++) {
				RowPerObjectColumnDefinition columnDef = firstArtInitial.getColumnDefinitions().get(i);
				String columnName = columnDef.getName();
				reasons.put(columnName, row.getColumnValue(columnName));
			}
			pd.addData(pId, reasons);
		}

		return pd;
	}

	protected void addColumn(EncounterDataSetDefinition edd, String name, Concept concept, DataConverter... converters) {
		ObsOnSameDateEncounterDataDefinition dd = new ObsOnSameDateEncounterDataDefinition();
		dd.setSingleObs(true);
		dd.setQuestion(concept);
		edd.addColumn(name, dd, "", converters);
	}
}
