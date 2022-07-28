package eu.uniroma2.cialfi.control;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import eu.uniroma2.cialfi.entity.Version;
import eu.uniroma2.cialfi.util.JSONUtil;


public class DataExtractor {

	public static List<Version> extractVersion(String projName) throws IOException, JSONException{

		//create a list of versions with their attributes
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
				versionList.add(new Version(id, name, dateTime));
			}
		}
		// order releases by date
		//Collections.sort(releases, new Comparator<LocalDateTime>(){
		//@Override
		//	public int compare(LocalDateTime o1, LocalDateTime o2) {
		//		return o1.compareTo(o2);
		//	}
		//});
		return versionList;
	}

	//this method extracts tickets concerning bugs that are closed or resolved
	public static void extractTicket(String projName) throws JSONException, IOException {

		Integer i = 0, total = 1, j = 0;
		String key, ovDateStr, fvDateStr, name = "", id = "", releaseDate = "";
		LocalDate fvDate, ovDate, date;
		LocalDateTime fvDateTime, ovDateTime, dateTime = null;
		ArrayList<Version> avList = new ArrayList<>();

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

			for (; i < total; i++) {
				//Iterate through each bug and print the key
				key = issues.getJSONObject(i).get("key").toString();
				if(fields.has("resolutiondate")) {
					fvDateStr = fields.get("resolutiondate").toString();
					fvDate = LocalDate.parse(fvDateStr);
					fvDateTime = fvDate.atStartOfDay();
				}
				if (fields.has("created")) {
					ovDateStr = fields.get("created").toString();
					ovDate = LocalDate.parse(ovDateStr);
					ovDateTime = ovDate.atStartOfDay();
				}
				for (; j < versions.length(); j++) {
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
						avList.add(new Version(id, name, dateTime));
					}
				}
				






				}  
			} while (i < total);
			return;
		}

		public static void createCSV(String projName) throws IOException, JSONException {
			//TODO devo prendere i ticket e tutti i dati per costruire il csv
			Integer i,numVersions;
			FileWriter fileWriter = null;

			List<Version> versionList = extractVersion(projName);
			try {
				fileWriter = null;
				String outname = projName + "VersionInfo.csv";
				//Name of CSV for output
				fileWriter = new FileWriter(outname);
				fileWriter.append("Index,Version ID,Version Name,Date");
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
