package org.openmrs.module.pihmalawi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring MVC controller that takes over /index.htm and /login.htm so users would be able to
 * select a location for the session
 */

@Controller
public class HomepageOverrideController {

    @RequestMapping("/index.htm")
    public String showOurHomepage() {
        return "forward:/findPatient.htm";
    }

}
