package edu.cornell.mannlib.vitro.webapp.visualization.visutils;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;


/* This class is a singleton implementation for locking the concurrent
construction of the models to resolve concurrency issue*/

public final class CustomLock{
	private static final CustomLock instance = new CustomLock();
	private static Lock lock;
	private CustomLock()
	{
		this.lock = new ReentrantLock();
	}
	
	public static CustomLock getInstance()
	{
		return instance;
	}
	
	public static Lock getLock()
	{
		return lock;
	}
}
