package org.openmrs.module.pihmalawi.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.MetadataLookup;
import org.openmrs.module.pihmalawi.ProgramHelper;
import org.openmrs.module.pihmalawi.reports.extension.HibernatePihMalawiQueryDao;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.util.OpenmrsUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PatientDataHelper {

	protected Log log = LogFactory.getLog(this.getClass());

	private Map<String, Concept> conceptCache = new HashMap<String, Concept>();
	private Map<String, Program> programCache = new HashMap<String, Program>();
	private Map<String, ProgramWorkflow> programWorkflowCache = new HashMap<String, ProgramWorkflow>();
	private Map<String, EncounterType> encounterTypeCache = new HashMap<String, EncounterType>();
	private Map<String, ProgramWorkflowState> programWorkflowStateCache = new HashMap<String, ProgramWorkflowState>();
	private Map<String, PatientIdentifierType> patientIdentifierTypeCache = new HashMap<String, PatientIdentifierType>();
	private Map<String, RelationshipType> relTypeCache = new HashMap<String, RelationshipType>();
	private ProgramHelper h = new ProgramHelper();

	// Data Set Utilities

	public void addCol(DataSetRow row, String label, Object value) {
		if (value == null) {
			value = "";
		}
		DataSetColumn c = new DataSetColumn(label, label, value.getClass());
		row.addColumnValue(c, value);
	}

	// Demographics

	public String getGivenName(Patient p) {
		return p.getGivenName();
	}

	public String getFamilyName(Patient p) {
		return p.getFamilyName();
	}

	public String getBirthdate(Patient p) {
		return formatYmd(p.getBirthdate());
	}

	public Integer getAgeOnDate(Patient p, Date d) {
		return p.getAge(d);
	}

	public Integer getAgeInMonthsOnDate(Patient p, Date d) {
		Calendar bday = Calendar.getInstance();
		bday.setTime(p.getBirthdate());

		Calendar onDay = Calendar.getInstance();
		onDay.setTime(d);

		int bDayMonths = bday.get(Calendar.YEAR) * 12 + bday.get(Calendar.MONTH);
		int onDayMonths = onDay.get(Calendar.YEAR) * 12 + onDay.get(Calendar.MONTH);
		return onDayMonths - bDayMonths;
	}

	public String getGender(Patient p) {
		return p.getGender();
	}

	// Address Fields

	public String getVillage(Patient p) {
		PersonAddress pa = p.getPersonAddress();
		if (pa != null) {
			return h(pa.getCityVillage());
		}
		return "";
	}

	public String getTraditionalAuthority(Patient p) {
		PersonAddress pa = p.getPersonAddress();
		if (pa != null) {
			return h(pa.getCountyDistrict());
		}
		return "";
	}

	public String getDistrict(Patient p) {
		PersonAddress pa = p.getPersonAddress();
		if (pa != null) {
			return h(pa.getStateProvince());
		}
		return "";
	}

	// Relationships

	public String vhwName(Patient p, boolean guardianIfNoChw) {

		List<Relationship> ships = Context.getPersonService().getRelationshipsByPerson(p);
		String vhw = "";

		RelationshipType vhwType = lookupRelationshipType("Patient/Village Health Worker");
		for (Relationship r : ships) {
			if (r.getRelationshipType().equals(vhwType)) {
				vhw = r.getPersonB().getPersonName().getFullName();
			}
		}
		if (guardianIfNoChw) {
			RelationshipType guardianType = lookupRelationshipType("Patient/Guardian");
			for (Relationship r : ships) {
				if (r.getRelationshipType().equals(guardianType)) {
					vhw = r.getPersonB().getPersonName().getFullName() + " (Guardian)";
				}
			}
		}
		return vhw;
	}

	// Identifiers

	public String preferredIdentifierAtLocation(Patient p, PatientIdentifierType identifierType, Location locationParameter) {
		String ret = "";
		List<PatientIdentifier> pis = p.getPatientIdentifiers(identifierType);
		for (PatientIdentifier pi : pis) {
			if (pi != null && pi.getLocation() != null && locationParameter != null && pi.getLocation().getId() == locationParameter.getId()) {
				ret = formatPatientIdentifier(pi.getIdentifier());
				if (pi.isPreferred()) {
					return ret;
				}
			}
		}
		return ret;
	}

	public String identifiers(Patient p, PatientIdentifierType piType) {
		String ids = "";
		if (piType != null) {
			for (PatientIdentifier pi : p.getPatientIdentifiers(piType)) {
				if (pi != null && pi.getLocation() != null) {
					ids += formatPatientIdentifier(pi.getIdentifier()) + " ";
				}
			}
		}
		return ids;
	}

	public String formatPatientIdentifier(String id) {
		try {
			if (id.endsWith(" HCC")) {
				int firstSpace = id.indexOf(" ");
				int lastSpace = id.lastIndexOf(" ");
				String number = id.substring(firstSpace + 1, lastSpace);
				try {
					DecimalFormat f = new java.text.DecimalFormat("0000");
					number = f.format(new Integer(number));
				} catch (Exception e) {
					// error while converting
					return id;
				}
				return id.substring(0, firstSpace) + "-" + number + "-HCC";
			} else {
				if (id.lastIndexOf(" ") > 0) {
					// for now assume that an id without leading zeros is there
					// when
					// there is a space
					String number = id.substring(id.lastIndexOf(" ") + 1);
					try {
						DecimalFormat f = new java.text.DecimalFormat("0000");
						number = f.format(new Integer(number));
					} catch (Exception e) {
						// error while converting
						return id;
					}
					return id.substring(0, id.lastIndexOf(" ")) + "-" + number;
				}
				return id;
			}
		} catch (Exception e) {
			return "(error)";
		}
	}

	// Obs

	public Obs getLatestObs(Patient p, String concept, List<EncounterType> onlyInEncountersOfType, Date endDate) {
		Concept c = MetadataLookup.concept(concept);
		List<Encounter> encs = null;
		if (onlyInEncountersOfType != null) {
			EncounterService es = Context.getEncounterService();
			encs = es.getEncounters(p, null, null, endDate, null, onlyInEncountersOfType, null, false);
		}
		ObsService os = Context.getObsService();
		List<Obs> l = os.getObservations(Arrays.asList((Person)p), encs, Arrays.asList(c), null, null, null, null, 1, null, null, endDate, false);
		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	public Obs getEarliestObs(Patient p, String concept, List<EncounterType> onlyInEncountersOfType, Date endDate) {
		Concept c = MetadataLookup.concept(concept);
		List<Encounter> encs = null;
		if (onlyInEncountersOfType != null) {
			EncounterService es = Context.getEncounterService();
			encs = es.getEncounters(p, null, null, endDate, null, onlyInEncountersOfType, null, false);
		}
		ObsService os = Context.getObsService();
		List<Obs> l = os.getObservations(Arrays.asList((Person)p), encs, Arrays.asList(c), null, null, null, null, null, null, null, endDate, false);
		Map<Date, Obs> m = new TreeMap<Date, Obs>();
		for (Obs o : l) {
			m.put(o.getObsDatetime(), o);
		}
		if (m.isEmpty()) {
			return null;
		}
		return m.values().iterator().next();
	}

	public Map<String, String> getReasonStartingArvs(Patient p, Date endDate) {
		Map<String, String> reasons = new LinkedHashMap<String, String>();
		List<EncounterType> artInitialEncounter = Arrays.asList(lookupEncounterType("ART_INITIAL"));

		Obs obs = getLatestObs(p, "CD4 count", artInitialEncounter, endDate);
		reasons.put("CD4", formatValue(obs));

		obs = getLatestObs(p, "Kaposis sarcoma side effects worsening while on ARVs?", artInitialEncounter, endDate);
		reasons.put("KS", formatValue(obs));

		obs = getLatestObs(p, "5965", artInitialEncounter, endDate); // TB Tx Status Concept, was giving a duplicate concept warning by name
		reasons.put("TB", formatValue(obs));

		obs = getLatestObs(p, "WHO stage", artInitialEncounter, endDate);
		reasons.put("STAGE", formatValue(obs));

		obs = getLatestObs(p, "730", artInitialEncounter, endDate); // CD4% Concept, was giving a duplicate concept warning by name
		reasons.put("TLC", formatValue(obs));

		obs = getLatestObs(p, "Presumed severe HIV criteria present", artInitialEncounter, endDate);
		reasons.put("PSHD", formatValue(obs));

		return reasons;
	}

	public String formatValueDatetime(Obs o) {
		if (o != null) {
			return formatYmd(o.getValueDatetime());
		}
		return "";
	}

	public Date getValueDatetime(Obs o) {
		if (o != null) {
			return o.getValueDatetime();
		}
		return null;
	}

	public String formatValue(Obs o) {
		if (o == null) {
			return "";
		}
		return o.getValueAsString(Context.getLocale());
	}

	public String formatObsDatetime(Obs o) {
		if (o != null) {
			return formatYmd(o.getObsDatetime());
		}
		return "";
	}

	public Date getObsDatetime(Obs o) {
		if (o != null) {
			return o.getObsDatetime();
		}
		return null;
	}


	// Orders

	public List<DrugOrder> getDrugOrdersByStartDateAscending(Patient p, String conceptNameOrId, Date onOrBeforeDate) {
		Map<Date, DrugOrder> m = new TreeMap<Date, DrugOrder>();
		Concept drugConcept = MetadataLookup.concept(conceptNameOrId);
		for (DrugOrder drugOrder : Context.getOrderService().getOrders(DrugOrder.class, Arrays.asList(p), Arrays.asList(drugConcept), OrderService.ORDER_STATUS.NOTVOIDED, null, null, null)) {
			if (onOrBeforeDate == null || drugOrder.getStartDate().compareTo(onOrBeforeDate) <= 0) {
				m.put(drugOrder.getStartDate(), drugOrder);
			}
		}
		return new ArrayList<DrugOrder>(m.values());
	}

	public DrugOrder getEarliestDrugOrder(Patient p, String conceptNameOrId, Date onOrBeforeDate) {
		List<DrugOrder> l = getDrugOrdersByStartDateAscending(p, conceptNameOrId, onOrBeforeDate);
		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	public Set<Concept> getDrugsTakingOnDate(Patient p, Date onDate) {
		Set<Concept> l = new HashSet<Concept>();
		for (DrugOrder drugOrder : Context.getOrderService().getOrders(DrugOrder.class, Arrays.asList(p), null, OrderService.ORDER_STATUS.NOTVOIDED, null, null, null)) {
			if (drugOrder.isCurrent(onDate)) {
				l.add(drugOrder.getConcept());
			}
		}
		return l;
	}

	public String formatConcepts(Collection<Concept> concepts, String separator) {
		Set<String> names = new TreeSet<String>();
		if (concepts != null) {
			for (Concept c : concepts) {
				names.add(c.getName().getName());
			}
		}
		return OpenmrsUtil.join(names, separator);
	}

	public String formatOrderStartDate(Order order) {
		if (order != null) {
			return formatYmd(order.getStartDate());
		}
		return "";
	}

	// Program Enrollments

	public String formatStateName(PatientState state) {
		if (state != null) {
			return state.getState().getConcept().getName().getName();
		}
		return "";
	}

	public String formatStateStartDate(PatientState state) {
		if (state != null) {
			return formatYmd(state.getStartDate());
		}
		return "";
	}

	public Date getStateStartDate(PatientState state) {
		if (state != null) {
			return state.getStartDate();
		}
		return null;
	}

	public String formatStateEndDate(PatientState state) {
		if (state != null) {
			return formatYmd(state.getEndDate());
		}
		return "";
	}

	public String allEnrollmentsAsString(Patient p) {
		String programs = "";

		// just collect everything latest program enrollment you can find
		Set<PatientState> pss = h.getMostRecentStates(p);
		if (pss != null) {
			Iterator<PatientState> i = pss.iterator();
			while (i.hasNext()) {
				PatientState ps = i.next();
				programs += ps.getPatientProgram().getProgram().getName()
						+ ":&nbsp;" + ps.getState().getConcept().getName()
						+ "&nbsp;(since&nbsp;"
						+ formatYmd(ps.getStartDate()) + "); ";
			}
		}
		return programs;
	}

	public Encounter getFirstEncounterOfType(Patient p, List<EncounterType> encounterTypes, Date endDate) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(p, null, null, endDate, null, encounterTypes, null, false);
		Encounter ret = null;
		for (Encounter e : encounters) {
			if (ret == null || e.getEncounterDatetime().before(ret.getEncounterDatetime())) {
				ret = e;
			}
		}
		return ret;
	}

	protected void addOutcomeFromStateCols(DataSetRow row, Patient p,
										   Location locationParameter, ProgramWorkflow pw, ProgramWorkflowState stateBeforeStateChange) {
		try {
			PatientState ps = null;
			// enrollment outcome from location
			ps = h.getMostRecentStateAtLocation(p, pw, locationParameter
			);

			DataSetColumn c = new DataSetColumn("Outcome", "Outcome",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
			}
			c = new DataSetColumn("Outcome change date", "Outcome change Date",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, formatYmd(ps.getStartDate()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addOutcomeCols(DataSetRow row, Patient p,
								  Location locationParameter, Date endDate, ProgramWorkflow pw) {
		try {
			PatientState ps = null;
			if (locationParameter != null) {
				// enrollment outcome from location
				ps = h.getMostRecentStateAtLocationAndDate(p, pw, locationParameter, endDate
				);
			} else {
				ps = h.getMostRecentStateAtDate(p, pw, endDate);
			}

			DataSetColumn c = new DataSetColumn("Outcome", "Outcome",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, ps.getState().getConcept().getName()
						.getName());
			}
			c = new DataSetColumn("Outcome change date", "Outcome change Date",
					String.class);
			if (ps != null) {
				row.addColumnValue(c, formatYmd(ps.getStartDate()));
			}
			c = new DataSetColumn("Outcome location", "Outcome location",
					String.class);
			if (ps != null && locationParameter == null) {
				// register for all locations
				row.addColumnValue(c, h.getEnrollmentLocation(ps
						.getPatientProgram()));
			} else if (ps != null && locationParameter != null) {
				row.addColumnValue(c, locationParameter);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentOutcomeWithinDatabaseCols(DataSetRow row,
														  Patient p, ProgramWorkflow pw) {
		try {
			PatientState ps = null;
			ps = h.getMostRecentState(p, pw);

			DataSetColumn c1 = new DataSetColumn("Last Outcome in DB (not filtered)",
					"Last Outcome in DB", String.class);
			DataSetColumn c2 = new DataSetColumn("Last Outcome change date",
					"Last Outcome change Date", String.class);
			DataSetColumn c3 = new DataSetColumn("Last Outcome change loc",
					"Last Outcome change loc", String.class);
			if (ps != null) {
				row.addColumnValue(c1, ps.getState().getConcept().getName()
						.getName());
				row.addColumnValue(c2, formatYmd(ps.getStartDate()));
				row.addColumnValue(c2, formatYmd(ps.getStartDate()));
				row.addColumnValue(c3, h.getEnrollmentLocation(ps
						.getPatientProgram()));
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
				row.addColumnValue(c3, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}

	}

	protected void addFirstTimeChangeToStateDateCols(DataSetRow row, Patient p,
													 ProgramWorkflowState state, String header, Date endDate) {
		DataSetColumn c1 = new DataSetColumn(header + " date",
				header + " date", String.class);
		DataSetColumn c2 = new DataSetColumn(header + " location", header
				+ " location", String.class);
		PatientState ps = h.getFirstTimeInState(p, state.getProgramWorkflow()
				.getProgram(), state, endDate);
		if (ps != null) {
			row.addColumnValue(c1, formatYmd(ps.getStartDate()));
			// first on art at
			row.addColumnValue(
					c2,
					firstTimeInStateAtLocation(p, state.getProgramWorkflow()
							.getProgram(), state, endDate));
		} else {
			row.addColumnValue(c1, h(""));
			row.addColumnValue(c2, h(""));
		}
	}

	protected void addEnrollmentDateCols(DataSetRow row, Patient p, Location locationParameter, Program program, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, String.class);
			if (locationParameter != null) {
				PatientProgram pp = h.getMostRecentProgramEnrollmentAtLocation(p, program, locationParameter);
				row.addColumnValue(c, formatYmd(pp.getDateEnrolled()));
			}
			else {
				row.addColumnValue(c, h("(not applicable)"));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addEnrollmentDateCols(DataSetRow row, Patient p,
										 Location locationParameter, ProgramWorkflowState state, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, String.class);
			if (locationParameter != null) {
				List<PatientState> states = h.getPatientStatesByWorkflowAtLocation(
						p, state, locationParameter);
				PatientState firstState = states.get(0);
				row.addColumnValue(c, formatYmd(firstState
						.getPatientProgram().getDateEnrolled()));
			} else {
				row.addColumnValue(c, h("(not applicable)"));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstTimeEnrollmentCols(DataSetRow row, Patient p,
											  ProgramWorkflowState state, Date endDate, String label) {
		try {
			DataSetColumn c = new DataSetColumn(label, label, String.class);
			row.addColumnValue(
					c,
					formatYmd(h.getFirstTimeInState(p,
							state.getProgramWorkflow().getProgram(), state, endDate)
							.getPatientProgram().getDateEnrolled()));

			// first on art at
			c = new DataSetColumn(label + " location", label + " location",
					String.class);
			row.addColumnValue(
					c,
					firstTimeInStateAtLocation(p, state.getProgramWorkflow()
							.getProgram(), state, endDate));
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstEncounterCols(DataSetRow row, Patient p,
										 EncounterType encounterType, String label, Date endDate) {
		try {
			List<Encounter> encounters = Context.getEncounterService().getEncounters(p, null, null, endDate, null, Arrays.asList(encounterType), null, false);
			DataSetColumn c1 = new DataSetColumn(label + " date", label
					+ " date", String.class);
			DataSetColumn c2 = new DataSetColumn(label + " location", label
					+ " location", String.class);
			if (!encounters.isEmpty()) {
				Encounter e = encounters.get(encounters.size() - 1);
				row.addColumnValue(c1,
						formatYmd(e.getEncounterDatetime()));
				row.addColumnValue(c2, e.getLocation());
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addFirstChangeToStateCols(DataSetRow row, Patient p,
											 ProgramWorkflowState state, Date endDate, String label) {
		try {
			DataSetColumn c1 = new DataSetColumn(label + " date", label
					+ " date", String.class);
			DataSetColumn c2 = new DataSetColumn(label + " location", label
					+ " location", String.class);
			PatientState ps = h.getFirstTimeInState(p, state.getProgramWorkflow()
					.getProgram(), state, endDate);
			if (ps != null) {
				row.addColumnValue(c1, formatYmd(ps.getStartDate()));
				row.addColumnValue(
						c2,
						firstTimeInStateAtLocation(p, state
								.getProgramWorkflow().getProgram(), state, endDate));
			} else {
				row.addColumnValue(c1, h(""));
				row.addColumnValue(c2, h(""));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentVitalsCols(DataSetRow row, Patient p,
										   Date endDateParameter) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDateParameter, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es,
					Arrays.asList(lookupConcept("Height (cm)")), null, null,
					null, null, 1, null, null, endDateParameter, false);
			DataSetColumn c = null;
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				c = new DataSetColumn("Height (cm)", "Height (cm)",
						String.class);
				row.addColumnValue(c, (o.getValueNumeric()));
			}
			obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es,
					Arrays.asList(lookupConcept("Weight (kg)")), null, null,
					null, null, 1, null, null, endDateParameter, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				c = new DataSetColumn("Weight (kg)", "Weight (kg)",
						String.class);
				row.addColumnValue(c, (o.getValueNumeric()));
				c = new DataSetColumn("Weight date", "Weight date",
						String.class);
				row.addColumnValue(c, formatYmd(o.getObsDatetime()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentDatetimeObsCols(DataSetRow row, Patient p,
												Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
								.getName(), String.class);
				row.addColumnValue(c, formatYmd(o.getValueDatetime()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentObsCols(DataSetRow row, Patient p,
										Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
								.getName(), String.class);
				row.addColumnValue(c, o.getValueAsString(Context.getLocale()));

				c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", "Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", String.class);
				row.addColumnValue(c, formatYmd(o.getObsDatetime()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	protected void addMostRecentNumericObsCols(DataSetRow row, Patient p,
											   Concept concept, Date endDate) {
		try {
			List<Encounter> es = Context.getEncounterService().getEncounters(p,
					null, null, endDate, null, null, null, false);
			List<Obs> obs = Context.getObsService().getObservations(
					Arrays.asList((Person) p), es, Arrays.asList(concept),
					null, null, null, null, 1, null, null, endDate, false);
			if (obs.iterator().hasNext()) {
				Obs o = obs.iterator().next();
				DataSetColumn c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName(),
						"Last "
								+ concept.getName(Context.getLocale())
								.getName(), String.class);
				row.addColumnValue(c, o.getValueNumeric());

				c = new DataSetColumn("Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", "Last "
						+ concept.getName(Context.getLocale()).getName()
						+ " Date", String.class);
				row.addColumnValue(c, formatYmd(o.getObsDatetime()));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}



	private Location firstTimeInStateAtLocation(Patient p, Program program,
												ProgramWorkflowState firstTimeInState, Date endDate) {
		PatientState ps = h.getFirstTimeInState(p, program, firstTimeInState, endDate);
		if (ps != null) {
			return h.getEnrollmentLocation(ps.getPatientProgram()
			);
		}
		return null;
	}

	protected PatientProgram currentPatientProgram(Program program,
												   Patient patient) {
		List<org.openmrs.PatientProgram> pps = Context.getProgramWorkflowService()
				.getPatientPrograms(patient, program, null, null, new Date(),
						null, false);
		if (pps.size() == 1) {
			return (PatientProgram) pps.get(0);
		}
		return null;
	}

	// Metadata Lookup Utilities

	protected PatientIdentifierType lookupPatientIdentifierType(String name) {
		if (!patientIdentifierTypeCache.containsKey(name)) {
			patientIdentifierTypeCache.put(name, Context.getPatientService().getPatientIdentifierTypeByName(name));
		}
		return patientIdentifierTypeCache.get(name);
	}

	protected Concept lookupConcept(String name) {
		if (!conceptCache.containsKey(name)) {
			conceptCache.put(name, Context.getConceptService().getConceptByName(name));
		}
		return conceptCache.get(name);
	}

	protected EncounterType lookupEncounterType(String string) {
		if (!encounterTypeCache.containsKey(string)) {
			encounterTypeCache.put(string, Context.getEncounterService().getEncounterType(string));
		}
		return encounterTypeCache.get(string);
	}

	protected ProgramWorkflow lookupProgramWorkflow(String program,
													String workflow) {
		if (!programWorkflowCache.containsKey(""+program+workflow)) {
			programWorkflowCache.put(""+program+workflow, lookupProgram(program).getWorkflowByName(workflow));
		}
		return programWorkflowCache.get(""+program+workflow);
	}

	protected Program lookupProgram(String program) {
		if (!programCache.containsKey(program)) {
			programCache.put(program, Context.getProgramWorkflowService().getProgramByName(
					program));
		}
		return programCache.get(program);
	}

	protected ProgramWorkflowState lookupProgramWorkflowState(String program,
															  String workflow, String state) {
		if (!programWorkflowStateCache.containsKey(""+program+workflow+state)) {
			programWorkflowStateCache.put(""+program+workflow+state, Context.getProgramWorkflowService().getProgramByName(program)
					.getWorkflowByName(workflow).getStateByName(state));
		}
		return programWorkflowStateCache.get(""+program+workflow+state);
	}

	protected RelationshipType lookupRelationshipType(String typeName) {
		if (!relTypeCache.containsKey(typeName)) {
			relTypeCache.put(typeName, Context.getPersonService().getRelationshipTypeByName(typeName));
		}
		return relTypeCache.get(typeName);
	}

	// Utilities

	public String formatYmd(Date d) {
		if (d == null) {
			return "";
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}

	private String h(String s) {
		return ("".equals(s) || s == null ? "&nbsp;" : s);
	}

	private SessionFactory sessionFactory() {
		return ((HibernatePihMalawiQueryDao) Context.getRegisteredComponents(HibernatePihMalawiQueryDao.class).get(0)).getSessionFactory();
	}
}

