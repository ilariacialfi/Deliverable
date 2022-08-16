package eu.uniroma2.cialfi.boundary;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.json.JSONException;

import eu.uniroma2.cialfi.control.GitDataExtractor;
import eu.uniroma2.cialfi.control.JiraDataExtractor;
import eu.uniroma2.cialfi.util.Projects;
public class Main {

	public static void main(String[] args) throws IOException, JSONException, InvalidRemoteException, TransportException, GitAPIException {

		//TODO per ora faccio estrarre i dati, poi probabilmente devo modificare questa
		//funzione in modo che restituisca il path al csv da passe a weka?
		//o forse weka va utilizzato esternamente e manualmente?
		for (Projects p : Projects.values()) {
			System.out.println("Creating CSV file ...");
			JiraDataExtractor.createCSV(p.toString());
			System.out.println("Importing commits ...");
			GitDataExtractor.getCommit(p.toString());
			
		}

	}

}
