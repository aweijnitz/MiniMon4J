package minimon.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Periodically invokes all metrics collectors.
 * 
 */
@Service("collectoService")
public class CollectorService {
	
	private static Logger logger = Logger.getLogger(CollectorService.class);
	protected static int DEFAULT_INTERVAL = 10;
	protected static TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS; 

	private boolean isRunning = false;
	
	@Autowired
	private ScheduledExecutorService metricsCollector;
	private List<Collector> collectors = new ArrayList<>();

	@Autowired
	public void setCollectors(List<Collector> collectors) {
		logger.debug("Collectors.length "+collectors.size());
		this.collectors = collectors;
	}

	/**
	 * Add data collector. Immediately scheduled for invocation.
	 * @param c An instance of a Collector
	 * @param interval in seconds
	 */
	public synchronized void addDataCollector(Collector c, int interval) {
		if(!isRunning())
			start();
		collectors.add(c);
		metricsCollector.scheduleAtFixedRate(c, 0, interval, TimeUnit.SECONDS);
	}
	
	public synchronized void  start() {
		logger.info("Starting service.");
		for(Collector c : collectors) {
			metricsCollector.scheduleAtFixedRate(c, 0, DEFAULT_INTERVAL, DEFAULT_TIMEUNIT);
		}
		setRunning(true);
	}
	
	public synchronized void stop() {
		logger.info("Stopping service.");
		metricsCollector.shutdown();
		setRunning(false);
	}

	protected ScheduledExecutorService getMetricsCollector() {
		return metricsCollector;
	}

	protected void setMetricsCollector(ScheduledExecutorService metricsCollector) {
		this.metricsCollector = metricsCollector;
	}

	private boolean isRunning() {
		return isRunning;
	}

	private void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
