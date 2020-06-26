package project.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CreateStandard {

	public static void main(String[] args) throws IOException {
		
		String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		
        DatasetMethods dm = new DatasetMethods();
        
		HashMap<String, ArrayList<String>> exactAuthorsList = dm.readAuthorsList(AUTHORSLISTPATH);
		HashMap<String, String> exactTitlesList = new HashMap<String, String>();
		
		for(String isbn: exactAuthorsList.keySet()) {
			exactTitlesList.put(isbn, getInfoBooks(url + isbn, isbn));
		}
		
		writeStandardFile(exactTitlesList);
		
	}
	
	public static String getInfoBooks(String getUrl, String isbn) throws IOException {
		
		String infoBook = "";
		
		URL obj = new URL(getUrl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			infoBook = response.toString();
		} else {
			System.out.println("GET request not worked");
		}
		
		String title = getTitle(infoBook, isbn);
		
		return title;
		
	}
	
	public static String getTitle(String infoBook, String isbn) {
		
		Gson gson = new Gson();

		JsonObject json = gson.fromJson(infoBook, JsonObject.class);
		
		String title = "";
		JsonObject jsonVolumeInfo;
		try {		
		if (json.has("items")) {
			jsonVolumeInfo = json.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
	    
		    title = gson.toJson(jsonVolumeInfo.get("title"));
		    if (jsonVolumeInfo.has("subtitle"))
		    	title += ": " + gson.toJson(jsonVolumeInfo.get("subtitle"));	    		
		}
		}catch (Exception e) {
			title = "ERROR";
		}
		
		return title.replaceAll("\"", "");
		
	}
	
	public static void writeStandardFile(HashMap<String, String> titles) {
		
		try {
			File myObj = new File("titles.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter("titles.txt");
			for (String isbn: titles.keySet()) {
	            myWriter.write(isbn + "\t" + titles.get(isbn) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
}