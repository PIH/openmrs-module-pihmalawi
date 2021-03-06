package org.openmrs.module.pihmalawi.rest.controller;


import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.pihmalawi.patient.YendaNafePatientRegistrationService;
import org.openmrs.module.pihmalawi.patient.ExtendedPatientService;
import org.openmrs.module.pihmalawi.validator.YendaNafePatientRegistrationValidator;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *  End point will handle adding patients in the EMR from medic mobile
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + YendaNafePatientRestController.PIHMALAWI + YendaNafePatientRestController.YENDANAFE+YendaNafePatientRestController.PATIENT)
public class YendaNafePatientRestController {

    public static final String PIHMALAWI = "/pihmalawi";
    public static final String YENDANAFE = "/yendanafe";
    public static final String PATIENT = "/patient";

    public static final String YENDANAFEUIID ="23ca7da8-362d-4cf5-abd5-a40221d60da1";

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private YendaNafePatientRegistrationValidator yendaNafePatientRegistrationValidator;

    @Autowired
    private ExtendedPatientService extendedPatientService;

    @Autowired
    private YendaNafePatientRegistrationService yendaNafePatientRegistrationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object createPatient(
            @RequestBody YendaNafePatientRegistrationModel body) {

        List<SimpleObject> results = new ArrayList<SimpleObject>();

        String validationResult = yendaNafePatientRegistrationValidator.validateRegistrationModel(body, extendedPatientService,locationService);

        if(!validationResult.equals(""))
        {
            return new ResponseEntity<String>(validationResult, HttpStatus.BAD_REQUEST);
        }
        try{

            Patient newPatient = yendaNafePatientRegistrationService.createPatient(body,YENDANAFEUIID,personService,patientService,userService,locationService, PihMalawiPatientIdentifierTypes.YENDANAFE_IDENTIFIER.uuid());
            SimpleObject registeredPatient = new SimpleObject();
            registeredPatient.add("uuid",newPatient.getUuid());
            registeredPatient.add("Name",newPatient.getGivenName()+" "+ newPatient.getFamilyName());

            return new ResponseEntity<SimpleObject>(registeredPatient, HttpStatus.OK);

        }
        catch (Exception ex)
        {
            SimpleObject simpleObject = new SimpleObject();
            simpleObject.add("error",ex.getMessage());
            simpleObject.add("stacktrace",ex.getStackTrace());
            return new ResponseEntity<SimpleObject>(simpleObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
