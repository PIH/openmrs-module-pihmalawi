package org.openmrs.module.pihmalawi.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.page.PageModel;

/**
 * Test Page Controller
 */
public class TestPageController {

    public void get(PageModel model) throws Exception {
        model.addAttribute("locations", Context.getLocationService().getAllLocations());
    }
}
