package minimon.rest;

import static org.junit.Assert.*;
import minimon.rest.test.config.DataTransformerTestConfig;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rounderdb.RounderDB;

public class DataTransformerTest {
	private static Logger logger = Logger.getLogger(DataTransformerTest.class);

	static AnnotationConfigApplicationContext springContext;

	@BeforeClass
	public static void setup() {
		springContext = new AnnotationConfigApplicationContext(
				DataTransformerTestConfig.class);
	}

	@Test
	public void testFormatArchive() {
		DataTransformer dt = springContext.getBean(DataTransformer.class);
		RounderDB db = (RounderDB) springContext.getBean("testDB");
		db.add("cpuLoad", 1.2, 123L);
		String transformed = dt.formatArchiveJSON("cpuLoad");
		logger.debug("Tranformed string: "+transformed);
		String expected = "(.*)\\{\"x\":123,\"y\":1.2\\}(.*)";
		assertTrue(transformed.matches(expected));
	}

	@AfterClass
	public static void tearDown() {
		springContext.close();
	}

}
