package progetto.architettureDati;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class App {
    
	public static void main(String[] args) {
		
		String DATASETPATH = System.getProperty("user.dir") + "\\book.txt";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		
        DatasetMethods dm = new DatasetMethods();
        ComputeMetrics c = new ComputeMetrics();
        
		ArrayList<Book> books = dm.readDataset(DATASETPATH);
		HashMap<String, ArrayList<String>> exactAuthorsList = dm.readAuthorsList(AUTHORSLISTPATH);
		
		boolean checkControlDigit = true;
		
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		
		TreeMap<String, Integer> sortedOccurrences = new TreeMap<String, Integer>();
		
		for (Book b: books) {
			if (b.getIsbn().length() == 10)
				b.convertFromIsbn10ToIsbn13();
			else {
				checkControlDigit = b.checkControlDigit();
				if (!checkControlDigit)
					System.out.println(b.getIsbn());
			}
			float tupleCompleteness = c.computeTupleCompleteness(b);
			authors.add(b.getAuthor());
			isbn.add(b.getIsbn());
			sources.add(b.getSource());
			titles.add(b.getTitle());
		}
		
		HashMap<String, ArrayList<String>> authorsGroupedByIsbn = dm.groupBooksByIsbn(books);

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
		System.out.println(semanticAccuracy.toString());
		
		System.out.println(semanticAccuracy.size());
		
		/*for (String _isbn: exactAuthorsList.keySet()){
            System.out.print(_isbn + exactAuthorsList.get(_isbn).toString());
            for (String s: exactAuthorsList.get(_isbn)) {
            	System.out.print(" " + s);
            }
            System.out.print("\n");
		}*/
		
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
		
		/*while (!isbn.isEmpty()) {
			int occurrences = Collections.frequency(isbn, isbn.get(0));
			sortedOccurrences.put(isbn.get(0), occurrences);
			isbn.removeAll(Collections.singleton(isbn.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		
		/*while (!authors.isEmpty()) {
			int occurrences = Collections.frequency(authors, authors.get(0));
			sortedOccurrences.put(authors.get(0), occurrences);
			authors.removeAll(Collections.singleton(authors.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
    }
	
}
