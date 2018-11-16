/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.pihmalawi.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.pihmalawi.metadata.HivMetadata;
import org.openmrs.module.pihmalawi.reporting.library.BaseCohortDefinitionLibrary;
import org.openmrs.module.reporting.cohort.PatientIdSet;
import org.openmrs.module.reporting.common.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Provides a construct for maintaining patient data
 */
@Component
public class IC3ScreeningDataLoader extends ScheduledExecutorFactoryBean {

    private final static Log log = LogFactory.getLog(IC3ScreeningDataLoader.class);

    private static DaemonToken daemonToken = null;
    private static boolean running = false;

    @Autowired
    HivMetadata metadata;

    @Autowired
    BaseCohortDefinitionLibrary baseCohorts;

    @Autowired
    IC3ScreeningData ic3ScreeningData;

    public IC3ScreeningDataLoader() {
        ScheduledExecutorTask task = new ScheduledExecutorTask();
        task.setDelay(1);
        task.setPeriod(1);
        task.setTimeUnit(TimeUnit.MINUTES);
        task.setRunnable(new Runnable() {
            public void run() {
                log.debug("Running patient data cache task");
                runImmediately();
            }
        });
        setScheduledExecutorTasks(new ScheduledExecutorTask[] {task});
    }

    public void runImmediately() {
        if (daemonToken != null) {
            Daemon.runInDaemonThread(new RefreshForLocationsRunnable(), daemonToken);
        }
        else {
            log.warn("Daemon token is null, not yet running task.");
        }
    }

    class RefreshForLocationsRunnable implements Runnable {
        @Override
        public void run() {
            log.debug("Running already = " + running);
            if (!running) {
                running = true;
                try {
                    Date today = DateUtil.getDateTime(2018, 11, 8);

                    // First pre-load all actively enrolled patients who have appointments
                    Cohort patientsWithAppts = ic3ScreeningData.evaluateCohort(baseCohorts.getPatientsWithScheduledAppointmentOnEndDate(), today, null);
                    for (Location location : metadata.getSystemLocations()) {
                        Cohort activePatients = ic3ScreeningData.evaluateCohort(baseCohorts.getPatientsActiveInHivOrChronicCareProgramAtLocationOnEndDate(), today, location);
                        Cohort activeWithAppts = PatientIdSet.intersect(activePatients, patientsWithAppts);
                        ic3ScreeningData.getDataForCohort(activeWithAppts, today, location);
                    }

                    // Next load all patients who had a visit at the given location and given date
                    for (Location location : metadata.getSystemLocations()) {
                        Cohort activeVisitPatients = ic3ScreeningData.evaluateCohort(baseCohorts.getPatientsWithAVisitOnEndDateAtLocation(), today, location);
                        ic3ScreeningData.getDataForCohort(activeVisitPatients, today, location);
                    }
                }
                finally {
                    running = false;
                }
            }
        }
    }

    public static void setDaemonToken(DaemonToken daemonToken) {
        IC3ScreeningDataLoader.daemonToken = daemonToken;
    }

    public static boolean isRunning() {
        return running;
    }
}
