package eu.uniroma2.cialfi.control;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.DepthWalk.Commit;

import eu.uniroma2.cialfi.entity.Ticket;
import eu.uniroma2.cialfi.entity.Version;

public class GitDataExtractor {
	private Git git;
	private File repo;
	private List<Ticket> ticketList;
	private List<Version> versionList;
	private List<Commit> commitList;
	
	//git = Git.init().setDirectory("/path/to/repo").call();
	//https://www.baeldung.com/jgit
}
