package project.utilities;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.TreeMap;

import edu.emory.mathcs.backport.java.util.Arrays;
import project.dataQuality.Metrics;
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
	
	public Map<String, Float> computeSortedSourceAffidability(HashMap<String, ArrayList<Book>> booksGroupedByIsbn, 
			ArrayList<Float> accuracies) {
		int counter = 0;
		String sourceToConsider = "";
		HashMap<String, ArrayList<Float>> sourceAffidability = new HashMap<String, ArrayList<Float>>();
		
		for(String isbn: booksGroupedByIsbn.keySet()) {
			for (Book book: booksGroupedByIsbn.get(isbn)) {
				sourceToConsider = book.getSource();
				if (sourceAffidability.containsKey(sourceToConsider))
					sourceAffidability.get(sourceToConsider).add(accuracies.get(counter));
				else {
					ArrayList<Float> accuracy = new ArrayList<Float>();
					accuracy.add(accuracies.get(counter));
					sourceAffidability.put(sourceToConsider, accuracy);
				}
				counter++;	
			}
		}
		
		return sortMapByValue(sourceAffidability);
	
	}
	
	public Map<String, Float> sortMapByValue(HashMap<String, ArrayList<Float>> hMap) {
		
		ArrayList<Float> values;
		Map<String, Float> sourceAffidabilities = new HashMap<String, Float>();
		
		for (String s: hMap.keySet()) {
			values = hMap.get(s);
			if (values.size() >= 5) {
				sourceAffidabilities.put(s, new Metrics().computeMean(values));
			}
		}
		
		return new MapUtil().sortByValue(sourceAffidabilities);
	}
	
	public LinkedList<String> getKeysOrderByValueWithinThreshold(Map<String, Float> sortedMapByValue, float threshold) {
		LinkedList<String> keysOrderByValue = new LinkedList<String>();
		
		Iterator<Map.Entry<String, Float>> iterator = sortedMapByValue.entrySet().iterator();
		
	    while (iterator.hasNext()) {
	        Map.Entry<String, Float> entry = iterator.next();
	        
	        if (threshold >= entry.getValue())
	        	break;
	        
	        keysOrderByValue.addLast(entry.getKey());;
	    }
		
		return keysOrderByValue;
	}
	
	public String convertHTMLSymbols(String string) {
	
		string = string.replaceAll("&quot;", "\"");
		string = string.replaceAll("andquot;", "\"");
		string = string.replaceAll("&amp;", "&");
		string = string.replaceAll("andamp;", "&");
		string = string.replaceAll("&apos;", "\'");
		string = string.replaceAll("andapos;", "\'");
		string = string.replaceAll("&#146;", "’");
		string = string.replaceAll("and#146;", "’");
		string = string.replaceAll("&#128;", "€");
		string = string.replaceAll("and#128;", "€");
		string = string.replaceAll("&#153;", "™");
		string = string.replaceAll("and#153;", "™");
		string = string.replaceAll("&acirc;", "â");
		string = string.replaceAll("andacirc;", "â");
		string = string.replaceAll("&cent;", "¢");
		string = string.replaceAll("andcent;", "¢");
		string = string.replaceAll("&#132;", "„");
		string = string.replaceAll("and#132;", "„");
		string = string.replaceAll("&oacute;", "ó");
		string = string.replaceAll("andoacute;", "ó");
		string = string.replaceAll("&reg", "®");
		string = string.replaceAll("andreg;", "®");
		string = string.replaceAll("&eacute;", "é");
		string = string.replaceAll("andeacute;", "é");
		string = string.replaceAll("&Acirc;", "Â");
		string = string.replaceAll("andAcirc;", "Â");
		string = string.replaceAll("&Atilde;", "Ã");
		string = string.replaceAll("andAtilde;", "Ã");
		string = string.replaceAll("&copy;", "©");
		string = string.replaceAll("and&copy;", "©");
		string = string.replaceAll("&ouml;", "ö");
		string = string.replaceAll("andouml;", "ö");
		string = string.replaceAll("&szlig;", "ß");
		string = string.replaceAll("andszlig;", "ß");		

		return string;
		
	}
	
	public void printFrequenciesOccurrences(ArrayList<Float> accuracies, String attribute) {
		
		String floatRange = "";
		HashMap<String, Integer> sortedOccurrences = new HashMap<String, Integer>();
		
		for(float accuracy: accuracies) {
			if (accuracy <= 0.1)
				floatRange = "<= 0.1";
			else
				if (accuracy <= 0.2)
					floatRange = "0.1 - 0.2";
				else
					if (accuracy <= 0.3)
						floatRange = "0.2 - 0.3";
					else
						if (accuracy <= 0.4)
							floatRange = "0.3 - 0.4";
						else
							if (accuracy <= 0.5)
								floatRange = "0.4 - 0.5";
							else
								if (accuracy <= 0.6)
									floatRange = "0.5 - 0.6";
								else
									if (accuracy <= 0.7)
										floatRange = "0.6 - 0.7";
									else
										if (accuracy <= 0.8)
											floatRange = "0.7 - 0.8";
										else
											if (accuracy <= 0.9)
												floatRange = "0.8 - 0.9";
											else
												floatRange = "> 0.9";
			if (sortedOccurrences.containsKey(floatRange))
				sortedOccurrences.put(floatRange, sortedOccurrences.get(floatRange) + 1);
			else
				sortedOccurrences.put(floatRange, 1);
		}
		
		System.out.println("Attribute " + attribute);
		for (String s: sortedOccurrences.keySet()) {
			System.out.println(s + "|||" + sortedOccurrences.get(s));
		}
		
	}
	
}
