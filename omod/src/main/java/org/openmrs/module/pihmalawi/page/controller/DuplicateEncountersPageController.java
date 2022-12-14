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

public class DuplicateEncountersPageController {

    protected final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model) {
        model.addAttribute("locations", Context.getLocationService().getAllLocations());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -6);
        List<Encounter> duplicateEncounters = Context.getService(ExtendedEncounterService.class).getDuplicateEncounters(cal.getTime(), new Date(), null);
        model.addAttribute("duplicateEncounters", duplicateEncounters);

    }

    public String post(@RequestParam(value = "from-date-field", required = false) Date fromDate,
                       @RequestParam(value = "to-date-field", required = false) Date toDate,
                       PageModel model,
                       PageRequest pageRequest) {

        List<Encounter> duplicateEncounters = Context.getService(ExtendedEncounterService.class).getDuplicateEncounters(fromDate, toDate, null);

        String paramName = pageRequest.getRequest().getParameter("searchDuplicateEncounters");
        if (paramName != null ) {
            log.debug("search for duplicate encounters");
        }
        paramName = pageRequest.getRequest().getParameter("deleteDuplicateEncounters");
        if (paramName != null )  {
            List<Encounter> deletedEncounters = Context.getService(ExtendedEncounterService.class).deleteDuplicateEncounters(duplicateEncounters);
            model.addAttribute("deletedEncounters", deletedEncounters);
            duplicateEncounters = Context.getService(ExtendedEncounterService.class).getDuplicateEncounters(fromDate, toDate, null);
            log.debug("delete duplicate encounters");
        }
        model.addAttribute("duplicateEncounters", duplicateEncounters);
        if (fromDate != null) {
            model.addAttribute("fromDate", fromDate);
        }
        return null;
    }
}
