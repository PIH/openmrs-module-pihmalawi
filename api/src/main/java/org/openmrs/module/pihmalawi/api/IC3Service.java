package org.openmrs.module.pihmalawi.api;

import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
public interface IC3Service extends OpenmrsService{

    List<Map<String, Object>> getIC3AppointmentData(String locationUuid, String endDate, String patientUuid);
}
