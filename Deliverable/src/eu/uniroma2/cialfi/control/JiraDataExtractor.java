package eu.uniroma2.cialfi.control;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import eu.uniroma2.cialfi.entity.Ticket;
import eu.uniroma2.cialfi.entity.Version;
import eu.uniroma2.cialfi.util.JSONUtil;


public class JiraDataExtractor {

	/*this method creates a csv file with these columns:
	 * Index,Version ID,Version Name,Date,Buggy
	 */
	public static void createCSV(String projName) throws IOException, JSONException {
		Integer i,numVersions;
		FileWriter fileWriter = null;

		List<Version> versionList = extractVersion(projName);
		List<Ticket> ticketList = extractTicket(projName, versionList);
		List<String> bugList = getBuggyVersions(ticketList, versionList);

		//TODO PROVA
		printResults(versionList, ticketList);

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
				String id = versionList.get(i).getId();
				String name = versionList.get(i).getName();
				LocalDateTime date = versionList.get(i).getDate();
				String bugginess = "";

				if (bugList.contains(id)) {
					bugginess = "yes";
				} else {
					bugginess = "no";
				}

				fileWriter.append(index.toString());
				fileWriter.append(",");
				fileWriter.append(id);
				fileWriter.append(",");
				fileWriter.append(name);
				fileWriter.append(",");
				fileWriter.append(date.toString());
				fileWriter.append(",");
				fileWriter.append(bugginess);
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

	//TODO PROVA
	private static void printResults(List<Version> versionList, List<Ticket> ticketList) {
		FileWriter fileWriter = null;
		String outname = "Results.csv";
		try {
			//Name of CSV for output
			fileWriter = new FileWriter(outname);

			fileWriter.append("Versions\n");
			for (Version v : versionList) {
				fileWriter.append(v.getDate().toString());
				fileWriter.append("\n");
			}

			for (Ticket t: ticketList) {
				fileWriter.append("\nticket " + t.getKey());
				fileWriter.append("\nfv: " + t.getFv().getDate());
				fileWriter.append("\nov: " + t.getOv().getDate());

				try {
					fileWriter.append("\nAffected Versions:");
					for (Version v : t.getAv()) {

						fileWriter.append("\nid: " + v.getId() +
								" date: " + v.getDate());
					}
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {}

	}

	//this method extracts all the versions of the project
	private static List<Version> extractVersion(String projName) throws IOException, JSONException{

		List<Version> versionList = new ArrayList<>();
		Map<LocalDateTime, String> preVersionMap = new HashMap<>();
		Map<LocalDateTime, String> preIdMap = new HashMap<>();

		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;

		JSONObject json = JSONUtil.readJsonFromUrl(url);
		JSONArray versions = json.getJSONArray("versions");

		for (int i = 0; i < versions.length(); i++ ) {

			String name = "";
			String id = "";
			String releaseDate = "";
			LocalDate date;
			LocalDateTime dateTime = null;

			//extract only versions with name, id, releaseDate
			try {
				name = versions.getJSONObject(i).get("name").toString();
				id = versions.getJSONObject(i).get("id").toString();
				releaseDate = versions.getJSONObject(i).get("releaseDate").toString();
				date = LocalDate.parse(releaseDate);
				dateTime = date.atStartOfDay();

				preVersionMap.put(dateTime, name);
				preIdMap.put(dateTime, id);

			} catch(Exception e) {
				//try next 
			}
		}
		//order versions by date
		Map <LocalDateTime, String> versionMap = new TreeMap<>(preVersionMap);
		Map <LocalDateTime, String> idMap = new TreeMap<>(preIdMap);
		for (Map.Entry<LocalDateTime, String> entry : versionMap.entrySet()) {
			String id = idMap.get(entry.getKey());
			versionList.add(new Version(id, entry.getValue(), entry.getKey()));
		}
		return versionList;
	}

	//this method extracts tickets concerning bugs that are closed or resolved
	private static List<Ticket> extractTicket(String projName, List<Version> versionList) throws JSONException, IOException {

		List<Ticket> ticketList = new ArrayList<>();

		//extract the total number of tickets
		String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
				+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
				+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created";
		JSONObject json = JSONUtil.readJsonFromUrl(url);
		int total = json.getInt("total");
		String newUrl = url + "&startAt=0&maxResults=" + total;

		JSONObject newJson = JSONUtil.readJsonFromUrl(newUrl);
		JSONArray issues = newJson.getJSONArray("issues");

		//Iterate through each issue
		for (int i=0; i < total; i++) {

			Version fv = null, ov = null;

			//ticket key
			String key = issues.getJSONObject(i%1000).get("key").toString();

			JSONObject fields = issues.getJSONObject(i%1000).getJSONObject("fields");
			JSONArray versions = fields.getJSONArray("versions");

			//fixed version = version after bug resolution
			if(fields.has("resolutiondate")) {
				String dateStr = fields.get("resolutiondate").toString();
				dateStr = dateStr.substring(0, dateStr.length()-12);
				LocalDateTime dateTime = LocalDateTime.parse(dateStr);
				//find version after this date
				for (Version v : versionList) {
					if (v.getDate().isAfter(dateTime) || v.getDate().isEqual(dateTime)) {
						System.out.println("ticket: " + key + " fv: " + v.getDate());
						fv = v;
						break;
					}
				}
			}


			//opening version = version after ticket creation
			if (fields.has("created")) {
				String dateStr = fields.get("created").toString();
				dateStr = dateStr.substring(0, dateStr.length()-12);
				LocalDateTime dateTime = LocalDateTime.parse(dateStr);
				//find version after this date
				for (Version v : versionList) {
					if (v.getDate().isAfter(dateTime) || v.getDate().isEqual(dateTime)) {
						System.out.println("ticket: " + key + " ov: " + v.getDate());
						ov = v;
						break;
					}
				}
			}

			//extract affected versions list & then create ticket list
			List<Version> avList = new ArrayList<>();
			if (fv != null && ov != null) {
				avList = getAffectedVersions(versions, ov, fv, versionList);
				Ticket ticket = new Ticket(key, ov, fv, avList);
				ticketList.add(ticket);
			} else {
				//TODO rimuovi questo else
				System.out.println("esiste un ticket senza fv o ov");
			}

		}  
		return ticketList;
	}

	private static List<Version> getAffectedVersions(JSONArray versions, Version ov, Version fv, List<Version> versionList) {
		List<Version> avList = new ArrayList<>();
		List<Version> avListComplete = new ArrayList<>();
		Map<LocalDateTime, String> preAVMap = new HashMap<>();

		//affected versions list
		for (int i=0; i < versions.length(); i++) {
			try {
				String id = versions.getJSONObject(i).get("id").toString();
				//find version by id
				for (Version v : versionList) {
					if (v.getId().equals(id)) {
						preAVMap.put(v.getDate(), id);
					}
				}
			} catch(Exception e) {
			}
		}

		//order affected version list
		Map <LocalDateTime, String> avMap = new TreeMap<>(preAVMap);
		for (Map.Entry<LocalDateTime, String> entry : avMap.entrySet()) {
			String name = avMap.get(entry.getKey());
			avList.add(new Version(entry.getValue(), name, entry.getKey()));
		}


		//pick first affected version 
		Version firstAV = null;
		LocalDateTime firstDate = null;
		if (!avList.isEmpty()) {
			firstAV = avList.get(0);
			firstDate = firstAV.getDate();
		}

		//check: first AV < OV else first AV = OV
		LocalDateTime ovDate = ov.getDate();
		if (firstDate == null || ovDate.isBefore(firstDate)) {	
			firstDate = ovDate;
			firstAV = ov;
		} 

		//set last AV = FV-1
		LocalDateTime fvDate = fv.getDate();
		LocalDateTime lastDate = null;
		Version lastAV = null;
		for (int i = 0; i < versionList.size(); i++) {
			Version v = versionList.get(i);
			if (v.getDate().equals(fvDate)) {
				try {
					lastAV = versionList.get(i-1);
					lastDate = lastAV.getDate();
				} catch (Exception e) {}
			}
		}

		//check: OV < last AV = FV-1
		if (lastDate != null && ovDate.isAfter(lastDate)) {
			//OV is forced to be FV-1
			ovDate = lastDate;
		} else {
			lastDate = ovDate;
		}

		//find first affected version index
		int versionIndex = -1;
		for (int i = 0; i < versionList.size(); i++) {
			Version v = versionList.get(i);
			if (v.getDate().equals(firstDate)) {
				versionIndex = i;
			}
		}
		//replace avList with a complete list with each version
		//between initial AV and FV (not included)
		avListComplete.add(firstAV);
		versionIndex++;
		while (versionIndex != -1 && versionIndex < versionList.size()) {
			LocalDateTime versDate = versionList.get(versionIndex).getDate();

			if (versDate.isBefore(lastDate)) {
				avListComplete.add(versionList.get(versionIndex));
				versionIndex++;
			} else if (versDate.isEqual(lastDate)){
				avListComplete.add(lastAV);
				versionIndex = -1;
			} else {
				versionIndex = -1;
			}
		}
		return avListComplete;
	}

	private static List<String> getBuggyVersions(List<Ticket> ticketList, List<Version> versionList) {

		List<Version> partialList = new ArrayList<>(); 
		List<String> listWithDup =  new ArrayList<>();
		List<String> listWithoutDup = new ArrayList<>();
		Set<String> uniqueValues = new HashSet<>();

		//took av for all the tickets
		for (Ticket t : ticketList) {
			try {
				partialList = t.getAv();
				for (Version av : partialList) {
					listWithDup.add(av.getId());
				}
			}catch (Exception e) {

			}

		}

		//remove duplicates
		for (String s : listWithDup) {
			if (uniqueValues.add(s)) {
				listWithoutDup.add(s);
			}
		}
		System.out.println(listWithoutDup);
		return listWithoutDup;
	}
}
