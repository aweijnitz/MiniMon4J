package minimon;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import minimon.rest.test.config.MiniMonAppTestConf;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import rounderdb.RounderDB;

/**
 * Integration test to test the full call chain.
 */
public class MiniMonAppTest {

	private static Logger logger = Logger.getLogger(MiniMonAppTest.class);

	private static AnnotationConfigApplicationContext springContext;
	private static int PORT = 9876;
	private static String HOST = "localhost";
	private static String OK_URL = "http://" + HOST + ":" + PORT + "/cpuLoad";
	private static String FAIL_URL = "http://" + HOST + ":" + PORT + "/fail";

	/**
	 * Initialize service, add some data and start the REST API
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		springContext = new AnnotationConfigApplicationContext(
				MiniMonAppTestConf.class);

		// Add some data
		RounderDB db = (RounderDB) springContext.getBean("testDB");
		db.add("cpuLoad", 1.2, 123L);
		db.add("cpuLoad", 1.2, 123L);
		db.add("cpuLoad", 1.2, 123L);
		db.add("cpuLoad", 1.2, 123L);

		// Pass test conf
		MiniMonApp.setSpringContext(springContext);

		// Start REST API
		MiniMonApp.startRESTService(HOST, PORT);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MiniMonApp.stopRESTService();
		springContext.close();
	}

	@Test
	public void testResponseCode_HTTP_OK() throws ClientProtocolException,
			IOException {

		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				assertTrue(status == HttpURLConnection.HTTP_OK);
				return "";
			}
		};

		makeRequest(OK_URL, responseHandler);
	}

	@Test
	public void testResponseBody_HTTP_OK() throws ClientProtocolException,
			IOException {

		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				assertTrue(status == HttpURLConnection.HTTP_OK);
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}

		};

		String response = makeRequest(OK_URL, responseHandler);
		String expected = "[{\"name\":\"cpuLoad_0\",\"data\":[{\"x\":123,\"y\":1.2}";
		assertTrue(response.indexOf(expected) == 0);

	}

	@Test
	public void testRespnseCode_HTTP_NOT_FOUND()
			throws ClientProtocolException, IOException {
		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				assertTrue(status == HttpURLConnection.HTTP_NOT_FOUND);
				return "";
			}
		};

		makeRequest(FAIL_URL, responseHandler);
	}

	@Test
	public void testResonseBody_HTTP_NOT_FOUND()
			throws ClientProtocolException, IOException {
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				assertTrue(status == HttpURLConnection.HTTP_NOT_FOUND);
				if (status == HttpURLConnection.HTTP_NOT_FOUND) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}

		};

		String response = makeRequest(FAIL_URL, responseHandler);
		String expected = "{\"Error\":";
		assertTrue(response.indexOf(expected) == 0);
	}

	
	/**
	 * Make HTTP request and return the response body.
	 * 
	 * @param url
	 * @param handler Custom response handler (good place to make assertions)
	 * @return response body as String
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String makeRequest(String url, ResponseHandler<String> handler)
			throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String responseBody = "";
		try {
			HttpGet httpget = new HttpGet(url);

			logger.info("executing request " + httpget.getURI());
			responseBody = httpclient.execute(httpget, handler);
/*			logger.debug("----------------------------------------");
			logger.debug(responseBody);
			logger.debug("----------------------------------------");*/

		} finally {
			httpclient.close();
		}

		return responseBody;
	}

}
