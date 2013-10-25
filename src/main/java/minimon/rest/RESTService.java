package minimon.rest;

import io.undertow.Undertow;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * REST Service provider. 
 * Uses an embedded <a href='http://undertow.io/' target='_top'>Undertow</a> instance
 * to serve requests.
 * The API takes the name of a metrics archive as part of the path and returns a 
 * JSON reporesentation of the archive.
 * 
 * Example usage, see class doc for {@link minimon.MiniMonApp.class MiniMonApp}
 *
 */
@Service("RESTService")
public class RESTService {
	private static Logger logger = Logger.getLogger(RESTService.class);

	@Autowired
	private DataHTTPHandler handler;
	private Undertow server;
	
	public RESTService() {	}

	public RESTService(String host, int port) {
		this.configure(host, port);
	}
	
	public void configure(String host, int port) {
		logger.info("Configuring server for http://" + host + ":" + port);

		this.server = Undertow.builder().addListener(port, "localhost")
				.setHandler(handler).build();
	}

	public void start() {
		logger.info("Starting REST service");
		server.start();
	}

	public void stop() {
		logger.info("Stopping REST service");
		server.stop();
	}

	
}
