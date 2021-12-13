package org.openmrs.module.pihmalawi.page.controller.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihmalawi.Utils;
import org.openmrs.module.pihmalawi.patient.ExtendedPatientService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Calendar;
import java.util.List;

public class ExportPatientsPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                      UiUtils ui,
                      @SpringBean("patientService") PatientService patientService,
                      @SpringBean("extendedPatientService") ExtendedPatientService extendedPatientService,
                      @SpringBean EmrApiProperties emrApiProperties) {

        Calendar calendar = Calendar.getInstance();
        // set to January 1, 2021
        calendar.set(2021, 0, 1, 0, 0, 0);
        List<Patient> patientsChanged = extendedPatientService.getPatientsByDateChanged(calendar.getTime());
        for (Patient patient : patientsChanged) {
            log.info("patient:" + patient.getUuid() + "; " + patient.toString());

        }
        model.addAttribute("patients", patientsChanged);
        model.addAttribute("addressHierarchyLevels", Utils.getAddressHierarchyLevels());

    }
}
