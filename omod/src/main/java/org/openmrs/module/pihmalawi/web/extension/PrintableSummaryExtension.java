
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
package org.openmrs.module.pihmalawi.web.extension;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;

public class PrintableSummaryExtension extends Extension {

    /**
     * Returns the required privilege in order to see this section. Can be a comma delimited list of
     * privileges. If the default empty string is returned, only an authenticated user is required
     *
     * @return Privilege string
     */
    public String getRequiredPrivilege() {
        return "View clinical data";
    }

    @Override
    public String getOverrideContent(String bodyContent) {
        StringBuilder sb = new StringBuilder();
        if (Context.getUserContext().hasPrivilege(getRequiredPrivilege()) ) {
            sb.append("<a href=\"");
            sb.append("pihmalawi/printableSummary.page");
            sb.append("?patientId=").append(getParameterMap().get("patientId")).append("\">");
            sb.append("HIV Patient Summary");
            sb.append("</a>&nbsp;&nbsp;<br />");
            sb.append("<a href=\"");
            sb.append("pihmalawi/ncdInwardSummary.page");
            sb.append("?patientId=").append(getParameterMap().get("patientId")).append("\">");
            sb.append("NCD Inward Summary");
        }
        return sb.toString();
    }

    @Override
    public MEDIA_TYPE getMediaType() {
        return MEDIA_TYPE.html;
    }
}
