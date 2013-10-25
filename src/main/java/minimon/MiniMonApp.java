package minimon;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import minimon.config.AppConfig;
import minimon.monitor.CollectorService;
import minimon.rest.RESTService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Main entry point. This class fires up the server and starts collecting and
 * serving metrics. Metrics is served over a REST API in JSON format.
 * 
 * Example request: <code>curl -X GET "http://localhost:8000/cpuLoad"</code>
 * 
 */
public class MiniMonApp {

	private static Logger logger = Logger.getLogger(MiniMonApp.class);

	private static AnnotationConfigApplicationContext springContext;

	public static void main(String[] args) {
		int port = 8000;
		String rootDir = null;
		String host = "localhost";

		if (args.length > 0)
			port = parsePort(args[0], port);
		if (args.length > 1)
			rootDir = defaultRootDir(args[1]);

		// Start REST Webservice and add shutdown hook
		//
		startRESTService(host, port);
		attachShutDownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Stopping REST Service");
				getSpringContext().getBean(RESTService.class).stop();
			}
		});

		// Start monitoring jobs (data collectors) and add shutdown hook
		//
		startCollectorService();
		attachShutDownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Stopping Collector Service");
				getSpringContext().getBean(CollectorService.class).stop();
			}
		});

	}

	protected static void startCollectorService() {
		getSpringContext().getBean(CollectorService.class).start();
	}

	protected static void stopCollectorService() {
		getSpringContext().getBean(CollectorService.class).stop();
	}

	protected static RESTService startRESTService(String host, int port) {

		RESTService service = getSpringContext().getBean(RESTService.class);
		service.configure("localhost", port);
		service.start();

		return service;
	}

	protected static void stopRESTService() {
		getSpringContext().getBean(RESTService.class).stop();
	}

	private static int parsePort(String portStr, int defaultPort) {
		int port = defaultPort;
		if (portStr != null)
			port = Integer.parseInt(portStr);
		return port;
	}

	private static String defaultRootDir(String arg) {
		if (arg == null) {
			// figure out the current baseDir
			Path relativePath = Paths.get("");
			String baseDir = relativePath.toAbsolutePath().toString();
			arg = baseDir + File.separator + "src" + File.separator + "main"
					+ File.separator + "webapp";
		}
		return arg;
	}

	protected synchronized static void setSpringContext(
			AnnotationConfigApplicationContext context) {
		springContext = context;
	}

	public static AnnotationConfigApplicationContext getSpringContext() {
		if (springContext == null) {
			synchronized (MiniMonApp.class) {
				springContext = new AnnotationConfigApplicationContext(
						AppConfig.class);
			}
		}
		return springContext;
	}

	public static void attachShutDownHook(Thread hook) {
		Runtime.getRuntime().addShutdownHook(hook);
		logger.debug("Attached shutdown hook");
	}
}
