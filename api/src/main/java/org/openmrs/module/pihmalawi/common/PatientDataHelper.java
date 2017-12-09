package org.openmrs.module.pihmalawi.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PatientDataHelper {

	protected Log log = LogFactory.getLog(this.getClass());

	private Map<String, EncounterType> encounterTypeCache = new HashMap<String, EncounterType>();
	private Map<String, RelationshipType> relTypeCache = new HashMap<String, RelationshipType>();
	private CommonMetadata commonMetadata = new CommonMetadata();

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
			if (pi != null && pi.getLocation() != null && locationParameter != null && pi.getLocation().equals(locationParameter)) {
				ret = formatPatientIdentifier(pi.getIdentifier());
				if (pi.isPreferred()) {
					return ret;
				}
			}
		}
		return ret;
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
		Concept c = commonMetadata.getConcept(concept);
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
		Concept c = commonMetadata.getConcept(concept);
		List<Encounter> encs = null;
		if (onlyInEncountersOfType != null) {
			encs = Context.getEncounterService().getEncounters(p, null, null, endDate, null, onlyInEncountersOfType, null, false);
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

		obs = getLatestObs(p, "CD4 percent", artInitialEncounter, endDate);
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

	/*public List<DrugOrder> getDrugOrdersByStartDateAscending(Patient p, String conceptNameOrId, Date onOrBeforeDate) {
		Map<Date, DrugOrder> m = new TreeMap<Date, DrugOrder>();
		Concept drugConcept = commonMetadata.getConcept(conceptNameOrId);
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
	}*/

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

	public Encounter getFirstEncounterOfType(Patient p, List<EncounterType> encounterTypes, Date endDate) {
		return getFirstEncounterAtLocationOfType(p, encounterTypes, endDate, null);
	}
    public Encounter getFirstEncounterAtLocationOfType(Patient p, List<EncounterType> encounterTypes, Date endDate, Location location) {
        List<Encounter> encounters = Context.getEncounterService().getEncounters(p, null, null, endDate, null, encounterTypes, null, false);
        Encounter ret = null;
        for (Encounter e : encounters) {
            if (ret == null || e.getEncounterDatetime().before(ret.getEncounterDatetime())) {
                if (location == null || location.equals(e.getLocation())) {
                    ret = e;
                }
            }
        }
        return ret;
    }

	// Metadata Lookup Utilities

	protected EncounterType lookupEncounterType(String string) {
		if (!encounterTypeCache.containsKey(string)) {
			encounterTypeCache.put(string, Context.getEncounterService().getEncounterType(string));
		}
		return encounterTypeCache.get(string);
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
}

