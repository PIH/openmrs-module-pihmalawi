package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.pihmalawi.models.YendaNafePatientRegistrationModel;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + YendaNafePatientRestController.PIHMALAWI + YendaNafePatientRestController.YENDANAFE+YendaNafePatientRestController.PATIENT)
public class YendaNafePatientRestController {

    public static final String PIHMALAWI = "/pihmalawi";
    public static final String YENDANAFE = "/yendanafe";
    public static final String PATIENT = "/patient";

    public final String personUri = "http://localhost:8080/openmrs/ws/rest/v1/person";

    @Autowired
    private PatientService patientService;

    @Autowired
    private PersonService personService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;
    private Date date = new Date();

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object getScreeningData(
            @RequestBody YendaNafePatientRegistrationModel body) {

        List<SimpleObject> results = new ArrayList<SimpleObject>();
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.add("hello","Matiya");


/*
        //User user = userService.getUserByUuid(body.get("created_by"));
        //Date date = new Date();

        String fullName = body.name;
        String[] nameSplitted = fullName.split("\\s+");
        String firstName = nameSplitted[0];
        String lastName = nameSplitted[1];

        JsonObject newPerson = new JsonObject();

        JsonObject personname = new JsonObject();
        personname.put("givenName",firstName);
        personname.put("familyName",lastName);

        JSONArray names = new JSONArray(personname);
        newPerson.put("names",names);
        newPerson.put("gender",body.sex);
        newPerson.put("birthdate","2020-08-08");

        JSONArray personAddresses = new JSONArray();

        JsonObject personAddress = new JsonObject();
        personAddress.put("address1","");
        personAddress.put("country","Malawi");
       // personAddress.setCountyDistrict(body.get("district"));
        personAddress.put("cityVillage",body.village);
        personAddress.put("postalCode","");
        personAddresses.put(personAddress);
        newPerson.put("addresses",personAddresses);

        RestTemplate restTemplate = new RestTemplate();

        // create auth credentials
        //String authStr = "username:password";
        String authStr = "kmatiya:zxqw1234@@##";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        // create request
        HttpEntity request = new HttpEntity(newPerson,headers);


        // make a request
        ResponseEntity<String> response = new RestTemplate().exchange(personUri, HttpMethod.POST, request, String.class);

        // get JSON response
        String json = response.getBody();

        SimpleObject simpleObject = new SimpleObject();
        simpleObject.add("response",json);
        results.add(simpleObject); */
        return results;

       /* Location patient_location = locationService.getLocationByUuid(body.get("location"));
        PatientIdentifierType patientIdentifierType =  patientService.getPatientIdentifierTypeByUuid(PihMalawiPatientIdentifierTypes.YENDANAFE_IDENTIFIER.uuid());
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        SortedSet<PatientIdentifier> patientIdentifiers = new TreeSet<PatientIdentifier>();
        patientIdentifier.setIdentifierType(patientIdentifierType);
        patientIdentifier.setLocation(patient_location);
        patientIdentifier.setPreferred(true);
        patientIdentifier.setDateCreated(date);
        patientIdentifiers.add(patientIdentifier);
        Patient newPatient = new Patient();
        newPatient.setPersonId(savedPerson.getPersonId());
        newPatient.setIdentifiers(patientIdentifiers);
        newPatient.setBirthdate(new Date(body.get("dateOfBirth")));
        Patient savedPatient = patientService.savePatient(newPatient);

        SimpleObject registeredPatient = new SimpleObject();
        registeredPatient.add("uuid",savedPatient.getUuid());
        results.add(registeredPatient);

        return results;
        SimpleObject simpleObject = new SimpleObject();
        simpleObject.add("input",body);
        results.add(simpleObject);
        return results; */
    }
}
