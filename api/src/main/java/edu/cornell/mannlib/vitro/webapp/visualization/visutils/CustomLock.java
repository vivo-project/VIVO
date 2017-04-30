/* $This file is distributed under the terms of the license in LICENSE$ */
package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;


/* This class is a singleton implementation for locking the concurrent
construction of the models to resolve concurrency issue*/

public final class CustomLock{
	private static final Lock lock = new ReentrantLock();

	public static Lock getLock()
	{
		return lock;
	}
}
