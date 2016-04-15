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

package org.openmrs.module.pihmalawi.metadata;

import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.metadatadeploy.descriptor.LocationAttributeTypeDescriptor;

/**
 * Constants for all defined location attribute types
 */
public class LocationAttributeTypes {

    public static LocationAttributeTypeDescriptor LOCATION_CODE = new LocationAttributeTypeDescriptor() {
        public String uuid() { return "62eb8441-0326-11e6-8c93-e82aea237783"; }
        public String name() { return "Location Code"; }
        public String description() { return "Short name or code for used in identifier types and for concise display of locations"; }
        public Class<?> datatype() { return FreeTextDatatype.class; }
    };
}