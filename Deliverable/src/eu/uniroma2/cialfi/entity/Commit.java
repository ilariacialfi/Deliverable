package eu.uniroma2.cialfi.entity;

import java.time.LocalDateTime;

public class Commit {
	private String name;
	private String author;
	private String comment;
	private LocalDateTime time;

	public Commit(String name, String author, String comment, LocalDateTime time) {
		this.name = name;
		this.author = author;
		this.comment = comment;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//return the Opening Version
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	//return the Fixed Version
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	//return the Affected Versions
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}
