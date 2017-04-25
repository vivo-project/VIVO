/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.utils.threads.VitroBackgroundThread;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility class that populates and returns a cache.
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

    private final RDFServiceCallable<T> resultBuilder;

    /**
     * Background task tracker
     */
    private FutureTask<T> backgroundTask = null;
    private Thread backgroundCompletion = null;
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

    public Date cachedWhen() { return new Date(lastCacheTime); }

    /**
     * Return the cached results if present, or start the task.
     * Will wait for completion if the cache is not already populated, otherwise the refresh will happen in the background.
     *
     * @param rdfService an RDF service to use, in foreground mode, if the background service is missing
     */
    public synchronized T get(RDFService rdfService) {
        return get(rdfService, false);
    }

    /**
     * Return the cached results if present, or start the task.
     * Will wait for completion if the cache is not already populated, otherwise the refresh will happen in the background.
     *
     * @param rdfService an RDF service to use, in foreground mode, if the background service is missing
     */
    public synchronized T getNoWait(RDFService rdfService) {
        return get(rdfService, true);
    }

    /**
     * Return the cached results if present, or start the task.
     * Will wait for completion if the cache is not already populated, otherwise the refresh will happen in the background.
     *
     * @param rdfService an RDF service to use, in foreground mode, if the background service is missing
     */
    public synchronized T get(RDFService rdfService, boolean allowWaits) {
        // First, check if there are results from the previous background task, and update the cache
        if (backgroundTask != null && backgroundTask.isDone()) {
            completeBackgroundTask();
        }

        // If we have cached results
        if (cachedResults != null) {
            // If the background service exists, and the cache is considered invalid
            if (backgroundRDFService != null && resultBuilder.invalidateCache(System.currentTimeMillis() - lastCacheTime)) {
                // In most cases, only wait for half a second
                long waitFor = 500;

                if (backgroundTask == null) {
                    // Start the background task to refresh the cache
                    startBackgroundTask(backgroundRDFService);

                    // As we've just started the background task, allow a wait time of 1 second
                    waitFor = 1000;
                }

                // See if we expect it to complete in time, and if so, wait for it
                if (allowWaits && isExpectedToCompleteIn(waitFor)) {
                    completeBackgroundTask(waitFor);
                }
            }
        } else {
            // No cached results, so fetch the results using any available RDF service
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
    public void build(RDFService rdfService) {
        // First, check if there are results from the previous background task, and update the cache
        if (backgroundTask != null && backgroundTask.isDone()) {
            buildComplete();
        } else if (backgroundTask == null) {
            buildStart(rdfService);
        }
    }

    private synchronized void buildComplete() {
        if (backgroundTask != null && backgroundTask.isDone()) {
            completeBackgroundTask();
        }
    }

    private synchronized void buildStart(RDFService rdfService) {
        if (backgroundTask == null) {
            // If we have a background RDF service, we can launch the task in the background and leave it
            if (backgroundRDFService != null) {
                startBackgroundTask(backgroundRDFService);
                completeBackgroundTaskAsync();
            } else if (rdfService != null) {
                // No background service, so use the passed RDF service, and wait for completion
                startBackgroundTask(rdfService);
                completeBackgroundTask();
            }
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
     * @param rdfService An RDFService
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
    private synchronized void completeBackgroundTaskAsync() {
        if (backgroundCompletion == null) {
            backgroundCompletion = new Thread(new Runnable() {
                @Override
                public void run() {
                    completeBackgroundTask(-1);
                }
            });
            backgroundCompletion.setDaemon(true);
            backgroundCompletion.start();
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
                backgroundCompletion = null;
            }
        } catch (InterruptedException e) {
            // Task was interrupted, so abort it
            abortBackgroundTask();
        } catch (ExecutionException e) {
            // There was a problem inside the task, so abort and throw an exception
            abortBackgroundTask();
            throw new RuntimeException("Background RDF thread through an exception", e.getCause());
        } catch (TimeoutException e) {
            // Ignore a timeout waiting for the results
        }
    }

    /**
     * Set the RDF service to be used for background threads (called from a startup servlet)
     * @param rdfService An RDFService
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
         * @param affinity Affinity
         */
        public RDFServiceCallable(Affinity affinity) { this.affinity = affinity; }

        /**
         * Set the RDF service to be used
         * @param rdfService An RDFService
         */
        final void setRDFService(RDFService rdfService) {
            this.rdfService = rdfService;
        }

        /**
         * Entry point for the background threads, ensuring the right start / cleanup is done
         * @throws Exception Any exception
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
         * @param rdfService An RDFService
         * @throws Exception Any exception
         */
        protected abstract T callWithService(RDFService rdfService) throws Exception;

        /**
         * Method to determine if the cache should be invalidated for the current results
         * Default implementation dynamically adjusts the cache time based on the efficiency of creating results
         * @param timeCached The time of caching
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
        private final int maxThreads = 1;

        static class ThreadControl {
            ThreadControl(long started, long expectedDuration) {
                this.started = started;
                this.expectedDuration = expectedDuration;
            }

            final long started;
            final long expectedDuration;
            final CountDownLatch latch = new CountDownLatch(1);
        }

        // Map of executing threads, and the time they expect to need to execute
        private final Map<Thread, ThreadControl> threadToExecutionTime = new HashMap<>();
        private final Set<Thread> executingThreads = new HashSet<>();

        /**
         * Called by a background thread to determine if it is allowed to start
         * @param expectedExecutionTime time that the thread expects to take (usually the last execution time)
         */
        private void requestStart(long expectedExecutionTime) {
            Thread executingThread = Thread.currentThread();

            // Ask if the task needs to be queued
            CountDownLatch latch = queueThis(executingThread, expectedExecutionTime);

            // We got a latch from the queue, so wait for it to clear
            if (latch != null) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
            }
        }

        /**
         * Adds a thread to the map, returns whether the thread needs to wait
         * @param thread The thread to add
         * @param time start time of the thread
         * @return true if the thread needs to wait, false if it can continue
         */
        private synchronized CountDownLatch queueThis(Thread thread, Long time) {
            // If we have fewer that the max threads running
            if (executingThreads.size() < maxThreads) {
                // Add thread to executing set
                executingThreads.add(thread);

                // Not queued - we can continue
                return null;
            } else {
                ThreadControl control = new ThreadControl(System.currentTimeMillis(), time);

                // Add the thread to the map
                threadToExecutionTime.put(thread, control);

                // Give the caller a handle to the latch for the queued thread
                return control.latch;
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
                    ThreadControl nextToReleaseControl = null;
                    long current = System.currentTimeMillis();
                    boolean favourStartTime = false;

                    // Find the thread that expects to take the least time
                    for (Thread thread : threadToExecutionTime.keySet()) {
                        ThreadControl threadControl = threadToExecutionTime.get(thread);

                        // If there are threads that have been waiting over 2 seconds, favour the oldest thread
                        if (threadControl.started + 2000 < current) {
                            favourStartTime = true;
                        }

                        if (nextToRelease == null) {
                            nextToRelease = thread;
                            nextToReleaseControl = threadControl;
                        } else {
                            if (favourStartTime) {
                                // Find the oldest thread
                                if (threadControl.started < nextToReleaseControl.started) {
                                    nextToRelease = thread;
                                    nextToReleaseControl = threadControl;
                                }
                            } else if (threadControl.expectedDuration < nextToReleaseControl.expectedDuration) {
                                nextToRelease = thread;
                                nextToReleaseControl = threadControl;
                            }
                        }
                    }

                    // Notify the Thread we are releasing to continue
                    if (nextToRelease != null) {
                        threadToExecutionTime.remove(nextToRelease);
                        executingThreads.add(nextToRelease);
                        nextToReleaseControl.latch.countDown();
                    }
                }
            }
        }
    }
}
