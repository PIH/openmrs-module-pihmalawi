package org.openmrs.module.pihmalawi.test;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Validates that the FULL production concept CSVs (<code>configuration/configuration/concepts/concepts.csv</code>
 * and friends - <code>conceptsets</code>, <code>conceptclasses</code>, <code>conceptsources</code>) load correctly
 * via Initializer, and that the resulting concept count and a handful of well-known concept UUIDs match what's
 * expected.
 * <p>
 * This is deliberately NOT part of the routine {@code mvn test} run: loading the full ~8,455-concept production
 * set through Initializer's service-layer processing (as opposed to {@link org.openmrs.module.pihmalawi.BaseMalawiTest}'s
 * small, targeted test-only concept set - see MLW-1839 Task 6) is slow and would be a serious drag on every
 * ordinary test run. Instead, this is a manually-run, on-demand check that the full concept CSVs remain loadable
 * and internally consistent, following the same {@code @Ignore}d, manually-run convention already used by
 * {@link org.openmrs.module.pihmalawi.reporting.PerformanceTest} in this module (the DBUnit-fixture-generating
 * {@code CreateCoreMetadata} that also followed this convention was retired alongside those fixtures - see MLW-1839
 * Task 9).
 * <p>
 * <b>To run:</b> remove the {@code @Ignore} annotation (or run via
 * {@code mvn test -pl api -Dtest=ValidateFullConceptSetup -DfailIfNoTests=false}) and re-add the annotation
 * afterward.
 */
@Ignore
public class ValidateFullConceptSetup extends BaseModuleContextSensitiveTest {

    /**
     * The full production concept count as of MLW-1839 (counted directly from
     * {@code configuration/configuration/concepts/concepts.csv}'s row count). Update this if the production
     * concept set changes materially - this is a coarse sanity check, not a byte-for-byte content assertion (see
     * the table-classification lesson on generic-payload tables: identity/count checks are the right level of
     * rigor here, not chasing exact row-for-row equality).
     */
    private static final int EXPECTED_CONCEPT_COUNT = 8455;

    /**
     * A handful of well-known concept UUIDs referenced directly by this module's own metadata helper classes
     * (see {@code HivMetadata}, {@code ChronicCareMetadata}, {@code CommonMetadata} - the same classes MLW-1839
     * Task 6's static analysis scanned to build the small test-only concept set) - spot-checked here to confirm
     * the full production CSV actually resolves the concepts real application logic depends on, not just that
     * *some* ~8,455 concepts loaded.
     */
    private static final String[] SPOT_CHECK_CONCEPT_UUIDS = {
        "6559f498-977f-11e1-8993-905e29aff6c1", // "HIV program" concept (see MLW-1839 Task 7 finding)
        "65671c9a-977f-11e1-8993-905e29aff6c1", // "Chronic care diagnosis" (ChronicCareMetadata.CHRONIC_CARE_DIAGNOSIS)
        "655e2f90-977f-11e1-8993-905e29aff6c1", // "True" (boolean answer concept used throughout)
    };

    @Override
    public Boolean useInMemoryDatabase() {
        return true;
    }

    @Test
    public void fullConceptCsvsShouldLoadAndMatchExpectedCount() throws Exception {
        this.deleteAllData();
        this.initializeInMemoryDatabase();
        this.authenticate();

        // concept_datatype/concept_map_type are standard OpenMRS core defaults with no Initializer domain of
        // their own (see BaseMalawiTest's identical note) - initializeInMemoryDatabase() doesn't seed them, so
        // every concept row's datatype/mapping-type reference would otherwise fail to resolve.
        executeDataSet("org/openmrs/module/pihmalawi/metadata/concept_datatype.xml");
        executeDataSet("org/openmrs/module/pihmalawi/metadata/concept_map_type.xml");

        String basedir = System.getProperty("basedir", System.getProperty("user.dir"));
        File realConfigRoot = new File(basedir, "../configuration").getCanonicalFile();
        File realConfigSource = new File(realConfigRoot, "configuration");
        Assert.assertTrue("Expected to find the real Initializer configuration directory at " + realConfigSource,
            realConfigSource.isDirectory());

        // Copy just the domains this test needs into a scratch app-data directory, rather than
        // pointing Initializer directly at the real checkout - this avoids writing checksum files
        // (see below) into the actual working tree on every manual run.
        Set<String> domainsToLoad = new HashSet<>(Arrays.asList("conceptclasses", "conceptsources", "concepts", "conceptsets"));
        File tempRoot = Files.createTempDirectory("pihmalawi-validate-full-concepts").toFile();
        File configDest = new File(tempRoot, "configuration");
        FileUtils.forceMkdir(configDest);
        for (String domain : domainsToLoad) {
            FileUtils.copyDirectory(new File(realConfigSource, domain), new File(configDest, domain));
        }

        OpenmrsUtil.setApplicationDataDirectory(tempRoot.getAbsolutePath());
        System.setProperty("OPENMRS_APPLICATION_DATA_DIRECTORY", tempRoot.getAbsolutePath());
        Properties runtimeProperties = Context.getRuntimeProperties();
        runtimeProperties.setProperty(OpenmrsConstants.APPLICATION_DATA_DIRECTORY_RUNTIME_PROPERTY, tempRoot.getAbsolutePath());
        Context.setRuntimeProperties(runtimeProperties);

        InitializerService initializerService = Context.getService(InitializerService.class);

        // See BaseMalawiTest's identical note: Initializer records a per-file checksum
        // unconditionally and skips re-processing unchanged content, which would incorrectly skip
        // every domain on a second run against the same scratch directory content within one JVM.
        ConfigDirUtil.deleteFilesByExtension(initializerService.getChecksumsDirPath(), ConfigDirUtil.CHECKSUM_FILE_EXT);

        for (Loader loader : initializerService.getLoaders()) {
            if (domainsToLoad.contains(loader.getDomainName())) {
                loader.loadUnsafe(Collections.<String> emptyList(), true);
            }
        }

        ConceptService conceptService = Context.getConceptService();
        int actualCount = conceptService.getAllConcepts().size();
        Assert.assertEquals("Expected the full production concept set to load in its entirety", EXPECTED_CONCEPT_COUNT, actualCount);

        for (String uuid : SPOT_CHECK_CONCEPT_UUIDS) {
            Concept concept = conceptService.getConceptByUuid(uuid);
            Assert.assertNotNull("Expected concept " + uuid + " to resolve after loading the full production CSVs", concept);
        }
    }
}
