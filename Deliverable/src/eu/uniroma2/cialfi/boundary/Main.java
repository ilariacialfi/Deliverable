package eu.uniroma2.cialfi.boundary;

import java.io.IOException;

import org.json.JSONException;

import eu.uniroma2.cialfi.control.JiraDataExtractor;
public class Main {

	public static void main(String[] args) throws IOException, JSONException {
	
		//TODO per ora faccio estrarre i dati, poi probabilmente devo modificare questa
		//funzione in modo che restituisce il path al csv da passe a weka?
		//o forse weka va utilizzato esternamente e manualmente?
		JiraDataExtractor.createCSV("BOOKKEEPER");
		JiraDataExtractor.createCSV("ZOOKEEPER");
		
		
	

	}

}
