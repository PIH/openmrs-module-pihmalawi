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

package org.openmrs.module.pihmalawi.common;

/**
 * Trace constants
 */
public class TraceConstants {

    public enum TraceType {

        TWO_WEEK_PHASE_1(2, 6, true),
        TWO_WEEK_PHASE_2(2, 6, false),
        SIX_WEEK(6, 12, false),
        TWELVE_WEEK(12, null, false);

        TraceType(Integer minWeeks, Integer maxWeeks, boolean phase1Only) {
            this.minWeeks = minWeeks;
            this.maxWeeks = maxWeeks;
            this.phase1Only = phase1Only;
        }

        private Integer minWeeks;
        public Integer getMinWeeks() {
            return minWeeks;
        }
        public Integer getMinDaysInclusive() { return minWeeks * 7; }

        private Integer maxWeeks;
        public Integer getMaxWeeks() {
            return maxWeeks;
        }
        public Integer getMaxDaysInclusive() { return maxWeeks == null ? null : (maxWeeks * 7) - 1; }

        private boolean phase1Only;
        public boolean isPhase1Only() {
            return phase1Only;
        }
    }

    public enum Category {

        LATE_HIV_VISIT("HIV patients w/ missed appointment"),
        HIGH_VIRAL_LOAD("HIV patients w/ high viral load after first or repeat test"),
        REPEAT_VIRAL_LOAD("HIV patients due for repeat viral load test (3 months following intensive adherence intervention)"),
        EID_12_MONTH_TEST("EID patients due for 12 month test"),
        EID_24_MONTH_TEST("EID patients due for 24 month test"),
        EID_POSITIVE_6_WK("EID patients with positive/reactive test result at 6 weeks"),
        EID_NEGATIVE("EID patients with negative confirmatory test"),
        LATE_NCD_VISIT_NORMAL_PRIORITY("NCD patients w/ missed appointment who do not have high priority NCD condition"),
        LATE_NCD_VISIT_HIGH_PRIORITY("NCD patients w/ missed appointment who have high priority NCD condition");

        private String description;
        Category(String description) { this.description = description; }
        public String getDescription() { return description; }
    }

    public enum ReturnVisitCategory {
        TODAY("TODAY"),
        NEXT_CLINIC_DAY("NEXT CLINIC DAY"),
        APPOINTMENT_DATE("APPOINTMENT DATE");

        private String description;
        ReturnVisitCategory(String description) { this.description = description; }
        public String getDescription() { return description; }
    }

    public enum HighPriorityCategory {

        HIV("HIV"),
        HIGH_BP("BP > 180/110"),
        ON_INSULIN("On Insulin"),
        SEVERE_ASTHMA("Severe Persistent Asthma"),
        HIGH_SIEZURES("> 5 Siezures per month"),
        SICKLE_CELL("Sickle Cell Disease"),
        CHRONIC_KIDNEY_DISEASE("Chronic Kidney Disease"),
        RHEUMATIC_HEART_DISEASE("Rheumatic Heart Disease"),
        CONGESTIVE_HEART_FAILURE("Congestive Heart Failure");

        HighPriorityCategory(String description) {
            this.description = description;
        }

        private String description;
        public String getDescription() { return description; }
    }
}