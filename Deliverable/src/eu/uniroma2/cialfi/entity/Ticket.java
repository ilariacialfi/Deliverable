package eu.uniroma2.cialfi.entity;

import java.util.List;

public class Ticket {
	private String key;
	private Version ov;
	private Version fv;
	private List<Version> av;

	public Ticket(String key, Version ov, Version fv, List<Version> av) {
		this.key = key;
		this.ov = ov;
		this.fv = fv;
		this.av = av;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	//return the Opening Version
	public Version getOv() {
		return ov;
	}

	public void setOv(Version ov) {
		this.ov = ov;
	}

	//return the Fixed Version
	public Version getFv() {
		return fv;
	}

	public void setFv(Version fv) {
		this.fv = fv;
	}

	//return the Affected Versions
	public List<Version> getAv() {
		return av;
	}

	public void setAv(List<Version> av) {
		this.av = av;
	}
}
