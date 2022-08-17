package eu.uniroma2.cialfi.boundary;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.json.JSONException;

import eu.uniroma2.cialfi.control.GitDataExtractor;
import eu.uniroma2.cialfi.control.JiraDataExtractor;
import eu.uniroma2.cialfi.util.ProjectsEnum;
public class Main {
	static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException, JSONException, InvalidRemoteException, TransportException, GitAPIException {
		
		//TODO per ora faccio estrarre i dati, poi probabilmente devo modificare questa
		//funzione in modo che restituisca il path al csv da passe a weka?
		//o forse weka va utilizzato esternamente e manualmente?
		for (ProjectsEnum p : ProjectsEnum.values()) {
			logger.log(Level.INFO, "Creating CSV file ...");
			JiraDataExtractor.createCSV(p.toString());
			logger.log(Level.INFO, "Importing commits ...");
			GitDataExtractor.getCommit(p.toString());
			
		}

	}

}
