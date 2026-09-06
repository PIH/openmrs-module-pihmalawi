# MLW-1732: Replace Metadata Deploy bundles with Initializer — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove all use of the `metadatadeploy` and `metadatasharing` modules from `openmrs-module-pihmalawi`, replacing metadata installation with `openmrs-module-initializer` CSV/XML domains, consistent with the pattern already used in `configuration/configuration/{locations,attributetypes,concepts,...}`.

**Architecture:** Each `metadatadeploy` `Bundle` class is replaced by one or more Initializer CSV files under `configuration/configuration/<domain>/`. Java constant-holder classes that only depend on `metadatadeploy` for their `XxxDescriptor` marker interfaces are flattened to plain `String` constants, consolidated into a new `PihMalawiConfigConstants` class. `MetadataInitializer.java` is kept (not deleted) but repurposed to explicitly drive Initializer's loaders instead of installing metadatadeploy bundles, with Initializer's own automatic startup loading disabled. Only after every reference is migrated do we drop the `metadatadeploy`/`metadatasharing` Maven dependencies.

**Tech Stack:** Java 8, Maven, OpenMRS module framework, `openmrs-module-initializer` 2.12.0 (already a dependency), Spring `@Component`.

**Spec:** `docs/superpowers/specs/2026-09-04-metadatadeploy-to-initializer-design.md`

## Global Constraints

- Do not touch the `configuration/pom.xml` build mechanism (`openmrs-packager-maven-plugin` + `constants.yml`) — that conversion to a `content.properties`-based content package is explicitly out of scope for this ticket (separate future ticket).
- Where the same UUID is needed in more than one CSV cell, add it once to `configuration/constants.yml` and reference it via `${...}`; hard-code single-use UUIDs directly in the CSV.
- Runtime lookup helper classes with no `metadatadeploy` dependency (`HivMetadata`, `TbMetadata`, `CommonMetadata`, `ChronicCareMetadata`, `group/*TreatmentGroup`, `Metadata.java`) are out of scope — do not modify their non-metadatadeploy logic.
- Only flatten a descriptor constant into `PihMalawiConfigConstants` if something *outside* the bundle/descriptor file itself actually consumes it (checked per task below). If nothing outside consumes it, hard-code the literal UUID directly in the CSV and drop the Java constant entirely — per the instruction to eliminate as much as possible.
- Never run `mvn` with `-o` (offline) unless it's already the project convention; always run the full `mvn -pl api,omod -am test` (or narrower `-Dtest=...`) after each task's code changes, from the repo root.
- Keep `metadatadeploy-api`/`metadatasharing-api` dependencies in `pom.xml` until Task 15 — earlier tasks intentionally leave them in place so partially-migrated code still compiles.

---

## File Structure

New files:
- `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java` — flat `String` UUID/name constants, consolidated from the deleted descriptor classes.
- `configuration/configuration/encounterroles/encounterRoles.csv`
- `configuration/configuration/visittypes/visitTypes.csv`
- `configuration/configuration/relationshiptypes/relationshipTypes.csv`
- `configuration/configuration/personattributetypes/personAttributeTypes.csv`
- `configuration/configuration/patientidentifiertypes/identifierTypes.csv`
- `configuration/configuration/roles/roles.csv`
- `configuration/configuration/privileges/privileges.csv`
- `configuration/configuration/encountertypes/encounterTypes.csv`
- `configuration/configuration/programs/programs.csv`
- `configuration/configuration/programworkflows/programWorkflows.csv`
- `configuration/configuration/programworkflowstates/programWorkflowStates.csv`

Modified (structure-relevant): `configuration/configuration/attributetypes/attributetypes.csv`, `configuration/configuration/globalproperties/gp.xml`, `configuration/constants.yml`, `api/src/main/java/org/openmrs/module/pihmalawi/activator/MetadataInitializer.java`, `api/src/main/java/org/openmrs/module/pihmalawi/activator/PihMalawiModuleActivator.java`, `pom.xml`, `distro/openmrs-distro.properties`, `omod/src/main/resources/config.xml`.

Deleted: every class under `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/` (16 files) and `.../deploy/handler/ProgramAttributeTypeDeployHandler.java` and `.../deploy/PihConstructors.java`, plus `Locations.java`, `LocationTags.java`, `LocationAttributeTypes.java`, `PersonAttributeTypes.java`, `PihMalawiPatientIdentifierTypes.java`, `Programs.java` (all fully replaced by `PihMalawiConfigConstants` and/or CSV).

Moved: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/concept/*.java` (14 files) → `api/src/main/java/org/openmrs/module/pihmalawi/metadata/concept/*.java`.

---

### Task 1: Scaffold `PihMalawiConfigConstants`; migrate Locations, LocationTags, LocationAttributeTypes

These three domains were checked against the existing CSVs and are **already fully present** — `configuration/configuration/locations/locations.csv` already has rows for `BINJE_OUTREACH_CLINIC` (`3093e2ab-0eee-4bc2-aacf-8d51d77c7698`), `FELEMU_OUTREACH_CLINIC` (`794df119-65e6-4098-8e12-851063267217`), and `KASAMBA_OUTREACH_CLINIC` (`6368bada-6e65-44dc-a093-c3a17a0f40f8`); `locationtags.csv` already has all 13 tags `LocationTagBundle` installs; `attributetypes.csv` already has the one row `LocationAttributeTypeBundle` installs (`62eb8441-0326-11e6-8c93-e82aea237783`, entity `Location`, name `Location Code`). **No CSV changes needed in this task** — verify this first, then just delete the Java bundles and flatten the Java constants.

**Files:**
- Create: `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/Locations.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/LocationTags.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/LocationAttributeTypes.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationTagBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationAttributeTypeBundle.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/location/impl/LocationUuidHandlerImpl.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/CommonMetadata.java:668,672,706`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/ChronicCareMetadata.java:225`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/HivMetadata.java:215`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/reporting/reports/MedicMobileIC3TraceReport.java:91,110`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/provider/VhwProviderIdentifierGenerator.java:62`
- Modify (temporarily, cleaned up in Task 12): `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/CoreConfigurationBundle.java` — leave its `Locations.UNKNOWN.uuid()` reference working by using the new constant now (it gets deleted wholesale in Task 12, but must compile until then).

**Interfaces:**
- Produces: `PihMalawiConfigConstants` class with these `public static final String` fields, all in package `org.openmrs.module.pihmalawi`:
  - `LOCATION_UNKNOWN_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"`
  - `LOCATION_DAMBE_CLINIC_UUID = "976dcd06-c40e-4e2e-a0de-35a54c7a52ef"`, `LOCATION_DAMBE_CLINIC_NAME = "Dambe Clinic"`
  - `LOCATION_LIGOWE_HC_UUID = "0d417e38-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_LIGOWE_HC_NAME = "Ligowe HC"`
  - `LOCATION_LUWANI_RHC_UUID = "0d416506-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_LUWANI_RHC_NAME = "Luwani RHC"`
  - `LOCATION_MAGALETA_HC_UUID = "0d414eae-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_MAGALETA_HC_NAME = "Magaleta HC"`
  - `LOCATION_MATANDANI_RHC_UUID = "0d415200-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_MATANDANI_RHC_NAME = "Matandani Rural Health Center"`
  - `LOCATION_NENO_DHO_UUID = "0d414ce2-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_NENO_DHO_NAME = "Neno District Hospital"`
  - `LOCATION_NENO_INWARD_PATIENTS_UUID = "985193ce-761a-4011-9d3e-24ddf61eba0f"`, `LOCATION_NENO_INWARD_PATIENTS_NAME = "Neno inward patients"`
  - `LOCATION_NENO_MISSION_HC_UUID = "0d416830-5ab4-11e0-870c-9f6107fee88e"`, `LOCATION_NENO_MISSION_HC_NAME = "Nsambe HC"`
  - `LOCATIONTAG_UPPER_NENO_NAME = "Upper Neno"`
  - `LOCATIONTAG_LOWER_NENO_NAME = "Lower Neno"`
  - `LOCATIONTAG_CHRONIC_CARE_LOCATION_NAME = "Chronic Care Location"`
  - `LOCATIONTAG_HIV_STATIC_NAME = "Static HIV"`
  - `LOCATIONTAG_MEDIC_MOBILE_FACILITY_UUID = "7ae7db90-a601-41e7-bb09-fcdbbfbeaa87"`
  - `LOCATIONTAG_TRACE_PHASE_1_LOCATION_NAME = "TRACE PHASE 1"`
  - `LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID = "62eb8441-0326-11e6-8c93-e82aea237783"`

  (`BINJE_OUTREACH_CLINIC`, `FELEMU_OUTREACH_CLINIC`, `KASAMBA_OUTREACH_CLINIC` from `Locations.java`, and `HIV_OUTREACH`/`LOGIN_LOCATION`/`VISIT_LOCATION`/`ADMISSION_LOCATION`/`TRANSFER_LOCATION`/`DISPENSING_LOCATION`/`HEALTH_FACILITY` from `LocationTags.java` have **no consumers outside their own now-deleted descriptor/bundle files** — confirmed by `grep -rl "Locations\.[A-Z]"` / `"LocationTags\.[A-Z]"` across `api`+`omod`. Do not add them to `PihMalawiConfigConstants`; they are simply dropped since nothing references them.)

- [ ] **Step 1: Confirm the three CSVs already contain matching UUIDs (no new CSV rows needed)**

Run:
```bash
grep -i "3093e2ab-0eee-4bc2-aacf-8d51d77c7698\|794df119-65e6-4098-8e12-851063267217\|6368bada-6e65-44dc-a093-c3a17a0f40f8" configuration/configuration/locations/locations.csv
grep -c "" configuration/configuration/locationtags/locationtags.csv
grep -i "62eb8441-0326-11e6-8c93-e82aea237783" configuration/configuration/attributetypes/attributetypes.csv
```
Expected: all three UUIDs found in `locations.csv`; `locationtags.csv` has 16 lines (1 header + 15 tags, which is a superset of the 13 `LocationTagBundle` installs); the `LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID` value found in `attributetypes.csv`. If any check fails, stop and add the missing row(s) before continuing (do not proceed on the "already present" assumption).

- [ ] **Step 2: Create `PihMalawiConfigConstants.java`**

```java
package org.openmrs.module.pihmalawi;

/**
 * Flat UUID/name constants for pihmalawi metadata, replacing the descriptor-based
 * constant classes that depended on the metadatadeploy module (removed in MLW-1732).
 */
public class PihMalawiConfigConstants {

    // Locations
    public static final String LOCATION_UNKNOWN_UUID = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
    public static final String LOCATION_DAMBE_CLINIC_UUID = "976dcd06-c40e-4e2e-a0de-35a54c7a52ef";
    public static final String LOCATION_DAMBE_CLINIC_NAME = "Dambe Clinic";
    public static final String LOCATION_LIGOWE_HC_UUID = "0d417e38-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_LIGOWE_HC_NAME = "Ligowe HC";
    public static final String LOCATION_LUWANI_RHC_UUID = "0d416506-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_LUWANI_RHC_NAME = "Luwani RHC";
    public static final String LOCATION_MAGALETA_HC_UUID = "0d414eae-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_MAGALETA_HC_NAME = "Magaleta HC";
    public static final String LOCATION_MATANDANI_RHC_UUID = "0d415200-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_MATANDANI_RHC_NAME = "Matandani Rural Health Center";
    public static final String LOCATION_NENO_DHO_UUID = "0d414ce2-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_NENO_DHO_NAME = "Neno District Hospital";
    public static final String LOCATION_NENO_INWARD_PATIENTS_UUID = "985193ce-761a-4011-9d3e-24ddf61eba0f";
    public static final String LOCATION_NENO_INWARD_PATIENTS_NAME = "Neno inward patients";
    public static final String LOCATION_NENO_MISSION_HC_UUID = "0d416830-5ab4-11e0-870c-9f6107fee88e";
    public static final String LOCATION_NENO_MISSION_HC_NAME = "Nsambe HC";

    // Location tags
    public static final String LOCATIONTAG_UPPER_NENO_NAME = "Upper Neno";
    public static final String LOCATIONTAG_LOWER_NENO_NAME = "Lower Neno";
    public static final String LOCATIONTAG_CHRONIC_CARE_LOCATION_NAME = "Chronic Care Location";
    public static final String LOCATIONTAG_HIV_STATIC_NAME = "Static HIV";
    public static final String LOCATIONTAG_MEDIC_MOBILE_FACILITY_UUID = "7ae7db90-a601-41e7-bb09-fcdbbfbeaa87";
    public static final String LOCATIONTAG_TRACE_PHASE_1_LOCATION_NAME = "TRACE PHASE 1";

    // Location attribute types
    public static final String LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID = "62eb8441-0326-11e6-8c93-e82aea237783";
}
```

- [ ] **Step 3: Update `LocationUuidHandlerImpl.java`**

Replace the whole method body:

```java
package org.openmrs.module.pihmalawi.location.impl;

import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;
import org.openmrs.module.pihmalawi.location.LocationUuidHandler;
import org.springframework.stereotype.Component;

/**
 *  Implements getting UUIDs for locations stored in memory
 */
@Component
public class LocationUuidHandlerImpl implements LocationUuidHandler {

    @Override
    public String getLocationUiidByLocationName(String location) {
        String formattedLocation = location.trim().toLowerCase();
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_DAMBE_CLINIC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_DAMBE_CLINIC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_LIGOWE_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_LIGOWE_HC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_LUWANI_RHC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_LUWANI_RHC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_MAGALETA_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_MAGALETA_HC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_MATANDANI_RHC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_MATANDANI_RHC_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_DHO_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_DHO_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_INWARD_PATIENTS_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_INWARD_PATIENTS_UUID;
        }
        if (formattedLocation.equals(PihMalawiConfigConstants.LOCATION_NENO_MISSION_HC_NAME.toLowerCase())) {
            return PihMalawiConfigConstants.LOCATION_NENO_MISSION_HC_UUID;
        }
        return PihMalawiConfigConstants.LOCATION_UNKNOWN_UUID;
    }
}
```

- [ ] **Step 4: Update the remaining consumers**

In `CommonMetadata.java`:
- Line 668: `return getLocationsForTag(LocationTags.UPPER_NENO.name());` → `return getLocationsForTag(PihMalawiConfigConstants.LOCATIONTAG_UPPER_NENO_NAME);`
- Line 672: `return getLocationsForTag(LocationTags.LOWER_NENO.name());` → `return getLocationsForTag(PihMalawiConfigConstants.LOCATIONTAG_LOWER_NENO_NAME);`
- Line 706: `LocationAttributeType locationCode = getLocationAttributeType(LocationAttributeTypes.LOCATION_CODE.uuid());` → `LocationAttributeType locationCode = getLocationAttributeType(PihMalawiConfigConstants.LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID);`
- Add `import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;`; remove the now-unused `import org.openmrs.module.pihmalawi.metadata.LocationTags;` and `LocationAttributeTypes` imports if present.

In `ChronicCareMetadata.java:225`: `List<Location> l = getLocationsForTag(LocationTags.CHRONIC_CARE_LOCATION.name());` → `getLocationsForTag(PihMalawiConfigConstants.LOCATIONTAG_CHRONIC_CARE_LOCATION_NAME)`. Add the import, remove the `LocationTags` import.

In `HivMetadata.java:215`: `List<Location> l = getLocationsForTag(LocationTags.HIV_STATIC.name());` → `getLocationsForTag(PihMalawiConfigConstants.LOCATIONTAG_HIV_STATIC_NAME)`. Add the import, remove the `LocationTags` import.

In `MedicMobileIC3TraceReport.java`:
- Line 91: `LocationTag locationTag = locationService.getLocationTagByUuid(LocationTags.MEDIC_MOBILE_FACILITY.uuid());` → `getLocationTagByUuid(PihMalawiConfigConstants.LOCATIONTAG_MEDIC_MOBILE_FACILITY_UUID)`
- Line 110: `mappings.put(PHASE_1_PARAM, location.hasTag(LocationTags.TRACE_PHASE_1_LOCATION.name()));` → `location.hasTag(PihMalawiConfigConstants.LOCATIONTAG_TRACE_PHASE_1_LOCATION_NAME)`
- Add the import, remove the `LocationTags` import.

In `VhwProviderIdentifierGenerator.java:62`: `if (StringUtils.equals(attribute.getAttributeType().getUuid(), LocationAttributeTypes.LOCATION_CODE.uuid())) {` → `StringUtils.equals(attribute.getAttributeType().getUuid(), PihMalawiConfigConstants.LOCATIONATTRIBUTETYPE_LOCATION_CODE_UUID)`. Add the import, remove the `LocationAttributeTypes` import.

In `CoreConfigurationBundle.java`: `properties.put(EmrApiConstants.GP_UNKNOWN_LOCATION, Locations.UNKNOWN.uuid());` → `properties.put(EmrApiConstants.GP_UNKNOWN_LOCATION, PihMalawiConfigConstants.LOCATION_UNKNOWN_UUID);`. Add the import, remove the `Locations` import. (This whole file is deleted in Task 12 — this edit only exists to keep it compiling in the meantime.)

- [ ] **Step 5: Delete the three bundle files**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationTagBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/LocationAttributeTypeBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/Locations.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/LocationTags.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/LocationAttributeTypes.java
```

- [ ] **Step 6: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS. If it fails on an unresolved `Locations`/`LocationTags`/`LocationAttributeTypes` reference, grep for the symbol again (`grep -rn "\bLocations\.\|\bLocationTags\.\|\bLocationAttributeTypes\." api omod`) — the search above may have missed a test file — and fix it the same way.

- [ ] **Step 7: Run affected tests**

Run: `mvn -pl api -am test -Dtest=MalawiPatientValidatorTest`
Expected: PASS (this test doesn't touch these classes directly, but confirms the module still builds/tests cleanly). Also run the full test suite once at the end of Task 15.

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "MLW-1732: Flatten Locations/LocationTags/LocationAttributeTypes into PihMalawiConfigConstants"
```

---

### Task 2: Move `metadata/deploy/bundle/concept/*` to `metadata/concept/*`

These 14 classes are plain UUID constant holders (no `metadatadeploy` dependency, no `@Component`, no `install()` method) actively used by the `*ProgramMetadata` classes and elsewhere. They're just misplaced under a `deploy.bundle` path. This is a pure package rename.

**Files:**
- Move (all 14): `ChwManagementConcepts.java`, `CommonConcepts.java`, `CoreConceptMetadataBundle.java`, `IC3ScreeningConcepts.java`, `MasterCardConcepts.java`, `NutritionConcepts.java`, `PalliativeCareConcepts.java`, `PdcCleftLipPalateConcepts.java`, `PdcConcepts.java`, `PdcDevelopmentalDelayConcepts.java`, `PdcOtherDiagnosisVisitConcepts.java`, `PdcTrisomy21Concepts.java`, `ProgramConcepts.java`, `TbProgramConcepts.java`, `TeenClubConcepts.java`
  from `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/concept/`
  to `api/src/main/java/org/openmrs/module/pihmalawi/metadata/concept/`
- Modify: every file that imports `org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.*`

**Interfaces:**
- Produces: same 14 class names, now under package `org.openmrs.module.pihmalawi.metadata.concept`.

- [ ] **Step 1: Find every importer**

Run:
```bash
grep -rl "org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept" --include="*.java" api omod
```
Expected output (record this list — you will edit each one in Step 3):
```
api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProviderAttributeTypeBundle.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/Programs.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/PalliativeCareMetadata.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/MentalHealthMetadata.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/PdcMetadata.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/NutritionProgramMetadata.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/TbProgramMetadata.java
api/src/main/java/org/openmrs/module/pihmalawi/metadata/TeenClubProgramMetadata.java
```
(plus the 14 files being moved, which reference each other via same-package access and need no import change — only cross-package importers need edits. If the actual grep output differs from this list, use the actual output — grep is authoritative, this is provided as a sanity check.)

- [ ] **Step 2: Move the files**

```bash
git mkdir -p api/src/main/java/org/openmrs/module/pihmalawi/metadata/concept 2>/dev/null || mkdir -p api/src/main/java/org/openmrs/module/pihmalawi/metadata/concept
for f in ChwManagementConcepts CommonConcepts CoreConceptMetadataBundle IC3ScreeningConcepts MasterCardConcepts NutritionConcepts PalliativeCareConcepts PdcCleftLipPalateConcepts PdcConcepts PdcDevelopmentalDelayConcepts PdcOtherDiagnosisVisitConcepts PdcTrisomy21Concepts ProgramConcepts TbProgramConcepts TeenClubConcepts; do
  git mv "api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/concept/${f}.java" "api/src/main/java/org/openmrs/module/pihmalawi/metadata/concept/${f}.java"
done
```

- [ ] **Step 3: Fix the package declaration in each moved file**

In each of the 14 moved files, change:
```java
package org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept;
```
to:
```java
package org.openmrs.module.pihmalawi.metadata.concept;
```

- [ ] **Step 4: Fix every importer found in Step 1**

In each importing file, change every line matching `import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ClassName;` to `import org.openmrs.module.pihmalawi.metadata.concept.ClassName;`. Example (`ProviderAttributeTypeBundle.java`):
```java
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.concept.ChwManagementConcepts;
```
becomes:
```java
import org.openmrs.module.pihmalawi.metadata.concept.ChwManagementConcepts;
```

- [ ] **Step 5: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "MLW-1732: Move metadata concept constant classes out of deploy.bundle package"
```

---

### Task 3: EncounterRoles domain

`EncounterRoleBundle` installs 5 encounter roles. Its nested `EncounterRoles` constant class has exactly one external consumer (`CoreConfigurationBundle`, for 3 of the 5 UUIDs) — that consumer is deleted in Task 12 when its global properties move to `gp.xml` as literal strings. **No `PihMalawiConfigConstants` entries are needed for this domain** — it becomes CSV-only.

**Files:**
- Create: `configuration/configuration/encounterroles/encounterRoles.csv`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/EncounterRoleBundle.java`
- Modify (temporarily): `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/CoreConfigurationBundle.java` — replace the 3 `EncounterRoleBundle.EncounterRoles.*` references with literal UUID strings (this file is deleted wholesale in Task 12).

**Interfaces:** none produced (no Java consumers remain).

- [ ] **Step 1: Create the CSV**

```csv
Uuid,Void/Retire,Name,Description
bad21515-fd04-4ff6-bfcd-78456d12f168,,Dispenser,Provider that dispenses medications or other products
98bf2792-3f0a-4388-81bb-c78b29c0df92,,Nurse,A person educated and trained to care for the sick or disabled.
4f10ad1a-ec49-48df-98c7-1391c6ac7f05,,Consulting Clinician,Clinician who is primarily responsible for examining and diagnosing a patient
cbfe0b9d-9923-404c-941b-f048adc8cdc0,,Administrative Clerk,This role is used for creating a Check-in encounter
c458d78e-8374-4767-ad58-9f8fe276e01c,,Ordering Provider,"For encounters associated with orders, used to store the provider responsible for placing the order"
```

Save at `configuration/configuration/encounterroles/encounterRoles.csv`.

- [ ] **Step 2: Update `CoreConfigurationBundle.java`'s 3 references to literals**

```java
properties.put(EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, "4f10ad1a-ec49-48df-98c7-1391c6ac7f05"); // Consulting Clinician
properties.put(EmrApiConstants.GP_ORDERING_PROVIDER_ENCOUNTER_ROLE, "c458d78e-8374-4767-ad58-9f8fe276e01c"); // Ordering Provider
properties.put(EmrApiConstants.GP_CHECK_IN_CLERK_ENCOUNTER_ROLE, "cbfe0b9d-9923-404c-941b-f048adc8cdc0"); // Administrative Clerk
```
Remove the now-unused `import org.openmrs.module.pihmalawi.metadata.deploy.bundle.EncounterRoleBundle;` if it becomes unused (it won't — `EncounterRoleBundle` is being deleted in this same task, so this import must be removed).

- [ ] **Step 3: Delete the bundle**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/EncounterRoleBundle.java
```

- [ ] **Step 4: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate EncounterRoleBundle to Initializer encounterroles.csv"
```

---

### Task 4: VisitTypes domain

`VisitTypeBundle` installs 1 visit type. Its nested `VisitTypes` constant is consumed only by `CoreConfigurationBundle` and `MetadataInitializer.java` — both cleaned up in Task 12. **CSV-only, no `PihMalawiConfigConstants` entry.**

**Files:**
- Create: `configuration/configuration/visittypes/visitTypes.csv`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/VisitTypeBundle.java`
- Modify (temporarily): `CoreConfigurationBundle.java`, `MetadataInitializer.java` — replace `VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT` with the literal `"f01c54cb-2225-471a-9cd5-d348552c337c"`.

- [ ] **Step 1: Create the CSV**

```csv
Uuid,Void/Retire,Name,Description
f01c54cb-2225-471a-9cd5-d348552c337c,,Clinic or Hospital Visit,"Patient visits the clinic/hospital (as opposed to a home visit, or telephone contact)"
```

Save at `configuration/configuration/visittypes/visitTypes.csv`.

- [ ] **Step 2: Update `CoreConfigurationBundle.java`**

```java
properties.put(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, "f01c54cb-2225-471a-9cd5-d348552c337c"); // Clinic or Hospital Visit
```
Remove the `import ...VisitTypeBundle;` if now unused in this file.

- [ ] **Step 3: Update `MetadataInitializer.java`**

Change:
```java
saveGlobalProperty(EmrApiConstants.GP_VISIT_ASSIGNMENT_HANDLER_ENCOUNTER_TYPE_TO_VISIT_TYPE_MAP,
        EncounterTypes.CHECK_IN.uuid() + ":" + VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT);
```
Leave the `EncounterTypes.CHECK_IN.uuid()` half alone for now (handled in Task 10) — just replace the `VisitTypeBundle.VisitTypes.CLINIC_OR_HOSPITAL_VISIT` half with the literal `"f01c54cb-2225-471a-9cd5-d348552c337c"`, and remove the `VisitTypeBundle` import. (The whole `saveGlobalProperty` call is removed entirely in Task 12 — this is an interim compile-safe edit.)

- [ ] **Step 4: Delete the bundle**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/VisitTypeBundle.java
```

- [ ] **Step 5: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate VisitTypeBundle to Initializer visittypes.csv"
```

---

### Task 5: RelationshipTypes domain

`RelationshipTypeBundle` installs 1 relationship type (`CHW to Patient`). Its nested `RelationshipTypes.CHW_TO_PATIENT` constant has **zero consumers outside the bundle file itself** (confirmed by grep). CSV-only.

**Files:**
- Create: `configuration/configuration/relationshiptypes/relationshipTypes.csv`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/RelationshipTypeBundle.java`

- [ ] **Step 1: Create the CSV**

```csv
UUID,Void/Retire,A is to B,B is to A,Description
eb567be2-fda1-4746-9d51-833de8a7e81f,,Community Health Worker,Patient,CHW to Patient relationship
```

Save at `configuration/configuration/relationshiptypes/relationshipTypes.csv`.

- [ ] **Step 2: Delete the bundle**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/RelationshipTypeBundle.java
```

- [ ] **Step 3: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate RelationshipTypeBundle to Initializer relationshiptypes.csv"
```

---

### Task 6: PersonAttributeTypes domain

`PersonAttributeTypeBundle` installs 2 person attribute types (`TEST_PATIENT`, `UNKNOWN_PATIENT`, defined in `PersonAttributeTypes.java`). Neither constant has a consumer outside `PersonAttributeTypes.java`/`PersonAttributeTypeBundle.java` (confirmed by grep — `TEST_PATIENT`'s UUID is already available externally via `EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID` if ever needed again). CSV-only.

**Files:**
- Create: `configuration/configuration/personattributetypes/personAttributeTypes.csv`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/PersonAttributeTypes.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PersonAttributeTypeBundle.java`

- [ ] **Step 1: Create the CSV**

```csv
Uuid,Void/Retire,Name,Description,Format,Foreign uuid,Searchable
4f07985c-88a5-4abd-aa0c-f3ec8324d8e7,,Test Patient,Flag to describe if the patient was created for demonstration or testing purposes,java.lang.Boolean,,
8b56eac7-5c76-4b9c-8c6f-1deab8d3fc47,,Unknown patient,Used to flag patients that cannot be identified during the check-in process,java.lang.Boolean,,
```

Save at `configuration/configuration/personattributetypes/personAttributeTypes.csv`. (`4f07985c-88a5-4abd-aa0c-f3ec8324d8e7` is `EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID`, verified from the `emrapi-api-3.4.0.jar` bytecode; `Format` is `java.lang.Boolean` since `TEST_PATIENT` and `UNKNOWN_PATIENT` both declared `Class<?> format() { return Boolean.class; }`. The old bundle's `sortWeight()` values (8 and 13) have no column in this Initializer domain and are dropped — sort weight only affected admin-UI ordering.)

- [ ] **Step 2: Delete the Java classes**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/PersonAttributeTypes.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PersonAttributeTypeBundle.java
```

- [ ] **Step 3: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate PersonAttributeTypeBundle to Initializer personattributetypes.csv"
```

---

### Task 7: PatientIdentifierTypes domain

`PatientIdentifierBundle` installs 7 identifier types defined in `PihMalawiPatientIdentifierTypes.java`. Unlike the last few domains, this one **is** widely consumed (`ChronicCareMetadata`, `PalliativeCareMetadata`, `PdcMetadata`, `TbProgramMetadata`, `MalawiPatientValidator`, `MalawiPatientValidatorTest`, `YendaNafePatientRestController`) — needs `PihMalawiConfigConstants` entries with both `_UUID` and `_NAME`.

**Files:**
- Create: `configuration/configuration/patientidentifiertypes/identifierTypes.csv`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/PihMalawiPatientIdentifierTypes.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PatientIdentifierBundle.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/ChronicCareMetadata.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/PalliativeCareMetadata.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/PdcMetadata.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/TbProgramMetadata.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/validator/MalawiPatientValidator.java`
- Modify: `api/src/test/java/org/openmrs/module/pihmalawi/validator/MalawiPatientValidatorTest.java`
- Modify: `omod/src/main/java/org/openmrs/module/pihmalawi/rest/controller/YendaNafePatientRestController.java`

**Interfaces:**
- Produces (added to `PihMalawiConfigConstants`):
  - `PATIENTIDENTIFIERTYPE_NUTRITION_PROGRAM_NUMBER_UUID = "C9888967-8584-4F36-86B8-51AC368BC720"`, `_NAME = "Nutrition Program Number"`
  - `PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_UUID = "f2b29f9b-69d0-4339-b1aa-55a511672558"`, `_NAME = "Palliative Care Number"`
  - `PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_UUID = "f51dfa3a-95de-4040-b4eb-52d2de718a74"`, `_NAME = "IC3 Identifier"`
  - `PATIENTIDENTIFIERTYPE_YENDANAFE_IDENTIFIER_UUID = "e4a1a524-d557-11ea-87d0-0242ac130003"`, `_NAME = "Yendanafe Identifier"`
  - `PATIENTIDENTIFIERTYPE_IC3D_IDENTIFIER_UUID = "70690634-6522-4552-ba66-43eda7c30217"`, `_NAME = "IC3D Identifier"`
  - `PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_UUID = "f7de1b97-013e-49ad-a596-4ada6ede1053"`, `_NAME = "PDC Identifier"`
  - `PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_UUID = "F4319B47-4141-48DF-9F41-5CF7E6301EC6"`, `_NAME = "TB program identifier"`

- [ ] **Step 1: Create the CSV**

```csv
Uuid,Void/Retire,Name,Description,Required,Format,Format description,Validator,Location behavior,Uniqueness behavior
C9888967-8584-4F36-86B8-51AC368BC720,,Nutrition Program Number,Number assigned to patient enrolled into the nutrition program.,0,,Ex. NNO 1234 NP,,REQUIRED,
f2b29f9b-69d0-4339-b1aa-55a511672558,,Palliative Care Number,Number assigned to patient on palliative care.,0,,Ex. NNO 101 PC,,REQUIRED,
f51dfa3a-95de-4040-b4eb-52d2de718a74,,IC3 Identifier,ID assigned to patients at IC3 clinic who have not be enrolled in a program,0,,,,REQUIRED,
e4a1a524-d557-11ea-87d0-0242ac130003,,Yendanafe Identifier,ID assigned to patients when registering them from Yendanafe Application,0,,,,REQUIRED,
70690634-6522-4552-ba66-43eda7c30217,,IC3D Identifier,ID assigned to patients when enrolled in IC3D Study,0,,,,REQUIRED,
f7de1b97-013e-49ad-a596-4ada6ede1053,,PDC Identifier,ID assigned to patients when enrolled in PDC,0,,,,REQUIRED,
F4319B47-4141-48DF-9F41-5CF7E6301EC6,,TB program identifier,Identifier assigned to patient enrolled in the TB program.,0,,Ex. NNO 101 TB,,REQUIRED,
```

Save at `configuration/configuration/patientidentifiertypes/identifierTypes.csv`. (`Required` is `0` because none of the original descriptors overrode `required()` from its default of `false`. `Location behavior` is `REQUIRED` for all 7, matching every descriptor's `PatientIdentifierType.LocationBehavior.REQUIRED` override.)

- [ ] **Step 2: Add the constants to `PihMalawiConfigConstants.java`**

Append:
```java
    // Patient identifier types
    public static final String PATIENTIDENTIFIERTYPE_NUTRITION_PROGRAM_NUMBER_UUID = "C9888967-8584-4F36-86B8-51AC368BC720";
    public static final String PATIENTIDENTIFIERTYPE_NUTRITION_PROGRAM_NUMBER_NAME = "Nutrition Program Number";
    public static final String PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_UUID = "f2b29f9b-69d0-4339-b1aa-55a511672558";
    public static final String PATIENTIDENTIFIERTYPE_PALLIATIVE_CARE_NUMBER_NAME = "Palliative Care Number";
    public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_UUID = "f51dfa3a-95de-4040-b4eb-52d2de718a74";
    public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_NAME = "IC3 Identifier";
    public static final String PATIENTIDENTIFIERTYPE_YENDANAFE_IDENTIFIER_UUID = "e4a1a524-d557-11ea-87d0-0242ac130003";
    public static final String PATIENTIDENTIFIERTYPE_YENDANAFE_IDENTIFIER_NAME = "Yendanafe Identifier";
    public static final String PATIENTIDENTIFIERTYPE_IC3D_IDENTIFIER_UUID = "70690634-6522-4552-ba66-43eda7c30217";
    public static final String PATIENTIDENTIFIERTYPE_IC3D_IDENTIFIER_NAME = "IC3D Identifier";
    public static final String PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_UUID = "f7de1b97-013e-49ad-a596-4ada6ede1053";
    public static final String PATIENTIDENTIFIERTYPE_PDC_IDENTIFIER_NAME = "PDC Identifier";
    public static final String PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_UUID = "F4319B47-4141-48DF-9F41-5CF7E6301EC6";
    public static final String PATIENTIDENTIFIERTYPE_TB_PROGRAM_IDENTIFIER_NAME = "TB program identifier";
```

- [ ] **Step 3: Update consumers**

Run `grep -n "PihMalawiPatientIdentifierTypes\." <file>` on each of the 7 files listed above and replace every occurrence: `PihMalawiPatientIdentifierTypes.XXX.uuid()` → `PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_XXX_UUID`, `PihMalawiPatientIdentifierTypes.XXX.name()` → `PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_XXX_NAME`, `PihMalawiPatientIdentifierTypes.XXX.description()` → `PihMalawiConfigConstants.PATIENTIDENTIFIERTYPE_XXX_DESCRIPTION` (add a `_DESCRIPTION` constant only for `IC3_IDENTIFIER` — `MalawiPatientValidatorTest.java:24` is the one call site that uses `.description()`; add `public static final String PATIENTIDENTIFIERTYPE_IC3_IDENTIFIER_DESCRIPTION = "ID assigned to patients at IC3 clinic who have not be enrolled in a program";` to `PihMalawiConfigConstants.java` for this). In each modified file, add `import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;` and remove `import org.openmrs.module.pihmalawi.metadata.PihMalawiPatientIdentifierTypes;`.

Note: `PalliativeCareMetadata.java`, `PdcMetadata.java`, `TbProgramMetadata.java`, `ChronicCareMetadata.java` are hybrid files (their `ProgramDescriptor` parts are handled in Task 11) — only touch the `PihMalawiPatientIdentifierTypes` references in this task; leave everything else in those files alone until Task 11.

- [ ] **Step 4: Delete the Java classes**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/PihMalawiPatientIdentifierTypes.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PatientIdentifierBundle.java
```

- [ ] **Step 5: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Run the validator test**

Run: `mvn -pl api -am test -Dtest=MalawiPatientValidatorTest`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate PatientIdentifierBundle to Initializer identifiertypes.csv"
```

---

### Task 8: Provider and Program attribute types → extend `attributetypes.csv`

`ProviderAttributeTypeBundle` installs 5 provider attribute types; `ProgramAttributeTypeBundle` installs 1 program attribute type. Only `ProviderAttributeTypes.HEALTH_FACILITY` has an external consumer (`VhwProviderIdentifierGenerator.java:49`) — everything else is CSV-only. `ProgramAttributeTypeBundle`'s attribute already uses the existing `PihMalawiConstants.TRANSFERRED_OUT_PROGRAM_ATTRIBUTE_TYPE` constant (outside the `metadata` package, unrelated to this migration) — leave that constant exactly where it is.

**Files:**
- Modify: `configuration/configuration/attributetypes/attributetypes.csv`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProviderAttributeTypeBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProgramAttributeTypeBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/handler/ProgramAttributeTypeDeployHandler.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/provider/VhwProviderIdentifierGenerator.java:49`

**Interfaces:**
- Produces: `PROVIDERATTRIBUTETYPE_HEALTH_FACILITY_UUID = "94047146-7918-4927-9401-F4284A10C7FD"`.

- [ ] **Step 1: Append rows to `attributetypes.csv`**

The current file is:
```csv
UUID,Entity name,Name,Description,Datatype classname,Datatype config,Min occurs,Max occurs,Preferred handler classname
62eb8441-0326-11e6-8c93-e82aea237783,Location,Location Code,Short name or code for used in identifier types and for concise display of locations,org.openmrs.customdatatype.datatype.FreeTextDatatype,,0,1,
```
Append these 6 rows:
```csv
30375A78-FA92-4C5C-A2FD-7E8339EC69CF,Provider,Phone Number,Provider phone number,org.openmrs.customdatatype.datatype.FreeTextDatatype,,0,1,
0c267ae8-f793-4cf8-9b27-93accaa45d86,Provider,Households,Number of households monitored by a VHW,org.openmrs.customdatatype.datatype.FreeTextDatatype,,0,1,
c8ef8a16-a8cd-4748-b0ea-e8a1ec503fbb,Provider,Date Hired,The date the provider was hired.,org.openmrs.customdatatype.datatype.DateDatatype,,0,1,org.openmrs.web.attribute.handler.DateFieldGenDatatypeHandler
C0E1F105-DD36-4577-B00E-87A08D446A3A,Provider,Passed HH Test,Passed HH Test,org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype,0E483511-6278-4D1A-881A-6385C223FAC7,0,1,
94047146-7918-4927-9401-F4284A10C7FD,Provider,Health Facility,Health Facility,org.openmrs.module.coreapps.customdatatype.LocationDatatype,Health Facility,0,1,
D576AAE9-6537-4549-88A5-49A761AA93E0,Program,Transferred out location,Transferred out facility location,org.openmrs.customdatatype.datatype.FreeTextDatatype,Health Facility,1,1,
```
Notes:
- The `Passed HH Test` row's UUID (`C0E1F105-DD36-4577-B00E-87A08D446A3A` in the old bundle) is replaced with the concept UUID it references (`ChwManagementConcepts.HH_MODEL_TEST_CONCEPT = "0E483511-6278-4D1A-881A-6385C223FAC7"`) — **stop and check this**: re-open the original `ProviderAttributeTypeBundle.java` (via `git show HEAD:api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProviderAttributeTypeBundle.java` if already deleted) and confirm the attribute type's own UUID is `C0E1F105-DD36-4577-B00E-87A08D446A3A`, distinct from the coded concept's UUID `0E483511-6278-4D1A-881A-6385C223FAC7` used as `Datatype config`. Use `C0E1F105-DD36-4577-B00E-87A08D446A3A` as this row's own UUID (first column), and `0E483511-6278-4D1A-881A-6385C223FAC7` only in the `Datatype config` column.
- The old bundle only installed "Passed HH Test" conditionally (only if the `ChwManagementConcepts.HH_MODEL_TEST_CONCEPT` concept already existed in the DB, checked at runtime). Initializer CSVs don't support conditional rows — since `HH_MODEL_TEST_CONCEPT` is a concept defined and owned by this same module's own `concepts.csv` (verify: `grep -i "0E483511-6278-4D1A-881A-6385C223FAC7" configuration/configuration/concepts/concepts.csv`), it will always exist once concepts load (which happens before attribute types in Initializer's fixed domain order), so the unconditional CSV row is safe. If the grep finds no match, stop and flag this to the user before proceeding — it means the conditional logic was load-bearing.

- [ ] **Step 2: Add the constant to `PihMalawiConfigConstants.java`**

```java
    // Provider attribute types
    public static final String PROVIDERATTRIBUTETYPE_HEALTH_FACILITY_UUID = "94047146-7918-4927-9401-F4284A10C7FD";
```

- [ ] **Step 3: Update `VhwProviderIdentifierGenerator.java:49`**

Change:
```java
String healthFacility = ProviderAttributeTypeBundle.ProviderAttributeTypes.HEALTH_FACILITY;
```
to:
```java
String healthFacility = PihMalawiConfigConstants.PROVIDERATTRIBUTETYPE_HEALTH_FACILITY_UUID;
```
Add `import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;`, remove `import org.openmrs.module.pihmalawi.metadata.deploy.bundle.ProviderAttributeTypeBundle;`.

- [ ] **Step 4: Delete the Java classes**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProviderAttributeTypeBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProgramAttributeTypeBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/handler/ProgramAttributeTypeDeployHandler.java
```

- [ ] **Step 5: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate Provider/Program attribute type bundles into attributetypes.csv"
```

---

### Task 9: Roles and Privileges domain

`UserRoleBundle` installs 1 new privilege and 3 new roles (referencing many pre-existing core privileges/roles by name — those are **not** created here, only referenced). No external consumers of `UserRoleBundle.UserRoles` exist. CSV-only.

**Files:**
- Create: `configuration/configuration/privileges/privileges.csv`
- Create: `configuration/configuration/roles/roles.csv`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/UserRoleBundle.java`

- [ ] **Step 1: Create `privileges.csv`**

```csv
Uuid,Privilege name,Description
cbbfac84-e94f-42a9-9925-6e99f7a8a1b9,View clinical data,View patient's clinical data
```

Save at `configuration/configuration/privileges/privileges.csv`.

- [ ] **Step 2: Create `roles.csv`**

```csv
Uuid,Role name,Description,Inherited roles,Privileges
9c419a7e-f11f-4d66-8944-aab24b538f9c,Clinical data,Access to clinical data,,View clinical data
6e1ec1d3-80bd-4e08-8665-75784f09df02,View nutrition patients,View patient records who are enrolled in the nutrition program.,,View Patient Programs; Get Order Types; Get Field Types; View Orders; Get Identifier Types; Get Forms; Get Orders; Patient Dashboard - View Overview Section; View Field Types; View People; Get Patient Programs; View Visit Attribute Types; Get People; Get Person Attribute Types; View Observations; Get Allergies; Patient Overview - View Patient Actions; View Problems; Get Problems; Get Programs; View Identifier Types; View Patient Identifiers; Patient Dashboard - View Demographics Section; View Encounter Types; Provider Management API; Manage Forms; View Allergies; View Navigation Menu; View Visits; Get Visit Types; Get Visit Attribute Types; Get Visits; Get Providers; View Visit Types; View Users; View Order Types; Patient Dashboard - View Visits Section; View Person Attribute Types; View Unpublished Forms; Patient Overview - View Programs; Patient Overview - View Problem List; Patient Overview - View Allergies; Get Encounters; Get Users; View Programs; Get Locations; Get Encounter Types; Get Patients; View Encounters; View Forms; Get Observations; View Locations; Get Patient Identifiers; View Providers; View Patients; Patient Dashboard - View Encounters Section; Manage Encounter Roles; Patient Dashboard - View Patient Summary
d2742175-fdf4-4d3d-81cc-f8a072df0688,Edit nutrition patients,This role allows the user to view and edit patient nutrition data.,View nutrition patients,Add Allergies; Add Encounters; Add Observations; Add Patient Identifiers; Add Patient Programs; Add Patients; Add People; Add Visits; Delete Encounters; Delete Observations; Delete Patient Identifiers; Delete Patient Programs; Delete Patients; Delete People; Delete Visits; Edit Allergies; Edit Observations; Edit Orders; Edit Patient Identifiers; Edit Patient Programs; Edit Patients; Edit People; Edit Users; Edit Visits; Form Entry; Get Concepts; Get Encounter Roles; Remove Allergies; Remove Problems; SQL Level Access; View Calculations; View Concepts; View Encounter Roles
B29C7016-303E-4B95-B31E-D6B89F16F443,Manage nutrition program,Access to managing the nutrition program,Anonymous; Provider; View nutrition patients; Edit nutrition patients,
```

Save at `configuration/configuration/roles/roles.csv`. (Row 4's "Inherited roles" column lists `Anonymous; Provider; View nutrition patients; Edit nutrition patients` — matching the original bundle's `install(role(..., idSet("Anonymous","Provider","View nutrition patients","Edit nutrition patients"), null, ...))`, where the third `idSet` argument was inherited roles and the fourth `null` was privileges.)

- [ ] **Step 2: Delete the bundle**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/UserRoleBundle.java
```

- [ ] **Step 3: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate UserRoleBundle to Initializer roles/privileges CSVs"
```

---

### Task 10: EncounterTypes domain (mechanical conversion)

`EncounterTypeBundle.java` calls `install(EncounterTypes.X)` for 98 of the 99 constants defined in `EncounterTypes.java` (only `ADMINISTRATION` is defined but never installed by the bundle — it references a pre-existing encounter type, not one this module creates). This is the largest single domain; convert it mechanically rather than by hand-copying each row, using the exact rule and worked examples below, since transcription errors are the main risk on a table this size.

**Files:**
- Create: `configuration/configuration/encountertypes/encounterTypes.csv`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/EncounterTypes.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/EncounterTypeBundle.java`
- Modify: every consumer found in Step 4 below (~30+ files — reporting library/report/evaluator classes, `omod` taglibs and controllers, `MetadataInitializer.java`, tests)

**Interfaces:**
- Produces: for every `EncounterTypeDescriptor` constant `X` in `EncounterTypes.java`, add `PihMalawiConfigConstants.ENCOUNTERTYPE_<X>_UUID` (always) and `PihMalawiConfigConstants.ENCOUNTERTYPE_<X>_NAME` (only where a consumer calls `.name()` on it — determined in Step 4).

- [ ] **Step 1: Read the source files in full**

Read `api/src/main/java/org/openmrs/module/pihmalawi/metadata/EncounterTypes.java` (620 lines, 99 constants) and `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/EncounterTypeBundle.java` (98 `install(EncounterTypes.X)` calls, already fully known from git history — the exact 98 identifiers are, in order: `ART_ANNUAL_SCREENING, ART_INITIAL, ART_FOLLOWUP, ART_FOLLOWUP_UP_TESTING, HTN_DIABETES_INITIAL, HTN_DIABETES_FOLLOWUP, HTN_DIABETES_TESTS, HTN_DIABETES_ANNUAL_TESTS, HTN_DIABETES_HOSPITALIZATIONS, ASTHMA_INITIAL, ASTHMA_FOLLOWUP, ASTHMA_HOSPITALIZATION, ASTHMA_PEAKFLOW, EPILEPSY_INITIAL, EPILEPSY_FOLLOWUP, MENTAL_HEALTH_INITIAL, MENTAL_HEALTH_SCREENING, MENTAL_HEALTH_FOLLOWUP, CHF_INITIAL, CHF_FOLLOWUP, CHF_ANNUAL_SCREENING, CHF_QUARTERLY_HIV_SCREENING, CHF_HOSPITALIZATIONS, CHF_ECHOCARDIOGRAM, CHF_ELECTROCARDIOGRAPHIC, CHF_CHEST_XRAY, CKD_INITIAL, CKD_FOLLOWUP, CKD_ANNUAL_SCREENING, CKD_QUARTERLY_SCREENING, CKD_HOSPITALIZATIONS, CKD_IMAGING, NCD_OTHER_INITIAL, NCD_OTHER_FOLLOWUP, NCD_OTHER_QUARTERLY_LABS, NCD_OTHER_ANNUAL_LABS, NCD_OTHER_HOSPITAL, PALLIATIVE_INITIAL, PALLIATIVE_FOLLOWUP, CHECK_IN, BLOOD_PRESSURE_SCREENING, BLOOD_SUGAR_SCREENING, LAB_STATION_SCREENING, NUTRITION_SCREENING, NURSE_EVALUATION, HTC_SCREENING, CERVICAL_CANCER_SCREENING, VIRAL_LOAD_SCREENING, DNA_PCR_SCREENING, ADHERENCE_COUNSELING, TB_SCREENING, TB_TEST_RESULTS, IC3_CLINICIAN_PLAN, TRACE_INITIAL, TRACE_FOLLOWUP, PDC_INITIAL, PDC_FOLLOWUP, PDC_TRISOMY21_INITIAL, PDC_TRISOMY21_FOLLOWUP, PDC_CLEFT_CLIP_PALLET_INITIAL, PDC_CLEFT_CLIP_PALLET_FOLLOWUP, PDC_DEVELOPMENTAL_DELAY_FOLLOWUP, PDC_DEVELOPMENTAL_DELAY_INITIAL, PDC_OTHER_DIAGNOSIS_INITIAL, PDC_OTHER_DIAGNOSIS_FOLLOWUP, PDC_HOSPITALIZATION_HISTORY, VISION_TEST, HEARING_TEST, RADIOLOGY_SCREENING, PDC_COMPLICATIONS, PDC_TRISOMY21_LAB_TESTS, PDC_HB_AND_OTHER_LAB_TESTS, HIE_AND_DEV_DELAY_LAB_TESTS, NUTRITION_INITIAL, NUTRITION_FOLLOWUP, NUTRITION_ADULTS_INITIAL, NUTRITION_ADULTS_FOLLOWUP, NUTRITION_PDC_INITIAL, NUTRITION_PDC_FOLLOWUP, NUTRITION_INFANT_INITIAL, NUTRITION_INFANT_FOLLOWUP, NUTRITION_PREGNANT_TEENS_INITIAL, NUTRITION_PREGNANT_TEENS_FOLLOWUP, TEEN_CLUB_INITIAL, TEEN_CLUB_FOLLOWUP, TEEN_CLUB_INTAKE_SURVEY, SICKLE_CELL_DISEASE_INITIAL, SICKLE_CELL_DISEASE_FOLLOWUP, SICKLE_CELL_QUARTERLY_SCREENING, SICKLE_CELL_ANNUAL_SCREENING, SICKLE_CELL_HOSPITALIZATIONS, OLD_TB_INITIAL, OLD_TB_FOLLOWUP, TB_INITIAL, TB_FOLLOWUP, TB_TESTS, TB_POST_LUNG_DISEASE`. `PDC_DEVELOPMENTAL_DELAY_FOLLOWUP` appears twice in the bundle's install list — install it once in the CSV.)

- [ ] **Step 2: Generate `encounterTypes.csv`**

Header: `Uuid,Void/Retire,Name,Description`. For each of the 98 identifiers listed in Step 1 (deduplicated), add one row: `Uuid` = that constant's `uuid()` return value, `Void/Retire` = empty, `Name` = its `name()` return value, `Description` = its `description()` return value. Two fully worked example rows, transcribed directly from the source file, to pin down the exact format (quote any description containing a comma):

```csv
Uuid,Void/Retire,Name,Description
664b9442-977f-11e1-8993-905e29aff6c1,,DIABETES HYPERTENSION INITIAL VISIT,Diabetes hypertension initial visit
66079de4-a8df-11e5-bf7f-feff819cdc9f,,DIABETES HYPERTENSION FOLLOWUP,Diabetes hypertension followup
```
(these correspond to `HTN_DIABETES_INITIAL` and `HTN_DIABETES_FOLLOWUP`). Continue for all 98 rows, reading each constant's exact `uuid()`/`name()`/`description()` values directly from the source file opened in Step 1 — do not paraphrase or infer values.

Save at `configuration/configuration/encountertypes/encounterTypes.csv`.

- [ ] **Step 3: Verify row count**

Run: `wc -l configuration/configuration/encountertypes/encounterTypes.csv`
Expected: 99 (1 header + 98 unique encounter types — remember `PDC_DEVELOPMENTAL_DELAY_FOLLOWUP` is only one row despite appearing twice in the old bundle's install list).

- [ ] **Step 4: Find every consumer and determine which need `_NAME`**

Run:
```bash
grep -rl "\bEncounterTypes\.[A-Z]" --include="*.java" api omod
```
For each file found, grep within it for `EncounterTypes\.[A-Z_]*\.name()` to find which specific constants need a `_NAME` constant (not just `_UUID`) — e.g. `omod/.../ETraceAccessTag.java` and `omod/.../EMastercardAccessTag.java` call `.name()` on several (`TRACE_INITIAL`, `ASTHMA_INITIAL`, `HTN_DIABETES_INITIAL`, `EPILEPSY_INITIAL`, `PALLIATIVE_INITIAL`, `CHF_INITIAL`, `CKD_INITIAL`, `NCD_OTHER_INITIAL`, `MENTAL_HEALTH_INITIAL` — confirmed from earlier investigation). Build the complete list from the actual grep output — this file set is large enough that hand-enumerating it here would be error-prone; the grep is the source of truth.

- [ ] **Step 5: Add constants to `PihMalawiConfigConstants.java`**

For all 99 `EncounterTypes.java` constants (not just the 98 in the CSV — `ADMINISTRATION` still needs a Java constant even without a CSV row, since it references a pre-existing encounter type), add `public static final String ENCOUNTERTYPE_<NAME>_UUID = "...";` using the exact UUID from `EncounterTypes.java`. For each constant identified in Step 4 as needing `.name()`, also add `public static final String ENCOUNTERTYPE_<NAME>_NAME = "...";` using its exact `name()` value.

- [ ] **Step 6: Update every consumer**

For each file from Step 4, replace `EncounterTypes.X.uuid()` → `PihMalawiConfigConstants.ENCOUNTERTYPE_X_UUID` and `EncounterTypes.X.name()` → `PihMalawiConfigConstants.ENCOUNTERTYPE_X_NAME`. Add `import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;` and remove `import org.openmrs.module.pihmalawi.metadata.EncounterTypes;` in each. Also fix `MetadataInitializer.java`'s remaining `EncounterTypes.CHECK_IN.uuid()` reference the same way (this is the other half of the concatenation started in Task 4).

- [ ] **Step 7: Delete the Java classes**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/EncounterTypes.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/EncounterTypeBundle.java
```

- [ ] **Step 8: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS. Fix any remaining unresolved `EncounterTypes` references the Step 4 grep may have missed (re-run `grep -rn "EncounterTypes\." api omod` — it should return zero results before continuing).

- [ ] **Step 9: Run the affected report tests**

Run: `mvn -pl api -am test -Dtest=HIVCohortReportTest,HivVisitsReportTest,WeeklyEncounterByLocationReportTest,WeeklyEncounterByUserReportTest`
Expected: PASS.

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate EncounterTypeBundle to Initializer encountertypes.csv"
```

---

### Task 11: Programs, ProgramWorkflows, ProgramWorkflowStates domain (mechanical conversion)

This is the most complex domain: `ProgramBundle` (1 program, defined in `Programs.java`) and `PihMalawiMetadataBundle` (7 programs, defined across `PalliativeCareMetadata.java`, `MentalHealthMetadata.java`, `PdcMetadata.java`, `NutritionProgramMetadata.java`, `TeenClubProgramMetadata.java`, `TbProgramMetadata.java` (2 programs)) install a total of **8 programs**, each with nested `ProgramWorkflowDescriptor`s, each with nested `ProgramWorkflowStateDescriptor`s.

**Important:** `PalliativeCareMetadata.java`, `MentalHealthMetadata.java`, `PdcMetadata.java`, `NutritionProgramMetadata.java`, `TeenClubProgramMetadata.java`, `TbProgramMetadata.java` are **hybrid files** — they extend `CommonMetadata` and contain runtime-lookup helper methods (e.g. `getProgram(PALLIATIVE_CARE_PROGRAM.name())`) *in addition to* the `ProgramDescriptor`/`ProgramWorkflowDescriptor`/`ProgramWorkflowStateDescriptor` static fields. **Do not delete these files.** Only remove the descriptor static fields and their `metadatadeploy` imports, and update the file's own internal helper methods to reference the new `PihMalawiConfigConstants` fields instead of the descriptor fields being removed. `Programs.java` (used only by `ProgramBundle`, no runtime-helper methods) is fully deleted.

**Files:**
- Create: `configuration/configuration/programs/programs.csv`
- Create: `configuration/configuration/programworkflows/programWorkflows.csv`
- Create: `configuration/configuration/programworkflowstates/programWorkflowStates.csv`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/PihMalawiConfigConstants.java`
- Modify: `configuration/constants.yml`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/Programs.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProgramBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PihMalawiMetadataBundle.java`
- Modify (remove descriptor fields only, keep the rest): `PalliativeCareMetadata.java`, `MentalHealthMetadata.java`, `PdcMetadata.java`, `NutritionProgramMetadata.java`, `TeenClubProgramMetadata.java`, `TbProgramMetadata.java`
- Modify: any other consumer found in Step 6 below

**Interfaces:**
- Produces: `PROGRAM_<NAME>_UUID` for all 8 programs; `PROGRAMWORKFLOW_<NAME>_UUID` and `PROGRAMWORKFLOWSTATE_<NAME>_UUID` only for the ones a consumer outside the defining file actually uses (checked in Step 6 — most are only referenced by their own `install()`/`workflows()`/`states()` tree and don't need to survive as Java constants at all once the CSV exists).

- [ ] **Step 1: Confirm the 8 top-level programs**

These are already fully known (verified against the source files):

| Program | UUID | Name | Description | Concept UUID (symbolic) |
|---|---|---|---|---|
| `Programs.CHRONIC_CARE_PROGRAM` | `6685164a-977f-11e1-8993-905e29aff6c1` | CHRONIC CARE PROGRAM | Chronic Care Program | `ProgramConcepts.CHRONIC_CARE_PROGRAM_CONCEPT` = `655f4f42-977f-11e1-8993-905e29aff6c1` |
| `PalliativeCareMetadata.PALLIATIVE_CARE_PROGRAM` | `acbd87f3-566f-4386-a11e-877e612d3911` | Palliative care program | Palliative care program | `PalliativeCareConcepts.PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID` (read from `metadata/concept/PalliativeCareConcepts.java`) |
| `MentalHealthMetadata.MH_CARE_PROGRAM` | `60357F01-536E-4B59-A851-B000F801FB13` | MENTAL HEALTH CARE PROGRAM | Mental Health Care Program | `ProgramConcepts.MH_CARE_PROGRAM_CONCEPT` = `406AD643-79A3-4019-9888-3EFBB9B24FB0` |
| `PdcMetadata.PDC_PROGRAM` | `cffd61d1-f087-41df-86c7-fbd6b6e9ab1e` | PEDIATRIC DEVELOPMENT CLINIC PROGRAM | Pediatric Development Clinic Program | `ProgramConcepts.PDC_PROGRAM_CONCEPT` = `74f09d38-4e1e-4acb-a8d0-04b7090fcb77` |
| `NutritionProgramMetadata.NUTRITION_PROGRAM` | `FECD888E-D547-4E1D-A012-56CA8874D2E1` | Nutrition program | Nutrition program | `NutritionConcepts.NUTRITION_PROGRAM_CONCEPT_UUID` (read from `metadata/concept/NutritionConcepts.java`); outcomes concept `ProgramConcepts.GENERIC_OUTCOME_CONCEPT_UUID` = `73eb05c2-e4be-4d82-bcad-ffec1be67d01` |
| `TeenClubProgramMetadata.TEEN_CLUB_PROGRAM` | `54100564-4759-4CBD-9A73-B38D6DBAC7B9` | Teen club program | Teen club program | `TeenClubConcepts.TEEN_CLUB_PROGRAM_CONCEPT_UUID` (read from `metadata/concept/TeenClubConcepts.java`); outcomes concept `73eb05c2-e4be-4d82-bcad-ffec1be67d01` |
| `TbProgramMetadata.OLD_TB_PROGRAM` | `66850d9e-977f-11e1-8993-905e29aff6c1` | Old TB PROGRAM | Old Tuberculosis Program | `TbProgramConcepts.OLD_TB_PROGRAM_CONCEPT_UUID` (read from `metadata/concept/TbProgramConcepts.java`) |
| `TbProgramMetadata.TB_PROGRAM` | `52D0036A-AB35-475E-A4D4-1826CCD985D6` | TB PROGRAM | Tuberculosis Program | `TbProgramConcepts.TB_PROGRAM_CONCEPT_UUID` (read from `metadata/concept/TbProgramConcepts.java`) |

Read `metadata/concept/PalliativeCareConcepts.java`, `NutritionConcepts.java`, `TeenClubConcepts.java`, `TbProgramConcepts.java` now to resolve the 4 concept UUIDs marked "read from" above — record the literal values before continuing to Step 2.

- [ ] **Step 2: Read every workflow/state in full**

Read, in full: `Programs.java` (already fully known: `CHRONIC_CARE_TREATMENT_STATUS` workflow with states `STATUS_ON_TREATMENT`/`STATUS_IN_ADVANCED_CARE`/`STATUS_TRANSFERRED_OUT`/`STATUS_DIED`/`STATUS_DISCHARGED`/`STATUS_DEFAULTED`/`STATUS_TREATMENT_STOPPED`, plus 6 more disease-specific workflows each with the same 7-state shape reusing the same `ProgramConcepts.CHRONIC_CARE_STATUS_*` concept UUIDs — `SICKLE_CELL_DISEASE_TREATMENT_WORKFLOW`, `CKD_TREATMENT_WORKFLOW`, `CHF_TREATMENT_WORKFLOW`, `DIABETES_HYPERTENSION_TREATMENT_WORKFLOW`, `NCD_OTHER_TREATMENT_WORKFLOW`, `ASTHMA_TREATMENT_WORKFLOW`), `PalliativeCareMetadata.java`, `MentalHealthMetadata.java`, `PdcMetadata.java`, `NutritionProgramMetadata.java`, `TeenClubProgramMetadata.java`, `TbProgramMetadata.java`.

- [ ] **Step 3: Add reused UUIDs to `constants.yml`**

The `ProgramConcepts.CHRONIC_CARE_STATUS_*` concept UUIDs are each reused across 7 different workflows in `Programs.java` alone (on-treatment, in-advanced-care, transferred-out, died, discharged, defaulted, treatment-stopped — 7 concepts × up to 7 workflows). Per the Global Constraints, add each reused UUID once to `configuration/constants.yml`:

```yaml
concept:
  chronicCareStatusOnTreatment:
    uuid: "65664784-977f-11e1-8993-905e29aff6c1"
  chronicCareStatusInAdvancedCare:
    uuid: "9af03945-c8c1-11e8-9bc6-0242ac110001"
  chronicCareStatusTransferredOut:
    uuid: "655b604e-977f-11e1-8993-905e29aff6c1"
  chronicCareStatusDied:
    uuid: "655b5e46-977f-11e1-8993-905e29aff6c1"
  chronicCareStatusDischarged:
    uuid: "6566dba4-977f-11e1-8993-905e29aff6c1"
  chronicCareStatusDefaulted:
    uuid: "655b5f4a-977f-11e1-8993-905e29aff6c1"
  chronicCareStatusTreatmentStopped:
    uuid: "655a6acc-977f-11e1-8993-905e29aff6c1"
```
(these 7 lines are new — do not overwrite the existing `mentalHealthTreatmentStatus`/`epilepsyTreatmentStatus`/`program.mentalHealth`/`programWorkflow.*` entries already in the file; append after them.) Reference these from `programworkflowstates.csv` as `${concept.chronicCareStatusOnTreatment.uuid}` etc. As you work through Step 2's other files, apply the same rule to any other UUID you find reused across more than one CSV cell (e.g. any concept reused by more than one state row); hard-code everything used exactly once.

- [ ] **Step 4: Generate `programs.csv`**

Header: `Uuid,Name,Description,Void/Retire,Program concept,Outcomes concept`. One row per program from the Step 1 table:

```csv
Uuid,Name,Description,Void/Retire,Program concept,Outcomes concept
6685164a-977f-11e1-8993-905e29aff6c1,CHRONIC CARE PROGRAM,Chronic Care Program,,655f4f42-977f-11e1-8993-905e29aff6c1,
acbd87f3-566f-4386-a11e-877e612d3911,Palliative care program,Palliative care program,,<PalliativeCareConcepts.PALLIATIVE_CARE_PROGRAM_CONCEPT_UUID from Step 1>,
60357F01-536E-4B59-A851-B000F801FB13,MENTAL HEALTH CARE PROGRAM,Mental Health Care Program,,406AD643-79A3-4019-9888-3EFBB9B24FB0,
cffd61d1-f087-41df-86c7-fbd6b6e9ab1e,PEDIATRIC DEVELOPMENT CLINIC PROGRAM,Pediatric Development Clinic Program,,74f09d38-4e1e-4acb-a8d0-04b7090fcb77,
FECD888E-D547-4E1D-A012-56CA8874D2E1,Nutrition program,Nutrition program,,<NutritionConcepts.NUTRITION_PROGRAM_CONCEPT_UUID from Step 1>,73eb05c2-e4be-4d82-bcad-ffec1be67d01
54100564-4759-4CBD-9A73-B38D6DBAC7B9,Teen club program,Teen club program,,<TeenClubConcepts.TEEN_CLUB_PROGRAM_CONCEPT_UUID from Step 1>,73eb05c2-e4be-4d82-bcad-ffec1be67d01
66850d9e-977f-11e1-8993-905e29aff6c1,Old TB PROGRAM,Old Tuberculosis Program,,<TbProgramConcepts.OLD_TB_PROGRAM_CONCEPT_UUID from Step 1>,
52D0036A-AB35-475E-A4D4-1826CCD985D6,TB PROGRAM,Tuberculosis Program,,<TbProgramConcepts.TB_PROGRAM_CONCEPT_UUID from Step 1>,
```
Replace every `<... from Step 1>` placeholder with the literal UUID you resolved in Step 1 before saving — the file must contain no placeholder text. Save at `configuration/configuration/programs/programs.csv`.

- [ ] **Step 5: Generate `programWorkflows.csv` and `programWorkflowStates.csv`**

Header for `programWorkflows.csv`: `Uuid,Void/Retire,Program,Workflow concept`. Header for `programWorkflowStates.csv`: `Uuid,Void/Retire,Workflow,State concept,Initial,Terminal`.

For each `ProgramWorkflowDescriptor` read in Step 2, add one row to `programWorkflows.csv`: `Uuid` = its `uuid()`, `Program` = the owning program's UUID from Step 1/4 (use `${...}` if that program UUID is also reused elsewhere — the 6 disease-specific `Programs.java` workflows all belong to the single `CHRONIC_CARE_PROGRAM`, so add `program.chronicCare.uuid: "6685164a-977f-11e1-8993-905e29aff6c1"` to `constants.yml` and reference `${program.chronicCare.uuid}` in all 7 of those rows), `Workflow concept` = its `conceptUuid()`.

For each `ProgramWorkflowStateDescriptor` read in Step 2, add one row to `programWorkflowStates.csv`: `Uuid` = its `uuid()`, `Workflow` = the owning workflow's UUID (as a `${...}` reference if that workflow is reused by more than one state row — it always is, since every workflow has multiple states, so **every** `Workflow` column value should be a `${...}` reference to a `constants.yml` entry you add for that workflow's UUID), `State concept` = its `conceptUuid()` (using `${...}` for the `ProgramConcepts.CHRONIC_CARE_STATUS_*` ones per Step 3, literal for anything used only once), `Initial`/`Terminal` = its `initial()`/`terminal()` booleans as `true`/blank (Initializer treats a blank as `false`).

Worked example — the full `CHRONIC_CARE_TREATMENT_STATUS` workflow (add to `constants.yml`: `programWorkflow.chronicCareTreatmentStatus.uuid: "6687086a-977f-11e1-8993-905e29aff6c1"`):

`programWorkflows.csv` row:
```csv
6687086a-977f-11e1-8993-905e29aff6c1,,${program.chronicCare.uuid},${concept.chronicCareStatusOnTreatment.uuid}
```
Wait — a `ProgramWorkflowDescriptor`'s `conceptUuid()` for `CHRONIC_CARE_TREATMENT_STATUS` is `ProgramConcepts.CHRONIC_CARE_TREATMENT_STATUS_CONCEPT` (`65766b96-977f-11e1-8993-905e29aff6c1`), a *different* concept from any of the 7 state concepts — add it to `constants.yml` too if reused, else hard-code it (it's used only once here, so hard-code: `65766b96-977f-11e1-8993-905e29aff6c1`).

`programWorkflowStates.csv` rows for this one workflow's 7 states:
```csv
Uuid,Void/Retire,Workflow,State concept,Initial,Terminal
66882650-977f-11e1-8993-905e29aff6c1,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusOnTreatment.uuid},true,
7c4d2e56-c8c2-11e8-9bc6-0242ac110001,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusInAdvancedCare.uuid},true,
6688275e-977f-11e1-8993-905e29aff6c1,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusTransferredOut.uuid},,true
6688286c-977f-11e1-8993-905e29aff6c1,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusDied.uuid},,true
6688297a-977f-11e1-8993-905e29aff6c1,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusDischarged.uuid},,true
3a4eb919-b942-4c9c-ba0e-defcebe5cd4b,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusDefaulted.uuid},,true
dbe76d47-dbc4-4608-a578-97b6b62d9f63,,${programWorkflow.chronicCareTreatmentStatus.uuid},${concept.chronicCareStatusTreatmentStopped.uuid},,true
```
Apply this exact pattern (own UUID literal in the `Uuid` column; every foreign-key column as a `${...}` reference; concept columns as `${...}` when the concept is reused, literal otherwise; booleans as `true`/blank) to every remaining workflow and state read in Step 2, across all 8 programs. There is no shortcut around reading each source file's exact `uuid()`/`conceptUuid()`/`initial()`/`terminal()` values — copy them verbatim, do not infer or guess a value that wasn't explicitly present in the source.

- [ ] **Step 6: Determine which descriptors need to survive as Java constants**

Run:
```bash
grep -rln "\bPrograms\.[A-Z]\|PALLIATIVE_CARE_PROGRAM\|MH_CARE_PROGRAM\|PDC_PROGRAM\b\|NUTRITION_PROGRAM\b\|TEEN_CLUB_PROGRAM\b\|OLD_TB_PROGRAM\|TB_PROGRAM\b" --include="*.java" api omod | grep -v "PalliativeCareMetadata.java\|MentalHealthMetadata.java\|PdcMetadata.java\|NutritionProgramMetadata.java\|TeenClubProgramMetadata.java\|TbProgramMetadata.java\|Programs.java\|ProgramBundle.java\|PihMalawiMetadataBundle.java"
```
For every file in the result, note exactly which program constant it uses and whether via `.uuid()` or `.name()`. (Workflow/state descriptors are consumed only from within their own defining file's `workflows()`/`states()` trees in every case investigated for this plan — if this grep turns up a workflow/state constant used elsewhere too, add the matching `PROGRAMWORKFLOW_`/`PROGRAMWORKFLOWSTATE_` constant to `PihMalawiConfigConstants.java` the same way as the programs below.)

- [ ] **Step 7: Add the 8 program constants to `PihMalawiConfigConstants.java`**

```java
    // Programs
    public static final String PROGRAM_CHRONIC_CARE_UUID = "6685164a-977f-11e1-8993-905e29aff6c1";
    public static final String PROGRAM_PALLIATIVE_CARE_UUID = "acbd87f3-566f-4386-a11e-877e612d3911";
    public static final String PROGRAM_MH_CARE_UUID = "60357F01-536E-4B59-A851-B000F801FB13";
    public static final String PROGRAM_PDC_UUID = "cffd61d1-f087-41df-86c7-fbd6b6e9ab1e";
    public static final String PROGRAM_NUTRITION_UUID = "FECD888E-D547-4E1D-A012-56CA8874D2E1";
    public static final String PROGRAM_TEEN_CLUB_UUID = "54100564-4759-4CBD-9A73-B38D6DBAC7B9";
    public static final String PROGRAM_OLD_TB_UUID = "66850d9e-977f-11e1-8993-905e29aff6c1";
    public static final String PROGRAM_TB_UUID = "52D0036A-AB35-475E-A4D4-1826CCD985D6";
```
Add `_NAME` constants only for whichever of these Step 6 found consumed via `.name()`.

- [ ] **Step 8: Update the hybrid files' internal self-references**

In each of `PalliativeCareMetadata.java`, `MentalHealthMetadata.java`, `PdcMetadata.java`, `NutritionProgramMetadata.java`, `TeenClubProgramMetadata.java`, `TbProgramMetadata.java`: remove the `ProgramDescriptor`/`ProgramWorkflowDescriptor`/`ProgramWorkflowStateDescriptor` static field declarations and the three `org.openmrs.module.metadatadeploy.descriptor.*` imports (keep the class's `extends CommonMetadata` and any other non-descriptor methods/fields untouched). Where the file's own remaining helper methods referenced the deleted fields (e.g. `PalliativeCareMetadata.java`'s `return getProgram(PALLIATIVE_CARE_PROGRAM.name());`), replace with the UUID constant: `return getProgram(PihMalawiConfigConstants.PROGRAM_PALLIATIVE_CARE_UUID);` (the `getProgram(String lookup)` helper inherited from `Metadata.java` accepts a UUID directly, so this is a safe substitution, not just a mechanical rename). Add `import org.openmrs.module.pihmalawi.PihMalawiConfigConstants;` to each.

- [ ] **Step 9: Update every other consumer found in Step 6**

Same substitution pattern as prior tasks: `X.uuid()` → `PihMalawiConfigConstants.PROGRAM_..._UUID`, `X.name()` → `PihMalawiConfigConstants.PROGRAM_..._NAME`, fix imports.

- [ ] **Step 10: Delete `Programs.java` and the two bundles**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/Programs.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/ProgramBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/PihMalawiMetadataBundle.java
```

- [ ] **Step 11: Verify row counts**

Run:
```bash
wc -l configuration/configuration/programs/programs.csv               # expect 9 (1 header + 8)
wc -l configuration/configuration/programworkflows/programWorkflows.csv
wc -l configuration/configuration/programworkflowstates/programWorkflowStates.csv
```
Cross-check the workflow/state counts by counting the `ProgramWorkflowDescriptor`/`ProgramWorkflowStateDescriptor` instances you read across all 7 source files in Step 2 — every one must appear exactly once in the corresponding CSV.

- [ ] **Step 12: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 13: Run the affected tests**

Run: `mvn -pl api -am test -Dtest=ArtRegisterTest,HccRegisterTest,IC3MonthlyIndicatorReportTest,IC3QuarterlyIndicatorReportTest`
Expected: PASS.

- [ ] **Step 14: Commit**

```bash
git add -A
git commit -m "MLW-1732: Migrate Programs/PihMalawiMetadataBundle to Initializer programs CSVs"
```

---

### Task 12: Global properties consolidation; delete `CoreConfigurationBundle`

`CoreConfigurationBundle.install()` and the `saveGlobalProperty(...)` calls in `MetadataInitializer.started()` set global properties. Both get merged into `configuration/configuration/globalproperties/gp.xml`.

**Files:**
- Modify: `configuration/configuration/globalproperties/gp.xml`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/CoreConfigurationBundle.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/activator/MetadataInitializer.java` (remove the `saveGlobalProperty` calls and the `saveGlobalProperty` helper method itself — the explicit Initializer loader invocation replacing them is done in Task 13)

**Interfaces:** none — this task removes Java code, it doesn't add any.

- [ ] **Step 1: Append the properties to `gp.xml`**

Current `gp.xml`:
```xml
<config>
    <globalProperties>
        <!-- SPA -->
        <globalProperty>
            <property>spa.frontend.directory</property>
            <value>configuration/frontend</value>
        </globalProperty>

        <globalProperty>
            <property>pihmalawi.serializer.whitelist.types</property>
            <value>org.openmrs.**,org.apache.commons.collections.comparators.ComparableComparator</value>
        </globalProperty>
    </globalProperties>
</config>
```
Add a new `<globalProperty>` entry for each of the following (values taken verbatim from `CoreConfigurationBundle.java` and `MetadataInitializer.java`, with the encounter-role/visit-type/encounter-type UUIDs already resolved to literals in Tasks 3/4/10):

```xml
        <globalProperty>
            <property>htmlformentry.dateFormat</property>
            <value>dd/MM/yyyy</value>
        </globalProperty>
        <globalProperty>
            <property>htmlformentry.timeFormat</property>
            <value>h:mm aa</value>
        </globalProperty>
        <globalProperty>
            <property>htmlformentry.showDateFormat</property>
            <value>false</value>
        </globalProperty>
        <globalProperty>
            <property>htmlformentry.unknownConcept</property>
            <value>65576584-977f-11e1-8993-905e29aff6c1</value>
        </globalProperty>
        <globalProperty>
            <property>reporting.testPatientsCohortDefinition</property>
            <value></value>
        </globalProperty>
        <globalProperty>
            <property>uiframework.formatter.dateFormat</property>
            <value>dd/MM/yyyy</value>
        </globalProperty>
        <globalProperty>
            <property>uiframework.formatter.datetimeFormat</property>
            <value>dd/MM/yyyy h:mm aa</value>
        </globalProperty>
        <globalProperty>
            <property>emr.unknownLocation</property>
            <value>8d6c993e-c2cc-11de-8d13-0010c6dffd0f</value>
        </globalProperty>
        <globalProperty>
            <property>emr.clinicianEncounterRole</property>
            <value>4f10ad1a-ec49-48df-98c7-1391c6ac7f05</value>
        </globalProperty>
        <globalProperty>
            <property>emr.orderingProviderEncounterRole</property>
            <value>c458d78e-8374-4767-ad58-9f8fe276e01c</value>
        </globalProperty>
        <globalProperty>
            <property>emr.checkInClerkEncounterRole</property>
            <value>cbfe0b9d-9923-404c-941b-f048adc8cdc0</value>
        </globalProperty>
        <globalProperty>
            <property>emr.atFacilityVisitType</property>
            <value>f01c54cb-2225-471a-9cd5-d348552c337c</value>
        </globalProperty>
        <globalProperty>
            <property>webservices.rest.maxResultsAbsolute</property>
            <value>1000</value>
        </globalProperty>
        <globalProperty>
            <property>webservices.rest.maxResultsDefault</property>
            <value>500</value>
        </globalProperty>
        <globalProperty>
            <property>providermanagement.locationTag</property>
            <value>Health Facility</value>
        </globalProperty>
        <globalProperty>
            <property>dashboard.identifiers</property>
            <value>{"9":["ARV Number","HCC Number","KS Number","Chronic Care Number","Palliative Care Number","PDC Identifier","Nutrition Program Number","TB program identifier"]}</value>
        </globalProperty>
        <globalProperty>
            <property>patient_identifier.importantTypes</property>
            <value>ARV Number,HCC Number,Chronic Care Number,Palliative Care Number,PDC Identifier,Nutrition Program Number,TB program identifier</value>
        </globalProperty>
        <globalProperty>
            <property>emrapi.visitAssignmentHandlerAtVisitStart.encounterTypeToVisitTypeMapping</property>
            <value><!-- CHECK_IN encounter type uuid, from PihMalawiConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID (Task 10) -->:f01c54cb-2225-471a-9cd5-d348552c337c</value>
        </globalProperty>
        <globalProperty>
            <property>visits.enableVisits</property>
            <value>true</value>
        </globalProperty>
        <globalProperty>
            <property>FormEntry.enableDashboardTab</property>
            <value>false</value>
        </globalProperty>
        <globalProperty>
            <property>visits.autoCloseVisitType</property>
            <value>f01c54cb-2225-471a-9cd5-d348552c337c</value>
        </globalProperty>
        <globalProperty>
            <property>visits.encounterTypeToVisitTypeMapping</property>
            <value><!-- CHECK_IN encounter type uuid --> :f01c54cb-2225-471a-9cd5-d348552c337c</value>
        </globalProperty>
```

Two of the property values above need the actual `CHECK_IN` encounter type UUID substituted for the `<!-- ... -->` placeholder — that UUID is `PihMalawiConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID`'s literal value, which you already recorded while doing Task 10 (look it up in `PihMalawiConfigConstants.java` if not memorized — it's `EncounterTypes.CHECK_IN`'s `uuid()` from the original file). Write the final value as `<uuid>:f01c54cb-2225-471a-9cd5-d348552c337c` with no comment or placeholder text remaining. Also double check the exact global property name constants (`EmrApiConstants.GP_VISIT_ASSIGNMENT_HANDLER_ENCOUNTER_TYPE_TO_VISIT_TYPE_MAP`, `OpenmrsConstants.GLOBAL_PROPERTY_ENABLE_VISITS`, `OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE`, `OpenmrsConstants.GP_ENCOUNTER_TYPE_TO_VISIT_TYPE_MAPPING`, `HtmlFormEntryConstants.GP_*`, `ReportingConstants.GLOBAL_PROPERTY_TEST_PATIENTS_COHORT_DEFINITION`, `UiFrameworkConstants.GP_FORMATTER_*`) against the actual constant values in their respective jars before finalizing this file — the property-name strings shown above are best-effort transcriptions of what those constants are documented to resolve to; if any differs from what's shown here (check via `javap -constants` on the relevant jar, the same technique used earlier in this investigation for `EmrApiConstants`), use the actual value instead.

Save `gp.xml`.

- [ ] **Step 2: Remove the `saveGlobalProperty` machinery from `MetadataInitializer.java`**

Delete the `saveGlobalProperty(String, String)` method and every call to it inside `started()`, along with the now-unused `GlobalProperty`/`Context.getAdministrationService()` usage those calls needed and the `PihMalawiConstants`/`EmrApiConstants`/`OpenmrsConstants` imports that become unused as a result. Leave the `deployService.installBundles(...)` line alone for now — it's replaced in Task 13.

- [ ] **Step 3: Delete `CoreConfigurationBundle.java`**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/CoreConfigurationBundle.java
```

- [ ] **Step 4: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "MLW-1732: Consolidate global properties into gp.xml, delete CoreConfigurationBundle"
```

---

### Task 13: Rewire `MetadataInitializer` to drive Initializer explicitly; delete remaining dead code

At this point every `Bundle` class is deleted. `MetadataInitializer.started()` still calls `deployService.installBundles(Context.getRegisteredComponents(MetadataBundle.class))`, which is now a no-op (no `MetadataBundle` components remain) but still compiles against `metadatadeploy-api`. Replace it with explicit, manually-controlled Initializer loading, matching the pattern in `openmrs-module-rwandaemr`'s `RwandaEmrActivator`/`InitializerSetup`.

**Files:**
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/activator/MetadataInitializer.java`
- Modify: `api/src/main/java/org/openmrs/module/pihmalawi/activator/PihMalawiModuleActivator.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/VersionedPihConceptBundle.java`
- Delete: `api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/PihConstructors.java`

**Interfaces:**
- Consumes: `org.openmrs.module.initializer.api.InitializerService` (already a transitive dependency via `initializer-api`, already used elsewhere indirectly — verify with `grep -rn "InitializerService" api/pom.xml` if unsure whether an explicit dependency is declared; it is not needed since `initializer-api` is already a pihmalawi dependency per `pom.xml:174-175`).
- Produces: `MetadataInitializer.started()` with no `metadatadeploy` references at all.

- [ ] **Step 1: Confirm `VersionedPihConceptBundle` and `PihConstructors` are unused**

Run:
```bash
grep -rn "extends VersionedPihConceptBundle" --include="*.java" api omod
grep -rn "PihConstructors\." --include="*.java" api omod
```
Expected: both empty (already verified during investigation — `VersionedPihConceptBundle` is never subclassed, and `PihConstructors` is only used by `ProgramAttributeTypeBundle`, deleted in Task 8).

- [ ] **Step 2: Rewrite `MetadataInitializer.java`**

```java
package org.openmrs.module.pihmalawi.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.util.Collections;

public class MetadataInitializer implements Initializer {

    protected static final Log log = LogFactory.getLog(MetadataInitializer.class);

    /**
     * @see Initializer#started()
     */
    @Override
    public synchronized void started() {
        InitializerService initializerService = Context.getService(InitializerService.class);
        for (Loader loader : initializerService.getLoaders()) {
            log.info("Loading from Initializer: " + loader.getDomainName());
            try {
                loader.loadUnsafe(Collections.<String>emptyList(), true);
            }
            catch (Exception e) {
                throw new IllegalStateException("An error occurred while loading Initializer domain: " + loader.getDomainName(), e);
            }
        }
    }

    @Override
    public void stopped() {
    }
}
```

- [ ] **Step 3: Disable Initializer's automatic startup load in `PihMalawiModuleActivator.java`**

Add a static initializer block near the top of the class (before the `getInitializers()`/`started()` methods), so it runs before Initializer's own module activator does:

```java
    // Disable Initializer's own automatic domain loading on module startup — MetadataInitializer
    // (run first in getInitializers(), below) takes explicit control of when domains load instead.
    static {
        System.setProperty("initializer.startup.load", "disabled");
    }
```

- [ ] **Step 4: Delete the two remaining dead files**

```bash
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/VersionedPihConceptBundle.java
git rm api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/PihConstructors.java
```

- [ ] **Step 5: Confirm the `metadata/deploy` directory is now empty except for the (now-unused) handler package**

Run: `find api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy -type f`
Expected: empty (the `handler/ProgramAttributeTypeDeployHandler.java` was deleted in Task 8, `bundle/*` all deleted across Tasks 1/3-13, `PihConstructors.java` just deleted). If anything remains, investigate before continuing — it means an earlier task's deletion didn't happen as planned.

- [ ] **Step 6: Remove the now-empty `deploy` directory tree**

```bash
find api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy -type d -empty -delete
```

- [ ] **Step 7: Compile**

Run: `mvn -pl api,omod -am compile`
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "MLW-1732: Rewire MetadataInitializer to explicitly drive Initializer loaders"
```

---

### Task 14: Remove `metadatadeploy`/`metadatasharing` dependencies

Every reference is now gone. This is the final cleanup — safe now that nothing in `api`/`omod` imports either module.

**Files:**
- Modify: `pom.xml`
- Modify: `distro/openmrs-distro.properties`
- Modify: `omod/src/main/resources/config.xml`

- [ ] **Step 1: Confirm no references remain**

Run:
```bash
grep -rln "metadatadeploy\|metadatasharing\|MetadataDeploy\|MetadataSharing" --include="*.java" api omod
```
Expected: empty. If not empty, stop and fix those files before continuing.

- [ ] **Step 2: Edit `pom.xml`**

Remove:
```xml
        <metadatadeployVersion>1.13.0</metadatadeployVersion>
```
and
```xml
        <metadatasharingVersion>1.10.0</metadatasharingVersion>
```
from the `<properties>` block, and remove both dependency blocks:
```xml
            <artifactId>metadatadeploy-api</artifactId>
            <version>${metadatadeployVersion}</version>
```
and
```xml
            <artifactId>metadatasharing-api</artifactId>
            <version>${metadatasharingVersion}</version>
```
(remove the full `<dependency>...</dependency>` element each belongs to, not just these two lines).

- [ ] **Step 3: Edit `distro/openmrs-distro.properties`**

Remove:
```
omod.metadatadeploy=${metadatadeployVersion}
```
and
```
omod.metadatasharing=${metadatasharingVersion}
```

- [ ] **Step 4: Edit `omod/src/main/resources/config.xml`**

Remove:
```xml
        <require_module version="${metadatadeployVersion}">org.openmrs.module.metadatadeploy</require_module>
```
and
```xml
        <require_module version="${metadatasharingVersion}">org.openmrs.module.metadatasharing</require_module>
```

- [ ] **Step 5: Full clean build**

Run: `mvn clean install -pl api,omod,configuration,distro -am`
Expected: BUILD SUCCESS, with no resolution errors for the two removed artifacts anywhere in the reactor.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "MLW-1732: Remove metadatadeploy and metadatasharing dependencies"
```

---

### Task 15: Full verification pass

**Files:** none (verification only).

- [ ] **Step 1: Full test suite**

Run: `mvn clean test -pl api,omod -am`
Expected: BUILD SUCCESS, zero failures/errors.

- [ ] **Step 2: Confirm zero remaining metadatadeploy/metadatasharing references anywhere in the repo**

Run:
```bash
grep -rn "metadatadeploy\|metadatasharing" --include="*.xml" --include="*.java" --include="*.properties" . | grep -v "/target/"
```
Expected: empty output.

- [ ] **Step 3: Confirm the `metadata` package no longer contains any `metadatadeploy` imports**

Run:
```bash
grep -rln "org.openmrs.module.metadatadeploy\|org.openmrs.module.metadatasharing" --include="*.java" api omod
```
Expected: empty output.

- [ ] **Step 4: Spot-check CSV row counts against what was originally installed**

For each new/extended CSV, confirm the row count (minus header) matches the original bundle's `install()` call count, per the table in the design spec section 2 — e.g.:
```bash
tail -n +2 configuration/configuration/encountertypes/encounterTypes.csv | wc -l   # expect 98
tail -n +2 configuration/configuration/programs/programs.csv | wc -l              # expect 8
tail -n +2 configuration/configuration/encounterroles/encounterRoles.csv | wc -l  # expect 5
tail -n +2 configuration/configuration/visittypes/visitTypes.csv | wc -l          # expect 1
tail -n +2 configuration/configuration/relationshiptypes/relationshipTypes.csv | wc -l # expect 1
tail -n +2 configuration/configuration/personattributetypes/personAttributeTypes.csv | wc -l # expect 2
tail -n +2 configuration/configuration/patientidentifiertypes/identifierTypes.csv | wc -l # expect 7
tail -n +2 configuration/configuration/roles/roles.csv | wc -l                    # expect 3
tail -n +2 configuration/configuration/privileges/privileges.csv | wc -l          # expect 1
```

- [ ] **Step 5: Verify the `configuration` module still packages**

Run: `mvn -pl configuration -am package`
Expected: BUILD SUCCESS, and the resulting jar/zip under `configuration/target/` contains the new CSV files (spot check with `unzip -l` or `jar tf` on the built artifact).

- [ ] **Step 6: Report status to the user**

Summarize: total files deleted/moved/created, final `PihMalawiConfigConstants.java` constant count, any deviations from this plan encountered along the way (e.g. a consumer grep in an earlier task turning up more files than listed here), and confirmation that all `mvn test` runs passed. Do not commit anything in this task — it's read-only verification.
