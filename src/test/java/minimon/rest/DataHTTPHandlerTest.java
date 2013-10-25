package minimon.rest;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataHTTPHandlerTest {
	
	@Test
	public void testParseName() {
		String path = "/cpuLoad";
		String expected = "cpuLoad";
		DataHTTPHandler dh = new DataHTTPHandler();
		assertTrue(expected.equals(dh.parseName(path)));
	}
}
