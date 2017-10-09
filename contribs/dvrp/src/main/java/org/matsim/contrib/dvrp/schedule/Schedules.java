/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.dvrp.schedule;

import java.util.*;

import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.dvrp.schedule.Schedule.ScheduleStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


public class Schedules
{
    public static final Predicate<Task> STAY_TASK_PREDICATE = new Predicate<Task>() {
        @Override
        public boolean apply(Task input)
        {
            return (input instanceof StayTask);
        };
    };

    public static final Predicate<Task> DRIVE_TASK_PREDICATE = new Predicate<Task>() {
        @Override
        public boolean apply(Task input)
        {
            return (input instanceof DriveTask);
        };
    };

    public static final Comparator<Task> TASK_SCHEDULE_IDX_COMPARATOR = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2)
        {
            if (t1.getSchedule().equals(t2.getSchedule())) {
                throw new IllegalArgumentException("Cannot compare tasks from different schedules");
            }

            return t1.getTaskIdx() - t2.getTaskIdx();
        }
    };


    public static Task getFirstTask(Schedule schedule)
    {
        return schedule.getTasks().get(0);
    }


    public static Task getSecondTask(Schedule schedule)
    {
        return schedule.getTasks().get(1);
    }


    public static Task getNextToLastTask(Schedule schedule)
    {
        List<? extends Task> tasks = schedule.getTasks();
        return tasks.get(tasks.size() - 2);
    }


    public static Task getLastTask(Schedule schedule)
    {
        List<? extends Task> tasks = schedule.getTasks();
        return tasks.get(tasks.size() - 1);
    }


    public static boolean isFirstTask(Task task)
    {
        return task.getTaskIdx() == 0;
    }


    public static boolean isSecondTask(Task task)
    {
        return task.getTaskIdx() == 1;
    }


    public static boolean isNextToLastTask(Task task)
    {
        return task.getTaskIdx() == task.getSchedule().getTaskCount() - 2;
    }


    public static boolean isLastTask(Task task)
    {
        return task.getTaskIdx() == task.getSchedule().getTaskCount() - 1;
    }


    public static Task getNextTask(Schedule schedule)
    {
        int taskIdx = schedule.getStatus() == ScheduleStatus.PLANNED ? //
                0 : schedule.getCurrentTask().getTaskIdx() + 1;

        return schedule.getTasks().get(taskIdx);
    }


    public static Task getPreviousTask(Schedule schedule)
    {
        int taskIdx = schedule.getStatus() == ScheduleStatus.COMPLETED ? //
                schedule.getTaskCount() - 1 : schedule.getCurrentTask().getTaskIdx() - 1;

        return schedule.getTasks().get(taskIdx);
    }


    public static Link getLastLinkInSchedule(Schedule schedule)
    {
        List<? extends Task> tasks = schedule.getTasks();
        return tasks.isEmpty() ? //
                schedule.getVehicle().getStartLink() : //
                Tasks.getEndLink(tasks.get(tasks.size() - 1));
    }


    @SuppressWarnings("unchecked")
    public static Iterable<StayTask> createStayTaskIter(Schedule schedule)
    {
        return (Iterable<StayTask>)createTaskFilterIter(schedule, STAY_TASK_PREDICATE);
    }


    @SuppressWarnings("unchecked")
    public static Iterable<DriveTask> createDriveTaskIter(Schedule schedule)
    {
        return (Iterable<DriveTask>)createTaskFilterIter(schedule, DRIVE_TASK_PREDICATE);
    }


    public static Iterable<? extends Task> createTaskFilterIter(Schedule schedule,
            Predicate<? super Task> taskPredicate)
    {
        return Iterables.filter(schedule.getTasks(), taskPredicate);
    }
}
