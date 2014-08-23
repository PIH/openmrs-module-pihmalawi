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
package org.openmrs.module.pihmalawi.web.htmlwidgets;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.handler.UserHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * WidgetHandler for Users
 */
@Handler(supports={User.class}, order=25)
public class ApzuUserHandler extends UserHandler {

	@Autowired
	EvaluationService evaluationService;
	
	/**
	 * @see CodedHandler#setDefaults(WidgetConfig)
	 */
	@Override
	protected void setDefaults(WidgetConfig config) {
		config.setFormat("select");
		config.setDefaultAttribute("showEmptyOption", "true");
		config.setDefaultAttribute("emptyLabel", "All Users");
	}

	/** 
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig config, CodedWidget widget) {
		String maxYears = config.getAttributeValue("maxYearsSinceLastEncounter");
		if (StringUtils.isNotEmpty(maxYears)) {
			int maxYearNum = Integer.parseInt(maxYears);
			Date minEncounterDate = DateUtil.adjustDate(new Date(), -1*maxYearNum, DurationUnit.YEARS);
			HqlQueryBuilder q = new HqlQueryBuilder();
			q.select("distinct e.creator");
			q.from(Encounter.class, "e");
			q.whereGreaterOrEqualTo("e.encounterDatetime", minEncounterDate);
			List<User> users = evaluationService.evaluateToList(q, User.class, new EvaluationContext());
			for (User u : users) {
				widget.addOption(new Option(u.getId().toString(), getUserDisplay(u, config), null, u), config);
			}
		}
		else {
			super.populateOptions(config, widget);
		}
	}

	@Override
	protected String getUserDisplay(User u, WidgetConfig config) {
		return super.getUserDisplay(u, config) + " (" + u.getUsername() + ")";
	}
}
