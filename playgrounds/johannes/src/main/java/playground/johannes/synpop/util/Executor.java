/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,       *
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
package playground.johannes.synpop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jillenberger
 */
public class Executor {

    private static ThreadPoolExecutor service;

    private static void init() {
        if (service == null) {
            service = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            //TODO: Does not work as expected.
//            Timer timer = new Timer(true);
//            timer.scheduleAtFixedRate(new ShutdownTask(), 0, 60 * 1000);
        }
    }

    public static void shutdown() {
        if(service != null) service.shutdown();
    }

    public static Future<?> submit(Runnable task) {
        init();
        return service.submit(task);
    }

    public static void submitAndWait(List<? extends Runnable> runnables) {
        init();
        List<Future<?>> futures = new ArrayList<>(runnables.size());
        for(Runnable runnable : runnables) {
            futures.add(service.submit(runnable));
        }

        for(Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getFreePoolSize() {
        init();
        return service.getMaximumPoolSize() - service.getActiveCount();
    }

    private static class ShutdownTask extends TimerTask {

        @Override
        public void run() {
            if(service.getActiveCount() == 0) {
                service.shutdown();
            }
        }
    }
}
