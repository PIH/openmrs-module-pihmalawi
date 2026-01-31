# Concept Management Scripts

This folder contains information and scripts regarding moving concepts into OCL and generally ensuring concepts are
managed effectively and efficiently by the team developing and maintaining the PIH Malawi EMR system.

## Analysis of current state

Based on an analysis of the concept tables on Neno production on January 30, 2026, the following concept tables are used:

### Concept

concept
concept_class -> name (rest of it installed via iniz)
concept_datatype -> name (rest of it installed via iniz)
concept_description -> description, locale
concept_name -> name, locale, concept_name_type, locale_preferred
concept_numeric

NOTES ON CONCEPT DESCRIPTIONS: 
* The concept_name table has a locale representation of: 12013 en, 31 fr, 18 ht.  We will only retain en.
* The concept_description table has a locale representation of:  6254 en, 85 fr.  We will only retain en.
* The concept_description table has a few dozen instances where descriptions are duplicated.  Only one will be retained.

NOTES ON CONCEPT NAMES:
* The concept_name table has 8440 FULLY_SPECIFIED, 2160 null, 1161 SHORT names
* There is only 1 non-voided FULLY_SPECIFIED name for each concept
* There is only 1 non-voided SHORT name for each concept
* Number of synonyms:  1037=1, 291=2, 78=3, 52=4, 10=5, 1=6, 3=7, 2=8

### Concept Answers

concept_answer -> concept_id, answer_concept, answer_drug, sort_weight
drug -> only because this is reference by 6 entries in the concept_answer table.  How does OCL handle this?

### Concept Set Members

concept_set -> concept_id, concept_set, sort_weight

### Concept Reference Terms

concept_map_type -> name (TODO: Should we remove entities from this table that are not used)
concept_reference_map (concept_id, concept_reference_term_id, concept_map_type_id)
concept_reference_source -> name (rest of it installed via iniz)
concept_reference_term -> concept_source_id, code

### TBD

concept_name_tag -> tag, description (preferred_dmht, short_en, short_fr, preferred_bart, preferred_dashboard)
concept_name_tag_map -> concept_name_id, concept_name_tag_id

concept_synonym -> concept_id, synonym, locale (date_created between 2004-04-02 - 2008-11-19, assuming this is a Baobab-created table, and not anything we need to put in OCL)

### No data, or not needed for concept management

concept_attribute
concept_attribute_type
concept_complex
concept_proposal_tag_map
concept_proposal -> this has 2 entries, one of which has an FK to a specific encounter and obs.  Both from 2009.  But this does not refer to any specific concept.  This is more data than metadata.
concept_reference_range
concept_reference_term_map
concept_set_derived -> this isn't in the data model
concept_state_conversion -> this has a FK to program_workflow and program_workflow_state.  but we don't use this so let's not do this.
concept_stop_word -> this is not associated with a concept, just a free-standing table.
concept_word -> this does not exist in the DB

## Initializer Formats

A reasonable format to extract the concept data into, either as an intermediary format prior to passing to a tool to
load into OCL, or as a format this is actively used to managed concepts, is the initializer format.  It supports the
following as relates to the above tables:

