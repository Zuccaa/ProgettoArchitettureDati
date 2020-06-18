package progetto.architettureDati;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files


public class ReadDataset {

	String record = "";
	String[] infoBook = {"", "", "", ""};
	ArrayList<Book> books = new ArrayList<Book>();
	int counter = 0;
	
	public void readFile() {
		try {
			File myObj = new File(System.getProperty("user.dir") + "\\book.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				record = myReader.nextLine();
				while(record.contains("\t")) {
					infoBook[counter] = record.substring(0, record.indexOf("\t"));
					record = record.substring(record.indexOf("\t") + 1);
					counter++;
				}
				infoBook[counter] = record;
				counter = 0;				
				books.add(new Book(infoBook));
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		System.out.println(books.size());

	}
	
}
