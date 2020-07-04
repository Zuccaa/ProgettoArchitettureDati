package project.dataQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import project.pojo.Book;
import project.utilities.Attributes;
import project.utilities.DatasetMethods;

public class Deduplication {
	
	String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};

	public ArrayList<Book> computeDeduplication(HashMap<String, ArrayList<Book>> booksGroupedByIsbn) {
		
		DatasetMethods dm = new DatasetMethods();
		
		String author = "";
		String title = "";
		
		ArrayList<Book> booksDeduplicated = new ArrayList<Book>();
		ArrayList<String> authors = new ArrayList<String>();
		
		for (String isbn: booksGroupedByIsbn.keySet()) {
			for (Book b: booksGroupedByIsbn.get(isbn)) {
				authors.add(b.getAuthor());
			}
				author = computeDeduplicationOnAuthorList(authors);
				title = computeDeduplicationOnTitle(booksGroupedByIsbn.get(isbn));
				booksDeduplicated.add(new Book(isbn, title, author));
		}
		
		return booksDeduplicated;
		
	}
	
	public String computeDeduplicationOnAuthorList(ArrayList<String> authors) {

		String attributeWithMaxFrequency = "";
		String attributeConsidered = "";
		int frequency = 0;
		int maxFrequency = 0;
		
		while (!authors.isEmpty()) {
			attributeConsidered = authors.get(0);
			frequency = Collections.frequency(authors, attributeConsidered);
			if (frequency > maxFrequency && !Arrays.asList(NULLVALUES).contains(attributeConsidered) 
					&& attributeConsidered.matches("[a-z.,-;()/'\\s]+")) {
				maxFrequency = frequency;
				attributeWithMaxFrequency = attributeConsidered;
			}
			authors.removeAll(Collections.singleton(attributeConsidered));
		}
		
		return attributeWithMaxFrequency;
		
	}
	
	public String computeDeduplicationOnTitle(ArrayList<Book> books) {
		
		return "";
	}
}