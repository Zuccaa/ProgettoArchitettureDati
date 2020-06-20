package progetto.architettureDati;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.TreeMap;
import java.util.Map.Entry;


public class DatasetMethods {

	String record = "";
	String[] infoBook = {"", "", "", ""};
	ArrayList<Book> books = new ArrayList<Book>();
	int counter = 0;
	
	public ArrayList<Book> readFile() {
		try {
			File myObj = new File(System.getProperty("user.dir") + "\\book.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				record = myReader.nextLine();
				while(record.contains("\t")) {
					infoBook[counter] = record.substring(0, record.indexOf("\t")).toLowerCase();
					record = record.substring(record.indexOf("\t") + 1);
					counter++;
				}
				infoBook[counter] = record.toLowerCase();
				counter = 0;				
				books.add(new Book(infoBook));
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		return books;
		
	}
	
	
	public void writeOccurrences(TreeMap<String, Integer> sortedOccurrences) {
		try {
			File myObj = new File("filename.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter("filename.txt");
			for (Entry<String, Integer> entry : sortedOccurrences.entrySet()) {
			     myWriter.write("Key: " + entry.getKey() + " Value: " + entry.getValue() + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
