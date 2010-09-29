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
package org.openmrs.module.pihmalawi.reporting;

import java.util.List;
import java.util.Map;

import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.RenderingMode;

/**
 * ReportSpecification
 */
public interface ReportSpecification {
	
	/**
	 * @return the name of the Report
	 */
	public String getName();
	
	/**
	 * @return the description of the Report
	 */
	public String getDescription();
	
	/**
	 * @return the parameters of the Report
	 */
	public List<Parameter> getParameters();
	
	/**
	 * @return the rendering modes of the Report
	 */
	public List<RenderingMode> getRenderingModes();
	
	/**
	 * This method should be used to validate the input parameters and transform them into whatever parameters
	 * are required by the various definitions which will be used by the report
	 * @return the EvaluationContext to use for the report.
	 */
	public EvaluationContext validateAndCreateContext(Map<String, Object> parameters);
	
	/**
	 * @return the ReportData that is produced when evaluating this report
	 */
	public ReportData evaluateReport(EvaluationContext context);
}