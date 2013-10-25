package minimon.config;

import minimon.monitor.Collector;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rounderdb.Archive;
import rounderdb.RounderDB;
/**
 * Load/create DB instance and define data collectors.
 * 
 */
@Configuration
public class CollectorConfig {
	private static Logger logger = Logger.getLogger(CollectorConfig.class);
	
    @Bean(name="db")
    RounderDB rounderDB() {
    	logger.debug("Creating instance of RounderDB");
    	// TODO: Load existing instance from disk, or read config file and create new instance
    	RounderDB db = new RounderDB();
    	db.addArchive("cpuLoad", new Archive(3));
    	db.addArchive("freeMem", new Archive(3));
    	return db;
    }
    
	
	@Bean
	Collector randomCollector() {
		logger.debug("Creating randomCollector");
		return new Collector() {
			public void run() {
				Logger.getLogger(Collector.class).debug("Random collector RUN");
				addValue("cpuLoad", 0.5+Math.random());
			}
		};
	}
	
	@Bean
	Collector memoryCollector() {
		logger.debug("Creating memoryCollector");
		return new Collector() {
			public void run() {
				Logger.getLogger(Collector.class).debug("Memory collector RUN");
				addValue("freeMem", Runtime.getRuntime().freeMemory()/(1024*1024));
			}
		};
	}
	
	
}
