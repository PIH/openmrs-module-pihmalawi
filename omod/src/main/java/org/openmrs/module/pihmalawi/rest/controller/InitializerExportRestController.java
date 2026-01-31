package org.openmrs.module.pihmalawi.rest.controller;

import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.c.ConceptLineProcessor;
import org.openmrs.util.LocaleUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_UUID;
import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_VOID_RETIRE;
import static org.openmrs.module.initializer.api.BaseLineProcessor.LOCALE_SEPARATOR;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_CLASS;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_DATATYPE;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_FSNAME;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_INDEX_TERM;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_SHORTNAME;
import static org.openmrs.module.initializer.api.c.ConceptLineProcessor.HEADER_SYNONYM;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_CONCEPT;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_MEMBER;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_MEMBER_TYPE;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_MEMBER_TYPE_CONCEPT_SET;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_MEMBER_TYPE_Q_AND_A;
import static org.openmrs.module.initializer.api.c.ConceptSetLineProcessor.HEADER_SORT_WEIGHT;

@RestController
public class InitializerExportRestController {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    ConceptService conceptService;

    @GetMapping(value = "/rest/v1/pihmalawi/initializer/export")
    public ResponseEntity<byte[]> export(@RequestHeader HttpHeaders headers) {

        if (!Context.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        }

        MediaType mediaType = MediaType.parseMediaType("application/octet-stream");
        try {
            byte[] conceptExport = getConceptExport();
            return ResponseEntity.ok().contentType(mediaType).body(conceptExport);
        }
        catch (Exception e) {
            log.error("Error generating initializer export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

    public byte[] getConceptExport() {
        List<String[]> concepts = new ArrayList<>();
        List<String[]> numericConcepts = new ArrayList<>();
        List<String[]> conceptAnswers = new ArrayList<>();
        List<String[]> setMembers = new ArrayList<>();

        conceptAnswers.add(new String[] {HEADER_CONCEPT, HEADER_MEMBER, HEADER_SORT_WEIGHT, HEADER_MEMBER_TYPE});
        setMembers.add(new String[] {HEADER_CONCEPT, HEADER_MEMBER, HEADER_SORT_WEIGHT, HEADER_MEMBER_TYPE});

        Locale en = Locale.ENGLISH;
        String headerEn = LOCALE_SEPARATOR + en;

        // First collect and sort all names by concept
        int maxIndexTerms = 0;
        int maxSynonyms = 0;
        Map<Concept, Map<String, String>> conceptsAndNames = new LinkedHashMap<>();
        for (Concept c : conceptService.getAllConcepts()) {
            Map<String, String> names = new LinkedHashMap<>();
            names.put(HEADER_FSNAME + headerEn, c.getFullySpecifiedName(en).getName());

            String shortName = c.getShortNameInLocale(en) == null ? null : c.getShortNameInLocale(en).getName();
            if (shortName != null && names.containsValue(shortName)) {
                shortName = null;
            }
            names.put(HEADER_SHORTNAME + headerEn, shortName);

            int indexTermCounter=0;
            int synonymCounter=0;
            for (ConceptName name : c.getNames(false)) {
                if (name.getLocale().equals(en) && !names.containsValue(name.getName())) {
                    if (ConceptNameType.INDEX_TERM.equals(name.getConceptNameType())) {
                        indexTermCounter++;
                        names.put(HEADER_INDEX_TERM + " " + indexTermCounter + headerEn, name.getName());
                    }
                    else {
                        synonymCounter++;
                        names.put(HEADER_SYNONYM + " " + synonymCounter + headerEn, name.getName());
                    }
                }
            }
            if (indexTermCounter > maxIndexTerms) {
                maxIndexTerms = indexTermCounter;
            }
            if (synonymCounter > maxSynonyms) {
                maxSynonyms = synonymCounter;
            }
            conceptsAndNames.put(c, names);
        }

        // Next iterate over these to produce the CSV set that accounts for the max columns

        for (Concept c : conceptsAndNames.keySet()) {
            Map<String, String> m = new LinkedHashMap<>();

            m.put(HEADER_UUID, c.getUuid());
            m.put(HEADER_VOID_RETIRE, c.getRetired() ? "true" : null);
            m.put(HEADER_CLASS, c.getConceptClass().getName());
            m.put(HEADER_DATATYPE, c.getDatatype().getName());

            Map<String, String> names = conceptsAndNames.get(c);
            m.put(HEADER_FSNAME + headerEn, names.remove(HEADER_FSNAME + headerEn));
            m.put(HEADER_SHORTNAME + headerEn, names.get(HEADER_SHORTNAME + headerEn));
            for (int i=1; i<=maxIndexTerms; i++) {
                m.put(HEADER_INDEX_TERM + " " + i + headerEn, names.get(HEADER_INDEX_TERM + " " + i + headerEn));
            }
            for (int i=1; i<=maxSynonyms; i++) {
                m.put(HEADER_INDEX_TERM + " " + i + headerEn, names.get(HEADER_INDEX_TERM + " " + i + headerEn));
            }

            ConceptDescription description = c.getDescription(Locale.ENGLISH);
            m.put(ConceptLineProcessor.HEADER_DESC + headerEn, description == null ? null : description.getDescription());

            Collection<ConceptAnswer> answers = c.getAnswers(true);
            if (answers != null && !answers.isEmpty()) {
                for (ConceptAnswer a : answers) {
                    String concept = getConceptRef(a.getConcept());
                    // TODO: answer drugs are not supported yet in Iniz or OCL
                    String member = a.getAnswerDrug() != null ? a.getAnswerDrug().getUuid() : getConceptRef(a.getAnswerConcept());
                    String sortWeight = a.getSortWeight() == null ? null : a.getSortWeight().toString();
                    conceptAnswers.add(new String[] {concept, member, sortWeight, HEADER_MEMBER_TYPE_Q_AND_A});
                }
            }

            Collection<ConceptSet> conceptSets = c.getConceptSets();
            if (conceptSets != null && !conceptSets.isEmpty()) {
                for (ConceptSet cs : conceptSets) {
                    String concept = getConceptRef(cs.getConceptSet());
                    String member = getConceptRef(cs.getConcept());
                    String sortWeight = cs.getSortWeight() == null ? null : cs.getSortWeight().toString();
                    setMembers.add(new String[] {concept, member, sortWeight, HEADER_MEMBER_TYPE_CONCEPT_SET});
                }
            }

            // TODO: Figure out concept mappings.  There doesn't seem to be an Iniz domain for this.  For now we will just add to the Concepts csv

            if (c instanceof ConceptNumeric) {
                ConceptNumeric cn = (ConceptNumeric) c;
                m.put("absolute high", cn.getHiAbsolute() == null ? null : cn.getHiAbsolute().toString());
                m.put("critical high", cn.getHiCritical() == null ? null : cn.getHiCritical().toString());
                m.put("normal high", cn.getHiNormal() == null ? null : cn.getHiNormal().toString());
                m.put("absolute low", cn.getLowAbsolute() == null ? null : cn.getLowAbsolute().toString());
                m.put("critical low", cn.getLowCritical() == null ? null : cn.getLowCritical().toString());
                m.put("normal low", cn.getLowNormal() == null ? null : cn.getLowNormal().toString());
                m.put("allow decimals", cn.getAllowDecimal() == null ? null : cn.getAllowDecimal().toString());
                m.put("units", cn.getUnits());
                m.put("display precision", cn.getDisplayPrecision() == null ? null : cn.getDisplayPrecision().toString());

                if (numericConcepts.isEmpty()) {
                    numericConcepts.add(new ArrayList<>(m.keySet()).toArray(new String[0]));
                }
                numericConcepts.add(new ArrayList<>(m.values()).toArray(new String[0]));
            }
            else {
                if (concepts.isEmpty()) {
                    concepts.add(new ArrayList<>(m.keySet()).toArray(new String[0]));
                }
                concepts.add(new ArrayList<>(m.values()).toArray(new String[0]));
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(bos)) {
            addCsvFile(zos, "conceptNonNumerics.csv", concepts);
            addCsvFile(zos, "conceptNumerics.csv", numericConcepts);
            addCsvFile(zos, "conceptAnswers.csv", conceptAnswers);
            addCsvFile(zos, "conceptSetMembers.csv", setMembers);
        }
        catch (Exception e) {
            throw new RuntimeException("Error generating zip of concept csvs", e);
        }
        return bos.toByteArray();
    }

    String getConceptRef(Concept concept) {
        ConceptName fsn = concept.getFullySpecifiedName(Locale.ENGLISH);
        return fsn == null ? concept.getUuid() : fsn.getName();
    }

    void addCsvFile(ZipOutputStream zos, String fileName, List<String[]> data) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        try (OutputStreamWriter osw = new OutputStreamWriter(zos, StandardCharsets.UTF_8)) {
            try (CSVWriter writer = new CSVWriter(osw)) {
                writer.writeAll(data);
                writer.flush(); // Flush the writer
            }
        }
    }

}
