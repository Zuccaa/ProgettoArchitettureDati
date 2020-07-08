package project.utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.emory.mathcs.backport.java.util.Arrays;
import project.dataQuality.Metrics;
import project.pojo.Book;

import java.util.Map.Entry;


public class DatasetMethods {
	
	// Metodo per leggere i libri del dataset dal file txt (tab separated)
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
					// Le varie informazioni vengono separate andando a individuare
					// le occorrenze del tab
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
	
	// Metodo per leggere gli attributi di una tabella di riferimento
	public HashMap<String, String> readList(String filePath) {
		
		HashMap<String, String> mapWithISBN = new HashMap<String, String>();
		
		String tuple = "";

		String[] info;
		
		try {
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				tuple = myReader.nextLine();
				// L'ISBN e l'attributo si separano individuando il tab
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
	
	/* Metodo per separare i valori della mappa ricercando le occorrenze di ;
	 * In particolare, il metodo viene utilizzato per separare gli autori della tabella di riferimento
	 */
	public HashMap<String, ArrayList<String>> convertValuesIntoArrayListValues(HashMap<String, String> map) {
		
		HashMap<String, ArrayList<String>> newMap = new HashMap<String, ArrayList<String>>();
		String value = "";
		
		for (String isbn: map.keySet()) {
			value = map.get(isbn);
			newMap.put(isbn, new ArrayList<String>(Arrays.asList(value.split(";"))));
		}
		
		return newMap;
		
	}
	
	/* Metodo per scrivere le occorrenze di un determinato attributo con la relativa frequenza
	 * su file. E' stato usato solamente durante la fase di analisi del dataset
	 */
	public void writeOccurrences(TreeMap<String, Integer> sortedOccurrences) {
		
		try {
			FileWriter myWriter = new FileWriter("occurrences.txt");
			for (Entry<String, Integer> entry : sortedOccurrences.entrySet())
			     myWriter.write("Key: " + entry.getKey() + " Value: " + entry.getValue() + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
	// Metodo per stampare su file il dataset di books
	public void writeFile(ArrayList<Book> books, String filename) {
		
		try {
			FileWriter myWriter = new FileWriter(filename);
			for (Book b: books)
	            myWriter.write(b.toString() + "\n");
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
	
	// Metodo per raggruppare i libri in base all'ISBN
	public HashMap<String, ArrayList<Book>> groupBookByIsbn(ArrayList<Book> books) {
		
		HashMap<String, ArrayList<Book>> booksGroupedByIsbn = new HashMap<String, ArrayList<Book>>();
				
		for (Book b: books) {
			/* Se la mappa non contiene l'ISBN, allora viene inserita una nuova coppia
			 * con chiave ISBN e valore libro b; altrimenti, viene aggiunto il libro b
			 * alla lista della chiave ISBN già presente
			*/
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
	
	// Metodo per contare le frequenze di un attributo
	public HashMap<Float, Integer> countFrequencies(ArrayList<Float> attribute){
		
		HashMap<Float, Integer> sortedOccurrences = new HashMap<Float, Integer>();
		
		while (!attribute.isEmpty()) {
			int occurrences = Collections.frequency(attribute, attribute.get(0));
			sortedOccurrences.put(attribute.get(0), occurrences);
			attribute.removeAll(Collections.singleton(attribute.get(0)));
		}
		return sortedOccurrences;
		
	}
	
	
	// Metodo per calcolare l'indice di affidabilità delle fonti dalle accuratezze di un attributo
	public Map<String, Float> computeSortedSourceAffidability(HashMap<String, ArrayList<Book>> booksGroupedByIsbn, 
			ArrayList<Float> accuracies) {
		int counter = 0;
		String sourceToConsider = "";
		HashMap<String, ArrayList<Float>> sourceAffidability = new HashMap<String, ArrayList<Float>>();
		
		for(String isbn: booksGroupedByIsbn.keySet()) {
			for (Book book: booksGroupedByIsbn.get(isbn)) {
				sourceToConsider = book.getSource();
				/* Se la mappa non contiene la source, allora viene inserita una nuova coppia
				 * con chiave source e valore accuratezza; altrimenti, viene aggiunto il valore
				 * accuratezza alla lista della chiave source già presente
				*/
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
		
		// Viene ordinata la mappa in base ai valori
		return sortMapByValue(sourceAffidability);
	
	}
	
	// Metodo per ordinare una mappa (la lista delle sources) in base ai valori
	public Map<String, Float> sortMapByValue(HashMap<String, ArrayList<Float>> hMap) {
		
		ArrayList<Float> values;
		Map<String, Float> sourceAffidabilities = new HashMap<String, Float>();
		
		for (String s: hMap.keySet()) {
			values = hMap.get(s);
			// In questo caso, vengono considerate solamente le sources con almeno 5
			// valori di accuratezza (e quindi libri)
			if (values.size() >= 5) {
				sourceAffidabilities.put(s, new Metrics().computeMean(values));
			}
		}
		
		// Viene invocato un metodo d'appoggio per l'ordinamento della mappa
		return new MapUtil().sortByValue(sourceAffidabilities);
	}
	
	/* Metodo per estrapolare le chiavi di una mappa ordinata per valori, tali che i valori rispettivi siano
	*  maggiori di una certa soglia. In questo caso, si va ad estrapolare le sources in base agli indici
	*  d'affidabilità
	*/
	public LinkedList<String> getKeysOrderedByValueWithinThreshold(Map<String, Float> sortedMapByValue, float threshold) {
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
	
	// Metodo per convertire i caratteri HTML - ISO non correttamente codificati nel dataset
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
	
	// Metodo per printare i risultati delle accuratezze a livello di intervalli e frequenze
	public void printFrequenciesOccurrences(ArrayList<Float> accuracies, String attribute) {
		
		String floatRange = "";
		HashMap<String, Integer> sortedOccurrences = new HashMap<String, Integer>();
		
		for (float accuracy: accuracies) {
			for (int counter = 1; counter <= 10; counter++)
				if (accuracy <= (float) counter / 10) {
					floatRange = "[" + ((float) (counter - 1) / 10) + "; " + (float) counter / 10 + "]";
					break;
				}
		
			if (sortedOccurrences.containsKey(floatRange))
				sortedOccurrences.put(floatRange, sortedOccurrences.get(floatRange) + 1);
			else
				sortedOccurrences.put(floatRange, 1);
		}
		
		System.out.println("Accuratezza per " + attribute + ": ");
		for (String s: sortedOccurrences.keySet()) {
			System.out.println(s + " --> " + sortedOccurrences.get(s));
		}
		
	}
	
}
