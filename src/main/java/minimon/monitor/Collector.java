package minimon.monitor;

import org.springframework.beans.factory.annotation.Autowired;

import rounderdb.RounderDB;

/**
 * Base class for metrics collectors.
 */
public abstract class Collector implements Runnable {
	
	@Autowired
	private RounderDB db;
	
	protected void addValue(String archiveName, double value) {
		synchronized (db) { // RounderDB is currently not thread safe. Instance 'db' is singelton
			db.add(archiveName, value);			
		}
	}
	
	@Override
	public abstract void run();
}
