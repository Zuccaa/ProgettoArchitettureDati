package progetto.architettureDati;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ComputeMetrics {
	
	float tupleCompleteness;
	float attributeCompleteness;
	float tableCompleteness;
	
	public float getTupleCompleteness() {
		return tupleCompleteness;
	}

	public void setTupleCompleteness(float tupleCompleteness) {
		this.tupleCompleteness = tupleCompleteness;
	}

	public float getAttributeCompleteness() {
		return attributeCompleteness;
	}

	public void setAttributeCompleteness(float attributeCompleteness) {
		this.attributeCompleteness = attributeCompleteness;
	}

	public float getTableCompleteness() {
		return tableCompleteness;
	}

	public void setTableCompleteness(float tableCompleteness) {
		this.tableCompleteness = tableCompleteness;
	}
	
	String[] nullValues = {"", "0", "n/a", "not available (na)", "not available (na).", "not available"};
	
	public float computeTupleCompleteness(Book book) {
		
		int counterNull = 0;
		int N = 4;
		
		//String[] bookAttributes = {book.getIsbn().toLowerCase(), book.getAuthor().toLowerCase(), 
		//		book.getSource().toLowerCase(), book.getTitle().toLowerCase()};
		
		String[] bookAttributes = {book.getAuthor().toLowerCase()};
		for (String attribute: bookAttributes) {
			if (Arrays.asList(nullValues).contains(attribute)) {
				counterNull++;
			}
		}
		
		setTupleCompleteness(counterNull/N);
		
		return tupleCompleteness;
	}
	
}
