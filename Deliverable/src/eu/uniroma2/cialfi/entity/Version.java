package eu.uniroma2.cialfi.entity;

import java.time.LocalDateTime;

public class Version {

	private String id;
	private String name;
	private LocalDateTime date;

	public Version(String id, String name, LocalDateTime date) {
		this.id = id;
		this.name = name;
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getDate() {
		return date;
	}
 
	public void setDate(LocalDateTime date) {
		this.date = date;
	}

}
