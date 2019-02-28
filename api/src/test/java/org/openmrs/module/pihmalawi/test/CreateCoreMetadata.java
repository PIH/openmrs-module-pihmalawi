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
package org.openmrs.module.pihmalawi.test;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;
import org.openmrs.module.pihmalawi.StandaloneContextSensitiveTest;
import org.openmrs.module.reporting.common.ObjectUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Produces the coreMetadata.xml test dataset.  This should be run as follows:
 * 1. Install a new, empty mirebalais implementation following the steps on the wiki
 *      (it's probably okay to use an existing database, as long as it's nearly-empty of things like users and providers)
 * 2. Point your .OpenMRS/openmrs-runtime.properties file at this database
 * 3. Specify a location where you want the dataset to be written (leaving this blank will skip the execution of this)
 * 4. Run this as a unit test
 * This should produce a new version of coreMetadata.xml at the location you have specified,
 * and you can copy it into the resources folder if and as appropriate
 * 5. If any of your <provider .../> rows have a provider_role_id attribute, remove that
 */
public class CreateCoreMetadata extends StandaloneContextSensitiveTest {

    @Override
    protected boolean isEnabled() {
        return false;
    }

	public String getOutputDirectory() {
		return "/Users/cioan/workspace/pih/pihmalawi/api/src/test/resources/org/openmrs/module/pihmalawi/metadata";
	}

	@Test
	public void shouldCreateDbUnitTestFiles() throws Exception {
        performTest();
    }

	public Map<String, String> getTables() {
        Map<String, String> m = new LinkedHashMap<String, String>();
        m.put("concept", "SELECT * FROM concept");
        m.put("concept_answer", "SELECT * FROM concept_answer");
        m.put("concept_class", "SELECT * FROM concept_class");
        m.put("concept_datatype", "SELECT * FROM concept_datatype");
        m.put("concept_description", "SELECT * FROM concept_description");
        m.put("concept_map_type", "SELECT * FROM concept_map_type");
        m.put("concept_name", "SELECT * FROM concept_name");
        //m.put("concept_name_tag", "SELECT * FROM concept_name");
        //m.put("concept_name_tag_map", "SELECT * FROM concept_name");
        m.put("concept_numeric", "SELECT * FROM concept_numeric");
        m.put("concept_reference_map", "SELECT * FROM concept_reference_map");
        m.put("concept_reference_source", "SELECT * FROM concept_reference_source");
        m.put("concept_reference_term", "SELECT * FROM concept_reference_term");
        m.put("concept_reference_term_map", "SELECT * FROM concept_reference_term_map");
        m.put("concept_set", "SELECT * FROM concept_set");
        m.put("encounter_role", "SELECT * FROM encounter_role");
        m.put("encounter_type", "SELECT * FROM encounter_type");
        m.put("location", "SELECT * FROM location");
        m.put("location_attribute", "SELECT * FROM location_attribute");
        m.put("location_attribute_type", "SELECT * FROM location_attribute_type");
        m.put("location_tag", "SELECT * FROM location_tag");
        m.put("location_tag_map", "SELECT * FROM location_tag_map");
        m.put("order_type", "SELECT * FROM order_type");
        m.put("patient_identifier_type", "SELECT * FROM patient_identifier_type");
        m.put("person_attribute_type", "SELECT * FROM person_attribute_type");
        m.put("privilege", "SELECT * FROM privilege");
        m.put("program", "SELECT * FROM program");
        m.put("program_workflow", "SELECT * FROM program_workflow");
        m.put("program_workflow_state", "SELECT * FROM program_workflow_state");
        m.put("relationship_type", "SELECT * FROM relationship_type");
        m.put("visit_type", "SELECT * FROM visit_type");
        return m;
    }

    @Override
	public void performTest() throws Exception {

		// only run this test if it is being run alone and if an output directory has been specified
		if (getLoadCount() != 1 || ObjectUtil.isNull(getOutputDirectory())) {
            return;
        }

		for (Map.Entry<String, String> e : getTables().entrySet()) {
		    writeTable(e.getKey(), e.getValue());
        }
	}

	public void writeTable(String tableName, String query) throws Exception {

        // database connection for dbunit
        IDatabaseConnection connection = new DatabaseConnection(getConnection());

        // partial database export
        QueryDataSet initialDataSet = new QueryDataSet(connection);

        initialDataSet.addTable(tableName, query);
        File outputFile = new File(getOutputDirectory(), tableName+".xml");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FlatXmlDataSet.write(initialDataSet, baos);
        String contents = baos.toString("UTF-8");
        FileWriter writer = new FileWriter(outputFile);

        for (String line : contents.split(System.getProperty("line.separator"))) {
            if (line.contains("<concept ")) {
                line = line.replaceAll("short_name=\"([^\"]*)\"", "");
                line = line.replaceAll("description=\"([^\"]*)\"", "");
                line = line.replaceAll("form_text=\"([^\"]*)\"", "");
                line = line.replaceAll(" +", " ");
            }
            line = line.replaceAll("creator=\"[0-9]+\"", "creator=\"1\"");
            line = line.replaceAll("changed_by=\"[0-9]+\"", "changed_by=\"1\"");
            line = line.replaceAll("retired_by=\"[0-9]+\"", "retired_by=\"1\"");
            line = line.replaceAll("voided_by=\"[0-9]+\"", "voided_by=\"1\"");
            writer.write(line + System.getProperty("line.separator"));
        }

        writer.flush();
        writer.close();
    }
	
	/**
	 * Make sure we use the database defined by the runtime properties and not the hsql in-memory
	 * database
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase() {
		return false;
	}
}
