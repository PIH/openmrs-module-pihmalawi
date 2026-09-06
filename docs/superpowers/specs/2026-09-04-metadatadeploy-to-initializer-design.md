# MLW-1732: Replace Metadata Deploy bundles with Initializer

## Goal

Remove the `metadatadeploy` and `metadatasharing` modules from
`openmrs-module-pihmalawi` entirely — dependencies, distro references, and all
code usage — in favor of `openmrs-module-initializer`, consistent with how
metadata is already installed elsewhere in this codebase (see
`configuration/configuration/{locations,attributetypes,concepts,...}`) and in
other PIH `xxx-emr` projects.

This is a deliberately smaller first step ahead of the broader initiative to
make the whole system installable via Initializer, and ahead of a separate,
later ticket that will extract `distro`/`configuration` into a standalone
`apzu-emr`-style content-package project (using the `content.properties` +
`maven-resources-plugin`/`maven-assembly-plugin` pattern used by the other
`xxx-emr` content modules). That later ticket is out of scope here — this
design deliberately keeps using the existing `openmrs-packager-maven-plugin` /
`constants.yml` build mechanism already wired into `configuration/pom.xml`,
since converting that mechanism is unrelated, larger, structural work that
depends on the not-yet-created `apzu-emr` repo. When that split happens, the
CSVs this ticket adds should move over largely unchanged.

## Background

`metadatasharing` is confirmed unused in code — it appears only in
`pom.xml`/`config.xml`. Its removal is trivial.

`metadatadeploy` is used by ~16 `@Component`-registered `Bundle` classes under
`api/src/main/java/org/openmrs/module/pihmalawi/metadata/deploy/bundle/`,
installed via `MetadataInitializer.started()` calling
`deployService.installBundles(...)`. Separately, ~10 plain constant-holder
classes directly under `metadata/` (`EncounterTypes`, `Locations`,
`LocationTags`, `LocationAttributeTypes`, `PersonAttributeTypes`,
`PihMalawiPatientIdentifierTypes`, `Programs`, and the program/workflow/state
descriptors inside the `*ProgramMetadata` classes) only touch `metadatadeploy`
because they implement its `XxxDescriptor` marker interfaces
(`uuid()`/`name()`/`description()`) so the bundles can consume them — these
same classes are read directly by ~73 other files across reporting code,
controllers, and tests (e.g. `EncounterTypes.CHECK_IN.uuid()`).

Some classes that happen to live in the same `metadata` package
(`HivMetadata`, `TbMetadata`, `CommonMetadata`, `ChronicCareMetadata`,
`group/*TreatmentGroup`, `Metadata.java`) are unrelated runtime report-lookup
helpers with no `metadatadeploy` dependency at all — out of scope for this
ticket.

`metadata/deploy/bundle/concept/*` (14 files) and `VersionedPihConceptBundle`
looked like dead code at first glance (concepts are already fully managed via
`configuration/configuration/concepts*.csv`). Investigation showed:
- `VersionedPihConceptBundle` is genuinely dead — never subclassed anywhere —
  and will be deleted.
- The 14 classes in `metadata/deploy/bundle/concept/` (e.g. `ProgramConcepts`,
  `ChwManagementConcepts`) are **not** dead. They are plain UUID constant
  holders (no `metadatadeploy` dependency, no `@Component`, no `install()`
  method) actively referenced by the `*ProgramMetadata` descriptor classes and
  other code. They are simply misplaced under a `deploy.bundle` package path.

`pihmalawi` already has its own `Initializer` interface
(`activator/Initializer.java`) and an ordered list of implementations run
from `PihMalawiModuleActivator.started()` (`MetadataInitializer` currently
runs first, before `LocationInitializer`, `SoundexInitializer`, etc.). This
existing pattern is preserved rather than replaced.

## Design

### 1. Dependency removal

- `pom.xml`: remove `metadatadeployVersion`/`metadatasharingVersion`
  properties and the `metadatadeploy-api`/`metadatasharing-api` dependencies.
- `distro/openmrs-distro.properties`: remove `omod.metadatadeploy` and
  `omod.metadatasharing`.
- `omod/src/main/resources/config.xml`: remove the corresponding
  `require_module` entries for `org.openmrs.module.metadatadeploy` and
  `org.openmrs.module.metadatasharing`.

### 2. Bundle → Initializer CSV domain mapping

All new/extended CSVs live under the existing
`configuration/configuration/<domain>/` structure (the same pattern already
used for `locations`, `attributetypes`, `concepts`, etc.), built by the
existing `openmrs-packager-maven-plugin` mechanism already wired into
`configuration/pom.xml` — unchanged in this ticket.

| Bundle | Target |
|---|---|
| `EncounterTypeBundle` | new `encountertypes/` CSV (`ENCOUNTER_TYPES`) |
| `EncounterRoleBundle` | new `encounterroles/` CSV (`ENCOUNTER_ROLES`) |
| `VisitTypeBundle` | new `visittypes/` CSV (`VISIT_TYPES`) |
| `LocationBundle` | add 3 rows to existing `locations/locations.csv` |
| `LocationTagBundle` | merge into existing `locationtags/locationtags.csv` |
| `LocationAttributeTypeBundle`, `ProviderAttributeTypeBundle`, `ProgramAttributeTypeBundle` | extend existing `attributetypes/attributetypes.csv` (its `ATTRIBUTE_TYPES` domain natively supports Location/Provider/Program/Visit/Concept entities) |
| `PersonAttributeTypeBundle` | new `personattributetypes/` CSV (`PERSON_ATTRIBUTE_TYPES`, own format) |
| `PatientIdentifierBundle` | new `patientidentifiertypes/` CSV |
| `RelationshipTypeBundle` | new `relationshiptypes/` CSV |
| `UserRoleBundle` | new `roles/` + `privileges/` CSVs |
| `ProgramBundle` + `PihMalawiMetadataBundle` (+ `*ProgramMetadata` workflow/state descriptors) | new `programs/`, `programworkflows/`, `programworkflowstates/` CSVs |
| `CoreConfigurationBundle` + `MetadataInitializer.started()`'s `saveGlobalProperty(...)` calls | merge into existing `globalproperties/gp.xml` |
| `ProgramAttributeTypeDeployHandler`, `PihConstructors` | deleted, no longer needed |

Where the same UUID is needed in more than one CSV cell (e.g. a program's
UUID referenced from both `programs.csv` and `programworkflows.csv`, or a
shared concept UUID), it goes in `configuration/constants.yml` and is
referenced via `${...}` placeholders — that mechanism already exists in this
repo's build (currently unused/empty) and is what we use rather than
introducing the `content.properties` convention early. Single-use UUIDs are
hard-coded inline in the CSV.

### 3. `MetadataInitializer.java` is repurposed, not deleted

We do not want the Initializer module's own activator to load configuration
automatically on its own schedule — we want pihmalawi to control exactly
when Initializer domains load, consistent with the existing ordered
`Initializer` list in `PihMalawiModuleActivator`. This mirrors the pattern
already used in `openmrs-module-rwandaemr` (`RwandaEmrActivator` +
`InitializerSetup`):

- Disable Initializer's automatic startup load via
  `System.setProperty("initializer.startup.load", "disabled")` (static init,
  e.g. in `PihMalawiModuleActivator`).
- `MetadataInitializer.started()` no longer calls
  `deployService.installBundles(...)`. Instead it explicitly obtains
  `InitializerService` and iterates `getLoaders()`, loading each domain —
  same idea as `InitializerSetup.initialize()` in rwandaemr. This keeps
  metadata loading at the same point in the startup sequence it occupies
  today (first, before `LocationInitializer`, `SoundexInitializer`, etc.).
- The manual `saveGlobalProperty(...)` calls in `MetadataInitializer` go away
  entirely — those properties move into `globalproperties/gp.xml` (see
  above) and get picked up automatically by the same explicit Initializer
  load.

### 4. Java-side constant consolidation

- Delete `VersionedPihConceptBundle.java` (confirmed dead — never
  subclassed).
- Move the 14 classes under `metadata/deploy/bundle/concept/` to
  `org.openmrs.module.pihmalawi.metadata.concept` — a rename only, dropping
  the misleading `deploy.bundle` path. No content changes; they're already
  plain constants.
- Flatten every class that implements a `metadatadeploy` `XxxDescriptor`
  interface (`EncounterTypes`, `Locations`, `LocationTags`,
  `LocationAttributeTypes`, `PersonAttributeTypes`,
  `PihMalawiPatientIdentifierTypes`, `Programs`, the `*ProgramMetadata`
  program/workflow/state descriptors, and the small nested `Types` classes
  currently living inside soon-to-be-deleted bundles like
  `EncounterRoleBundle.EncounterRoles`) into plain
  `public static final String` constants (`_UUID` suffix; add a `_NAME`
  constant only where `.name()` is actually called by a consumer — confirmed
  several are). Consolidate all of these into one new class,
  `PihMalawiConfigConstants`, alongside the existing `PihMalawiConstants` —
  mirroring the `PihEmrConfigConstants` pattern in `openmrs-module-pihcore`.
  Very small, single-use classes (e.g. `LocationAttributeTypes`, with exactly
  one constant) fold into the consolidated class rather than staying as
  their own file.
- Leave untouched: `HivMetadata`, `TbMetadata`, `CommonMetadata`,
  `ChronicCareMetadata`, `group/*TreatmentGroup`, `Metadata.java`, and any
  other runtime lookup helper with no `metadatadeploy` dependency. This may
  be revisited in a future pass.
- Update all ~73 consumer files' call sites mechanically (e.g.
  `EncounterTypes.CHECK_IN.uuid()` →
  `PihMalawiConfigConstants.ENCOUNTERTYPE_CHECK_IN_UUID`).

### 5. Testing / verification

- `mvn compile` / `mvn test` across `api` + `omod` to catch every call-site
  break from the flattening.
- Manually verify each new/extended CSV domain's row count matches the
  corresponding bundle's `install()` calls, so no metadata is silently
  dropped in the migration.
- Confirm the `configuration`/`distro` build still resolves and packages
  correctly with the two modules removed.

## Sequencing note

This ticket stays entirely inside the current repo and current packaging
mechanism. A separate, later ticket will extract `distro`/`configuration`
into a standalone content-package project (`content.properties` +
`maven-resources-plugin`/`maven-assembly-plugin`, matching the other
`xxx-emr` content modules), at which point the CSVs added here move over with
only placeholder-syntax and build-wiring changes, not content changes.
