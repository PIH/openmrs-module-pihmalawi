package org.openmrs.module.pihmalawi.rest.controller;


import org.openmrs.module.pihmalawi.api.IC3Service;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/pihmalawi" + IC3RestController.IC3_REST_NAMESPACE)
public class IC3RestController {

    public static final String IC3_REST_NAMESPACE = "/ic3";

    @Autowired
    private IC3Service ic3Service;

   @RequestMapping(method = RequestMethod.GET)
   @ResponseBody
   public Object get(HttpServletRequest request, HttpServletResponse response) {
       RequestContext requestContext = RestUtil.getRequestContext(request, response);
       Representation rep = requestContext.getRepresentation();

       String location = requestContext.getParameter("location");
       String endDate = requestContext.getParameter("endDate");
       return ic3Service.getIC3AppointmentData(location, endDate);
   }

}
