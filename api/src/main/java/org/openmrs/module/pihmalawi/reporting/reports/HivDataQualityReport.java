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

import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.User;
import org.openmrs.api.PatientSetService.TimeModifier;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.EncounterAfterTerminalStateCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.definition.cohort.definition.InvalidIdentifierCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.pihmalawi.reporting.library.DataFactory;
import org.openmrs.module.pihmalawi.reporting.library.HivCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.definition.BirthAndDeathCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.OptionalParameterCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.BeanPropertyComparator;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class HivDataQualityReport extends ApzuReportManager {

	@Autowired
	private HivMetadata hivMetadata;

	@Autowired
	private BaseCohortDefinitionLibrary baseCohorts;

	@Autowired
	private HivCohortDefinitionLibrary hivCohorts;

	@Autowired
	private DataFactory df;

	public HivDataQualityReport() {}

	@Override
	public String getUuid() {
		return "c460b091-1e77-11e4-8c21-0800200c9a66";
	}

	@Override
	public String getName() {
		return "HIV Data Quality";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		Parameter userParam = new Parameter("user", "User (optional)", User.class);
		userParam.addToWidgetConfiguration("maxYearsSinceLastEncounter", "1");
		userParam.setRequired(false);
		l.add(userParam);
		return l;
	}

	@Override
	public ReportDefinition constructReportDefinition() {

		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());

		// Base cohort is all patients.  If user filter is applied, include only those patients whose most recent form entry encounter was done by that user
		CohortDefinition userPats = baseCohorts.getPatientsForWhomUserWasMostRecentToEnterAForm();
		CohortDefinition baseCohort = new OptionalParameterCohortDefinition(userPats, "user");
		rd.setBaseCohortDefinition(Mapped.mapStraightThrough(baseCohort));

		// Underlying cohorts

		CohortDefinition everInHivProgramByEndDate = hivCohorts.getEverEnrolledInHivProgramByEndDate();
		CohortDefinition inHivProgramOnEndDate = hivCohorts.getActivelyEnrolledInHivProgramAtLocationOnEndDate();
		CohortDefinition inDiedStateAtEnd = hivCohorts.getInDiedStateAtLocationOnEndDate();
		CohortDefinition inOnArtStateAtEnd = hivCohorts.getPatientsInOnArvsStateOnEndDate();
		CohortDefinition inPreArtStateAtEnd = hivCohorts.getPatientsInPreArtStateOnEndDate();
		CohortDefinition inExposedChildStateAtEnd = hivCohorts.getPatientsInExposedChildStateOnEndDate();
		CohortDefinition hasArtNumber = hivCohorts.getPatientsWithAnArvNumber();
		CohortDefinition hasAnyPreArvNumber = hivCohorts.getPatientsWithAnyPreArvNumber();
		CohortDefinition hasAnArtEncounter = hivCohorts.getPatientsWithAnArtEncounterByEndDate();
		CohortDefinition hasAnArtInitialEncounter = hivCohorts.getPatientsWithAnArtInitialEncounterByEndDate();
		CohortDefinition hasAnArtFollowupEncounter = hivCohorts.getPatientsWithAnArtFollowupEncounterByEndDate();
		CohortDefinition hasAPreArtInitialEncounter = hivCohorts.getPatientsWithAPreArtInitialEncounterByEndDate();
		CohortDefinition hasAPreArtFollowupEncounter = hivCohorts.getPatientsWithAPreArtFollowupEncounterByEndDate();
		CohortDefinition over25MonthsOld = baseCohorts.getPatientsMoreThan25MonthsOldByEndDate();

		CodedObsCohortDefinition exitNotForDied = new CodedObsCohortDefinition();
		exitNotForDied.setTimeModifier(TimeModifier.LAST);
		exitNotForDied.setQuestion(hivMetadata.getReasonForExitingCareConcept());
		exitNotForDied.setOperator(SetComparator.NOT_IN);
		exitNotForDied.setValueList(Arrays.asList(hivMetadata.getDiedConcept()));

		CodedObsCohortDefinition everExitedCare = new CodedObsCohortDefinition();
		everExitedCare.setTimeModifier(TimeModifier.ANY);
		everExitedCare.setQuestion(hivMetadata.getReasonForExitingCareConcept());

		BirthAndDeathCohortDefinition everDied = new BirthAndDeathCohortDefinition();
		everDied.setDiedOnOrBefore(new Date(0));

		InStateCohortDefinition inAnyStateDate = new InStateCohortDefinition();
		inAnyStateDate.setStates(hivMetadata.getAllStates());
		inAnyStateDate.addParameter(new Parameter("onDate", "On Date", Date.class));
		CohortDefinition inAnyStateOnEndDate = df.convert(inAnyStateDate, ObjectUtil.toMap("onDate=endDate"));

		// Indicator DSD

		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("indicators", Mapped.mapStraightThrough(dsd));

		List<Location> locations = hivMetadata.getHivStaticLocations();
		Map<Location, String> locationCodes = hivMetadata.getLocationShortNames();

		// Gap in ARV number sequence
		{
			Integer idType = hivMetadata.getArvNumberIdentifierType().getPatientIdentifierTypeId();
			List<CohortDefinition> allLastInRangeArvCohorts = new ArrayList<CohortDefinition>();
			for (Location location : locations) {
				String locCode = locationCodes.get(location);
				StringBuilder sql = new StringBuilder();
				sql.append("select 	pi.patient_id from patient_identifier pi, patient p ");
				sql.append("where 	pi.patient_id = p.patient_id and pi.voided = 0 and p.voided = 0 ");
				sql.append("and 	identifier in (");
				sql.append("  			select 	concat('").append(locCode).append(" ', cast(c.start as char))");
				sql.append("  			from 	(");
				sql.append("					select a.id_number as start from (");
				sql.append("						select 	substring(identifier, ").append(locCode.length() + 2).append(") as id_number ");
				sql.append("    					from 	patient_identifier where identifier_type=" + idType);
				sql.append("						and 	identifier like '").append(locCode).append(" %' and voided = 0");
				sql.append("					) as a");
				sql.append("  					left outer join (");
				sql.append("    					select 		substring(identifier, ").append(locCode.length() + 2).append(") as id_number ");
				sql.append("    					from 		patient_identifier ");
				sql.append("						where 		identifier_type=" + idType);
				sql.append("						and 		identifier like '").append(locCode).append(" %' ");
				sql.append(" 						and 		voided = 0");
				sql.append("					) as b ");
				sql.append("					on a.id_number + 1 = b.id_number");
				sql.append("  					where b.id_number is null");
				sql.append("			) as c ");
				sql.append("		)");
				SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
				addIndicator(dsd, "gap" + locCode.toLowerCase(), "Last In-range ARV number before gap in sequence", cd);
				allLastInRangeArvCohorts.add(cd);
			}
			addIndicator(dsd, "gapall", "Last In-range ARV number before gap in sequence all", df.getPatientsInAny(allLastInRangeArvCohorts.toArray(new CohortDefinition[]{})));
		}

		// Gap in HCC number sequence
		{
			Integer idType = hivMetadata.getHccNumberIdentifierType().getPatientIdentifierTypeId();
			List<CohortDefinition> allLastInRangeHccCohorts = new ArrayList<CohortDefinition>();
			for (Location location : locations) {
				String locCode = locationCodes.get(location);
				StringBuilder sql = new StringBuilder();
				sql.append("select 	pi.patient_id from patient_identifier pi, patient p ");
				sql.append("where 	pi.patient_id = p.patient_id and pi.voided = 0 and p.voided = 0 ");
				sql.append("and 	identifier in (");
				sql.append("  			select 	concat('").append(locCode).append(" ', cast(c.start as char), ' HCC')");
				sql.append("  			from 	(");
				sql.append("					select a.id_number as start from (");
				sql.append("						select 	replace(substring(identifier, ").append(locCode.length() + 2).append("), ' HCC', '') as id_number ");
				sql.append("    					from 	patient_identifier where identifier_type=" + idType);
				sql.append("						and 	identifier like '").append(locCode).append(" %' and voided = 0");
				sql.append("					) as a");
				sql.append("  					left outer join (");
				sql.append("    					select 		replace(substring(identifier, ").append(locCode.length() + 2).append("), ' HCC', '') as id_number ");
				sql.append("    					from 		patient_identifier ");
				sql.append("						where 		identifier_type=" + idType);
				sql.append("						and 		identifier like '").append(locCode).append(" %' ");
				sql.append(" 						and 		voided = 0");
				sql.append("					) as b ");
				sql.append("					on a.id_number + 1 = b.id_number");
				sql.append("  					where b.id_number is null");
				sql.append("			) as c ");
				sql.append("		)");
				SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
				addIndicator(dsd, "hccgap" + locCode.toLowerCase(), "Last In-range HCC number before gap in sequence", cd);
				allLastInRangeHccCohorts.add(cd);
			}
			addIndicator(dsd, "hccgapall", "Last In-range HCC number before gap in sequence all", df.getPatientsInAny(allLastInRangeHccCohorts.toArray(new CohortDefinition[]{})));
		}

		// Patients who have more than one ARV number
		{
			Integer idType = hivMetadata.getArvNumberIdentifierType().getPatientIdentifierTypeId();
			List<CohortDefinition> allWithMoreThanOneArvNumber = new ArrayList<CohortDefinition>();
			for (Location location : locations) {
				String locCode = locationCodes.get(location);
				StringBuilder sql = new StringBuilder();
				sql.append("select		p.patient_id ");
				sql.append("from 		patient_identifier i, patient p ");
				sql.append("where 		i.patient_id = p.patient_id ");
				sql.append("and 		i.identifier_type = ").append(idType).append(" ");
				sql.append("and 		p.voided = 0 and i.voided = 0 ");
				sql.append("and 		i.identifier like '" + locCode + " %' ");
				sql.append("group by 	p.patient_id ");
				sql.append("having		count(i.patient_id) > 1");
				SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
				addIndicator(dsd, "dup" + locCode.toLowerCase(), "Multiple ARV numbers " + locCode, cd);
				allWithMoreThanOneArvNumber.add(cd);
			}
			addIndicator(dsd, "dupall", "Multiple ARV numbers all", df.getPatientsInAny(allWithMoreThanOneArvNumber.toArray(new CohortDefinition[]{})));
		}

		// Wrong ARV identifier format
		{
			InvalidIdentifierCohortDefinition cd = new InvalidIdentifierCohortDefinition();
			cd.setIdentifierType(hivMetadata.getArvNumberIdentifierType());
			cd.setIdentifierFormat("^<location> [1-9][0-9]?[0-9]?[0-9]?$");
			addIndicator(dsd, "format", "Wrong identifier format", cd);
		}

		// Wrong z_deprecated PART Number identifier format // TODO: Do we still need this for a deprecated identifier type?
		{
			InvalidIdentifierCohortDefinition cd = new InvalidIdentifierCohortDefinition();
			cd.setIdentifierType(hivMetadata.getOldPartNumberIdentifierType());
			cd.setIdentifierFormat("^P-<location>-[0-9][0-9][0-9][0-9]$");
			addIndicator(dsd, "formatpart", "Wrong Pre-ART identifier format", cd);
		}

		// Wrong HCC Number identifier format
		{
			InvalidIdentifierCohortDefinition cd = new InvalidIdentifierCohortDefinition();
			cd.setIdentifierType(hivMetadata.getHccNumberIdentifierType());
			cd.setIdentifierFormat("^<location> [1-9][0-9]?[0-9]?[0-9]? HCC$");
			addIndicator(dsd, "formathcc", "Wrong HCC identifier format", cd);
		}

		// Empty or Invalid HIV Program Locations
		{
			StringBuilder sql = new StringBuilder();
			sql.append("select 	pp.patient_id ");
			sql.append("from 	patient_program pp, patient p ");
			sql.append("where 	pp.patient_id = p.patient_id and p.voided=0 and pp.voided=0 ");
			sql.append("and 	pp.program_id=" + hivMetadata.getHivProgram().getProgramId()).append(" ");
			sql.append("and 	(pp.location_id is null or pp.location_id not in (");
			for (int i = 0; i < locations.size(); i++) {
				sql.append(i == 0 ? "" : ", ");
				sql.append(locations.get(i).getLocationId());
			}
			sql.append("))");
			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			addIndicator(dsd, "unknwnloc", "Unknown location", cd);
		}

		// Programs that are not completed, but which have a terminal state
		{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT 	pp.patient_id ");
			sql.append("FROM 	( ");
			sql.append("			SELECT 	pp.patient_id a, pp.patient_program_id b, pws.program_workflow_state_id c, group_concat(ps.patient_state_id order by ps.patient_state_id desc) d ");
			sql.append("			FROM 	patient p, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps ");
			sql.append("			WHERE 	p.patient_id = pp.patient_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id ");
			sql.append("			AND 	pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id ");
			sql.append("			AND 	pws.program_workflow_id = ").append(hivMetadata.getHivProgram().getProgramId()).append(" ");
			sql.append("			AND 	pw.retired = 0 AND p.voided = 0 AND pp.voided = 0 AND ps.voided = 0 ");
			sql.append("			GROUP BY pp.patient_id, pp.patient_program_id ");
			sql.append(" 		) most_recent_state, ");
			sql.append("		patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps ");
			sql.append("WHERE 	most_recent_state.d=ps.patient_state_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id ");
			sql.append("AND 	pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id ");

			sql.append("AND 	pws.program_workflow_state_id in (").append(getTerminalHivProgramStateIds()).append(") and pp.date_completed is null ");

			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			addIndicator(dsd, "prgnotcompleted", "Terminal state, program not completed", cd);
		}

		// Programs that are completed, but without a terminal state
		{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT pp.patient_id ");
			sql.append("FROM ( ");
			sql.append("  SELECT pp.patient_id a, pp.patient_program_id b, pws.program_workflow_state_id c, group_concat(ps.patient_state_id order by ps.patient_state_id desc) d ");
			sql.append("  FROM patient p, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps ");
			sql.append("  WHERE p.patient_id = pp.patient_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id ");
			sql.append("    AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id ");
			sql.append("    AND pws.program_workflow_id = ").append(hivMetadata.getHivProgram().getProgramId()).append(" ");
			sql.append("    AND pw.retired = 0 AND p.voided = 0 AND pp.voided = 0 AND ps.voided = 0 ");
			sql.append("GROUP BY pp.patient_id, pp.patient_program_id) most_recent_state, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps ");
			sql.append("WHERE most_recent_state.d=ps.patient_state_id AND pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id ");
			sql.append("  AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id ");

			sql.append("  AND pws.program_workflow_state_id not in (").append(getTerminalHivProgramStateIds()).append(") and pp.date_completed is not null ");
			;

			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			addIndicator(dsd, "prgcompleted", "Non terminal state, program completed", cd);
		}

		// Patients who have two death states
		{
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT 		pp.patient_id ");
			sql.append("FROM 		patient p, patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps ");
			sql.append("WHERE 		p.patient_id = pp.patient_id and pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id ");
			sql.append("AND 		pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id ");
			sql.append("AND 		pws.program_workflow_id = ").append(hivMetadata.getTreatmentStatusWorkfow().getId()).append(" ");
			sql.append("AND 		pw.retired = 0 AND pp.voided = 0 and p.voided = 0 AND pws.retired = 0 AND ps.voided = 0 ");
			sql.append("AND 		pws.program_workflow_state_id = ").append(hivMetadata.getDiedState().getId()).append(" ");
			sql.append("GROUP BY 	pp.patient_id HAVING COUNT(*) > 1");
			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			addIndicator(dsd, "died2x", "Multiple death", cd);
		}

		// Patients ever in the HIV program who exited care for a reason other than death
		addIndicator(dsd, "exit", "Wrong exit from care in HIV", df.getPatientsInAll(exitNotForDied, everInHivProgramByEndDate));

		// Patients ever in the HIV program who died but did not have an exit from care obs
		addIndicator(dsd, "noexit", "Deceased without exit from care in HIV", df.createPatientComposition(everInHivProgramByEndDate, "AND", everDied, "AND NOT", everExitedCare));

		// Patients ever in the HIV program who are in the Died State but did not have an exit from care obs
		addIndicator(dsd, "noexit2", "Died without exit from care", df.createPatientComposition(everInHivProgramByEndDate, "AND", inDiedStateAtEnd, "AND NOT", everExitedCare));

		// Patients marked as died but who are not in Died state
		addIndicator(dsd, "nostate", "Deceased but not in state died in HIV", df.createPatientComposition(everInHivProgramByEndDate, "AND", everDied, "AND NOT", inDiedStateAtEnd));

		// Patients who have an ARV number at a location but who have not been in state On ARVs at this location
		{
			StringBuilder sql = new StringBuilder();
			sql.append("select 		pi.patient_id ");
			sql.append("from 		patient_identifier pi, person p, patient pa ");
			sql.append("where 		pi.patient_id = p.person_id and pa.patient_id = pi.patient_id and p.voided=0 and pa.voided=0 and pi.voided=0 ");
			sql.append("and 		pi.identifier_type=" + hivMetadata.getArvNumberIdentifierType().getPatientIdentifierTypeId()).append(" ");
			sql.append("and 		pi.patient_id not in ( ");
			sql.append("  				select 	pp.patient_id ");
			sql.append("     			from 	patient_program pp, patient_state ps ");
			sql.append("				where 	pi.patient_id = pp.patient_id and pp.location_id = pi.location_id and ps.patient_program_id = pp.patient_program_id ");
			sql.append("				and 	ps.voided=0 and pp.voided=0 ");
			sql.append("				and 	pp.program_id=" + hivMetadata.getHivProgram().getId()).append(" ");
			sql.append("				and 	ps.state=").append(hivMetadata.getOnArvsState().getId());
			sql.append("			)");
			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			System.out.println("Adding SQL: " + cd.getQuery());
			addIndicator(dsd, "noartbutnumber", "ARV Number for location without being in state On ARVs at this location", cd);
		}

		// Patients who have an HCC number at a location but who have not been in Pre-ART or Exposed Child States at this location
		{
			StringBuilder sql = new StringBuilder();
			sql.append("select 		pi.patient_id ");
			sql.append("from 		patient_identifier pi, person p, patient pa ");
			sql.append("where 		pi.patient_id = p.person_id and pa.patient_id = pi.patient_id and p.voided=0 and pa.voided=0 and pi.voided=0 ");
			sql.append("and 		pi.identifier_type=" + hivMetadata.getHccNumberIdentifierType().getId()).append(" ");
			sql.append("and 		pi.patient_id not in ( ");
			sql.append("  				select 	pp.patient_id ");
			sql.append("     			from 	patient_program pp, patient_state ps ");
			sql.append("				where 	pi.patient_id = pp.patient_id and pp.location_id = pi.location_id and ps.patient_program_id = pp.patient_program_id ");
			sql.append("				and 	ps.voided=0 and pp.voided=0 ");
			sql.append("				and 	pp.program_id=" + hivMetadata.getHivProgram().getId()).append(" ");
			sql.append("				and 	ps.state in (").append(hivMetadata.getPreArtState().getId()).append(",").append(hivMetadata.getExposedChildState().getId()).append(")");
			sql.append("			)");
			SqlCohortDefinition cd = new SqlCohortDefinition(sql.toString());
			addIndicator(dsd, "nohccbutnumber", "HCC Number for location without being in state Pre-ART or Exposed Child at this location", cd);
		}

		// Patients who are in state On ART but who have no ARV number
		addIndicator(dsd, "nonoart", "On ART without number", df.createPatientComposition(inOnArtStateAtEnd, "AND NOT", hasArtNumber));

		// Patients who have had an ART encounter but do not have an ART number
		addIndicator(dsd, "artnono", "ART encounter without number", df.createPatientComposition(hasAnArtEncounter, "AND NOT", hasArtNumber));

		// Patients who are in pre-ART state without HCC or PART numbers
		addIndicator(dsd, "nonopart", "Following without number (excluding Old Pre-ART numbers)", df.createPatientComposition(inPreArtStateAtEnd, "AND NOT", hasAnyPreArvNumber));

		// Patients who are in pre-ART state but have an ARV number
		addIndicator(dsd, "noart", "Following with art number", df.getPatientsInAll(inPreArtStateAtEnd, hasArtNumber));

		// Patients who are in the HIV program but are not in a particular state
		addIndicator(dsd, "state", "Not in relevant HIV state", df.createPatientComposition(inHivProgramOnEndDate, "AND NOT", inAnyStateOnEndDate));

		// Patients who are in state On ART but have never had an ART encounter
		addIndicator(dsd, "noartenc", "On ART without encounter", df.createPatientComposition(inOnArtStateAtEnd, "AND NOT", hasAnArtEncounter));

		// Patients who have an ART followup encounter but no ART initial encounter
		addIndicator(dsd, "noinitial", "Followup without Initial", df.createPatientComposition(hasAnArtFollowupEncounter, "AND NOT", hasAnArtInitialEncounter));

		// Patients who have a Pre-ART followup encounter but no Pre-ART initial encounter
		addIndicator(dsd, "partnoinitial", "Pre-ART Followup without Initial", df.createPatientComposition(hasAPreArtFollowupEncounter, "AND NOT", hasAPreArtInitialEncounter));

		// Patients who are in the Exposed Child state but above 25 months old
		addIndicator(dsd, "eidage", "EID Above 25 months", df.getPatientsInAll(inHivProgramOnEndDate, inExposedChildStateAtEnd, over25MonthsOld));

		// Patients at each site who have had an HIV encounter after a terminal state
		for (Location l : locations) {
			String siteCode = locationCodes.get(l);
			List<Location> clinicLocations = hivMetadata.getAllLocations(l);
			EncounterAfterTerminalStateCohortDefinition cd = new EncounterAfterTerminalStateCohortDefinition();
			cd.addParameter(new Parameter("endDate", "endDate", Date.class));
			cd.setProgram(hivMetadata.getHivProgram());
			cd.setEncounterTypes(hivMetadata.getHivAndExposedChildEncounterTypes());
			cd.setLocations(clinicLocations);
			addIndicator(dsd, "term" + siteCode.toLowerCase(), siteCode + ": Hiv encounter after terminal state", cd);
		}

		// Sort columns alphabetically by name
		Collections.sort(dsd.getColumns(), new BeanPropertyComparator("name"));

		return rd;
	}

	protected String getTerminalHivProgramStateIds() {
		List<Integer> l = new ArrayList<Integer>();
		for (ProgramWorkflowState s : hivMetadata.getTerminalStates()) {
			l.add(s.getProgramWorkflowStateId());
		}
		return OpenmrsUtil.join(l, ",");
	}

	protected void addIndicator(CohortIndicatorDataSetDefinition dsd, String key, String label, CohortDefinition cohortDefinition) {
		CohortIndicator ci = new CohortIndicator();
		ci.setType(CohortIndicator.IndicatorType.COUNT);
		ci.setCohortDefinition(Mapped.mapStraightThrough(cohortDefinition));
		dsd.addColumn(key, label, Mapped.mapStraightThrough(ci), "");
	}

	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		return l;
	}

	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
}
