package minimon.rest;

import java.net.HttpURLConnection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rounderdb.RounderDB;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

@Service("dataProvider")
public class DataHTTPHandler implements HttpHandler {

	private static Logger logger = Logger.getLogger(DataHTTPHandler.class);

	@Autowired
	private DataTransformer transformer;

	public void handleRequest(HttpServerExchange exchange) throws Exception {

		String archiveName = parseName(exchange.getRequestPath());
		String response = transformer.formatArchiveJSON(archiveName);

		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
		
		if(response == null) {
			exchange.setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
			exchange.getResponseSender().send("{\"Error\":\"'"+archiveName+"' archive NOT FOUND\"}");						
		}
		else {
			exchange.setResponseCode(HttpURLConnection.HTTP_OK);
			exchange.getResponseSender().send(response);			
		}
	}
	
	protected String parseName(String requestPath) {
		logger.debug("Parsing archive name from path: "+requestPath);
		String name = null;
		if(requestPath != null && requestPath.length() > 1)
			name = requestPath.substring(1); // Peel off the leading '/' and that's it
		return name;
	} 

}
