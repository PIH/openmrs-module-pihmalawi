package org.openmrs.module.pihmalawi.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IC3Service extends OpenmrsService{

    SimpleObject getIC3AppointmentData(String locationUuid, String endDate);
}
