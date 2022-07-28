package eu.uniroma2.cialfi.control;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import eu.uniroma2.cialfi.util.JSONUtil;


public class DataExtractor {

	public static HashMap<LocalDateTime, String> releaseNames;
	public static HashMap<LocalDateTime, String> releaseID;
	public static ArrayList<LocalDateTime> releases;
	public static Integer numVersions;

	public static void extractCsv(String projName) throws IOException, JSONException{

		//Fills the arraylist with releases dates and orders them
		//Ignores releases with missing dates
		releases = new ArrayList<LocalDateTime>();
		Integer i;
		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;

		JSONObject json = JSONUtil.readJsonFromUrl(url);
		JSONArray versions = json.getJSONArray("versions");
		releaseNames = new HashMap<LocalDateTime, String>();
		releaseID = new HashMap<LocalDateTime, String> ();
		for (i = 0; i < versions.length(); i++ ) {
			String name = "";
			String id = "";
			if(versions.getJSONObject(i).has("releaseDate")) {
				if (versions.getJSONObject(i).has("name"))
					name = versions.getJSONObject(i).get("name").toString();
				if (versions.getJSONObject(i).has("id"))
					id = versions.getJSONObject(i).get("id").toString();
				addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
						name,id);
			}
		}
		// order releases by date
		Collections.sort(releases, new Comparator<LocalDateTime>(){
			//@Override
			public int compare(LocalDateTime o1, LocalDateTime o2) {
				return o1.compareTo(o2);
			}
		});
		if (releases.size() < 6)
			return;
		FileWriter fileWriter = null;
		try {
			fileWriter = null;
			String outname = projName + "VersionInfo.csv";
			//Name of CSV for output
			fileWriter = new FileWriter(outname);
			fileWriter.append("Index,Version ID,Version Name,Date");
			fileWriter.append("\n");
			numVersions = releases.size();
			for ( i = 0; i < releases.size(); i++) {
				Integer index = i + 1;
				fileWriter.append(index.toString());
				fileWriter.append(",");
				fileWriter.append(releaseID.get(releases.get(i)));
				fileWriter.append(",");
				fileWriter.append(releaseNames.get(releases.get(i)));
				fileWriter.append(",");
				fileWriter.append(releases.get(i).toString());
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


	public static void addRelease(String strDate, String name, String id) {
		LocalDate date = LocalDate.parse(strDate);
		LocalDateTime dateTime = date.atStartOfDay();
		if (!releases.contains(dateTime))
			releases.add(dateTime);
		releaseNames.put(dateTime, name);
		releaseID.put(dateTime, id);
		return;
	}








}