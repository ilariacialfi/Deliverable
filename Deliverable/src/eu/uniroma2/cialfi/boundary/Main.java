package eu.uniroma2.cialfi.boundary;

import java.io.IOException;

import org.json.JSONException;

import eu.uniroma2.cialfi.control.DataExtractor;
public class Main {

	public static void main(String[] args) throws IOException, JSONException {

		DataExtractor.extractCsv("BOOKKEEPER");
		DataExtractor.extractCsv("ZOOKEEPER");

	}

}
