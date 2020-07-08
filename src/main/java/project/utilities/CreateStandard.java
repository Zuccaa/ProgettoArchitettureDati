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

/* Classe utilizzata per creare la tabella di riferimento relativa ai titoli dei libri.
 * Il txt generato è stata successivamente modificata a mano su alcuni piccoli dettagli.
 * Pertanto, il rerun di questa parte di codice produrrà una tabella leggermente
 * differente da quella presente in titles.txt
 */
public class CreateStandard {

	public static void main(String[] args) throws IOException {
		
		String url1 = "https://openlibrary.org/api/books?bibkeys=ISBN:";
		String url2 = "&jscmd=data&format=json";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		
        DatasetMethods dm = new DatasetMethods();
        
        // Viene importata la lista di riferimento degli autori
		HashMap<String, ArrayList<String>> exactAuthorsList = 
				dm.convertValuesIntoArrayListValues(dm.readList(AUTHORSLISTPATH));
		HashMap<String, String> exactTitlesList = new HashMap<String, String>();
		
		for(String isbn: exactAuthorsList.keySet()) {
			// Viene inserita nella lista l'ISBN con il titolo corretto associato
			exactTitlesList.put(isbn, getInfoBooks(url1 + isbn + url2, isbn));
		}
		
		// Viene scritta su file la tabella di riferimento per title
		writeStandardFile(exactTitlesList);
		
	}
	
	// Metodo per ricavare il titolo di un libro dall'API di OpenLibrary
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
		
		// Viene ricavato il titolo dal JSON restituito dall'interrogazione
		String title = getTitle(infoBook, isbn);
		
		return title;
		
	}
	
	
	// Metodo per ricavare il titolo dalla risposta alla query effettuata con l'API
	public static String getTitle(String infoBook, String isbn) {
		
		Gson gson = new Gson();
		
		JsonObject json = gson.fromJson(infoBook, JsonObject.class).getAsJsonObject("ISBN:" + isbn);
		
		String title = "";
		
		try {
			if (json.has("title")) {
				title = gson.toJson(json.get("title"));
				
			    if (json.has("subtitle"))
			    	title += ": " + gson.toJson(json.get("subtitle"));	    		
			}
		}catch (Exception e) {
			title = "ERROR";
		}
		
		return title.replaceAll("\"", "");
		
	}
	
	// Metodo per scrivere su file la tabella di riferimento di title
	public static void writeStandardFile(HashMap<String, String> titles) {
		
		try {
			FileWriter myWriter = new FileWriter("newTitles.txt");
			for (String isbn: titles.keySet())
	            myWriter.write(isbn + "\t" + titles.get(isbn) + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
}