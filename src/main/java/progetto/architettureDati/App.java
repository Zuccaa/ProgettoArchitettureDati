package progetto.architettureDati;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;


public class App {
    
	public static void main(String[] args) {
		
        DatasetMethods dm = new DatasetMethods();
        ComputeMetrics c = new ComputeMetrics();
		ArrayList<Book> books = dm.readFile();
		ArrayList<String> authors = new ArrayList<String>();
		TreeMap<String, Integer> sortedOccurrences = new TreeMap<String, Integer>();
		
		for (Book b: books) {
			float prova = c.computeTupleCompleteness(b);
			if (prova > 0) {
				System.out.println(prova);
			}
			authors.add(b.getAuthor().toLowerCase());
		}
		
		/*while (!authors.isEmpty()) {
			int occurrences = Collections.frequency(authors, authors.get(0));
			sortedOccurrences.put(authors.get(0), occurrences);
			authors.removeAll(Collections.singleton(authors.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
    }
	
}
