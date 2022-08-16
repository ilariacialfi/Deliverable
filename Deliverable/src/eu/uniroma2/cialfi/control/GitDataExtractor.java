package eu.uniroma2.cialfi.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.api.errors.InvalidRemoteException;
//import org.eclipse.jgit.api.errors.TransportException;
import org.apache.commons.io.FileUtils;

public class GitDataExtractor {


	public static void getCommit(String projName) throws IOException, GitAPIException{
		String repoURI = "https://github.com/apache/" + projName + ".git";
		Repository repo = null; 
		List<Ref> branches;
		RevWalk walk;
		Git git;
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Collection<File> javaFiles = new ArrayList<>();


		//clone project repository from git
		File dir = new File(projName);
		if (!dir.exists()) {
			System.out.println("Cloning repository " + projName);
			try {
				FileUtils.deleteDirectory(dir);
				Git.cloneRepository().setURI(repoURI).setDirectory(dir).call();
			} catch (Exception e) {}
		} else {
			System.out.println("Repository " + projName + " already cloned");
		}

		//list all java files	
		System.out.println("Java Files List of " + projName);
		//javaFiles = FilesUtil.ListJavaFiles(dir);
		javaFiles = FileUtils.listFiles(dir, new String[] {"java"}, true);
		for (File f : javaFiles) {
			System.out.println(f.getPath());
		}

		repo = new FileRepository(projName + "/.git");
		git = new Git(repo);
		walk = new RevWalk(repo);
		branches = git.branchList().call();
		//list all commits of all branches
		for (Ref branch : branches) {
			String treeName = branch.getName();

			System.out.println("Commits of branch: " + branch.getName());
			System.out.println("-------------------------------------");

			for (RevCommit commit : git.log().add(repo.resolve(treeName)).call()) {
				System.out.println(commit.getShortMessage());
			}

			return;
		}

	}
}
