package project.dataQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.commons.text.similarity.LevenshteinDistance;

import project.pojo.Book;

public class Metrics {
	
	String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};
	
	// Metodo per calcolare la completezza di tupla di un libro
	public float computeTupleCompleteness(Book book, boolean deduplicated) {
		
		int numberOfAttributes = 1;
		
		if (deduplicated)
			numberOfAttributes = book.getClass().getDeclaredFields().length - 1;
		else
			numberOfAttributes = book.getClass().getDeclaredFields().length;
		
		int counterNull = getNumberOfNullValuesInTuple(book);
		
		return (float) counterNull / numberOfAttributes;
	}
	
	// Metodo per calcolare la completezza di un attributo
	public float computeAttributeCompleteness(ArrayList<String> attribute) {
		
		int counterNull = 0;
		int NumberOfTuples = attribute.size();
		
		for (String element: attribute)
			// Per il rilevamento di un null, vengono considerate le rappresentazioni presenti in NULLVALUES
			if (Arrays.asList(NULLVALUES).contains(element))
				counterNull++;
		//System.out.println(counterNull);
		
		return (float) counterNull / NumberOfTuples;
	}
	
	
	// Metodo per calcolare la completezza di tabella
	public float computeTableCompleteness(ArrayList<Book> books) {
		
		int counterNull = 0;
		int NumberOfElements = books.size() * books.get(0).getClass().getDeclaredFields().length;
		
		for (Book book: books)
			counterNull += getNumberOfNullValuesInTuple(book);
		
		return (float) counterNull / NumberOfElements;
		
	}
	
	// Metodo per calcolare il numero di valori nulli presenti in un libro
	public int getNumberOfNullValuesInTuple(Book book) {
		
		int counterNull = 0;

		String[] bookAttributes = {book.getIsbn(), book.getAuthor(), book.getSource(), book.getTitle()};
		
		for (String attribute: bookAttributes)
			// Per il rilevamento di un null, vengono considerate le rappresentazioni presenti in NULLVALUES
			if (Arrays.asList(NULLVALUES).contains(attribute))
				counterNull++;
		
		return counterNull;
	}
	
	// Metodo per calcolare l'accuratezza semantica di tutti gli author list presenti nel dataset
	public ArrayList<Float> computeSemanticAccuracies(HashMap<String, ArrayList<Book>> booksGroupedByIsbn, 
			HashMap<String, ArrayList<String>> exactAuthorsList) {
		
		ArrayList<Float> semanticAccuracy = new ArrayList<Float>();
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> surnames = new ArrayList<String>();
		
		for(String isbn: booksGroupedByIsbn.keySet()) {
			// Vengono salvati i cognomi degli autori della tabella di riferimento
			surnames = getSurnamesFromAuthors(exactAuthorsList.get(isbn));
				
			for (Book b: booksGroupedByIsbn.get(isbn)) {
				// Vengono estratti i tokens della stringa di autori in analisi
				tokens = getTokensFromAuthors(b.getAuthor());
				// Viene calcolata l'accuratezza semantica utilizzando la funzione di distanza
				semanticAccuracy.add(computeSemanticAccuracy(tokens, surnames));
			}
		}
		
		return semanticAccuracy;
	}
	
	// Metodo per estrarre i tokens di author
	public ArrayList<String> getTokensFromAuthors(String authors) {
		
		// La separazione viene effettuata quando si incontra un carattere non alfanumerico
		return new ArrayList<String>(Arrays.asList(authors.split("\\W+")));
		
	}
	
	// Metodo per estrarre i cognomi degli autori della tabella di riferimento
	public ArrayList<String> getSurnamesFromAuthors(ArrayList<String> authors) {
		
		ArrayList<String> surnames = new ArrayList<String>();
		String[] tmp;
		for (String author: authors) {
			// Le parole vengono separate solamente dagli spazi
			tmp = author.split(" ");
			// Il cognome è sempre in fondo, quindi si prende l'ultimo elemento dell'array
			surnames.add(tmp[tmp.length - 1]);
		}
		
		return surnames;
	}
	
	// Metodo per calcolare l'accuratezza semantica di una tupla utilizzando la funzione di distanza
	public float computeSemanticAccuracy(ArrayList<String> tokens, ArrayList<String> surnames) {
		
		ArrayList<Integer> editDistance = new ArrayList<Integer>();
		int minEditDistance = 0;
		int THRESHOLD = 1;
		int sumOfSemanticAccuracy = 0;
		
		for (String s: surnames) {
			for (String t: tokens)
				// Viene utilizzata la distanza di Levensthein (distanza di edit)
				editDistance.add(LevenshteinDistance.getDefaultInstance().apply(t,s));
			minEditDistance = Collections.min(editDistance);
			// Se la distanza minima è minore della soglia, allora l'autore viene riconosciuto
			if (minEditDistance <= THRESHOLD)
				sumOfSemanticAccuracy += 1;
			editDistance.clear();
		}
		
		return (float) sumOfSemanticAccuracy / surnames.size();
		
	}
	
	// Metodo per calcolare la media di una lista di numeri
	public float computeMean(ArrayList<Float> numbers) {

		float sum = 0;
		
		for (Float n: numbers)
			sum += n;
		
		return sum / numbers.size();
		
	}
	
	// Metodo per calcolare l'accuratezza sintattica di un titolo
	public float computeSyntacticAccuracy(String title, String referenceTitle) {
		
		// Vengono identificati i bigrammi del titolo in analisi e di quello di riferimento
		ArrayList<String> bigramsTitle = computeBigrams(title);
		ArrayList<String> bigramsReferenceTitle = computeBigrams(referenceTitle);
		
		// Viene calcolata la distanza in bigrammi
		return 1 - (computeBigramsDistance(bigramsTitle, bigramsReferenceTitle));
	}
	
	// Metodo per calcolare tutti i bigrammi di una stringa
	public ArrayList<String> computeBigrams(String word) {
		
		ArrayList<String> bigrams = new ArrayList<String>();
		
		while (word.length() > 1) {
			char[] characters = {word.charAt(0), word.charAt(1)};
			bigrams.add(String.valueOf(characters));
			word = word.substring(1);
		}
		
		return bigrams;
		
	}
	
	// Metodo per calcolare la distanza in bigrammi utilizzando la formula
	public float computeBigramsDistance(ArrayList<String> bigrams1, ArrayList<String> bigrams2) {
		
		TreeSet<String> intersection = new TreeSet<String>();
		TreeSet<String> union = new TreeSet<String>();
		
		union.addAll(bigrams1);
		union.addAll(bigrams2);
		
		intersection.addAll(bigrams1);
		intersection.retainAll(bigrams2);
		
		return 1 - ((float) intersection.size() / union.size());
	}
	
}