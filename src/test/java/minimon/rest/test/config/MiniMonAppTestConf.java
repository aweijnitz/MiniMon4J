package minimon.rest.test.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import minimon.config.AppConfig;
import minimon.monitor.Collector;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

import rounderdb.Archive;
import rounderdb.RounderDB;

@Configuration
@ComponentScan(basePackages = { "minimon.rest", "minimon.monitor" })
public class MiniMonAppTestConf {
	private static Logger logger = Logger.getLogger(MiniMonAppTestConf.class);
	
    @Bean(name="testDB")
    RounderDB rounderDB() {
    	logger.debug("Creating test instance of RounderDB");
    	RounderDB db = new RounderDB();
    	db.addArchive("cpuLoad", new Archive(3));
    	db.addArchive("freeMem", new Archive(3));
    	return db;
    }
    
	@Bean
	Collector NOpCollector() {
		logger.debug("Creating empty NOpCollector");
		return new Collector() {
			public void run() {
				Logger.getLogger(Collector.class).debug("Empty collector RUN");
			}
		};
	}


	@Bean(name = "metricsCollector")
	ScheduledExecutorService metricsCollector() {
		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(5);
		return scheduledExecutorService;
	}

}
