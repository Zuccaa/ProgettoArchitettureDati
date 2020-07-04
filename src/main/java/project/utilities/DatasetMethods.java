package project.utilities;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.TreeMap;

import edu.emory.mathcs.backport.java.util.Arrays;
import project.pojo.Book;

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
					infoBook[counter] = tuple.substring(0, tuple.indexOf("\t"));
					tuple = tuple.substring(tuple.indexOf("\t") + 1);
					counter++;
				}
				infoBook[counter] = tuple;
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
	
	public HashMap<String, String> readList(String filePath) {
		
		HashMap<String, String> mapWithISBN = new HashMap<String, String>();
		
		String tuple = "";

		String[] info;
		
		try {
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				tuple = myReader.nextLine();
				info = tuple.split("\t");
				mapWithISBN.put(info[0], info[1].toLowerCase());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
				
		return mapWithISBN;
		
	}
	
	public HashMap<String, ArrayList<String>> convertValuesIntoArrayListValues(HashMap<String, String> map) {
		
		HashMap<String, ArrayList<String>> newMap = new HashMap<String, ArrayList<String>>();
		String value = "";
		
		for (String isbn: map.keySet()) {
			value = map.get(isbn);
			newMap.put(isbn, new ArrayList<String>(Arrays.asList(value.split(";"))));
		}
		
		return newMap;
		
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
	
	public void writeFile(ArrayList<Book> books, String filename) {
		
		try {
			File myObj = new File(filename);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter(filename);
			for (Book b: books) {
	            myWriter.write(b.toString() + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
	public HashMap<String, ArrayList<Book>> groupBookByIsbn(ArrayList<Book> books) {
		
		HashMap<String, ArrayList<Book>> booksGroupedByIsbn = new HashMap<String, ArrayList<Book>>();
		
		String getAttribute = "";
		
		for (Book b: books) {	
			if (!booksGroupedByIsbn.containsKey(b.getIsbn())) {
			    ArrayList<Book> booksGrouped = new ArrayList<Book>();
			    booksGrouped.add(b);
	
			    booksGroupedByIsbn.put(b.getIsbn(), booksGrouped);
			} else {
				booksGroupedByIsbn.get(b.getIsbn()).add(b);
			}
		}
		
		return booksGroupedByIsbn;
		
	}
	
	public HashMap<Float, Integer> countFrequencies(ArrayList<Float> attribute){
		
		HashMap<Float, Integer> sortedOccurrences = new HashMap<Float, Integer>();
		
		while (!attribute.isEmpty()) {
			int occurrences = Collections.frequency(attribute, attribute.get(0));
			sortedOccurrences.put(attribute.get(0), occurrences);
			attribute.removeAll(Collections.singleton(attribute.get(0)));
		}
		return sortedOccurrences;
		
	}
	
	public String convertHTMLSymbols(String string) {
	
		string = string.replaceAll("&quot;", "\"");
		string = string.replaceAll("andquot;", "\"");
		string = string.replaceAll("&amp;", "&");
		string = string.replaceAll("andamp;", "&");
		string = string.replaceAll("&apos;", "\'");
		string = string.replaceAll("andapos;", "\'");
		string = string.replaceAll("&quot;", "\"");
		string = string.replaceAll("&quot;", "\"");

		return string;
		
	}
	
}
