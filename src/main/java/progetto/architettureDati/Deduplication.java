package progetto.architettureDati;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Deduplication {

	public ArrayList<Book> computeDeduplication(ArrayList<Book> books) {
		
		DatasetMethods dm = new DatasetMethods();
		
		HashMap<String, ArrayList<String>> authorsGroupedByISBN = dm.groupAttributeByIsbn(books, Attributes.AUTHOR);
		HashMap<String, ArrayList<String>> titlesGroupedByISBN = dm.groupAttributeByIsbn(books, Attributes.TITLE);
		
		String author = "";
		String title = "";
		
		ArrayList<Book> booksDeduplicated = new ArrayList<Book>();
		
		for (String isbn: authorsGroupedByISBN.keySet()) {
			author = computeDeduplicationOnAttribute(authorsGroupedByISBN.get(isbn));
			title = computeDeduplicationOnAttribute(titlesGroupedByISBN.get(isbn));
			booksDeduplicated.add(new Book(isbn, title, author));
		}
		
		return booksDeduplicated;
		
	}
	
	public String computeDeduplicationOnAttribute(ArrayList<String> attributes) {
		
		String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};

		String attributeWithMaxFrequency = "";
		String attributeConsidered = "";
		int frequency = 0;
		int maxFrequency = 0;
		
		while (!attributes.isEmpty()) {
			attributeConsidered = attributes.get(0);
			frequency = Collections.frequency(attributes, attributeConsidered);
			if (frequency > maxFrequency && !Arrays.asList(NULLVALUES).contains(attributeConsidered) && attributeConsidered.matches("[a-z.,-;()/'\s]+")) {
				maxFrequency = frequency;
				attributeWithMaxFrequency = attributeConsidered;
			}
			attributes.removeAll(Collections.singleton(attributeConsidered));
		}
		
		return attributeWithMaxFrequency;
		
	}
	
}