package org.openmrs.module.pihmalawi.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.module.pihmalawi.task.MigrateViralLoadAndEIDTestResultsTask;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class MigrateViralLoadAndEIDTestResultsSetup {

    // TODO change this to *remove* this task once migration is complete
    public static void setupMigrateEIDTestResultsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();
        TaskDefinition task = schedulerService.getTaskByName(PihMalawiConstants.TASK_MIGRATE_EID_TEST_RESULTS);
        if (task == null) {
            task = new TaskDefinition();
        }

        task.setName(PihMalawiConstants.TASK_MIGRATE_EID_TEST_RESULTS);
        task.setDescription(PihMalawiConstants.TASK_MIGRATE_EID_TEST_RESULTS_DESCRIPTION);
        task.setTaskClass(MigrateViralLoadAndEIDTestResultsTask.class.getName());
        task.setStartTime(DateUtils.addMinutes(new Date(), 5));
        task.setRepeatInterval(999999999L);  // only run once
        task.setStartOnStartup(true);
        task.setLastExecutionTime(null);

        try {
            schedulerService.scheduleTask(task);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule migrate EID Test Results task", e);
        }

    }

}
