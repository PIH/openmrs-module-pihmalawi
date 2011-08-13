package org.openmrs.module.pihmalawi.exportToCsvMastercard;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;

/**
 * One-time script to split up the old initial encounters, which (sometimes)
 * also contained visit information
 */
public class ArtInitialToVisit {

	List<EncounterType> artInitials = null;
	List<EncounterType> artFollowups = null;
	Concept apptDate = null;
	Concept numberArvs;
	Concept weight;
	Concept height;
	private Form artVisitForm;
	private Concept amount;

	public ArtInitialToVisit() {
	}

	public static void main(String[] args) throws Exception {
		// parameters
		String importFile = args[0];
		String openmrsRuntimeProperties = args[1];
		String openmrsUser = args[2];
		String openmrsPw = args[3];

		// properties
		Properties prop = new Properties();
		prop.load(new FileInputStream(openmrsRuntimeProperties));
		String connectionUser = prop.getProperty("connection.username");
		String conncetionPw = prop.getProperty("connection.password");
		String conncetionUrl = prop.getProperty("connection.url");

		// connection init
		Context.startup(conncetionUrl, connectionUser, conncetionPw, prop);
		Context.openSession();
		Context.authenticate(openmrsUser, openmrsPw);

		new ArtInitialToVisit().run();
	}

	public void run() throws Exception {
		artInitials = Arrays.asList(Context.getEncounterService()
				.getEncounterType("ART_INITIAL"));
		artFollowups = Arrays.asList(Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP"));
		artVisitForm = Context.getFormService().getForm("ART Visit");
		apptDate = Context.getConceptService().getConcept("APPOINTMENT DATE");
		numberArvs = Context.getConceptService().getConcept(
				"NUMBER OF ANTIRETROVIRALS GIVEN");
		weight = Context.getConceptService().getConcept("WEIGHT (KG)");
		height = Context.getConceptService().getConcept("HEIGHT (CM)");
		amount = Context.getConceptService().getConcept("AMOUNT OF DRUG BROUGHT TO CLINIC");
		
//		Patient p = Context.getPatientService().getPatient(16817);
//		move(p);

		int i = 0;
		String[] ids = {"15901","15913","15918","15924","15940","15982","15983","15994","16003","16071","16237","16493","16590","16593","16760","16769","16771","16773","16785","16788","16793","16798","16806","16813","16817","16818","16829","16835","16837","16838","16860","16864","16867","16878","16882","16884","16885","16887","16891","16897","16904","16908","16909","16916","16917","16918","16923","16928","16929","16932","16936","16940","16948","16962","16963","16973","16975","16976","16981","17036","17037","17041","17046","17062","17085","17112","17127","17131","17170","17236","17257","17366","17374","17486","17565","17573","17627","17653","17658","17749","17757","17823","17839","17938","17944","18074","18096","18103","18219","18227","18244","18250","18275","18356","18461","18480","18509","18529","18532","18565","18606","18633","18658","18667","18678","18773","18777","18791","18825","18845","18912","18951","19017","19035","19215","19216","19229","19231","19238","19255","19336","19347","19348","19365","19378","19402","19439","19464","19505","19569","19601","19616","19650","19730","19759","19844","19870","19880","19903","19926","19929","19955","20083","20096","20243","20333","20334","20351","20352","20367","20377","20403","20448","20475","20485","20520","20538","20568","20573","20689","20723","20863","20965","21019","21115","21116","21118","21140","21142","21174","21249","21261","21264","21331","21335","21465","21483","21507","21561","21568","21605","21634","21653","21683","21715","21734","21770","21778","21804","21847","21896","21924","21926","21927","21980","22034","22065","22103","22107","22205","22210","22253","22264","22271","22292","22351","22352","22525","22539","22551","22567","22590","22596","22659","22698","22747","22751","22773","22863","22876","22934","22935","22977","23025","23090","23101","23106","23199","23227","23253","23446","23449","23455","23473","23490","23494","23537","23546","23561","23580","23595","23617","23625","23629","23638","23700","23774","23812","23834","23837","23839","23840","23876","23879","23887","23912","23953","24005","24007","24014","24087","24115","24174","24202","24206","24221","24244","24278","24281","24343","24421","24537","24551","24567","24595","24609","24628","24630","24634","24667","24671","24809","24820","24821","24843","24856","24907","25004","25005","25024","25029","25031","25091","25204","25251","25318","25319","25326","25327","25328","25345","25356","25407","25504","25627","25707","25729","25792","25858","25911","25965","25982","25984","26025","26090","26128","26131","26144","26163","26203","26221","26246","26261","26278","26338","26352","26374","26397","26402","26443","26529","26628","26638","26646","26668","26676","26707","26712","26721","26724","26761","26773","26989","27009","27102","27175","27186","27191","27207","27226","27228","27345","27366","27378","27649","27803","27817","27823","27874","27994","28007","28076","28143","28253","28270","28286","28491","28643","28806","28816","28820","28852","28856","28935","28942","28943","29037","29044","29059","29290","29409","29513","29584","29660","29709","29710","29742","29808","29891","29961","30017","30028","30197","30267","30289","30427","30568","30656","30688","30725","30968","30987","31012","31056","31116","31137","31196","31382","31470","31571","31615","31641","31743","31754","31871","31946","32125","32141","32159","32188","32204","32205","32255","32271","32302","32315","32337","32394","32397","32468","32495","32584","32623","32649","32653","32880","32892","32969","33183","33229","33254","33265","33422","33428","33429","33431","33436","33443","33596","33630","33659","33680","33752","33824","33870","33900","33906","33924","33982","34064","34090","34111","34128","34190","34236","34245","34248","34249","34269","34281","34367","34390","34453","34614","34631","34646","34675","34680","34688","34696","34707","34724","34776","34792","34834","34873","34875","34942","35042","35097","35100","35116","35233","35240","35245","35345","35397","35440","35454","35496","35527","35585","35635","35655","35673","35723","35933","35978","35985","35988","35989","36006","36030","36075","36161","36190","36374","36377","36465","36480","36559","36573","36600","36615","36620","36631","36643","36708","36813","36818","36842","36851","36855","36976","36977","36981","37067","37092","37104","37110","37116","37234","37250","37252","37269","37344","37376","37377","37422","37449","37535","37650","37656","37670","37690","37747","37850","37870","37904","37908","37910","38015","38128","38156","38181","38314","38349","38365","38377","38427","38435","38448","38482","38580","38656","38659","38749","38751","38781","38792","38806","38829","38844","39020","39028","39046","39047","39088","39308","39342","39427","39458","39481","39586","39608","39789","39807","39811","39840","39843","39853","39892","39895","39906","39935","39955","40056","40063","40097","40101","40171","40360","40365","40392","40429","40502","40539","40636","40703","40709","40735","40736","40893","40995","40998","41032","41038","41040","41048","41060","41088","41129","41130","41134","41148","41173","41250","41253","41290","41315","41319","41338","41386","41408","41498","41551","41635","41641","41751","41754","41767","41836","41841","41858","42131","42138","42140","42141","42247","42261","42316","42415","42417","42471","42572","42573","42575","42595","42739","42740","43018","43063","43064","43083","43104","43117","43137","43142","43167","43201","43234","43380","43411","43666","43682","43813","43815","43867","43871","43896","43898","43999","44067","44090","44098","44104","44145","44179","44272","44302","44326","44340","44342","44347","44395","44475","44481","44548","44589","44862","44867","44903","44961","45076","45087","45088","45173","45215","45237","45248","45254","45426","45452","45600","45831","45907","46095","46098","46115","46125","46129","46155","46398","46415","46545","46604","46654","46672","46690","46730","46867","46874","46927","46963","47064","47119","47121","47151","47239","47260","47496","47500","47502","47564","47595","47783","47871","47895","47909","47957","48009","48099","48159","48268","48302","48318","48369","48378","48379","48429","48431","48481","48505","48551","48571","48573","48670","48726","48897","48899","48905","48935","48961","48962","48986","48992","49001","49005","49022","49032","49033","49105","49162","49163","49231","49260","49264","49319","49386","49412","49444","49462","49472","49532","49606","49651","49658","49725","49746","49778","49808","49834","49845","49847","49892","49894","49940","49960","49986","50023","50121","50124","50130","50158","50191","50219","50220","50233","50266","50270","50331","50412","50464","50470","50521","50557","50593","50595","50642","50651","50652","50877","50893","50996","51011","51033","51053","51058","51080","51113","51114","51117","51118","51121","51126","51137","51145","51160","51215","51307","51334","51338","51344","51352","51353","51360","51364","51366","51380","51395","51415","51470","51472","51555","51644","51693","51794","51796","51831","51844","51942","51953","51980","51984","51985","51986","51988","51991","52041","52075","52145","52186","52308","52311","52348","52349","52384","52402","52429","52433","52435","52442","52480","52481","52492","52622","52674","52719","52756","52796","52840","52885","52905","52933","52941","52986","53013","53014","53138","53197","53213","53215","53285","53286","53314","53358","53422","53492","53554","53555","53559","53601","53602","53603","53605","53682","53716","53723","53741","53758","53836","53912","53982","54029","54043","54101","54104","54208","54210","54280","54282","54379","54382","54440","54446","54519","54544","54572","54577","54596","54612","54747","54748","54771","54893","54916","54951","54969","55022","55032","55039","55174","55175","55177","55216","55230","55270","55272","55294","55374","55409","55431","55476","55479","55501","55511","55512","55552","55558","55563","55611","55696","55726","55807","55811","55812","55816","55828","55847","55913","55920","55921","55922","55931","55932","55938","55940","55943","55945","56007","56013","56014","56044","56046","56081","56083","56096","56101","56248","56378","56432","56553","56554","56561","56592","56593","56594","56595","56609","56682","56704","56707","56708","56712"};
		for (String id : ids) {
			System.out.println("Process " + i++ + "/" + ids.length);
			Patient p2 = Context.getPatientService().getPatient(new Integer(id));
			if (p2.isPersonVoided()  || p2.isVoided()) {
				System.out.println("Patient/Person voided, skip it");
			} else {
				move(p2);
			}
		}
	}

	private void move(Patient p) {

		List<Encounter> el = Context.getEncounterService().getEncounters(p,
				null, null, null, null, artInitials, null, false);
		for (Encounter e : el) {
			List<Obs> os = Context.getObsService().getObservations(
					Arrays.asList((Person) p), Arrays.asList(e),
					Arrays.asList(apptDate), null, null, null, null, null,
					null, null, null, false);
			if (!os.isEmpty()) {
				// initial encounter with appt date exist; assuming visit
				// information in initial encounter
				List<Encounter> el2 = Context.getEncounterService()
						.getEncounters(p, null, e.getEncounterDatetime(),
								e.getEncounterDatetime(), null, artFollowups,
								null, false);
				if (el2.isEmpty()) {
					// no matching followup exists, create followup from initial
					createFollowupFromInitial(e);
				} else {
					System.out
							.println(p.getId()
									+ "\tAlready found follow up visit on same day as initial, ignore it.");
				}
			}
		}

	}

	private void createFollowupFromInitial(Encounter i) {
		System.out
				.println(i.getPatientId()
						+ "\tNo follow up visit found on same day as initial, create it.");

		Encounter f = createEncounter(i, Context.getEncounterService()
				.getEncounterType("ART_FOLLOWUP"));
		f.addObs(createObs(obs(i, apptDate)));
		f.addObs(createObs(obs(i, height)));
		f.addObs(createObs(obs(i, weight)));
		f.addObs(createObs(obs(i, numberArvs)));
		f.setForm(artVisitForm);
		Context.getEncounterService().saveEncounter(f);
	}

	private Obs obs(Encounter e, Concept concept) {
		List<Obs> os = Context.getObsService().getObservations(null,
				Arrays.asList(e), Arrays.asList(concept), null, null, null,
				null, null, null, null, null, false);
		if (!os.isEmpty()) {
			return os.get(0);
		}
		return null;
	}

	private Obs createObs(Obs blueprint) {
		if (blueprint == null) {
			return null;
		}
		Obs o = new Obs();
		o.setChangedBy(blueprint.getChangedBy());
		o.setCreator(blueprint.getCreator());
		o.setDateChanged(blueprint.getDateChanged());
		o.setDateCreated(blueprint.getDateCreated());
		o.setLocation(blueprint.getLocation());
		o.setObsDatetime(blueprint.getObsDatetime());
		o.setPerson(blueprint.getPerson());

		o.setConcept(blueprint.getConcept());
		o.setValueCoded(blueprint.getValueCoded());
		o.setValueCodedName(blueprint.getValueCodedName());
		o.setValueDatetime(blueprint.getValueDatetime());
		o.setValueNumeric(blueprint.getValueNumeric());
		o.setValueText(blueprint.getValueText());
		return o;
	}

	private Encounter createEncounter(Encounter blueprint,
			EncounterType newEncounterType) {
		Encounter e = new Encounter();
		e.setChangedBy(blueprint.getChangedBy());
		e.setCreator(blueprint.getCreator());
		e.setDateChanged(blueprint.getDateChanged());
		e.setDateCreated(blueprint.getDateCreated());
		e.setEncounterDatetime(blueprint.getEncounterDatetime());
		e.setEncounterType(newEncounterType);
		e.setLocation(blueprint.getLocation());
		e.setPatient(blueprint.getPatient());
		e.setProvider(blueprint.getProvider());
		return e;
	}
}
