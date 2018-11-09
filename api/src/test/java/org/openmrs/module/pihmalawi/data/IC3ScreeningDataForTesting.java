/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Test version of IC3 Screening data that allows setting a different date
 */
@Component
public class IC3ScreeningDataForTesting extends IC3ScreeningData {

    private Date evaluationDate;

    public Date getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(Date evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    @Override
    protected EvaluationContext getEvaluationContext() {
        EvaluationContext context = super.getEvaluationContext();
        context.addParameterValue(ReportingConstants.END_DATE_PARAMETER.getName(), evaluationDate);
        return context;
    }
}
