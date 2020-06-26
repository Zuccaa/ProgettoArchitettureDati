package project.dataQuality;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.text.similarity.LevenshteinDistance;

import project.pojo.Book;

public class Metrics {
	
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
	
	public ArrayList<Float> computeSemanticAccuracy(HashMap<String, ArrayList<String>> authorsGroupedByIsbn, 
			HashMap<String, ArrayList<String>> exactAuthorsList) {
		
		ArrayList<Float> semanticAccuracy = new ArrayList<Float>();
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> surnames = new ArrayList<String>();
		
		for(String isbn: authorsGroupedByIsbn.keySet()) {
			if (exactAuthorsList.get(isbn) != null) {
				surnames = getSurnamesFromAuthors(exactAuthorsList.get(isbn));
				
				while(!authorsGroupedByIsbn.get(isbn).isEmpty()) {
					tokens = getTokensFromAuthors(authorsGroupedByIsbn.get(isbn).get(0));
					authorsGroupedByIsbn.get(isbn).remove(0);
					 
					semanticAccuracy.add(computeNormalizedEditDistance(tokens, surnames));
				}
			}
		}
		
		return semanticAccuracy;
	}
	
	public ArrayList<String> getTokensFromAuthors(String authors) {
		
		return new ArrayList<String>(Arrays.asList(authors.split("\\W+")));
		
	}
	
	public ArrayList<String> getSurnamesFromAuthors(ArrayList<String> authors) {
		
		ArrayList<String> surnames = new ArrayList<String>();
		String[] tmp;
		for (String author: authors) {
			tmp = author.split(" ");
			surnames.add(tmp[tmp.length - 1]);
		}
		
		return surnames;
	}
	
	public float computeNormalizedEditDistance(ArrayList<String> tokens, ArrayList<String> surnames) {
		
		ArrayList<Integer> editDistance = new ArrayList<Integer>();
		int minEditDistance = 0;
		int THRESHOLD = 1;
		int sumOfSemanticAccuracy = 0;
		
		for (String s: surnames) {
			for (String t: tokens)
				editDistance.add(LevenshteinDistance.getDefaultInstance().apply(t,s));
			minEditDistance = Collections.min(editDistance);
			if (minEditDistance <= THRESHOLD)
				sumOfSemanticAccuracy += 1;
			editDistance.clear();
		}
		
		return (float) sumOfSemanticAccuracy / surnames.size();
		
	}
	
	public float computeOverallSemanticAccuracy(ArrayList<Float> semanticAccuracies) {

		float sumOfSemanticAccuracies = 0;
		
		for (Float semanticAccuracy: semanticAccuracies)
			sumOfSemanticAccuracies += semanticAccuracy;
		
		return sumOfSemanticAccuracies / semanticAccuracies.size();
		
	}
	
}