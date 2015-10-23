/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.utils.threads.VitroBackgroundThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utilicy class that populates and returns a cache.
 * Once the cache is populated, it can return the cached results whilst refreshing in the background.
 *
 * @param <T>
 */
public class CachingRDFServiceExecutor<T> {
    /**
     * Cache information
     */
    private T cachedResults;
    private long lastCacheTime;

    private RDFServiceCallable<T> resultBuilder;

    /**
     * Background task tracker
     */
    private FutureTask<T> backgroundTask = null;
    private long backgroundTaskStartTime = -1;

    /**
     * RDF Service to be used by background threads
     */
    private static RDFService backgroundRDFService = null;

    public CachingRDFServiceExecutor(RDFServiceCallable<T> resultBuilder) {
        this.resultBuilder = resultBuilder;
    }

    public boolean isCached() {
        return cachedResults != null;
    }

    /**
     * Return the cached results if present, or start the task.
     * Will wait for completion if the cache is not already populated, otherwise the refresh will happen in the background.
     *
     * @param rdfService an RDF service to use, in foreground mode, if the background service is missing
     * @return
     */
    public synchronized T get(RDFService rdfService) {
        // First, check if there are results from the previous background task, and update the cache
        if (backgroundTask != null && backgroundTask.isDone()) {
            completeBackgroundTask();
        }

        // If we have cached results
        if (cachedResults != null) {
            // If the background service exists, and the cache is considered invalid
            if (backgroundRDFService != null && resultBuilder.invalidateCache(System.currentTimeMillis() - lastCacheTime)) {
                // Determine how long we are prepared to wait for an answer
                long waitFor = (backgroundTask == null ? 1000 : 500);

                // Start the background task to refresh the cache
                startBackgroundTask(rdfService);

                // See if we expect it to complete in time, and if so, wait for it
                if (isExpectedToCompleteIn(waitFor)) {
                    completeBackgroundTask(waitFor);
                }
            }
        } else {
            // No cached results, so fetch the results using any availabe RDF service
            if (rdfService != null) {
                startBackgroundTask(rdfService);
            } else if (backgroundRDFService != null) {
                startBackgroundTask(backgroundRDFService);
            } else {
                throw new RuntimeException("Can't execute without an RDF Service");
            }

            // As there are no cached results, wait for an answer regardless of the RDF service used
            completeBackgroundTask();
        }

        return cachedResults;
    }

    /**
     * (Re)build the current cache
     *
     * @param rdfService an RDF service to use, if the background RDF service is not set
     */
    public synchronized void build(RDFService rdfService) {
        // First, check if there are results from the previous background task, and update the cache
        if (backgroundTask != null && backgroundTask.isDone()) {
            completeBackgroundTask();
        }

        // If we have a background RDF service, we can launch the task in the background and leave it
        if (backgroundRDFService != null) {
            startBackgroundTask(backgroundRDFService);
        } else if (rdfService != null) {
            // No background service, so use the paassed RDF service, and wait for completion
            startBackgroundTask(backgroundRDFService);
            completeBackgroundTask();
        }
    }

    /**
     * Determine if a task is likely to complete with the time frame specified
     *
     * @param interval - time in milliseconds that you want the task to complete in
     * @return true if the task is likely to complete
     */
    private boolean isExpectedToCompleteIn(long interval) {
        // If there is no background task, there is nothing to complete
        if (backgroundTask == null) {
            return false;
        }

        // If the task has already completed, then return true
        if (backgroundTask.isDone()) {
            return true;
        }

        // Get the current time
        long now = System.currentTimeMillis();

        // If the task has started, and has a previous execution time
        if (resultBuilder.startedAt > -1 && resultBuilder.executionTime > -1) {
            // Estimate a finish time, based on when the task started, and how long it last took
            long expectedFinish = resultBuilder.startedAt + resultBuilder.executionTime;

            // If we expect it to complete before the interval passes, return true
            if (expectedFinish < (now + interval)) {
                return true;
            }

        }

        // We expect the task to take longer than the timeout, so return false
        return false;
    }

    /**
     * Create and start a background thread using the configured task
     * @param rdfService
     */
    private void startBackgroundTask(RDFService rdfService) {
        // Ensure that there isn't already a task
        if (backgroundTask == null && rdfService != null) {
            // Set an RDF service to use
            resultBuilder.setRDFService(backgroundRDFService != null ? backgroundRDFService : rdfService);

            // Create the background task, and record the time
            backgroundTask = new FutureTask<T>(resultBuilder);
            backgroundTaskStartTime = System.currentTimeMillis();

            // Start a background thread, ensuring that it can be terminated by the host
            Thread thread = new VitroBackgroundThread(backgroundTask, resultBuilder.getClass().getName());
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Abort the current background task
     */
    private void abortBackgroundTask() {
        // Ensure that we have a background task
        if (backgroundTask != null) {
            // Cancel the background task and clear the start time
            backgroundTask.cancel(true);
            backgroundTask = null;
            backgroundTaskStartTime = -1;
        }
    }

    /**
     * Complete the background task
     */
    private void completeBackgroundTask() {
        completeBackgroundTask(-1);
    }

    /**
     * Complete the background task
     * @param waitFor - maximum time to wait for the results, -1 if forever
     */
    private void completeBackgroundTask(long waitFor) {
        try {
            // If we have a background task
            if (backgroundTask != null) {

                // Update the cached results
                if (waitFor < 0) {
                    cachedResults = backgroundTask.get();
                } else {
                    cachedResults = backgroundTask.get(waitFor, TimeUnit.MILLISECONDS);
                }

                // Set the time of the cache equal to the start time of the task that generated the results
                lastCacheTime = backgroundTaskStartTime;

                // Clear the background task information
                backgroundTask = null;
                backgroundTaskStartTime = -1;
            }
        } catch (InterruptedException e) {
            // Task was interrupted, so abort it
            abortBackgroundTask();
        } catch (ExecutionException e) {
            // There was a problem inside the task, so abort and throw an exception
            try {
                abortBackgroundTask();
            } finally {
                throw new RuntimeException("Background RDF thread through an exception", e.getCause());
            }
        } catch (TimeoutException e) {
            // Ignore a timeout waiting for the results
        }
    }

    /**
     * Set the RDF service to be used for background threads (called from a startup servlet)
     * @param rdfService
     */
    public static void setBackgroundRDFService(RDFService rdfService) {
        backgroundRDFService = rdfService;
    }

    /**
     * Class to be implemented by user to provide the means of generating the results
     * @param <T>
     */
    public static abstract class RDFServiceCallable<T> implements Callable<T> {
        // The RDF Service
        private RDFService rdfService;

        // Start and execution times
        private long startedAt = -1;
        private long executionTime = -1;

        // Affinity object to prevent tasks with the same affinity from running in parallel
        private Affinity affinity = null;

        /**
         * Default constructor
         */
        public RDFServiceCallable() { }

        /**
         * Constructor that allows an affinity object to be supplied
         * @param affinity
         */
        public RDFServiceCallable(Affinity affinity) { this.affinity = affinity; }

        /**
         * Set the RDF service to be used
         * @param rdfService
         */
        final void setRDFService(RDFService rdfService) {
            this.rdfService = rdfService;
        }

        /**
         * Entry point for the background threads, ensuring the right start / cleanup is done
         * @return
         * @throws Exception
         */
        @Override
        final public T call() throws Exception {
            try {
                // If we have an affinity object
                if (affinity != null) {
                    // Ask for permission to process processing
                    affinity.requestStart(executionTime);
                }

                // Record the start time
                startedAt = System.currentTimeMillis();

                // Call the user implementation, passing the RDF service
                T val = callWithService(rdfService);

                // Record how long it to to execute
                executionTime = System.currentTimeMillis() - startedAt;

                // Return the results
                return val;
            } finally {
                // Ensure that we reset the start time
                startedAt = -1;

                // Tell any affinity object that we have completed
                if (affinity != null) {
                    affinity.complete();
                }
            }

        }

        /**
         * Method for users to implement, to return the results
         * @param rdfService
         * @return
         * @throws Exception
         */
        protected abstract T callWithService(RDFService rdfService) throws Exception;

        /**
         * Method to determine if the cache should be invalidated for the current results
         * Default implementation dynamically adjusts the cache time based on the efficiency of creating results
         * @param timeCached
         * @return
         */
        boolean invalidateCache(long timeCached) {
            if (executionTime > -1) {
					/*
						Determine validity as a function of the time it takes to execute the query.

						Query exec time  | Keep cache for
						-----------------+-----------------
						10 seconds       | 20 minutes
						30 seconds       | 1 hour
						1 minute         | 2 hours
						5 minutes        | 10 hours


						Multiplier of the last execution time is 120.

						At most, keep a cache for one day (24 * 60 * 60 * 1000 = 86400000)
					 */

                return timeCached > Math.min(executionTime * 120, 86400000);
            }

            return false;
        }
    }

    /**
     * Affinity class that serializes background processing for tasks given the same affinity
     */
    public static class Affinity {
        private int maxThreads = 1;

        // Map of executing threads, and the time they expect to need to execute
        private Map<Thread, Long> threadToExecutionTime = new HashMap<>();
        private Set<Thread> executingThreads = new HashSet<>();

        /**
         * Called by a background thread to determine if it is allowed to start
         * @param expectedExecutionTime time that the thread expects to take (usualling the last execution time)
         */
        private void requestStart(long expectedExecutionTime) {
            // Ask if the task needs to be queued
            if (queueThis(Thread.currentThread(), expectedExecutionTime)) {
                // Synchronize the thread to call wait
                synchronized (Thread.currentThread()) {
                    try {
                        // Make the thread wait until it is notified to continue
                        Thread.currentThread().wait();
                    } catch(InterruptedException e) {
                    }
                }
            }
        }

        /**
         * Adds a thread to the map, returns whether the thread needs to wait
         * @param thread
         * @param time
         * @return true if the thread needs to wait, false if it can continue
         */
        private synchronized boolean queueThis(Thread thread, Long time) {
            // If we have fewer that the max threads running
            if (executingThreads.size() < maxThreads) {
                // Add thread to executing set
                executingThreads.add(thread);

                // Not queued - we can continue
                return false;
            } else {
                // Add the thread to the map
                threadToExecutionTime.put(thread, time);

                // Let the caller know that we are queued
                return true;
            }
        }

        /**
         * Complete a thread
         */
        private synchronized void complete() {
            // Check that we are tracking this thread
            if (executingThreads.contains(Thread.currentThread())) {

                // Remove the thread from the map
                executingThreads.remove(Thread.currentThread());

                // If there are still threads to execute, and we have not exhausted maximum threads
                while (threadToExecutionTime.size() > 0 && executingThreads.size() < maxThreads) {
                    Thread nextToRelease = null;
                    long executionTime = -1;

                    // Find the thread that expects to take the least time
                    for (Thread thread : threadToExecutionTime.keySet()) {
                        long thisTime = threadToExecutionTime.get(thread);

                        if (nextToRelease == null) {
                            nextToRelease = thread;
                            executionTime = thisTime;
                        } else if (thisTime < executionTime) {
                            nextToRelease = thread;
                            executionTime = thisTime;
                        }
                    }

                    // Synchronize on the thread we are releasing, and notify it to continue
                    synchronized (nextToRelease) {
                        threadToExecutionTime.remove(nextToRelease);
                        executingThreads.add(nextToRelease);
                        nextToRelease.notify();
                    }
                }
            }
        }
    }
}
