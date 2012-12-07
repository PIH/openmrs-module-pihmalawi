package org.openmrs.module.pihmalawi.scripting;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

public class MarkPatientsWithoutPaper {
	
	
	private static Log log = LogFactory.getLog(MarkPatientsWithoutPaper.class);
	
	public MarkPatientsWithoutPaper() {
	}
	
	public void run() throws Exception {
		importThroughSessions();
	}
	
	private void importThroughSessions() throws Exception {
		// todo, read from openmrs-runtime.properties junit.usser and . pw
		final String USER = "cneumann";
		final String PW = "<changeme>";
		
		// manually collected pre art numbers where the paper record is missing
		// use wget script to process
		String[] preArtIds = { "P-NNO-0004", "P-NNO-0006", "P-NNO-0008", "P-NNO-0010", "P-NNO-0016", "P-NNO-0018",
		        "P-NNO-0023", "P-NNO-0030", "P-NNO-0033", "P-NNO-0042", "P-NNO-0043", "P-NNO-0044", "P-NNO-0045",
		        "P-NNO-0047", "P-NNO-0048", "P-NNO-0049", "P-NNO-0050", "P-NNO-0051", "P-NNO-0052", "P-NNO-0053",
		        "P-NNO-0054", "P-NNO-0055", "P-NNO-0056", "P-NNO-0057", "P-NNO-0058", "P-NNO-0059", "P-NNO-0060",
		        "P-NNO-0061", "P-NNO-0062", "P-NNO-0063", "P-NNO-0064", "P-NNO-0066", "P-NNO-0067", "P-NNO-0068",
		        "P-NNO-0069", "P-NNO-0070", "P-NNO-0071", "P-NNO-0072", "P-NNO-0073", "P-NNO-0074", "P-NNO-0076",
		        "P-NNO-0077", "P-NNO-0078", "P-NNO-0079", "P-NNO-0080", "P-NNO-0081", "P-NNO-0083", "P-NNO-0084",
		        "P-NNO-0085", "P-NNO-0086", "P-NNO-0087", "P-NNO-0088", "P-NNO-0089", "P-NNO-0090", "P-NNO-0091",
		        "P-NNO-0092", "P-NNO-0093", "P-NNO-0094", "P-NNO-0095", "P-NNO-0096", "P-NNO-0098", "P-NNO-0099",
		        "P-NNO-0100", "P-NNO-0101", "P-NNO-0102", "P-NNO-0103", "P-NNO-0104", "P-NNO-0105", "P-NNO-0106",
		        "P-NNO-0107", "P-NNO-0108", "P-NNO-0109", "P-NNO-0110", "P-NNO-0111", "P-NNO-0112", "P-NNO-0113",
		        "P-NNO-0114", "P-NNO-0115", "P-NNO-0116", "P-NNO-0117", "P-NNO-0118", "P-NNO-0119", "P-NNO-0120",
		        "P-NNO-0122", "P-NNO-0123", "P-NNO-0124", "P-NNO-0125", "P-NNO-0126", "P-NNO-0127", "P-NNO-0128",
		        "P-NNO-0129", "P-NNO-0130", "P-NNO-0131", "P-NNO-0132", "P-NNO-0134", "P-NNO-0135", "P-NNO-0136",
		        "P-NNO-0137", "P-NNO-0138", "P-NNO-0139", "P-NNO-0140", "P-NNO-0141", "P-NNO-0142", "P-NNO-0143",
		        "P-NNO-0144", "P-NNO-0145", "P-NNO-0146", "P-NNO-0147", "P-NNO-0149", "P-NNO-0152", "P-NNO-0153",
		        "P-NNO-0154", "P-NNO-0155", "P-NNO-0156", "P-NNO-0158", "P-NNO-0159", "P-NNO-0162", "P-NNO-0163",
		        "P-NNO-0164", "P-NNO-0165", "P-NNO-0166", "P-NNO-0167", "P-NNO-0168", "P-NNO-0169", "P-NNO-0170",
		        "P-NNO-0171", "P-NNO-0172", "P-NNO-0173", "P-NNO-0174", "P-NNO-0175", "P-NNO-0176", "P-NNO-0177",
		        "P-NNO-0178", "P-NNO-0179", "P-NNO-0180", "P-NNO-0181", "P-NNO-0182", "P-NNO-0183", "P-NNO-0184",
		        "P-NNO-0185", "P-NNO-0186", "P-NNO-0187", "P-NNO-0188", "P-NNO-0189", "P-NNO-0190", "P-NNO-0191",
		        "P-NNO-0192", "P-NNO-0193", "P-NNO-0194", "P-NNO-0195", "P-NNO-0196", "P-NNO-0198", "P-NNO-0199",
		        "P-NNO-0200", "P-NNO-0201", "P-NNO-0202", "P-NNO-0203", "P-NNO-0204", "P-NNO-0205", "P-NNO-0206",
		        "P-NNO-0207", "P-NNO-0208", "P-NNO-0209", "P-NNO-0211", "P-NNO-0212", "P-NNO-0213", "P-NNO-0214",
		        "P-NNO-0215", "P-NNO-0216", "P-NNO-0217", "P-NNO-0218", "P-NNO-0219", "P-NNO-0220", "P-NNO-0221",
		        "P-NNO-0222", "P-NNO-0223", "P-NNO-0224", "P-NNO-0225", "P-NNO-0226", "P-NNO-0227", "P-NNO-0228",
		        "P-NNO-0229", "P-NNO-0230", "P-NNO-0231", "P-NNO-0232", "P-NNO-0233", "P-NNO-0234", "P-NNO-0235",
		        "P-NNO-0236", "P-NNO-0237", "P-NNO-0238", "P-NNO-0239", "P-NNO-0240", "P-NNO-0241", "P-NNO-0242",
		        "P-NNO-0245", "P-NNO-0246", "P-NNO-0247", "P-NNO-0248", "P-NNO-0249", "P-NNO-0250", "P-NNO-0251",
		        "P-NNO-0252", "P-NNO-0253", "P-NNO-0254", "P-NNO-0255", "P-NNO-0257", "P-NNO-0258", "P-NNO-0259",
		        "P-NNO-0260", "P-NNO-0261", "P-NNO-0263", "P-NNO-0264", "P-NNO-0266", "P-NNO-0267", "P-NNO-0268",
		        "P-NNO-0269", "P-NNO-0270", "P-NNO-0271", "P-NNO-0272", "P-NNO-0273", "P-NNO-0274", "P-NNO-0275",
		        "P-NNO-0276", "P-NNO-0277", "P-NNO-0278", "P-NNO-0279", "P-NNO-0280", "P-NNO-0281", "P-NNO-0282",
		        "P-NNO-0283", "P-NNO-0284", "P-NNO-0285", "P-NNO-0286", "P-NNO-0287", "P-NNO-0288", "P-NNO-0289",
		        "P-NNO-0290", "P-NNO-0291", "P-NNO-0292", "P-NNO-0293", "P-NNO-0294", "P-NNO-0295", "P-NNO-0296",
		        "P-NNO-0297", "P-NNO-0298", "P-NNO-0299", "P-NNO-0300", "P-NNO-0301", "P-NNO-0302", "P-NNO-0303",
		        "P-NNO-0304", "P-NNO-0305", "P-NNO-0307", "P-NNO-0308", "P-NNO-0310", "P-NNO-0311", "P-NNO-0312",
		        "P-NNO-0314", "P-NNO-0315", "P-NNO-0317", "P-NNO-0318", "P-NNO-0319", "P-NNO-0320", "P-NNO-0321",
		        "P-NNO-0322", "P-NNO-0323", "P-NNO-0324", "P-NNO-0325", "P-NNO-0326", "P-NNO-0327", "P-NNO-0328",
		        "P-NNO-0329", "P-NNO-0330", "P-NNO-0331", "P-NNO-0332", "P-NNO-0333", "P-NNO-0334", "P-NNO-0337",
		        "P-NNO-0338", "P-NNO-0339", "P-NNO-0340", "P-NNO-0342", "P-NNO-0343", "P-NNO-0344", "P-NNO-0345",
		        "P-NNO-0346", "P-NNO-0347", "P-NNO-0348", "P-NNO-0349", "P-NNO-0350", "P-NNO-0351", "P-NNO-0352",
		        "P-NNO-0353", "P-NNO-0354", "P-NNO-0356", "P-NNO-0357", "P-NNO-0358", "P-NNO-0360", "P-NNO-0361",
		        "P-NNO-0362", "P-NNO-0365", "P-NNO-0366", "P-NNO-0367", "P-NNO-0368", "P-NNO-0369", "P-NNO-0370",
		        "P-NNO-0371", "P-NNO-0372", "P-NNO-0373", "P-NNO-0374", "P-NNO-0375", "P-NNO-0376", "P-NNO-0377",
		        "P-NNO-0378", "P-NNO-0380", "P-NNO-0381", "P-NNO-0382", "P-NNO-0383", "P-NNO-0384", "P-NNO-0385",
		        "P-NNO-0386", "P-NNO-0387", "P-NNO-0388", "P-NNO-0389", "P-NNO-0390", "P-NNO-0391", "P-NNO-0392",
		        "P-NNO-0393", "P-NNO-0394", "P-NNO-0395", "P-NNO-0397", "P-NNO-0398", "P-NNO-0399", "P-NNO-0401",
		        "P-NNO-0402", "P-NNO-0403", "P-NNO-0404", "P-NNO-0406", "P-NNO-0407", "P-NNO-0408", "P-NNO-0409",
		        "P-NNO-0410", "P-NNO-0411", "P-NNO-0412", "P-NNO-0413", "P-NNO-0416", "P-NNO-0417", "P-NNO-0418",
		        "P-NNO-0420", "P-NNO-0421", "P-NNO-0422", "P-NNO-0423", "P-NNO-0424", "P-NNO-0426", "P-NNO-0427",
		        "P-NNO-0428", "P-NNO-0429", "P-NNO-0430", "P-NNO-0431", "P-NNO-0432", "P-NNO-0433", "P-NNO-0434",
		        "P-NNO-0435", "P-NNO-0436", "P-NNO-0437", "P-NNO-0438", "P-NNO-0439", "P-NNO-0440", "P-NNO-0441",
		        "P-NNO-0442", "P-NNO-0443", "P-NNO-0444", "P-NNO-0445", "P-NNO-0448", "P-NNO-0449", "P-NNO-0450",
		        "P-NNO-0452", "P-NNO-0453", "P-NNO-0454", "P-NNO-0455", "P-NNO-0456", "P-NNO-0458", "P-NNO-0459",
		        "P-NNO-0460", "P-NNO-0461", "P-NNO-0462", "P-NNO-0463", "P-NNO-0464", "P-NNO-0465", "P-NNO-0466",
		        "P-NNO-0467", "P-NNO-0468", "P-NNO-0469", "P-NNO-0470", "P-NNO-0471", "P-NNO-0472", "P-NNO-0473",
		        "P-NNO-0474", "P-NNO-0475", "P-NNO-0476", "P-NNO-0477", "P-NNO-0479", "P-NNO-0481", "P-NNO-0483",
		        "P-NNO-0484", "P-NNO-0487", "P-NNO-0488", "P-NNO-0489", "P-NNO-0490", "P-NNO-0491", "P-NNO-0494",
		        "P-NNO-0495", "P-NNO-0496", "P-NNO-0498", "P-NNO-0499", "P-NNO-0500", "P-NNO-0501", "P-NNO-0502",
		        "P-NNO-0504", "P-NNO-0505", "P-NNO-0506", "P-NNO-0508", "P-NNO-0509", "P-NNO-0510", "P-NNO-0511",
		        "P-NNO-0512", "P-NNO-0513", "P-NNO-0514", "P-NNO-0516", "P-NNO-0517", "P-NNO-0518", "P-NNO-0519",
		        "P-NNO-0520", "P-NNO-0521", "P-NNO-0523", "P-NNO-0524", "P-NNO-0525", "P-NNO-0526", "P-NNO-0527",
		        "P-NNO-0528", "P-NNO-0529", "P-NNO-0530", "P-NNO-0532", "P-NNO-0533", "P-NNO-0534", "P-NNO-0535",
		        "P-NNO-0537", "P-NNO-0538", "P-NNO-0539", "P-NNO-0540", "P-NNO-0541", "P-NNO-0542", "P-NNO-0543",
		        "P-NNO-0545", "P-NNO-0546", "P-NNO-0547", "P-NNO-0548", "P-NNO-0549", "P-NNO-0550", "P-NNO-0551",
		        "P-NNO-0552", "P-NNO-0553", "P-NNO-0554", "P-NNO-0555", "P-NNO-0556", "P-NNO-0557", "P-NNO-0559",
		        "P-NNO-0561", "P-NNO-0562", "P-NNO-0564", "P-NNO-0565", "P-NNO-0566", "P-NNO-0567", "P-NNO-0568",
		        "P-NNO-0569", "P-NNO-0570", "P-NNO-0571", "P-NNO-0572", "P-NNO-0573", "P-NNO-0574", "P-NNO-0575",
		        "P-NNO-0576", "P-NNO-0577", "P-NNO-0579", "P-NNO-0581", "P-NNO-0582", "P-NNO-0583", "P-NNO-0584",
		        "P-NNO-0586", "P-NNO-0587", "P-NNO-0588", "P-NNO-0589", "P-NNO-0590", "P-NNO-0591", "P-NNO-0593",
		        "P-NNO-0594", "P-NNO-0595", "P-NNO-0596", "P-NNO-0597", "P-NNO-0599", "P-NNO-0600", "P-NNO-0601",
		        "P-NNO-0602", "P-NNO-0603", "P-NNO-0604", "P-NNO-0606", "P-NNO-0607", "P-NNO-0608", "P-NNO-0609",
		        "P-NNO-0611", "P-NNO-0612", "P-NNO-0613", "P-NNO-0614", "P-NNO-0615", "P-NNO-0616", "P-NNO-0617",
		        "P-NNO-0618", "P-NNO-0619", "P-NNO-0620", "P-NNO-0621", "P-NNO-0623", "P-NNO-0624", "P-NNO-0625",
		        "P-NNO-0626", "P-NNO-0627", "P-NNO-0628", "P-NNO-0629", "P-NNO-0631", "P-NNO-0632", "P-NNO-0633",
		        "P-NNO-0634", "P-NNO-0636", "P-NNO-0637", "P-NNO-0638", "P-NNO-0639", "P-NNO-0640", "P-NNO-0641",
		        "P-NNO-0642", "P-NNO-0643", "P-NNO-0644", "P-NNO-0646", "P-NNO-0647", "P-NNO-0648", "P-NNO-0649",
		        "P-NNO-0650", "P-NNO-0651", "P-NNO-0652", "P-NNO-0653", "P-NNO-0654", "P-NNO-0657", "P-NNO-0658",
		        "P-NNO-0659", "P-NNO-0660", "P-NNO-0661", "P-NNO-0662", "P-NNO-0663", "P-NNO-0664", "P-NNO-0665",
		        "P-NNO-0667", "P-NNO-0668", "P-NNO-0669", "P-NNO-0670", "P-NNO-0671", "P-NNO-0672", "P-NNO-0673",
		        "P-NNO-0674", "P-NNO-0675", "P-NNO-0677", "P-NNO-0678", "P-NNO-0679", "P-NNO-0683", "P-NNO-0685",
		        "P-NNO-0686", "P-NNO-0687", "P-NNO-0688", "P-NNO-0689", "P-NNO-0690", "P-NNO-0692", "P-NNO-0693",
		        "P-NNO-0694", "P-NNO-0695", "P-NNO-0696", "P-NNO-0697", "P-NNO-0698", "P-NNO-0699", "P-NNO-0700",
		        "P-NNO-0701", "P-NNO-0702", "P-NNO-0704", "P-NNO-0705", "P-NNO-0707", "P-NNO-0708", "P-NNO-0709",
		        "P-NNO-0710", "P-NNO-0711", "P-NNO-0712", "P-NNO-0713", "P-NNO-0714", "P-NNO-0715", "P-NNO-0716",
		        "P-NNO-0717", "P-NNO-0718", "P-NNO-0719", "P-NNO-0720", "P-NNO-0721", "P-NNO-0722", "P-NNO-0724",
		        "P-NNO-0725", "P-NNO-0726", "P-NNO-0727", "P-NNO-0728", "P-NNO-0729", "P-NNO-0730", "P-NNO-0731",
		        "P-NNO-0733", "P-NNO-0734", "P-NNO-0735", "P-NNO-0736", "P-NNO-0737", "P-NNO-0738", "P-NNO-0740",
		        "P-NNO-0741", "P-NNO-0742", "P-NNO-0743", "P-NNO-0744", "P-NNO-0745", "P-NNO-0746", "P-NNO-0749",
		        "P-NNO-0752", "P-NNO-0753", "P-NNO-0755", "P-NNO-0756", "P-NNO-0757", "P-NNO-0759", "P-NNO-0761",
		        "P-NNO-0762", "P-NNO-0763", "P-NNO-0767", "P-NNO-0768", "P-NNO-0769", "P-NNO-0771", "P-NNO-0772",
		        "P-NNO-0773", "P-NNO-0774", "P-NNO-0775", "P-NNO-0776", "P-NNO-0777", "P-NNO-0778", "P-NNO-0779",
		        "P-NNO-0780", "P-NNO-0781", "P-NNO-0782", "P-NNO-0784", "P-NNO-0785", "P-NNO-0786", "P-NNO-0788",
		        "P-NNO-0789", "P-NNO-0790", "P-NNO-0791", "P-NNO-0792", "P-NNO-0793", "P-NNO-0794", "P-NNO-0795",
		        "P-NNO-0796", "P-NNO-0798", "P-NNO-0799", "P-NNO-0800", "P-NNO-0803", "P-NNO-0806", "P-NNO-0807",
		        "P-NNO-0808", "P-NNO-0809", "P-NNO-0810", "P-NNO-0811", "P-NNO-0814", "P-NNO-0816", "P-NNO-0817",
		        "P-NNO-0818", "P-NNO-0819", "P-NNO-0820", "P-NNO-0821", "P-NNO-0824", "P-NNO-0825", "P-NNO-0828",
		        "P-NNO-0830", "P-NNO-0831", "P-NNO-0832", "P-NNO-0833", "P-NNO-0834", "P-NNO-0835", "P-NNO-0836",
		        "P-NNO-0841", "P-NNO-0842", "P-NNO-0843", "P-NNO-0845", "P-NNO-0846", "P-NNO-0847", "P-NNO-0848",
		        "P-NNO-0849", "P-NNO-0850", "P-NNO-0852", "P-NNO-0853", "P-NNO-0854", "P-NNO-0856", "P-NNO-0857",
		        "P-NNO-0858", "P-NNO-0859", "P-NNO-0860", "P-NNO-0861", "P-NNO-0862", "P-NNO-0863", "P-NNO-0864",
		        "P-NNO-0866", "P-NNO-0868", "P-NNO-0869", "P-NNO-0870", "P-NNO-0872", "P-NNO-0874", "P-NNO-0877",
		        "P-NNO-0878", "P-NNO-0879", "P-NNO-0880", "P-NNO-0882", "P-NNO-0885", "P-NNO-0886", "P-NNO-0887",
		        "P-NNO-0888", "P-NNO-0890", "P-NNO-0891", "P-NNO-0894", "P-NNO-0895", "P-NNO-0896", "P-NNO-0897",
		        "P-NNO-0898", "P-NNO-0899", "P-NNO-0900", "P-NNO-0901", "P-NNO-0903", "P-NNO-0906", "P-NNO-0908",
		        "P-NNO-0909", "P-NNO-0915", "P-NNO-0916", "P-NNO-0918", "P-NNO-0919", "P-NNO-0920", "P-NNO-0921",
		        "P-NNO-0925", "P-NNO-0927", "P-NNO-0931", "P-NNO-0934", "P-NNO-0937", "P-NNO-0938", "P-NNO-0939",
		        "P-NNO-0940", "P-NNO-0941", "P-NNO-0944", "P-NNO-0946", "P-NNO-0947", "P-NNO-0948", "P-NNO-0949",
		        "P-NNO-0950", "P-NNO-0951", "P-NNO-0952", "P-NNO-0956", "P-NNO-0957", "P-NNO-0958", "P-NNO-0959",
		        "P-NNO-0960", "P-NNO-0961", "P-NNO-0962", "P-NNO-0963", "P-NNO-0964", "P-NNO-0966", "P-NNO-0967",
		        "P-NNO-0968", "P-NNO-0970", "P-NNO-0971", "P-NNO-0972", "P-NNO-0974", "P-NNO-0975", "P-NNO-0976",
		        "P-NNO-0977", "P-NNO-0978", "P-NNO-0979", "P-NNO-0980", "P-NNO-0982", "P-NNO-0983", "P-NNO-0984",
		        "P-NNO-0985", "P-NNO-0986", "P-NNO-0987", "P-NNO-0988", "P-NNO-0992", "P-NNO-0994", "P-NNO-0996",
		        "P-NNO-0997", "P-NNO-1002", "P-NNO-1005", "P-NNO-1006", "P-NNO-1008", "P-NNO-1010", "P-NNO-1012",
		        "P-NNO-1014", "P-NNO-1016", "P-NNO-1018", "P-NNO-1019", "P-NNO-1021", "P-NNO-1022", "P-NNO-1024",
		        "P-NNO-1025", "P-NNO-1026", "P-NNO-1027", "P-NNO-1029", "P-NNO-1030", "P-NNO-1032", "P-NNO-1033",
		        "P-NNO-1034", "P-NNO-1035", "P-NNO-1036", "P-NNO-1038", "P-NNO-1040", "P-NNO-1042", "P-NNO-1044",
		        "P-NNO-1046", "P-NNO-1047", "P-NNO-1048", "P-NNO-1050", "P-NNO-1051", "P-NNO-1052", "P-NNO-1054",
		        "P-NNO-1056", "P-NNO-1057", "P-NNO-1058", "P-NNO-1059", "P-NNO-1067", "P-NNO-1068", "P-NNO-1069",
		        "P-NNO-1070", "P-NNO-1071", "P-NNO-1073", "P-NNO-1074", "P-NNO-1075", "P-NNO-1076", "P-NNO-1077",
		        "P-NNO-1078", "P-NNO-1079", "P-NNO-1080", "P-NNO-1081", "P-NNO-1082", "P-NNO-1083", "P-NNO-1084",
		        "P-NNO-1085", "P-NNO-1089", "P-NNO-1090", "P-NNO-1094", "P-NNO-1095", "P-NNO-1097", "P-NNO-1100",
		        "P-NNO-1102", "P-NNO-1103", "P-NNO-1104", "P-NNO-1105", "P-NNO-1107", "P-NNO-1109", "P-NNO-1110",
		        "P-NNO-1111", "P-NNO-1112", "P-NNO-1113", "P-NNO-1117", "P-NNO-1119", "P-NNO-1123", "P-NNO-1124",
		        "P-NNO-1125", "P-NNO-1126", "P-NNO-1127", "P-NNO-1128", "P-NNO-1129", "P-NNO-1130", "P-NNO-1132",
		        "P-NNO-1134", "P-NNO-1135", "P-NNO-1136", "P-NNO-1137", "P-NNO-1140", "P-NNO-1143", "P-NNO-1144",
		        "P-NNO-1145", "P-NNO-1146", "P-NNO-1147", "P-NNO-1151", "P-NNO-1156", "P-NNO-1157", "P-NNO-1159",
		        "P-NNO-1164", "P-NNO-1165", "P-NNO-1166", "P-NNO-1167", "P-NNO-1170", "P-NNO-1171", "P-NNO-1172",
		        "P-NNO-1173", "P-NNO-1174", "P-NNO-1176", "P-NNO-1179", "P-NNO-1181", "P-NNO-1183", "P-NNO-1184",
		        "P-NNO-1188", "P-NNO-1189", "P-NNO-1190", "P-NNO-1192", "P-NNO-1193", "P-NNO-1194", "P-NNO-1195",
		        "P-NNO-1196", "P-NNO-1199", "P-NNO-1200", "P-NNO-1201", "P-NNO-1203", "P-NNO-1204", "P-NNO-1205",
		        "P-NNO-1206", "P-NNO-1208", "P-NNO-1210", "P-NNO-1211", "P-NNO-1220", "P-NNO-1222", "P-NNO-1223",
		        "P-NNO-1224", "P-NNO-1228", "P-NNO-1230", "P-NNO-1231", "P-NNO-1232", "P-NNO-1234", "P-NNO-1235",
		        "P-NNO-1236", "P-NNO-1237", "P-NNO-1240", "P-NNO-1245", "P-NNO-1248", "P-NNO-1251", "P-NNO-1252",
		        "P-NNO-1253", "P-NNO-1254", "P-NNO-1255", "P-NNO-1257", "P-NNO-1258", "P-NNO-1261", "P-NNO-1264",
		        "P-NNO-1266", "P-NNO-1268", "P-NNO-1269", "P-NNO-1272", "P-NNO-1273", "P-NNO-1275", "P-NNO-1278",
		        "P-NNO-1283", "P-NNO-1285", "P-NNO-1289", "P-NNO-1291", "P-NNO-1294", "P-NNO-1296", "P-NNO-1298",
		        "P-NNO-1299", "P-NNO-1304", "P-NNO-1307", "P-NNO-1308", "P-NNO-1313", "P-NNO-1317", "P-NNO-1320",
		        "P-NNO-1323", "P-NNO-1324", "P-NNO-1325", "P-NNO-1330", "P-NNO-1334", "P-NNO-1338", "P-NNO-1339",
		        "P-NNO-1340", "P-NNO-1343", "P-NNO-1345", "P-NNO-1347", "P-NNO-1348", "P-NNO-1353", "P-NNO-1357",
		        "P-NNO-1358", "P-NNO-1360", "P-NNO-1362", "P-NNO-1364", "P-NNO-1367", "P-NNO-1382", "P-NNO-1387",
		        "P-NNO-1389", "P-NNO-1392", "P-NNO-1400", "P-NNO-1401", "P-NNO-1402", "P-NNO-1416", "P-NNO-1419",
		        "P-NNO-1429", "P-NNO-1430", "P-NNO-1433", "P-NNO-1434", "P-NNO-1439", "P-NNO-1441", "P-NNO-1443",
		        "P-NNO-1445", "P-NNO-1447", "P-NNO-1449", "P-NNO-1453", "P-NNO-1456", "P-NNO-1461", "P-NNO-1462",
		        "P-NNO-1469", "P-NNO-1470", "P-NNO-1471", "P-NNO-1475", "P-NNO-1476", "P-NNO-1477", "P-NNO-1479",
		        "P-NNO-1485", "P-NNO-1490", "P-NNO-1491", "P-NNO-1492", "P-NNO-1497", "P-NNO-1498", "P-NNO-1499",
		        "P-NNO-1500", "P-NNO-1501", "P-NNO-1502", "P-NNO-1503" };
		
		for (int i = 0; i < preArtIds.length; i++) {
			if ((i % 50) == 0) {
				Context.closeSession();
				Context.openSession();
				Context.authenticate(USER, PW);
			}
			mark(preArtIds[i]);
		}
	}
	
	public void mark(String preArtId) throws Exception {
		EncounterService es = Context.getEncounterService();
		PatientService ps = Context.getPatientService();
		
		List<Patient> patients = ps.getPatients(null, preArtId,
		    Arrays.asList(ps.getPatientIdentifierTypeByName("PART Number")), false);
		if (patients.size() != 1) {
			log.warn("No exact match for \t" + preArtId);
			System.out.println("No exact match for \t" + preArtId);
			return;
		}
		Patient p = patients.get(0);
		if (p.isDead()) {
			log.warn("Patient died \t" + preArtId);
			System.out.println("Patient died \t" + preArtId);
			return;
		}
		if (p.getPatientIdentifier("ARV Number") != null) {
			log.warn("Already on ART for " + preArtId);
			System.out.println("Already on ART for\t " + preArtId);
			return;
		}
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		List<Encounter> encounters = es.getEncounters(p, null, c.getTime(), null, null, null, null, false);
		if (!encounters.isEmpty()) {
				log.warn("Encounter in the last 6 months and not on ART for \t" + preArtId);
				System.out.println("Enocunter in the last 6 months and not on ART for \t" + preArtId);
				return;
		}
		c.add(Calendar.MONTH, -6);
		encounters = es.getEncounters(p, null, c.getTime(), null, null, null, null, false);
		if (!encounters.isEmpty()) {
				log.warn("Encounter in the last 12 months and not on ART for \t" + preArtId);
				System.out.println("Enocunter in the last 12 months and not on ART for \t" + preArtId);
				return;
		}
		c.add(Calendar.MONTH, -6);
		encounters = es.getEncounters(p, null, c.getTime(), null, null, null, null, false);
		if (!encounters.isEmpty()) {
				log.warn("Encounter in the last 18 months and not on ART for \t" + preArtId);
				System.out.println("Enocunter in the last 18 months and not on ART for \t" + preArtId);
				return;
		}
		encounters = es.getEncounters(p, null, null, null, null, null, null, false);
		if (encounters.isEmpty()) {
				log.warn("Seems like no encounter ever and not on ART for \t" + preArtId);
				System.out.println("Seems like no encounter ever and not on ART for \t" + preArtId);
				return;
		}
		log.warn("\tInactive\t" + preArtId);
		System.out.println("Inactive\t" + preArtId);
		//		Encounter e = new Encounter();
		//		e.setProvider(Context.getUserService().getUser(16576));
		//		e.setEncounterType(es.getEncounterType("ADMINISTRATION"));
		//		
		//		es.saveEncounter(e);
	}
	
}
