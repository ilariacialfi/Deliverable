package eu.uniroma2.cialfi.control;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import eu.uniroma2.cialfi.entity.Ticket;
import eu.uniroma2.cialfi.entity.Version;
import eu.uniroma2.cialfi.util.JSONUtil;


public class DataExtractor {

	//this method extracts all the versions of the project
	public static List<Version> extractVersion(String projName) throws IOException, JSONException{

		Map<LocalDateTime, String> preVersionMap = new HashMap<>();
		Map<LocalDateTime, String> preIdMap = new HashMap<>();
		List<Version> versionList = new ArrayList<>();
		
		Integer i;
		String name = "";
		String id = "";
		String releaseDate = "";
		LocalDate date;
		LocalDateTime dateTime = null;

		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;

		JSONObject json = JSONUtil.readJsonFromUrl(url);
		JSONArray versions = json.getJSONArray("versions");

		for (i = 0; i < versions.length(); i++ ) {

			if(versions.getJSONObject(i).has("releaseDate")) {
				if (versions.getJSONObject(i).has("name"))
					name = versions.getJSONObject(i).get("name").toString();
				if (versions.getJSONObject(i).has("id"))
					id = versions.getJSONObject(i).get("id").toString();
				if (versions.getJSONObject(i).has("releaseDate")) {
					releaseDate = versions.getJSONObject(i).get("releaseDate").toString();
					date = LocalDate.parse(releaseDate);
					dateTime = date.atStartOfDay();
				}
				preVersionMap.put(dateTime, name);
				preIdMap.put(dateTime, id);
			}
		}
		//order versions by date
		Map <LocalDateTime, String> versionMap = new TreeMap<>(preVersionMap);
		Map <LocalDateTime, String> idMap = new TreeMap<>(preIdMap);
		for (Map.Entry<LocalDateTime, String> entry : versionMap.entrySet()) {
			id = idMap.get(entry.getKey());
			versionList.add(new Version(id, entry.getValue(), entry.getKey()));
		}
		return versionList;
	}

	//this method extracts tickets concerning bugs that are closed or resolved
	public static List<Ticket> extractTicket(String projName) throws JSONException, IOException {

		Integer i = 0, total = 1, j = 0;
		String key, dateStr, name = "", id = "", releaseDate = "";
		LocalDate date;
		LocalDateTime dateTime = null;
		Version fv = null, ov = null;
		List<Version> avList = new ArrayList<>();
		List<Ticket> ticketList = new ArrayList<>();

		//collect all the versions available
		List<Version> versionList = extractVersion(projName);

		//extract the total number of tickets
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
				+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
				+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created";
		JSONObject json = JSONUtil.readJsonFromUrl(url);
		total = json.getInt("total");

		//Get JSON API for closed bugs w/ AV in the project
		do {
			String newUrl = url + "&startAt=" + i.toString() + "&maxResults=" + total;
			JSONObject newJson = JSONUtil.readJsonFromUrl(newUrl);
			JSONArray issues = newJson.getJSONArray("issues");
			JSONObject fields = issues.getJSONObject(i).getJSONObject("fields");
			JSONArray versions = fields.getJSONArray("versions");

			//Iterate through each issue
			for (; i < total; i++) {
				//ticket key
				key = issues.getJSONObject(i%1000).get("key").toString();
				//System.out.println(key);
				//fixed version
				if(fields.has("resolutiondate")) {
					dateStr = fields.get("resolutiondate").toString();
					dateStr = dateStr.substring(0, dateStr.length()-12);
					dateTime = LocalDateTime.parse(dateStr);
					for (Version v : versionList) {
						if (v.getDate().isAfter(dateTime)) {
							fv = v;
						}
					}
				}
				//opening version
				if (fields.has("created")) {
					dateStr = fields.get("created").toString();
					dateStr = dateStr.substring(0, dateStr.length()-12);
					dateTime = LocalDateTime.parse(dateStr);
					//find opening version after this date
					for (Version v : versionList) {
						if (v.getDate().isAfter(dateTime)) {
							ov = v;
						}
					}
				}
				//affected versions list
				for (; j < versions.length(); j++) {
					if(versions.getJSONObject(j).has("id")) {
						//find version by id
						for (Version v : versionList) {
							if (v.getId().equals(versions.getJSONObject(j).get("id"))) {
								avList.add(v);
							}
						}
					}
				}


				Ticket ticket = new Ticket(key, ov, fv, avList);
				ticketList.add(ticket);
			}  
		} while (i < total);
		return ticketList;
	}

	/*this method creates a csv file with these columns:
	 * Index,Version ID,Version Name,Date,Buggy
	 */
	public static void createCSV(String projName) throws IOException, JSONException {
		//TODO devo prendere i ticket e tutti i dati per costruire il csv
		Integer i,numVersions;
		FileWriter fileWriter = null;

		List<Version> versionList = extractVersion(projName);
		List<Ticket> ticketList = extractTicket(projName);
		try {
			fileWriter = null;
			String outname = projName + "VersionInfo.csv";
			//Name of CSV for output
			fileWriter = new FileWriter(outname);
			fileWriter.append("Index,Version ID,Version Name,Date,Buggy");
			fileWriter.append("\n");
			numVersions = versionList.size();
			for ( i = 0; i < numVersions; i++) {
				Integer index = i + 1;
				fileWriter.append(index.toString());
				fileWriter.append(",");
				fileWriter.append(versionList.get(i).getId());
				fileWriter.append(",");
				fileWriter.append(versionList.get(i).getName());
				fileWriter.append(",");
				fileWriter.append(versionList.get(i).getDate().toString());
				fileWriter.append("\n");
				//if version is an av for any ticket set buggy = 'yes'
			}

		} catch (Exception e) {
			System.out.println("Error in csv writer");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
		return;
	}
}
