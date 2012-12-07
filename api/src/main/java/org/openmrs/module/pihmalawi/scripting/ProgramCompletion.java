package org.openmrs.module.pihmalawi.scripting;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;

public class ProgramCompletion {

	public ProgramCompletion() {
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

		new ProgramCompletion().run();
	}

	public void run() throws Exception {
		ProgramWorkflowService pws = Context.getProgramWorkflowService();

		int[] patientProgramIds = { 1089,1096,1098,1100,1107,1113,1119,1121,1122,1134,1136,1142,1146,1154,1157,1177,1191,1199,1200,1204,1216,1222,1225,1227,1229,1232,1271,1274,1281,1282,1283,1286,1290,1301,1309,1310,4711,1312,1316,5067,1329,1332,1338,1355,1370,1371,1382,1401,1403,1406,1429,1436,1442,1443,1448,1452,1453,1459,1465,1467,1571,1659,2490,1672,1679,6016,2515,2505,1689,1694,1696,4473,4958,4960,1767,1778,1819,1822,1823,1832,2538,1838,1864,1877,1883,2508,1896,1904,1905,2497,1910,1912,1920,1924,1939,1940,1952,1953,1959,1995,1998,1999,2006,2011,2013,2018,2020,2022,2025,2028,2035,2037,2038,2529,2069,2544,2090,2126,2132,2141,2144,2185,2199,2210,2260,2261,2266,2267,2270,2275,2304,2322,5724,2359,2377,2394,2403,2450,2462,2473,2475,2480,2484,2551,2598,2608,2612,2618,2632,2633,2683,2688,2690,2697,2739,2744,2776,2781,2791,2849,2857,2858,2863,2869,2872,2875,2881,2889,2890,2910,2919,2925,2935,4382,2956,4627,3007,3025,3043,3044,3047,3061,3070,3086,3157,3177,3192,3194,6051,3420,3441,3448,3449,3491,3495,3517,3575,3604,3629,3650,5939,3685,3687,3689,3690,3704,3705,3706,3710,3711,3733,3746,5977,3761,5979,3793,3814,3816,3817,3818,3875,3846,3849,3852,3853,3882,3901,3903,3909,4427,4004,4006,4008,4011,4044,4045,4051,4053,4084,4118,4120,4122,4123,4143,4147,4149,4168,6218,4187,4189,4190,4195,4211,4214,4223,4227,4281,4282,4283,4296,4413,4329,4332,4363,4503,5123,4506,4547,4618,4626,4800,4801,4882,5431,4933,5002,4994,4999,5008,5015,5035,5388,5134,5135,5136,5137,5173,5184,5206,5225,5246,5265,5302,5313,5315,5317,5328,5329,5330,5331,5353,5367,5370,5379,5385,5420,5435,5441,5443,5509,5516,5518,5519,5522,5524,5551,5556,5562,5569,5572,5596,5597,5598,5599,5600,5619,5621,5622,5631,5648,5649,5662,5668,5672,5680,5681,5685,5687,5688,5694,5703,5760,5765,5767,5772,5806,5811,5823,5833,5834,5836,5837,5839,5841,5842,5850,5851,5857,5858,5862,5881,5890,5895,5898,5901,5917,5919,5936,5941,5946,5951,5952,5954,5955,6511,5956,5971,5982,5993,6410,6005,6006,6013,6015,6019,6339,6502,6029,6035,6039,6048,6052,6053,6055,6056,6090,6091,6092,6093,6094,6097,6099,6101,6107,6212,6213,6215,6217,6219,6222,6258,6262,6281,6282,6283,6284,6291,6292,6322,6323,6324,6327,6329,6330,6747,6548,6549,6509 };

		// completeProgramInTerminalStates(pws, patientProgramIds);
		completeProgramAfterVerifiedInternalTransfer(pws, patientProgramIds);
		System.out.println("done");
	}

	private void completeProgramInTerminalStates(ProgramWorkflowService pws,
			int[] patientProgramIds) {
		// complete programs in terminal state
		for (int ppId : patientProgramIds) {
			PatientProgram pp = pws.getPatientProgram(ppId);
			PatientState ps = pp.getCurrentState(pws.getWorkflow(1));
			if (ps.getState().getTerminal() && pp.getDateCompleted() == null) {
				pp.setDateCompleted(ps.getStartDate());
				pws.savePatientProgram(pp);
			}
		}
	}

	private void completeProgramAfterVerifiedInternalTransfer(
			ProgramWorkflowService pws, int[] patientProgramIds) {
		ProgramWorkflowState transferredOut = pws.getState(pws.getWorkflow(1), "Patient transferred out");		
		for (int ppId : patientProgramIds) {
			System.out.println("ppId " + ppId);
			PatientProgram pp = pws.getPatientProgram(ppId);
			PatientState ps = pp.getCurrentState(pws.getWorkflow(1));
			if (ps == null || ps.getState() == null || ps.getState().getId() == null || pp == null) {
				System.out.println("error with patient " + pp.getPatient().getPatientId());
			} else {
			if (ps.getState().getId().equals(87) && pp.getDateCompleted() != null) {
				// internal transfer with completed program found 
				for (PatientProgram otherPp : pws.getPatientPrograms(
						pp.getPatient(), pp.getProgram(), null, null, null,
						null, false)) {
					if (pp.getPatientProgramId() != otherPp
							.getPatientProgramId()
							&& otherPp.getDateCompleted() == null && ps.getDateCreated().before(new Date(otherPp.getDateCreated().getTime() + (1000*60*60*24)))) {
						// another (more recent?) program found, complete the
						// old one
						try { 
						pp.transitionToState(transferredOut, ps.getStartDate());
						pp.setDateCompleted(ps.getStartDate());
						pws.savePatientProgram(pp);
						System.out.println(pp.getPatient().getPatientId());
						} catch (Exception e) {
							System.out.println("Error with patient 2: " + pp.getPatient().getPatientId());
						}
					}
				}
			}
		}
		}
		System.out.println("done2");
	}
}
