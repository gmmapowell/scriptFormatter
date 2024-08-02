package test.geofs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.doubled.PlaceString;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class ReadJson {

	@Test
	public void anEmptyJsonObjectCanBeReadFromAPlace() throws JSONException {
		Place pd = new PlaceString("{}");
		JSONObject jo = GeoFSUtils.readJSON(pd);
		assertNotNull(jo);
		assertEquals(0, jo.length());
	}

	@Test(expected=JSONException.class)
	public void AnErrorIsThrownFromANonJSONPlace() throws JSONException {
		Place pd = new PlaceString("hello, world");
		JSONObject jo = GeoFSUtils.readJSON(pd);
		assertNotNull(jo);
		assertEquals(0, jo.length());
	}

	@Test
	public void aSimpleJsonObjectCanBeReadFromAPlace() throws JSONException {
		Place pd = new PlaceString("{\"hello\":\"world\"}");
		JSONObject jo = GeoFSUtils.readJSON(pd);
		assertNotNull(jo);
		assertEquals(1, jo.length());
		assertEquals("world", jo.getString("hello"));
	}

}
