package project.dataQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import project.pojo.Book;
import project.utilities.Attributes;
import project.utilities.DatasetMethods;

public class Deduplication {
	
	String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};

	public ArrayList<Book> computeDeduplication(HashMap<String, ArrayList<Book>> booksGroupedByIsbn,
			LinkedList<String> sourcesOrderedByAffidabilityForAuthorList, 
			LinkedList<String> sourcesOrderedByAffidabilityForTitle) {
		
		DatasetMethods dm = new DatasetMethods();
		
		String author = "";
		String title = "";
		
		ArrayList<Book> booksDeduplicated = new ArrayList<Book>();
		ArrayList<String> authors = new ArrayList<String>();
		
		for (String isbn: booksGroupedByIsbn.keySet()) {
			for (Book b: booksGroupedByIsbn.get(isbn)) {
				authors.add(b.getAuthor());
			}
				author = computeTrustYourFriends(booksGroupedByIsbn.get(isbn), sourcesOrderedByAffidabilityForAuthorList,
						Attributes.AUTHOR);
				title = computeTrustYourFriends(booksGroupedByIsbn.get(isbn), sourcesOrderedByAffidabilityForTitle,
						Attributes.TITLE);
				booksDeduplicated.add(new Book(isbn, title, author));
		}
		
		return booksDeduplicated;
		
	}
	
	public String computeCryWithTheWolves(ArrayList<String> attribute) {

		String attributeWithMaxFrequency = "";
		String attributeConsidered = "";
		int frequency = 0;
		int maxFrequency = 0;
		
		while (!attribute.isEmpty()) {
			attributeConsidered = attribute.get(0);
			frequency = Collections.frequency(attribute, attributeConsidered);
			if (frequency > maxFrequency && !Arrays.asList(NULLVALUES).contains(attributeConsidered) 
					&& attributeConsidered.matches("[a-z.,-;()/'\\s]+")) {
				maxFrequency = frequency;
				attributeWithMaxFrequency = attributeConsidered;
			}
			attribute.removeAll(Collections.singleton(attributeConsidered));
		}
		
		return attributeWithMaxFrequency;
		
	}
	
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

		return computeCryWithTheWolves(attributes);
	}
}