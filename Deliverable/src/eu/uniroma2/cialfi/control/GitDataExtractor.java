package eu.uniroma2.cialfi.control;

import java.io.File;
//import java.io.IOException;
import org.eclipse.jgit.api.Git;

import eu.uniroma2.cialfi.util.FilesUtil;

//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.api.errors.InvalidRemoteException;
//import org.eclipse.jgit.api.errors.TransportException;
import org.apache.commons.io.FileUtils;

public class GitDataExtractor {


	public static void getCommit(String projName){
		String repoURI = "https://github.com/apache/" + projName + ".git";

		//clone project repository from git
		File dir = new File(projName);
		try {

			FileUtils.deleteDirectory(dir);
			Git.cloneRepository().setURI(repoURI).setDirectory(dir).call();
		} catch (Exception e) {}
	
		//catch all the files	
		System.out.println("STAMPO I FILE di " + projName);
		FilesUtil.ListFiles(dir);

		return;
	}

	//git = Git.init().setDirectory("/path/to/repo").call();
	//https://www.baeldung.com/jgit
}
