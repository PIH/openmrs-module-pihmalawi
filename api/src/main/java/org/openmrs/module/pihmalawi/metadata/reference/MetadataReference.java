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
package org.openmrs.module.pihmalawi.metadata.reference;

import org.openmrs.OpenmrsObject;

/**
 * Interface for a class that wraps a reference to a piece of metadata, and provides a means for retrieving the target object
 */
public interface MetadataReference<T extends OpenmrsObject> {

	String getReference();
	Class<T> getType();
	T getTarget();

}