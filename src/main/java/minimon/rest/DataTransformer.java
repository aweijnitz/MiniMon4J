package minimon.rest;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import rounderdb.Archive;
import rounderdb.RounderDB;
import rounderdb.store.DataPoint;

/**
 * Fetches archive data and transforms it to JSON.
 */
@Component
public class DataTransformer {
	
	private static Logger logger = Logger.getLogger(DataTransformer.class);


	@Autowired
	private RounderDB rounderDB;
	
	public DataTransformer() { 
		
	}
	
	public DataTransformer(RounderDB db) {
		this.rounderDB = db;
	}

	/*
	 * Return format [ {"name": archName_0, "data": [{"x": 121, "y":1.2}] },
	 * {"name": archName_1, "data": [{},{},{}]} ]
	 */

	public String formatArchiveJSON(String name) {
		// Note: 
		// This implementation is not scalable to huge buckets,
		// but since the typical bucket size is small (KB, not MB), it's ok.
		// Should switch to a stream oriented conversion if need to manage huge 
		// stores arise.
		// 
		ArrayList<Bucket> result = new ArrayList<>();
		Archive a = getDB().getArchive(name);
		if (a == null)
			return null;
		
		for (int i = 0; i < a.nrBuckets(); i++) {
			DataPoint[] points = a.getDataForBucket(i);
			result.add(new Bucket(name+"_"+i, points));
		}
		String json = toJSON(result);
		logger.debug("Returning: "+json);
		
		return json;
	}

	private String toJSON(ArrayList<Bucket> data) {
		Gson gson = new GsonBuilder()
		.registerTypeAdapter(DataPoint.class, new PointSerializer())
//		.setPrettyPrinting()
		.create();

		return gson.toJson(data);
	}
	

	private RounderDB getDB() {
		return rounderDB;
	}

	private void setDB(RounderDB rounderDB) {
		this.rounderDB = rounderDB;
	}

	
	// Helper classes below ________________
	// 
	
	private class PointSerializer implements JsonSerializer<DataPoint> {

		@Override
		public JsonElement serialize(DataPoint src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			JsonObject jo = new JsonObject();
			jo.add("x", new JsonPrimitive(new Long(src.getTimestamp())));
			jo.add("y", new JsonPrimitive(new Double(src.getValue())));
			
			return jo;
		}
	}

	private class Bucket {
		protected String name;
		protected DataPoint[] data;

		public Bucket(String name, DataPoint[] data) {
			this.name = name;
			this.data = data;
		}
	}

}
