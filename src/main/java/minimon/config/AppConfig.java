package minimon.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Misc. internal configuration.
 * 
 */
@Configuration
@ComponentScan("minimon")
public class AppConfig {

	private static Logger logger = Logger.getLogger(AppConfig.class);

	
	@Bean(name="metricsCollector")
	ScheduledExecutorService metricsCollector() {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
		return scheduledExecutorService;
	}
	
}
