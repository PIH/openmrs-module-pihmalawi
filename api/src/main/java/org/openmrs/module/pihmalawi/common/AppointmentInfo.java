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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.openmrs.module.reporting.common.DateUtil;

import java.util.Date;

/**
 * A simple object that contains information about the status of an appointment
 */
public class AppointmentInfo {

    //** PROPERTIES

    private boolean currentlyEnrolled;
    private Date lastEncounterDate;
    private Date nextScheduledDate;

    //***** CONSTRUCTORS *****

    public AppointmentInfo() {}

    public AppointmentInfo(Boolean currentlyEnrolled, Date lastEncounterDate, Date nextScheduledDate) {
        this.currentlyEnrolled = currentlyEnrolled;
        this.lastEncounterDate = lastEncounterDate;
        this.nextScheduledDate = nextScheduledDate;
    }

    //***** METHODS *****

    /**
     * @return true if the last "next scheduled date" is in the past
     */
    public boolean isOverdue() {
        if (!currentlyEnrolled) {
            return false;
        }
        Date today = DateUtil.getStartOfDay(new Date());
        return nextScheduledDate != null && nextScheduledDate.after(lastEncounterDate) && nextScheduledDate.before(today);
    }

    /**
     * @return the days remaining until the appointment date. a negative number indicates that number of days overdue. null indicates no scheduled appointment found.
     */
    public Integer getDaysToAppointment() {

        // No scheduled appointment
        if (nextScheduledDate == null || !currentlyEnrolled) {
            return null;
        }

        // No scheduled appointment since prevoius encounter
        if (lastEncounterDate != null && nextScheduledDate.compareTo(lastEncounterDate) <= 0) {
            return null;
        }

        Date today = DateUtil.getStartOfDay(new Date());
        Date apptDate = DateUtil.getStartOfDay(nextScheduledDate);
        int multiplier = apptDate.compareTo(today); // If appt date is in the past, multiply by -1

        Date fromDate = (multiplier < 0 ? apptDate : today);
        Date toDate = (multiplier < 0 ? today : apptDate);

        Days days = Days.daysBetween(new DateTime(fromDate), new DateTime(toDate.getTime()));
        return days.getDays()*multiplier;
    }

    //***** ACCESSORS ******


    public boolean isCurrentlyEnrolled() {
        return currentlyEnrolled;
    }

    public void setCurrentlyEnrolled(boolean currentlyEnrolled) {
        this.currentlyEnrolled = currentlyEnrolled;
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