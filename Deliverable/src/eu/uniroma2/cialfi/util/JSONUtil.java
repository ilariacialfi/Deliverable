package eu.uniroma2.cialfi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.uniroma2.cialfi.boundary.Main;

public class JSONUtil {
	static Logger logger = Logger.getLogger(JSONUtil.class.getName());

	//returns a json object containing the page 
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		String jsonText = "";
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
			jsonText = readAll(rd);
		} catch (Exception e) {
		}
		return new JSONObject(jsonText);
	}

	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		String jsonText = "";
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
			jsonText = readAll(rd);
		} catch (Exception e) {
		}
		return new JSONArray(jsonText);
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
}
