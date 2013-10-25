package minimon.rest.test.config;

import minimon.config.CollectorConfig;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.FilterType;

import rounderdb.Archive;
import rounderdb.RounderDB;

@Configuration
@ComponentScan(
		basePackages = {"minimon.rest"}, 
		excludeFilters = @ComponentScan.Filter(
				value = Service.class, 
				type = FilterType.ANNOTATION)
)
public class DataTransformerTestConfig {
	
	private static Logger logger = Logger.getLogger(DataTransformerTestConfig.class);

	
    @Bean(name="testDB")
    RounderDB rounderDB() {
    	logger.debug("Creating test instance of RounderDB");
    	RounderDB db = new RounderDB();
    	db.addArchive("cpuLoad", new Archive(3));
    	db.addArchive("freeMem", new Archive(3));
    	return db;
    }

}
