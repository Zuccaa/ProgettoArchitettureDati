package progetto.architettureDati;

import java.util.ArrayList;
import java.util.Arrays;

public class ComputeMetrics {
	
	String[] NULLVALUES = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};
	
	public float computeTupleCompleteness(Book book) {
		
		float tupleCompleteness = 0;
		int NumberOfAttributes = book.getClass().getDeclaredFields().length;
		int counterNull = getNumberOfNullValuesInTuple(book);
		
		tupleCompleteness = (float) counterNull / NumberOfAttributes;
		
		return tupleCompleteness;
	}
	
	public float computeAttributeCompleteness(ArrayList<String> attribute) {
		
		float attributeCompleteness = 0;
		int counterNull = 0;
		int NumberOfTuples = attribute.size();
		
		for (String element: attribute) {
			if (Arrays.asList(NULLVALUES).contains(element)) {
				counterNull++;
			}
		}
		
		System.out.println(counterNull);
		
		attributeCompleteness = (float) counterNull / NumberOfTuples;
		
		return attributeCompleteness;
	}
	
	public float computeTableCompleteness(ArrayList<Book> books) {
		
		float tableCompleteness = 0;
		int counterNull = 0;
		int NumberOfElements = books.size() * books.get(0).getClass().getDeclaredFields().length;
		
		for (Book book: books) {
			counterNull += getNumberOfNullValuesInTuple(book);
		}
		
		tableCompleteness = (float) counterNull / NumberOfElements;
		
		return tableCompleteness;
		
	}
	
	public int getNumberOfNullValuesInTuple(Book book) {
		
		int counterNull = 0;

		String[] bookAttributes = {book.getIsbn(), book.getAuthor(), book.getSource(), book.getTitle()};
		
		for (String attribute: bookAttributes) {
			if (Arrays.asList(NULLVALUES).contains(attribute)) {
				counterNull++;
			}
		}
		
		return counterNull;
	}
	
	
}
