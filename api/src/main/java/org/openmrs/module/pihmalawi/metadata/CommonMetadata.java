/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonMetadata extends Metadata {

	public static String APPOINTMENT_DATE = "Appointment date";

	public Concept getAppointmentDateConcept() {
		return getConcept(APPOINTMENT_DATE);
	}

	public static String NENO_HOSPITAL = "Neno District Hospital";
	public static String OUTPATIENT_LOCATION = "Outpatient";
	public static String REGISTRATION_LOCATION = "Registration";
	public static String VITALS_LOCATION = "Vitals";
	public static String MAGALETA_HC  = "Magaleta HC";
	public static String NSAMBE_HC  = "Nsambe HC";
	public static String LIGOWE_HC  = "Ligowe HC";
	public static String MATANDANI_HC = "Matandani Rural Health Center";
	public static String NENO_MISSION_HC = "Neno Mission HC";
	public static String LUWANI_HC = "Luwani RHC";
	public static String LISUNGWI_HOSPITAL = "Lisungwi Community Hospital";
	public static String MIDZEMBA_HC = "Midzemba HC";
	public static String CHIFUNGA_HC = "Chifunga HC";
	public static String MATOPE_HC = "Matope HC";
	public static String NKHULA_FALLS_HC = "Nkhula Falls RHC";
	public static String ZALEWA_HC = "Zalewa HC";

	public Location getNenoHospital() {
		return getLocation(NENO_HOSPITAL);
	}

	public Location getOutpatientLocation() {
		return getLocation(OUTPATIENT_LOCATION);
	}

	public Location getRegistrationLocation() {
		return getLocation(REGISTRATION_LOCATION);
	}

	public Location getVitalsLocation() {
		return getLocation(VITALS_LOCATION);
	}

	public Location getMagaletaHc() {
		return getLocation(MAGALETA_HC);
	}

	public Location getNsambeHc() {
		return getLocation(NSAMBE_HC);
	}

	public Location getLigoweHc() {
		return getLocation(LIGOWE_HC);
	}

	public Location getMatandaniHc() {
		return getLocation(MATANDANI_HC);
	}

	public Location getNenoMissionHc() {
		return getLocation(NENO_MISSION_HC);
	}

	public Location getLuwaniHc() {
		return getLocation(LUWANI_HC);
	}

	public Location getLisungwiHospital() {
		return getLocation(LISUNGWI_HOSPITAL);
	}

	public Location getMidzembaHc() {
		return getLocation(MIDZEMBA_HC);
	}

	public Location getChifungaHc() {
		return getLocation(CHIFUNGA_HC);
	}

	public Location getMatopeHc() {
		return getLocation(MATOPE_HC);
	}

	public Location getNkhulaFallsHc() {
		return getLocation(NKHULA_FALLS_HC);
	}

	public Location getZalewaHc() {
		return getLocation(ZALEWA_HC);
	}

	public List<Location> getPrimaryFacilities() {
		List<Location> l = new ArrayList<Location>();
		l.add(getNenoHospital());
		l.add(getMagaletaHc());
		l.add(getNsambeHc());
		l.add(getNenoMissionHc());
		l.add(getLigoweHc());
		l.add(getMatandaniHc());
		l.add(getLuwaniHc());
		l.add(getLisungwiHospital());
		l.add(getMatopeHc());
		l.add(getChifungaHc());
		l.add(getZalewaHc());
		l.add(getMidzembaHc());
		l.add(getNkhulaFallsHc());
		return l;
	}

	public List<Location> getAllLocations(Location primaryFacility) {
		List<Location> l = new ArrayList<Location>();
		l.add(primaryFacility);
		if (primaryFacility.equals(getNenoHospital())) {
			l.add(getOutpatientLocation());
			l.add(getRegistrationLocation());
			l.add(getVitalsLocation());
		}
		else if (primaryFacility.equals(getLisungwiHospital())) {
			l.add(getMidzembaHc());
		}
		return l;
	}
}