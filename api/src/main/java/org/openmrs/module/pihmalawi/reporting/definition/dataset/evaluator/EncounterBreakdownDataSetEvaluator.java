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
package org.openmrs.module.pihmalawi.reporting.definition.dataset.evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.definition.dataset.definition.EncounterBreakdownDataSetDefinition;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates an EncounterBreakdownDataSetDefinition and produces results
 */
@Handler(supports={EncounterBreakdownDataSetDefinition.class})
public class EncounterBreakdownDataSetEvaluator implements DataSetEvaluator {

	protected Log log = LogFactory.getLog(this.getClass());

	@Autowired
	HivMetadata metadata;

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * @throws EvaluationException 
	 * @see DataSetEvaluator#evaluate(DataSetDefinition, EvaluationContext)
	 */
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {
		
		context = ObjectUtil.nvl(context, new EvaluationContext());
		MapDataSet data = new MapDataSet(dataSetDefinition, context);

		EncounterBreakdownDataSetDefinition dsd = (EncounterBreakdownDataSetDefinition) dataSetDefinition;

		// Construct the encounter filters to iterate across
		Map<String, List<EncounterType>> encounterTypeFilters = new LinkedHashMap<String, List<EncounterType>>();
		List<EncounterType> otherEncounterTypes = Context.getEncounterService().getAllEncounterTypes();
		for (int encTypeNum=1; encTypeNum<=dsd.getTypes().size(); encTypeNum++) {
			EncounterType encounterType = dsd.getTypes().get(encTypeNum-1);
			encounterTypeFilters.put("enc"+encTypeNum, Arrays.asList(encounterType));
			otherEncounterTypes.remove(encounterType);
		}
		encounterTypeFilters.put("otherenc", otherEncounterTypes);

		// Construct the user and location filters to iterate across
		Map<String, List<User>> userFilters = new LinkedHashMap<String, List<User>>();
		Map<String, List<Location>> locationFilters = new LinkedHashMap<String, List<Location>>();

		if (dsd.getGrouping() == EncounterBreakdownDataSetDefinition.Grouping.User) {

			// Determine what users to iterate across.  Default to top 10, and "other"
			List<User> mostFrequentUsers = getUsersOrderedByNumEncounters(DateUtil.adjustDate(dsd.getEndDate(), -7*dsd.getNumberOfWeeks(), Calendar.DATE), dsd.getEndDate(), context);
			List<User> otherUsers = Context.getUserService().getAllUsers();

			for (int userNum=1; userNum<=mostFrequentUsers.size() && userNum <= 10; userNum++) {
				User user = mostFrequentUsers.get(userNum - 1);
				String userKey = "user"+userNum;
				userFilters.put(userKey, Arrays.asList(user));
				otherUsers.remove(user);
				data.addData(new DataSetColumn(userKey+"name", userKey+"name", String.class), user.getUsername());
			}
			userFilters.put("userother", otherUsers);
		}
		else {

			List<Location> locations = metadata.getSystemLocations();
			List<Location> otherLocations = Context.getLocationService().getAllLocations();
			for (int locationNum=1; locationNum<=locations.size(); locationNum++) {
				Location location = locations.get(locationNum - 1);
				String locationKey = "loc"+locationNum;
				List<Location> locList = metadata.getAllLocations(location);
				locationFilters.put(locationKey, locList);
				otherLocations.removeAll(locList);
				data.addData(new DataSetColumn(locationKey+"name", locationKey+"name", String.class), location.getName());
			}
			locationFilters.put("locother", otherLocations);

		}

		// Now, iterate across the product of all of these and add them to the data set

		Date startDate, endDate = null;

		for (int weekNum=0; weekNum<dsd.getNumberOfWeeks(); weekNum++) {

			endDate = (endDate == null ? dsd.getEndDate() : DateUtil.adjustDate(endDate, -7, Calendar.DATE));
			startDate = DateUtil.adjustDate(endDate, -6, Calendar.DATE);

			for (String encounterTypeKey : encounterTypeFilters.keySet()) {

				List<EncounterType> encounterTypes = encounterTypeFilters.get(encounterTypeKey);

				for (String userKey : userFilters.keySet()) {
					List<User> users = userFilters.get(userKey);
					String key = userKey + encounterTypeKey + "ago" + weekNum;
					addData(data, key, startDate, endDate, encounterTypes, users, null, context);
				}

				for (String locationKey : locationFilters.keySet()) {
					List<Location> locations = locationFilters.get(locationKey);
					String key = locationKey + encounterTypeKey + "ago" + weekNum;
					addData(data, key, startDate, endDate, encounterTypes, null, locations, context);
				}
			}
		}

		return data;
	}

	public List<User> getUsersOrderedByNumEncounters(Date startDate, Date endDate, EvaluationContext context) {
		List<User> ret = new ArrayList<User>();
		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("e.creator, count(*)");
		qb.from(Encounter.class, "e");
		qb.whereGreaterOrEqualTo("e.dateCreated", startDate);
		qb.whereLessOrEqualTo("e.dateCreated", endDate);
		qb.groupBy("e.creator");
		qb.orderDesc("count(*)");
		for (Object[] row : evaluationService.evaluateToList(qb, context)) {
			ret.add((User)row[0]);
		}
		return ret;
	}

	public void addData(MapDataSet dataSet, String key, Date startDate, Date endDate, List<EncounterType> encounterTypes, List<User> users, List<Location> locations, EvaluationContext context) {

		HqlQueryBuilder qb = new HqlQueryBuilder();
		qb.select("e.encounterId");
		qb.from(Encounter.class, "e");
		qb.whereGreaterOrEqualTo("e.dateCreated", startDate);
		qb.whereLessOrEqualTo("e.dateCreated", endDate);
		qb.whereIn("e.encounterType", encounterTypes);
		qb.whereIn("e.creator", users);
		qb.whereIn("e.location", locations);

		List<Integer> matchingEncounters = evaluationService.evaluateToList(qb, Integer.class, context);
		DataSetColumn column = new DataSetColumn(key, key, Integer.class);
		dataSet.addData(column, matchingEncounters.size());
	}
}
