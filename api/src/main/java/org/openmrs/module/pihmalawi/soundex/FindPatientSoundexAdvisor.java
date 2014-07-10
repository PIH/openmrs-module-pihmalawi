/**
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
package org.openmrs.module.pihmalawi.soundex;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.namephonetics.NamePhoneticsService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Includes soundex-based search results in the standard patient search, if enabled
 */
public class FindPatientSoundexAdvisor extends StaticMethodMatcherPointcutAdvisor implements Advisor {

	protected final Log log = LogFactory.getLog(getClass());

	public static final String SOUNDEX_PREFIX = "s:";
	public static final String SOUNDEX_SUFFIX = ":s";

	/**
	 * @see StaticMethodMatcherPointcutAdvisor#matches(Method, Class)
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		String methodName = method.getName();
		Class<?>[] types = method.getParameterTypes();
		if (methodName.equals("getPatients") || methodName.equals("getCountOfPatients")) {
			if (types.length == 1 || types.length == 3 && types[0] == String.class) {
				return true; // getPatients(String) and getPatients(String, Integer, Integer) and getCountOfPatients(String)
			}
		}
		return false;
	}

	/**
	 * @see StaticMethodMatcherPointcutAdvisor#getAdvice()
	 */
	@Override
	public Advice getAdvice() {
		return new PatientSearchAdvice();
	}

	private class PatientSearchAdvice implements MethodInterceptor {

		/**
		 * If the patient search starts or ends with the soundex activation string, do a soundex search,
		 * otherwise simply delegate to the standard patient search
		 */
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String methodName = invocation.getMethod().getName();
			Object[] arguments = invocation.getArguments();
			String query = (String) arguments[0];
			Integer start = 0;
			Integer limit = getMaximumSearchResults();
			if (arguments.length == 3) {
				start = (Integer)arguments[1];
				limit = (Integer)arguments[2];
			}

			if (query != null) {
				query = query.trim();
				if (query.toLowerCase().startsWith(SOUNDEX_PREFIX)) {
					query = query.substring(SOUNDEX_PREFIX.length());
					if (methodName.equals("getPatients")) {
						return getPatients(query, start, limit);
					}
					else {
						return getCountOfPatients(query);
					}
				}
				else if (query.toLowerCase().endsWith(SOUNDEX_SUFFIX)) {
					query = query.substring(0, query.length() - SOUNDEX_SUFFIX.length());
					if (methodName.equals("getPatients")) {
						return getPatients(query, start, limit);
					}
					else {
						return getCountOfPatients(query);
					}
				}
			}

			return invocation.proceed();
		}

		/**
		 * Current implementation only will search for a maximum of 2 names, separated by spaces
		 * If two names are entered, it will assume they represent "givenName familyName"
		 */
		protected List<Patient> getPatients(String searchString, Integer start, Integer limit) {

			long startTime = System.currentTimeMillis();

			// Get all matching person names for the search string
			Set<PersonName> matchingNames = Context.getService(NamePhoneticsService.class).getMatchingPersonNames(searchString);

			// Retrieve the unique list of persons for these names, and sort them by name
			Map<String, Integer> sortedPersonIds = new TreeMap<String, Integer>();
			for (PersonName pn : matchingNames) {
				if (!sortedPersonIds.values().contains(pn.getPerson().getPersonId())) {
					PersonName preferredName = pn.getPerson().getPersonName();
					String fullName = (preferredName == null ? pn.getFullName() : preferredName.getFullName());
					Integer personId = pn.getPerson().getPersonId();
					sortedPersonIds.put(fullName + "|" + personId, personId);
				}
			}

			// Convert these persons to patients
			List<Patient> patients = Context.getPatientSetService().getPatients(sortedPersonIds.values());

			// Return the specified subset of this list of patients, given the start and limit parameters
			if (start != null || limit != null) {
				start = (start == null) ? 0 : start;
				int end = (limit == null ? patients.size() : start+limit);
				end = (end > patients.size() ? patients.size() : end);
				patients = patients.subList(start, end);
			}

			long endTime = System.currentTimeMillis();
			log.warn("Found " + patients.size() + " results for " + searchString + " with start " + start + " and limit " + limit + " in: " + (endTime-startTime) + " ms.");

			return patients;
		}



		protected int getCountOfPatients(String searchString) {
			return getPatients(searchString, null, null).size();
		}

		/**
		 * Copied from HibernatePersonDAO to retrieve maximum search results
		 */
		protected Integer getMaximumSearchResults() {
			try {
				String gpVal = Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS);
				return Integer.valueOf(gpVal);
			}
			catch (Exception e) {
				log.debug("Mo maximum search results configured, using default value");
			}
			return OpenmrsConstants.GLOBAL_PROPERTY_PERSON_SEARCH_MAX_RESULTS_DEFAULT_VALUE;
		}
	}
}
