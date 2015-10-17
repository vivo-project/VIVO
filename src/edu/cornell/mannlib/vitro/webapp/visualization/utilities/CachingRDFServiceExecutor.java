package edu.cornell.mannlib.vitro.webapp.visualization.utilities;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CachingRDFServiceExecutor<T> {
    private T cachedResults;
    private long lastCacheTime;

    private RDFServiceCallable<T> resultBuilder;
    private FutureTask<T> backgroundTask = null;

    public CachingRDFServiceExecutor(RDFServiceCallable<T> resultBuilder) {
        this.resultBuilder = resultBuilder;
    }

    public synchronized T get(RDFService rdfService) {
        if (cachedResults != null) {
            if (!resultBuilder.invalidateCache(System.currentTimeMillis() - lastCacheTime)) {
                return cachedResults;
            }
        }

        try {
            if (backgroundTask == null) {
                resultBuilder.setRDFService(rdfService);
                backgroundTask = new FutureTask<T>(resultBuilder);

                Thread thread = new Thread(backgroundTask);
                thread.setDaemon(true);
                thread.start();

                if (cachedResults == null || resultBuilder.executionTime < 2000) {
                    completeBackgroundTask();
                }
            } else if (backgroundTask.isDone()) {
                completeBackgroundTask();
            }
        } catch (InterruptedException e) {
            abortBackgroundTask();
        } catch (ExecutionException e) {
            abortBackgroundTask();
            throw new RuntimeException("Background RDF thread through an exception", e.getCause());
        }

        return cachedResults;
    }

    private void abortBackgroundTask() {
        if (backgroundTask != null) {
            backgroundTask.cancel(true);
            backgroundTask = null;
        }
    }

    private void completeBackgroundTask() throws InterruptedException, ExecutionException {
        if (backgroundTask != null) {
            cachedResults = backgroundTask.get();
            lastCacheTime = System.currentTimeMillis();
            backgroundTask = null;
        }
    }

    public static abstract class RDFServiceCallable<T> implements Callable<T> {
        private RDFService rdfService;
        private long executionTime = -1;

        final void setRDFService(RDFService rdfService) {
            this.rdfService = rdfService;
        }

        @Override
        final public T call() throws Exception {
            long start = System.currentTimeMillis();
            T val = callWithService(rdfService);
            executionTime = System.currentTimeMillis() - start;
            return val;
        }

        protected abstract T callWithService(RDFService rdfService) throws Exception;

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
}
