package org.openmrs.module.pihmalawi.page.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.patient.ExtendedEncounterService;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VoidedEncountersPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -6);
        List<Encounter> voidedEncounters = Context.getService(ExtendedEncounterService.class).getDeletedEncountersByVoidReason(cal.getTime(), new Date(), "system deleting duplicate encounters");
        model.addAttribute("voidedEncounters", voidedEncounters);
    }

    public void post(@RequestParam(value = "from-date-field", required = false) Date fromDate,
                     @RequestParam(value = "to-date-field", required = false) Date toDate,
                     PageModel model,
                     PageRequest pageRequest) {

        List<Encounter> voidedEncounters = Context.getService(ExtendedEncounterService.class).getDeletedEncountersByVoidReason(fromDate, toDate, "system deleting duplicate encounters");

        model.addAttribute("voidedEncounters", voidedEncounters);
        if (fromDate != null) {
            model.addAttribute("fromDate", fromDate);
        }

    }
}
