package org.openmrs.module.pihmalawi.initializer;

import com.opencsv.CSVWriter;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.util.LocaleUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_DESC;
import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_NAME;
import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_UUID;
import static org.openmrs.module.initializer.api.BaseLineProcessor.HEADER_VOID_RETIRE;
import static org.openmrs.module.initializer.api.BaseLineProcessor.LIST_SEPARATOR;
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
import static org.openmrs.module.initializer.api.c.ConceptSourceLineProcessor.HEADER_HL7_CODE;
import static org.openmrs.module.initializer.api.c.ConceptSourceLineProcessor.HEADER_UNIQUE_ID;
import static org.openmrs.module.initializer.api.c.MappingsConceptLineProcessor.MAPPING_HEADER_PREFIX;
import static org.openmrs.module.initializer.api.c.MappingsConceptLineProcessor.MAPPING_HEADER_SEPARATOR;

@Component
public class ConceptExporter {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    ConceptService conceptService;

    public byte[] getConceptExport() {
        List<String[]> conceptClasses = new ArrayList<>();
        List<String[]> conceptSources = new ArrayList<>();
        List<String[]> concepts = new ArrayList<>();
        List<String[]> conceptAnswers = new ArrayList<>();
        List<String[]> setMembers = new ArrayList<>();

        conceptClasses.add(new String[] { HEADER_UUID, HEADER_VOID_RETIRE, HEADER_NAME, HEADER_DESC });
        conceptSources.add(new String[] { HEADER_UUID, HEADER_VOID_RETIRE, HEADER_NAME, HEADER_DESC, HEADER_HL7_CODE, HEADER_UNIQUE_ID });
        conceptAnswers.add(new String[] { HEADER_CONCEPT, HEADER_MEMBER, HEADER_SORT_WEIGHT, HEADER_MEMBER_TYPE });
        setMembers.add(new String[] { HEADER_CONCEPT, HEADER_MEMBER, HEADER_SORT_WEIGHT, HEADER_MEMBER_TYPE });

        // First populate the concept classes and sources

        List<ConceptClass> conceptClassList = conceptService.getAllConceptClasses(true);
        conceptClassList.sort(new BeanComparator<>("name"));
        for (ConceptClass cc : conceptClassList) {
            conceptClasses.add(new String[] { cc.getUuid(), cc.getRetired() ? "true" : null, cc.getName(), cc.getDescription() });
        }

        List<ConceptSource> conceptSourceList = conceptService.getAllConceptSources(true);
        conceptSourceList.sort(new BeanComparator<>("name"));
        for (ConceptSource cs : conceptSourceList) {
            conceptSources.add(new String[] { cs.getUuid(), cs.getRetired() ? "true" : null, cs.getName(), cs.getDescription(), cs.getHl7Code(), cs.getUniqueId() });
        }

        // Next populate concepts, mappings, answers, and set members

        Set<Locale> locales = new LinkedHashSet<>();
        locales.add(Locale.ENGLISH);
        locales.addAll(LocaleUtility.getLocalesInOrder());

        // First collect all data for which there is a variable number of headers
        Map<Concept, Map<String, String>> variableHeadersByConcept = new LinkedHashMap<>();
        Map<String, Set<String>> variableHeaders = new LinkedHashMap<>();
        variableHeaders.put(HEADER_FSNAME, new LinkedHashSet<>());
        variableHeaders.put(HEADER_SHORTNAME, new LinkedHashSet<>());
        variableHeaders.put(HEADER_INDEX_TERM, new LinkedHashSet<>());
        variableHeaders.put(HEADER_SYNONYM, new LinkedHashSet<>());
        variableHeaders.put(HEADER_DESC, new LinkedHashSet<>());
        variableHeaders.put(MAPPING_HEADER_PREFIX, new LinkedHashSet<>());

        Map<String, Integer> mappingHeaderCount = new HashMap<>();

        for (Concept c : conceptService.getAllConcepts()) {
            Map<String, String> headers = new LinkedHashMap<>();
            for (Locale l : locales) {
                ConceptName fsn = c.getFullySpecifiedName(l);
                if (fsn != null) {
                    String header = HEADER_FSNAME + LOCALE_SEPARATOR + l;
                    variableHeaders.get(HEADER_FSNAME).add(header);
                    headers.put(header, fsn.getName());
                }
                ConceptName shortName = c.getShortNameInLocale(l);
                if (shortName != null) {
                    String header = HEADER_SHORTNAME + LOCALE_SEPARATOR + l;
                    variableHeaders.get(HEADER_FSNAME).add(header);
                    headers.put(header, shortName.getName());
                }
                int indexTermCounter = 0;
                for (ConceptName indexTerm : c.getIndexTermsForLocale(l)) {
                    indexTermCounter++;
                    String header = HEADER_INDEX_TERM + " " + indexTermCounter + LOCALE_SEPARATOR + l;
                    variableHeaders.get(HEADER_INDEX_TERM).add(header);
                    headers.put(header, indexTerm.getName());
                }
                int synonymCounter = 0;
                for (ConceptName synonym : c.getSynonyms(l)) {
                    synonymCounter++;
                    String header = HEADER_SYNONYM + " " + synonymCounter + LOCALE_SEPARATOR + l;
                    variableHeaders.get(HEADER_SYNONYM).add(header);
                    headers.put(header, synonym.getName());
                }
                ConceptDescription description = c.getDescription(l, true);
                if (description != null) {
                    String header = HEADER_DESC + LOCALE_SEPARATOR + l;
                    variableHeaders.get(HEADER_DESC).add(header);
                    headers.put(header, description.getDescription());
                }
            }
            Collection<ConceptMap> conceptMappings = c.getConceptMappings();
            if (conceptMappings != null && !conceptMappings.isEmpty()) {
                for (ConceptMap cm : conceptMappings) {
                    String type = cm.getConceptMapType().getName();
                    String source = cm.getConceptReferenceTerm().getConceptSource().getName();
                    String code = cm.getConceptReferenceTerm().getCode();
                    String header = MAPPING_HEADER_PREFIX + MAPPING_HEADER_SEPARATOR + type + MAPPING_HEADER_SEPARATOR + source;
                    mappingHeaderCount.put(header, mappingHeaderCount.getOrDefault(header, 0) + 1);
                    headers.compute(header, (k, existingValue) -> existingValue == null ? code : existingValue + LIST_SEPARATOR + code);
                }
            }
            variableHeadersByConcept.put(c, headers);
        }

        // Sort the mappings by frequency
        List<String> mappingHeaders = mappingHeaderCount.entrySet().stream().sorted((e1, e2) -> {
            int comparison = e1.getValue().compareTo(e2.getValue()) * -1;
            if (comparison != 0) {
                return comparison;
            }
            return e1.getKey().compareTo(e2.getKey());
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        variableHeaders.get(MAPPING_HEADER_PREFIX).addAll(mappingHeaders);

        // Sort the concepts to ensure that the first, best name is used to sort first, followed by uuid
        variableHeadersByConcept = sortConcepts(variableHeadersByConcept, variableHeaders);

        // Next iterate over these to produce the CSV set that accounts for the max columns

        for (Concept c : variableHeadersByConcept.keySet()) {
            Map<String, String> m = new LinkedHashMap<>();

            m.put(HEADER_UUID, c.getUuid());
            m.put(HEADER_VOID_RETIRE, c.getRetired() ? "true" : null);
            m.put(HEADER_CLASS, c.getConceptClass().getName());
            m.put(HEADER_DATATYPE, c.getDatatype().getName());

            Map<String, String> headers = variableHeadersByConcept.get(c);
            for (Set<String> headerSet : variableHeaders.values()) {
                for (String header : headerSet) {
                    m.put(header, headers.get(header));
                }
            }

            Collection<ConceptAnswer> answers = c.getAnswers(true);
            if (answers != null && !answers.isEmpty()) {
                for (ConceptAnswer a : answers) {
                    String concept = getConceptRef(a.getConcept());
                    String member = getConceptRef(a.getAnswerConcept()); // TODO: Note, answerDrug is not supported
                    String sortWeight = a.getSortWeight() == null ? null : a.getSortWeight().toString();
                    conceptAnswers.add(new String[]{concept, member, sortWeight, HEADER_MEMBER_TYPE_Q_AND_A});
                }
            }

            Collection<ConceptSet> conceptSets = c.getConceptSets();
            if (conceptSets != null && !conceptSets.isEmpty()) {
                for (ConceptSet cs : conceptSets) {
                    String concept = getConceptRef(cs.getConceptSet());
                    String member = getConceptRef(cs.getConcept());
                    String sortWeight = cs.getSortWeight() == null ? null : cs.getSortWeight().toString();
                    setMembers.add(new String[]{concept, member, sortWeight, HEADER_MEMBER_TYPE_CONCEPT_SET});
                }
            }

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
            }
            else {
                m.put("absolute high", null);
                m.put("critical high", null);
                m.put("normal high", null);
                m.put("absolute low", null);
                m.put("critical low", null);
                m.put("normal low", null);
                m.put("allow decimals", null);
                m.put("units", null);
                m.put("display precision", null);
            }
            if (concepts.isEmpty()) {
                concepts.add(new ArrayList<>(m.keySet()).toArray(new String[0]));
            }
            concepts.add(new ArrayList<>(m.values()).toArray(new String[0]));
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(bos)) {
            addCsvFile(zos, "conceptClasses.csv", conceptClasses);
            addCsvFile(zos, "conceptSources.csv", conceptSources);
            addCsvFile(zos, "concepts.csv", concepts);
            addCsvFile(zos, "conceptAnswers.csv", conceptAnswers);
            addCsvFile(zos, "conceptSetMembers.csv", setMembers);
        } catch (Exception e) {
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
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(zos, StandardCharsets.UTF_8));
        csvWriter.writeAll(data);
        csvWriter.flush();
        zos.closeEntry();
    }

    Map<Concept, Map<String, String>> sortConcepts(Map<Concept, Map<String, String>> variableHeadersByConcept, Map<String, Set<String>> variableHeaders) {
        String nameHeader = null;
        for (Set<String> headers : variableHeaders.values()) {
            if (nameHeader == null && headers != null && !headers.isEmpty()) {
                nameHeader = headers.iterator().next();
            }
        }
        final String nameHeaderForSorting = nameHeader;
        Map<Concept, Map<String, String>> ret = new TreeMap<>((c1, c2) -> {
            Map<String, String> variableHeaders1 = variableHeadersByConcept.get(c1);
            Map<String, String> variableHeaders2 = variableHeadersByConcept.get(c2);
            String val1 = variableHeaders1.get(nameHeaderForSorting);
            String val2 = variableHeaders2.get(nameHeaderForSorting);
            val1 = (val1 == null ? "" : val1.trim().toLowerCase());
            val2 = (val2 == null ? "" : val2.trim().toLowerCase());
            int comparison = val1.compareTo(val2);
            if (comparison != 0) {
                return comparison;
            }
            return c1.getConceptId().compareTo(c2.getConceptId());
        });
        ret.putAll(variableHeadersByConcept);
        return ret;
    }
}
