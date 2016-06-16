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
package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Date;

/**
 * Converts a Date into the duration from the passed date, or current date by default
 */
public class DurationConverter implements DataConverter  {

    private DurationUnit durationUnit;
    private Date durationToDate;

	//***** CONSTRUCTORS *****

	/**
	 * Default constructor
	 */
	public DurationConverter(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

	//***** INSTANCE METHODS *****

	/**
	 * @see DataConverter#convert(Object)
	 */
	public Object convert(Object original) {
        if (original != null) {
            DateTime from = new DateTime((Date)original);
            if (durationToDate == null) {
                durationToDate = new Date();
            }
            DateTime to = new DateTime(durationToDate);
            if (durationUnit == DurationUnit.YEARS) {
                return Years.yearsBetween(from, to).getYears();
            }
            else if (durationUnit == DurationUnit.MONTHS) {
                return Months.monthsBetween(from, to).getMonths();
            }
            else if (durationUnit == DurationUnit.WEEKS) {
                return Weeks.weeksBetween(from, to).getWeeks();
            }
            else if (durationUnit == DurationUnit.DAYS) {
                return Days.daysBetween(from, to).getDays();
            }
            else if (durationUnit == DurationUnit.HOURS) {
                return Hours.hoursBetween(from, to).getHours();
            }
            else if (durationUnit == DurationUnit.MINUTES) {
                return Minutes.minutesBetween(from, to).getMinutes();
            }
            else if (durationUnit == DurationUnit.SECONDS) {
                return Seconds.secondsBetween(from, to).getSeconds();
            }
            else {
                throw new IllegalArgumentException("Unable to convert with duration unit: " + durationUnit);
            }
        }
        return null;
	}

	/**
	 * @see DataConverter#getDataType()
	 */
	public Class<?> getDataType() {
		return Date.class;
	}

	/**
	 * @see DataConverter#getInputDataType()
	 */
	public Class<?> getInputDataType() {
		return Integer.class;
	}

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Date getDurationToDate() {
        return durationToDate;
    }

    public void setDurationToDate(Date durationToDate) {
        this.durationToDate = durationToDate;
    }
}