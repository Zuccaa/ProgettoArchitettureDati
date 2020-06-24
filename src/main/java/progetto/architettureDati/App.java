package progetto.architettureDati;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

public class App {
    
	public static void main(String[] args) {
		
		String DATASETPATH = System.getProperty("user.dir") + "\\book.txt";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		
        DatasetMethods dm = new DatasetMethods();
        ComputeMetrics c = new ComputeMetrics();
        Deduplication d = new Deduplication();
        
		ArrayList<Book> books = dm.readDataset(DATASETPATH);
		HashMap<String, ArrayList<String>> exactAuthorsList = dm.readAuthorsList(AUTHORSLISTPATH);
		
		boolean checkControlDigit = true;
		
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		
		TreeMap<String, Integer> sortedOccurrences = new TreeMap<String, Integer>();
		
		ArrayList<Float> tupleCompleteness = new ArrayList<Float>();
		for (Book b: books) {
			if (b.getIsbn().length() == 10)
				b.convertFromIsbn10ToIsbn13();
			else {
				if (!b.checkControlDigit())
					System.out.println(b.getIsbn());
			}
			b.normalizeAuthor();
			tupleCompleteness.add(c.computeTupleCompleteness(b));
			authors.add(b.getAuthor());
			isbn.add(b.getIsbn());
			sources.add(b.getSource());
			titles.add(b.getTitle());
		}

		HashMap<Float, Integer> tupleFrequencies = dm.countFrequencies(tupleCompleteness);
		System.out.println(tupleFrequencies.toString());
		
		HashMap<String, ArrayList<String>> authorsGroupedByIsbn = dm.groupAttributeByIsbn(books, Attributes.AUTHOR);

		float authorCompleteness = c.computeAttributeCompleteness(authors);
		System.out.println("Author completeness: " + authorCompleteness);
		float isbnCompleteness = c.computeAttributeCompleteness(isbn);
		System.out.println("ISBN completeness: " + isbnCompleteness);
		float sourceCompleteness = c.computeAttributeCompleteness(sources);
		System.out.println("Source completeness: " + sourceCompleteness);
		float titleCompleteness = c.computeAttributeCompleteness(titles);
		System.out.println("Title completeness: " + titleCompleteness);
		float tableCompleteness = c.computeTableCompleteness(books);
		System.out.println("Table completeness: " + tableCompleteness);		
		
		ArrayList<Float> semanticAccuracy = c.computeSemanticAccuracy(authorsGroupedByIsbn, exactAuthorsList);
		//System.out.println(semanticAccuracy.toString());

		float overallSemanticAccuracy = c.computeOverallSemanticAccuracy(semanticAccuracy);
		System.out.println("Overall semantic accuracy: " + overallSemanticAccuracy);
		
		//HashMap<String, ArrayList<String>> titlesGroupedByIsbn = dm.groupAttributeByIsbn(books, Attributes.TITLE);
		/*for (String _isbn: titlesGroupedByIsbn.keySet()) {
            System.out.println(_isbn + ": " + titlesGroupedByIsbn.get(_isbn).toString());
		}*/
		
		ArrayList<Book> booksDeduplicated = d.computeDeduplication(books);
		
		ArrayList<String> authorsDeduplicated = new ArrayList<String>();
		ArrayList<String> titlesDeduplicated = new ArrayList<String>();
		ArrayList<String> isbnDeduplicated = new ArrayList<String>();
		ArrayList<String> sourcesDeduplicated = new ArrayList<String>();
		
		for (Book b: booksDeduplicated) {
			authorsDeduplicated.add(b.getAuthor());
			isbnDeduplicated.add(b.getIsbn());
			sourcesDeduplicated.add(b.getSource());
			titlesDeduplicated.add(b.getTitle());
		}
		
		float authorDeduplicatedCompleteness = c.computeAttributeCompleteness(authorsDeduplicated);
		System.out.println("Author deduplicated completeness: " + authorDeduplicatedCompleteness);
		float isbnDeduplicatedCompleteness = c.computeAttributeCompleteness(isbnDeduplicated);
		System.out.println("ISBN completeness: " + isbnDeduplicatedCompleteness);
		float sourceDeduplicatedCompleteness = c.computeAttributeCompleteness(sourcesDeduplicated);
		System.out.println("Source completeness: " + sourceDeduplicatedCompleteness);
		float titleDeduplicatedCompleteness = c.computeAttributeCompleteness(titlesDeduplicated);
		System.out.println("Title deduplicated completeness: " + titleDeduplicatedCompleteness);
		float tableDeduplicatedCompleteness = c.computeTableCompleteness(booksDeduplicated);
		System.out.println("Table deduplicated completeness: " + tableDeduplicatedCompleteness);
		
		HashMap<String, ArrayList<String>> authorsDeduplicatedGroupedByIsbn = 
				dm.groupAttributeByIsbn(booksDeduplicated, Attributes.AUTHOR);

		ArrayList<Float> semanticAccuracyDeduplicated = c.computeSemanticAccuracy(
				authorsDeduplicatedGroupedByIsbn, exactAuthorsList);
		//System.out.println(semanticAccuracy.toString());

		float overallSemanticAccuracyDeduplicated = c.computeOverallSemanticAccuracy(semanticAccuracyDeduplicated);
		System.out.println("Overall semantic accuracy deduplicated: " + overallSemanticAccuracyDeduplicated);
		/*for (Book b: booksDeduplicated) {
            System.out.println(b.toString());
		}*/
		dm.writeFile(booksDeduplicated);
        
		/*for (String _isbn: exactAuthorsList.keySet()){
            System.out.print(_isbn + exactAuthorsList.get(_isbn).toString());
            for (String s: exactAuthorsList.get(_isbn)) {
            	System.out.print(" " + s);
            }
            System.out.print("\n");
		}*/
		
		/*while (!titles.isEmpty()) {
			int occurrences = Collections.frequency(titles, titles.get(0));
			sortedOccurrences.put(titles.get(0), occurrences);
			titles.removeAll(Collections.singleton(titles.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		/*while (!sources.isEmpty()) {
			int occurrences = Collections.frequency(sources, sources.get(0));
			sortedOccurrences.put(sources.get(0), occurrences);
			sources.removeAll(Collections.singleton(sources.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		/*while (!isbn.isEmpty()) {
			int occurrences = Collections.frequency(isbn, isbn.get(0));
			sortedOccurrences.put(isbn.get(0), occurrences);
			isbn.removeAll(Collections.singleton(isbn.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		
		while (!authors.isEmpty()) {
			int occurrences = Collections.frequency(authors, authors.get(0));
			sortedOccurrences.put(authors.get(0), occurrences);
			authors.removeAll(Collections.singleton(authors.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);
		
    }
	
}
