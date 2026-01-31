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

package org.openmrs.module.pihmalawi.initializer;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Tests for performance issues
 */
public class ConceptExporterTest extends BaseModuleContextSensitiveTest {

	@Autowired
	ConceptExporter conceptExporter;

	@Test
	public void performTest() throws Exception {
		byte[] export = conceptExporter.getConceptExport();
		FileUtils.writeByteArrayToFile(new File("/tmp/concepts.zip"), export);
	}
}
