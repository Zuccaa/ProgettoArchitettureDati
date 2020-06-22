package progetto.architettureDati;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.TreeMap;

import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.Map.Entry;


public class DatasetMethods {
	
	public ArrayList<Book> readDataset(String filePath) {
		
		String tuple = "";
		String[] infoBook = {"", "", "", ""};
		ArrayList<Book> books = new ArrayList<Book>();
		
		int counter = 0;
		
		try {
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				tuple = myReader.nextLine();
				while(tuple.contains("\t")) {
					infoBook[counter] = tuple.substring(0, tuple.indexOf("\t")).toLowerCase();
					tuple = tuple.substring(tuple.indexOf("\t") + 1);
					counter++;
				}
				infoBook[counter] = tuple.toLowerCase();
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
	
	public HashMap<String, ArrayList<String>> readAuthorsList(String filePath) {
		
		HashMap<String, ArrayList<String>> authorsListWithISBN = new HashMap<String, ArrayList<String>>();
		
		String tuple = "";

		String[] info;
		
		try {
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				tuple = myReader.nextLine();
				info = tuple.split("\t");
				authorsListWithISBN.put(info[0], new ArrayList<String>(Arrays.asList(info[1].split(";"))));
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
				
		return authorsListWithISBN;
		
	}
	
	public void writeOccurrences(TreeMap<String, Integer> sortedOccurrences) {
		
		try {
			File myObj = new File("occurrences.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter("occurrences.txt");
			for (Entry<String, Integer> entry : sortedOccurrences.entrySet()) {
			     myWriter.write("Key: " + entry.getKey() + " Value: " + entry.getValue() + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
	public void writeFile(ArrayList<Book> books) {
		
		try {
			File myObj = new File("finalDataset.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter("finalDataset.txt");
			for (Book b: books) {
	            myWriter.write(b.getIsbn() + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String, ArrayList<String>> groupAttributeByIsbn(ArrayList<Book> books, Attributes at) {
		
		HashMap<String, ArrayList<String>> booksGroupedByIsbn = new HashMap<String, ArrayList<String>>();
		
		String getAttribute = "";
		
		for (Book b: books) {
			switch(at) {
				case AUTHOR:
					getAttribute = b.getAuthor();
					break;
				case TITLE:
					getAttribute = b.getTitle();
					break;
			}	
			if (!booksGroupedByIsbn.containsKey(b.getIsbn())) {
			    ArrayList<String> attributes = new ArrayList<String>();
			    attributes.add(getAttribute);
	
			    booksGroupedByIsbn.put(b.getIsbn(), attributes);
			} else {
				booksGroupedByIsbn.get(b.getIsbn()).add(getAttribute);
			}
		}
		
		return booksGroupedByIsbn;
		
	}
}
