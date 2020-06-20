package progetto.architettureDati;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;


public class App {
    
	public static void main(String[] args) {
		
        DatasetMethods dm = new DatasetMethods();
        ComputeMetrics c = new ComputeMetrics();
		ArrayList<Book> books = dm.readFile();
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		TreeMap<String, Integer> sortedOccurrences = new TreeMap<String, Integer>();
		
		for (Book b: books) {
			float tupleCompleteness = c.computeTupleCompleteness(b);
			authors.add(b.getAuthor());
			isbn.add(b.getIsbn());
			sources.add(b.getSource());
			titles.add(b.getTitle());
		}
		
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

		/*while (!title.isEmpty()) {
			int occurrences = Collections.frequency(title, title.get(0));
			sortedOccurrences.put(title.get(0), occurrences);
			title.removeAll(Collections.singleton(title.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		/*while (!source.isEmpty()) {
			int occurrences = Collections.frequency(source, source.get(0));
			sortedOccurrences.put(source.get(0), occurrences);
			source.removeAll(Collections.singleton(source.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		while (!isbn.isEmpty()) {
			int occurrences = Collections.frequency(isbn, isbn.get(0));
			sortedOccurrences.put(isbn.get(0), occurrences);
			isbn.removeAll(Collections.singleton(isbn.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);
		
		/*while (!authors.isEmpty()) {
			int occurrences = Collections.frequency(authors, authors.get(0));
			sortedOccurrences.put(authors.get(0), occurrences);
			authors.removeAll(Collections.singleton(authors.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
    }
	
}
