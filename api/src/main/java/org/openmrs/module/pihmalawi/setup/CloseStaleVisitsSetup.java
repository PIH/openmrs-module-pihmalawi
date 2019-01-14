package org.openmrs.module.pihmalawi.setup;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.adt.CloseStaleVisitsTask;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.module.pihmalawi.PihMalawiConstants;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class CloseStaleVisitsSetup {

    public static void setupCloseStaleVisitsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();
        TaskDefinition task = schedulerService.getTaskByName(PihMalawiConstants.TASK_CLOSE_STALE_VISITS_NAME);
        if (task == null) {
            task = new TaskDefinition();
            task.setName(PihMalawiConstants.TASK_CLOSE_STALE_VISITS_NAME);
            task.setDescription(PihMalawiConstants.TASK_CLOSE_STALE_VISITS_DESCRIPTION);
            task.setTaskClass(CloseStaleVisitsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(PihMalawiConstants.TASK_CLOSE_STALE_VISITS_REPEAT_INTERVAL);
            task.setStartOnStartup(true);

            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule close stale visits task", e);
            }
        } else {
            boolean changed = GeneralUtils.setPropertyIfDifferent(task, "taskClass", CloseStaleVisitsTask.class.getName());

            if (changed) {
                schedulerService.saveTaskDefinition(task);
            }

            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule close stale visits task", e);
                }
            }

        }
    }
}
