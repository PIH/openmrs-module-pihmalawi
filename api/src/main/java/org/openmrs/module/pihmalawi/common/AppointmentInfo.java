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

import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple object that contains information about the status of an appointment
 */
public class AppointmentInfo {

    //** PROPERTIES

    private Date effectiveDate;
    private String encounterType;
    private Date lastEncounterDate;
    private Date nextScheduledDate;

    //***** CONSTRUCTORS *****

    public AppointmentInfo(Date effectiveDate, String encounterType, Date lastEncounterDate, Date nextScheduledDate) {
        this.effectiveDate = effectiveDate;
        this.encounterType = encounterType;
        this.lastEncounterDate = lastEncounterDate;
        this.nextScheduledDate = nextScheduledDate;
    }

    //***** METHODS *****

    /**
     * @return true if the last "next scheduled date" is in the past
     */
    public boolean isOverdue() {
        Date today = DateUtil.getStartOfDay(effectiveDate);
        return nextScheduledDate != null && nextScheduledDate.after(lastEncounterDate) && nextScheduledDate.before(today);
    }

    /**
     * @return the days remaining until the appointment date. a negative number indicates that number of days overdue. null indicates no scheduled appointment found.
     */
    public Integer getDaysToAppointment() {

        // No scheduled appointment
        if (nextScheduledDate == null) {
            return null;
        }

        // No scheduled appointment since prevoius encounter
        if (lastEncounterDate != null && nextScheduledDate.compareTo(lastEncounterDate) <= 0) {
            return null;
        }

        Date today = DateUtil.getStartOfDay(effectiveDate);
        Date apptDate = DateUtil.getStartOfDay(nextScheduledDate);
        int multiplier = apptDate.compareTo(today); // If appt date is in the past, multiply by -1

        Date fromDate = (multiplier < 0 ? apptDate : today);
        Date toDate = (multiplier < 0 ? today : apptDate);

        Days days = Days.daysBetween(new DateTime(fromDate), new DateTime(toDate.getTime()));
        return days.getDays()*multiplier;
    }

    public Double getWeeksOutOfCare() {
        Integer daysToAppt = getDaysToAppointment();
        if (daysToAppt == null || daysToAppt >= 0) {
            return 0.0;
        }
        return daysToAppt/-7.0;
    }

    @Override
    public String toString() {
        List<String> l = new ArrayList<String>();
        if (lastEncounterDate != null) {
            l.add("Last Actual: " + DateFormatUtils.format(lastEncounterDate, "yyyy-MM-dd"));
        }
        if (nextScheduledDate != null) {
            l.add("Next Scheduled: " + DateFormatUtils.format(nextScheduledDate, "yyyy-MM-dd"));
        }
        return OpenmrsUtil.join(l, ", ");
    }

    //***** ACCESSORS ******

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public Date getLastEncounterDate() {
        return lastEncounterDate;
    }

    public void setLastEncounterDate(Date lastEncounterDate) {
        this.lastEncounterDate = lastEncounterDate;
    }

    public Date getNextScheduledDate() {
        return nextScheduledDate;
    }

    public void setNextScheduledDate(Date nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }
}
