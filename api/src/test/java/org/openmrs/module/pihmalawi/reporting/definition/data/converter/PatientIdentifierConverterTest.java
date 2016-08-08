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

package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PatientIdentifier;

import java.util.UUID;

/**
 * Produces uuids for embedding in reports
 */
public class PatientIdentifierConverterTest {

	@Test
	public void shouldConvertIdentifiers() {
	    test("NNO 100", "NNO-0100");
        test("testing", "testing");
        test("NNO 100 HCC", "NNO-0100-HCC");
        test("NNO 100 CCC", "NNO-0100-CCC");
        test("NNO-100", "NNO-100");
	}

	protected void test(String original, String expected) {
	    PatientIdentifierConverter c = new PatientIdentifierConverter();
        PatientIdentifier pi = new PatientIdentifier();
        pi.setIdentifier(original);
        String result = (String)c.convert(pi);
        Assert.assertEquals(expected, result);
    }
}
