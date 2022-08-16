package eu.uniroma2.cialfi.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import org.apache.commons.io.FileUtils;

public class GitDataExtractor {
	static Logger logger = Logger.getLogger(GitDataExtractor.class.getName());

	public static void getCommit(String projName) throws IOException, GitAPIException{
		String repoURI = "https://github.com/apache/" + projName + ".git";
		
		Collection<File> javaFiles;

		//clone project repository from Git
		cloneRepo(projName, repoURI);
		//list all java files	
		javaFiles = listJavaFiles(projName);
		//TODO devo andare a vedere quali file vengono modificati da commmit riguardanti risoluzione di bug
		
	}

	private static Collection<File> listJavaFiles(String projName) throws IOException, GitAPIException {
		logger.log(Level.INFO, "Java Files List of {0}", projName);
		
		List<Ref> branches;
		File dir = new File(projName);
		Collection<File> javaFiles = new ArrayList<>();
		Repository repo = new FileRepository(projName + "/.git");
		Git git = new Git(repo);
		
		javaFiles = FileUtils.listFiles(dir, new String[] {"java"}, true);
		for (File f : javaFiles) {
			logger.log(Level.INFO, f.getPath());
		}

	
		try {
			//walk = new RevWalk(repo);
			branches = git.branchList().call();
			//list all commits of all branches
			for (Ref branch : branches) {
				String treeName = branch.getName();

				logger.log(Level.INFO, "Commits of branch: {0}", branch.getName());
				logger.log(Level.INFO, "-------------------------------------");

				for (RevCommit commit : git.log().add(repo.resolve(treeName)).call()) {
					System.out.println(commit.getShortMessage());
				}
			}
		} finally {
			git.close();
		}
		return javaFiles;
	}

	private static void cloneRepo(String projName, String repoURI) {
		File dir = new File(projName);
		if (!dir.exists()) {
			logger.log(Level.INFO, "Cloning repository {0}", projName);
			try {
				FileUtils.deleteDirectory(dir);
				Git.cloneRepository().setURI(repoURI).setDirectory(dir).call();
			} catch (Exception e) {}
		} else {
			logger.log(Level.INFO, "Repository {0} already cloned", projName);
		}
	}
}
