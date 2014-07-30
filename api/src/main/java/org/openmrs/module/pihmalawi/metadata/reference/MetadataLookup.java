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
package org.openmrs.module.pihmalawi.metadata.reference;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.OrderType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for looking up a piece of metadata, using uuid, name, id and mappings to try to uniquely locate it
 */
public class MetadataLookup {

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * @return the Program that matches the passed uuid, concept name, name, or primary key id
	 */
	public static Program getProgram(String lookup) {
		Program program = Context.getProgramWorkflowService().getProgramByUuid(lookup);
		if (program == null) {
			program = Context.getProgramWorkflowService().getProgramByName(lookup);
		}
		if (program == null) {
			for (Program p : Context.getProgramWorkflowService().getAllPrograms()) {
				if (p.getName().equalsIgnoreCase(lookup)) {
					program = p;
				}
			}
		}
		if (program == null) {
			try {
				program = Context.getProgramWorkflowService().getProgram(Integer.parseInt(lookup));
			}
			catch(Exception e) { }
		}
		if (program == null) {
			throw new IllegalArgumentException("Unable to find program using key: " + lookup);
		}

		return program;
	}

	/**
	 * @return the ProgramWorkflow matching the given programLookup and workflowLookup
	 */
	public static ProgramWorkflow getProgramWorkflow(String programLookup, String workflowLookup) {
		Program p = getProgram(programLookup);
		ProgramWorkflow wf = p.getWorkflowByName(workflowLookup);

		if (wf == null) {
			for (ProgramWorkflow programWorkflow : p.getAllWorkflows()) {
				if (workflowLookup.equalsIgnoreCase(programWorkflow.getConcept().getName().toString())) {
					wf = programWorkflow;
				}
				else if (workflowLookup.equalsIgnoreCase(programWorkflow.getUuid())) {
					wf = programWorkflow;
				}
				else if (workflowLookup.equalsIgnoreCase(programWorkflow.getId().toString())) {
					wf = programWorkflow;
				}
			}
		}
		if (wf == null) {
			throw new IllegalArgumentException("Unable to find workflow using " + programLookup + " - " + workflowLookup);
		}
		return wf;
	}

	/**
	 * @return the ProgramWorkflowState matching the given programLookup and workflowLookup and stateLookup
	 */
	public static ProgramWorkflowState getProgramWorkflowState(String programLookup, String workflowLookup, String stateLookup) {
		ProgramWorkflow wf = getProgramWorkflow(programLookup, workflowLookup);
		ProgramWorkflowState s = wf.getStateByName(stateLookup);
		if (s == null) {
			for (ProgramWorkflowState state : wf.getStates()) {
				if (stateLookup.equalsIgnoreCase(state.getConcept().getName().toString())) {
					s = state;
				}
				else if (stateLookup.equalsIgnoreCase(state.getConcept().getUuid())) {
					s = state;
				}
				else if (stateLookup.equalsIgnoreCase(state.getUuid())) {
					s = state;
				}
				else if (stateLookup.equalsIgnoreCase(state.getId().toString())) {
					s = state;
				}
				else if (stateLookup.equalsIgnoreCase(state.getConcept().getId().toString())) {
					s = state;
				}
			}
		}
		if (s == null) {
			throw new IllegalArgumentException("Unable to find state using " + programLookup + " - " + workflowLookup + " - " + stateLookup);
		}
		return s;
	}

	/**
	 * @return the PatientIdentifier that matches the passed uuid, name, or primary key id
	 */
	public static PatientIdentifierType getPatientIdentifierType(String lookup) {
		PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByUuid(lookup);
		if (pit == null) {
			pit = Context.getPatientService().getPatientIdentifierTypeByName(lookup);
		}
		if (pit == null) {
			try{
				pit = Context.getPatientService().getPatientIdentifierType(Integer.parseInt(lookup));
			}
			catch(Exception e) { }
		}
		if (pit == null) {
			throw new RuntimeException("Unable to find Patient Identifier using key: " + lookup);
		}
		return pit;
	}

	/**
	 * @return the Concept that matches the passed uuid, name, source:code mapping, or primary key id
	 */
	public static Concept getConcept(String lookup) {
		Concept c = Context.getConceptService().getConceptByUuid(lookup);
		if (c == null) {
			c = Context.getConceptService().getConceptByName(lookup);
		}
		if (c == null) {
			try {
				String[] split = lookup.split("\\:");
				if (split.length == 2) {
					c = Context.getConceptService().getConceptByMapping(split[1], split[0]);
				}
			}
			catch (Exception e) {}
		}
		if (c == null) {
			try {
				c = Context.getConceptService().getConcept(Integer.parseInt(lookup));
			}
			catch (Exception e) {}
		}
		if (c == null) {
			throw new IllegalArgumentException("Unable to find Concept using key: " + lookup);
		}
		return c;
	}

	/**
	 * @return the List of Concepts that matches the passed comma-separated list of concept lookups
	 */
	public static List<Concept> getConceptList(String lookup) {
		List<Concept> l = new ArrayList<Concept>();
		if (StringUtils.isNotEmpty(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(getConcept(s));
			}
		}
		return l;
	}

	/**
	 * @return the List of Concepts that matches the passed any separated list of concept lookups
	 */
	public static List<Concept> getConceptList(String lookup, String separator) {
		List<Concept> l = new ArrayList<Concept>();
		if (StringUtils.isNotEmpty(lookup)) {
			if(StringUtils.isNotEmpty(separator)){
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(getConcept(s));
				}
			}else{
				l.add(getConcept(lookup));
			}
		}
		return l;
	}

	/**
	 * @return the List of Concepts that matches the passed comma-separated list of concept lookups
	 */
	public static List<Concept> getConceptsInSet(String lookup) {
		List<Concept> ret = new ArrayList<Concept>();
		Concept set = getConcept(lookup);
		for (Concept c : set.getSetMembers()) {
			ret.add(c);
		}
		return ret;
	}

	/**
	 * @return the Form that matches the passed uuid, name, or primary key id
	 */
	public static Form getForm(String lookup) {
		Form form = Context.getFormService().getFormByUuid(lookup);
		if (form == null) {
			form = Context.getFormService().getForm(lookup);
		}
		if (form == null) {
			try {
				form = Context.getFormService().getForm(Integer.parseInt(lookup));
			}
			catch(Exception e) { }
		}
		if (form == null) {
			throw new IllegalArgumentException("Unable to find Form using key: " + lookup);
		}
		return form;
	}

	/**
	 * @return the List of Forms that matches the passed comma-separated list of Form lookups
	 */
	public static List<Form> getFormList(String lookup) {
		List<Form> l = new ArrayList<Form>();
		if (StringUtils.isNotEmpty(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(getForm(s));
			}
		}
		return l;
	}

	/**
	 * @return the List of Forms that matches the passed any separated list of Form lookups
	 */
	public static List<Form> getFormList(String lookup, String separator) {
		List<Form> l = new ArrayList<Form>();
		if (StringUtils.isNotEmpty(lookup)) {
			if(StringUtils.isNotEmpty(separator)){
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(getForm(s));
				}
			}else{
				l.add(getForm(lookup));
			}
		}
		return l;
	}

	/**
	 * @return the EncounterType that matches the passed uuid, name, or primary key id
	 */
	public static EncounterType getEncounterType(String lookup) {
		EncounterType et = Context.getEncounterService().getEncounterTypeByUuid(lookup);
		if (et == null) {
			et = Context.getEncounterService().getEncounterType(lookup);
		}
		if (et == null) {
			try {
				et = Context.getEncounterService().getEncounterType(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find EncounterType using key: " + lookup);
		}

		return et;
	}

	/**
	 * @return the List of EncounterTypes that matches the passed comma-separated list of Encounter lookups
	 */
	public static List<EncounterType> getEncounterTypeList(String lookup) {
		List<EncounterType> l = new ArrayList<EncounterType>();
		if (StringUtils.isNotEmpty(lookup)) {
			String[] split = lookup.split(",");
			for (String s : split) {
				l.add(getEncounterType(s));

			}
		}
		return l;
	}

	/**
	 * @return the List of EncounterTypes that matches the passed any separated list of Encounter lookups
	 */
	public static List<EncounterType> getEncounterTypeList(String lookup, String separator) {
		List<EncounterType> l = new ArrayList<EncounterType>();
		if (StringUtils.isNotEmpty(lookup)) {
			if(StringUtils.isNotEmpty(separator)){
				String[] split = lookup.split(separator);
				for (String s : split) {
					l.add(getEncounterType(s));
				}
			}else{
				l.add(getEncounterType(lookup));
			}
		}
		return l;
	}

	/**
	 * @return the RelationshipType that matches the passed uuid, name, or primary key id
	 */
	public static RelationshipType getRelationshipType(String lookup) {
		RelationshipType rt = Context.getPersonService().getRelationshipTypeByUuid(lookup);
		if (rt == null) {
			rt =  Context.getPersonService().getRelationshipTypeByName(lookup);
		}
		if (rt == null) {
			try {
				rt =  Context.getPersonService().getRelationshipType(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (rt == null) {
			throw new IllegalArgumentException("Unable to find RelationshipType using key: " + lookup);
		}
		return rt;
	}

	/**
	 * @return the OrderType that matches the passed uuid, name, or primary key id
	 */
	public static OrderType getOrderType(String lookup) {
		OrderType ot = Context.getOrderService().getOrderTypeByUuid(lookup);
		if (ot == null) {
			for (OrderType orderType : Context.getOrderService().getAllOrderTypes()) {
				if (orderType.getName().equalsIgnoreCase(lookup)) {
					ot = orderType;
				}
			}
		}
		if (ot == null) {
			try {
				ot =  Context.getOrderService().getOrderType(Integer.parseInt(lookup));
			}
			catch(Exception e) { }
		}
		if (ot == null) {
			throw new IllegalArgumentException("Unable to find OrderType using key: " + lookup);
		}
		return ot;
	}

	/**
	 * @return the Location that matches the passed uuid, name, or primary key id
	 */
	public static Location getLocation(String lookup) {
		Location et = Context.getLocationService().getLocationByUuid(lookup);
		if (et == null) {
			et = Context.getLocationService().getLocation(lookup);
		}
		if (et == null) {
			try {
				et = Context.getLocationService().getLocation(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find Location using key: " + lookup);
		}

		return et;
	}

	/**
	 * @return the Location Tag that matches the passed uuid, name, or primary key id
	 */
	public static LocationTag getLocationTag(String lookup) {
		LocationTag et = Context.getLocationService().getLocationTagByUuid(lookup);
		if (et == null) {
			et = Context.getLocationService().getLocationTagByName(lookup);
		}
		if (et == null) {
			try {
				et = Context.getLocationService().getLocationTag(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find Location Tag using key: " + lookup);
		}

		return et;
	}

	public static List<Location> getLocationsForTag(String lookup) {
		LocationTag tag = getLocationTag(lookup);
		return Context.getLocationService().getLocationsByTag(tag);
	}

	/**
	 * @return the PersonAttributeType that matches the passed uuid, name, or primary key id
	 */
	public static PersonAttributeType getPersonAttributeType(String lookup) {
		PersonAttributeType et = Context.getPersonService().getPersonAttributeTypeByUuid(lookup);
		if (et == null) {
			et = Context.getPersonService().getPersonAttributeTypeByName(lookup);
		}
		if (et == null) {
			try {
				et = Context.getPersonService().getPersonAttributeType(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (et == null) {
			throw new IllegalArgumentException("Unable to find PersonAttributeType using key: " + lookup);
		}

		return et;
	}

	/**
	 * @return the ConceptClass that matches the passed uuid, name, or primary key id
	 */
	public static ConceptClass getConceptClass(String lookup) {
		ConceptClass cc = Context.getConceptService().getConceptClassByUuid(lookup);
		if (cc == null) {
			cc = Context.getConceptService().getConceptClassByName(lookup);
		}
		if (cc == null) {
			try {
				cc = Context.getConceptService().getConceptClass(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (cc == null) {
			throw new IllegalArgumentException("Unable to find Concept Class using key: " + lookup);
		}
		return cc;
	}

	/**
	 * @return the ConceptDatatype that matches the passed uuid, name, or primary key id
	 */
	public static ConceptDatatype getConceptDatatype(String lookup) {
		ConceptDatatype cc = Context.getConceptService().getConceptDatatypeByUuid(lookup);
		if (cc == null) {
			cc = Context.getConceptService().getConceptDatatypeByName(lookup);
		}
		if (cc == null) {
			try {
				cc = Context.getConceptService().getConceptDatatype(Integer.parseInt(lookup));
			}
			catch(Exception e) {}
		}
		if (cc == null) {
			throw new IllegalArgumentException("Unable to find Concept Datatype using key: " + lookup);
		}
		return cc;
	}

	/**
	 * @return the global property with the passed name, or the default value if not found
	 */
	public static String getGlobalProperty(String propertyName, String defaultValue) {
		String s = Context.getAdministrationService().getGlobalProperty(propertyName);
		return StringUtils.isNotEmpty(s) ? s : defaultValue;
	}
}
