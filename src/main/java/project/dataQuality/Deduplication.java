package project.dataQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import project.pojo.Book;
import project.utilities.Attributes;

public class Deduplication {
	
	String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};

	// Metodo per effettuare la deduplicazione del dataset book
	public ArrayList<Book> computeDeduplication(HashMap<String, ArrayList<Book>> booksGroupedByIsbn,
			LinkedList<String> sourcesOrderedByAffidabilityForAuthorList, 
			LinkedList<String> sourcesOrderedByAffidabilityForTitle) {
				
		String author = "";
		String title = "";
		
		ArrayList<Book> booksDeduplicated = new ArrayList<Book>();
		
		for (String isbn: booksGroupedByIsbn.keySet()) {
			// Gli attributi author e title vengono determinati applicando la strategia di Trust Your Friends
			author = computeTrustYourFriends(booksGroupedByIsbn.get(isbn), sourcesOrderedByAffidabilityForAuthorList,
					Attributes.AUTHOR);
			title = computeTrustYourFriends(booksGroupedByIsbn.get(isbn), sourcesOrderedByAffidabilityForTitle,
					Attributes.TITLE);
			
			booksDeduplicated.add(new Book(isbn, title, author));
		}
		
		return booksDeduplicated;
		
	}
	
	// Metodo per applicare la strategia di fusione Trust Your Friends
	public String computeTrustYourFriends(ArrayList<Book> books, LinkedList<String> sources, Attributes at) {

		String sourceBook = "";
		String attributeBook = "";
		ArrayList<String> attributes = new ArrayList<String>();
		
		for (Book b: books) {
			switch(at) {
				case AUTHOR:
					attributeBook = b.getAuthor();
					break;
				case TITLE:
					attributeBook = b.getTitle();
			}
			sourceBook = b.getSource();
			for (String s: sources)
				if (sourceBook.equals(s))
					return attributeBook;
			attributes.add(attributeBook);
		}

		// Se non si raggiunge il return precedente, allora si applica la strategia Cry With The Wolves
		return computeCryWithTheWolves(attributes);
	}
	
	// Metodo per applicare la strategia Cry With The Wolves
	public String computeCryWithTheWolves(ArrayList<String> attribute) {

		String attributeWithMaxFrequency = "";
		String attributeConsidered = "";
		int frequency = 0;
		int maxFrequency = 0;
		
		while (!attribute.isEmpty()) {
			attributeConsidered = attribute.get(0);
			frequency = Collections.frequency(attribute, attributeConsidered);
			if (frequency > maxFrequency && !Arrays.asList(NULLVALUES).contains(attributeConsidered)) {
				maxFrequency = frequency;
				attributeWithMaxFrequency = attributeConsidered;
			}
			attribute.removeAll(Collections.singleton(attributeConsidered));
		}
		
		return attributeWithMaxFrequency;
		
	}
}