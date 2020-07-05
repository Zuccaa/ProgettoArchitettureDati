package project.app;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

import project.dataQuality.Metrics;
import project.dataQuality.Deduplication;
import project.pojo.Book;
import project.utilities.Attributes;
import project.utilities.DatasetMethods;
import project.utilities.MapUtil;

public class App {
    
	public static void main(String[] args) throws IOException {
		
		String DATASETPATH = System.getProperty("user.dir") + "\\book.txt";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		String TITLESLISTPATH = System.getProperty("user.dir") + "\\titles.txt";
		
        DatasetMethods dm = new DatasetMethods();
        Metrics m = new Metrics();
        Deduplication d = new Deduplication();
        MapUtil mu = new MapUtil();
        
		ArrayList<Book> books = dm.readDataset(DATASETPATH);
		HashMap<String, ArrayList<String>> exactAuthorsList = 
				dm.convertValuesIntoArrayListValues(dm.readList(AUTHORSLISTPATH));
		HashMap<String, String> exactTitlesList = dm.readList(TITLESLISTPATH);

		boolean checkControlDigit = true;
		
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		
		TreeMap<String, Integer> sortedOccurrences = new TreeMap<String, Integer>();
		TreeMap<String, Integer> sortedOccurrencesFloat = new TreeMap<String, Integer>();
		
		ArrayList<Float> tupleCompleteness = new ArrayList<Float>();
		
		for (Book b: books) {
			if (b.getIsbn().length() == 10)
				b.convertFromIsbn10ToIsbn13();
			else {
				if (!b.checkControlDigit())
					System.out.println(b.getIsbn());
			}
			b.normalizeAuthor();
			b.normalizeTitle();
			tupleCompleteness.add(m.computeTupleCompleteness(b));
			authors.add(b.getAuthor());
			isbn.add(b.getIsbn());
			sources.add(b.getSource());
			titles.add(b.getTitle());
		}

		HashMap<Float, Integer> tupleFrequencies = dm.countFrequencies(tupleCompleteness);
		System.out.println(tupleFrequencies.toString());
		
		HashMap<String, ArrayList<Book>> booksGroupedByIsbn = dm.groupBookByIsbn(books);

		float authorCompleteness = m.computeAttributeCompleteness(authors);
		System.out.println("Author completeness: " + authorCompleteness);
		float isbnCompleteness = m.computeAttributeCompleteness(isbn);
		System.out.println("ISBN completeness: " + isbnCompleteness);
		float sourceCompleteness = m.computeAttributeCompleteness(sources);
		System.out.println("Source completeness: " + sourceCompleteness);
		float titleCompleteness = m.computeAttributeCompleteness(titles);
		System.out.println("Title completeness: " + titleCompleteness);
		float tableCompleteness = m.computeTableCompleteness(books);
		System.out.println("Table completeness: " + tableCompleteness);	
		
		//HashMap<String, ArrayList<String>> titlesGroupedByIsbn = dm.groupAttributeByIsbn(books, Attributes.TITLE);
		ArrayList<Float> titleAccuracies = new ArrayList<Float>();
		
		for(String _isbn: booksGroupedByIsbn.keySet())
			for(Book book: booksGroupedByIsbn.get(_isbn))
				titleAccuracies.add(m.computeSyntacticAccuracy(book.getTitle(), exactTitlesList.get(_isbn)));
		
		Map<String, Float> sortedSourceAffidabilityForTitles = dm.computeSortedSourceAffidability(
				booksGroupedByIsbn, titleAccuracies);

		String floatValue = "";
		
		for(float accuracy: titleAccuracies) {
			if (accuracy <= 0.1)
				floatValue = "<= 0.1";
			else
				if (accuracy <= 0.2)
					floatValue = "0.1 - 0.2";
				else
					if (accuracy <= 0.3)
						floatValue = "0.2 - 0.3";
					else
						if (accuracy <= 0.4)
							floatValue = "0.3 - 0.4";
						else
							if (accuracy <= 0.5)
								floatValue = "0.4 - 0.5";
							else
								if (accuracy <= 0.6)
									floatValue = "0.5 - 0.6";
								else
									if (accuracy <= 0.7)
										floatValue = "0.6 - 0.7";
									else
										if (accuracy <= 0.8)
											floatValue = "0.7 - 0.8";
										else
											if (accuracy <= 0.9)
												floatValue = "0.8 - 0.9";
											else
												floatValue = "> 0.9";
			if (sortedOccurrencesFloat.containsKey(floatValue))
				sortedOccurrencesFloat.put(floatValue, sortedOccurrencesFloat.get(floatValue) + 1);
			else
				sortedOccurrencesFloat.put(floatValue, 1);
		}
		
		//dm.writeOccurrences(sortedOccurrencesFloat);
		
		float meanTitleAccuracy = m.computeMean(titleAccuracies);
		System.out.println("Overall title accuracy: " + meanTitleAccuracy);
		
		ArrayList<Float> authorListAccuracy = m.computeSemanticAccuracy(booksGroupedByIsbn, exactAuthorsList);
		System.out.println(titleAccuracies.size() + "|||" + authorListAccuracy.size());
		
		Map<String, Float> sortedSourceAffidabilityForAuthorList = dm.computeSortedSourceAffidability(
				booksGroupedByIsbn, authorListAccuracy);
		
		float overallAuthorListAccuracy = m.computeMean(authorListAccuracy);
		System.out.println("Overall author list accuracy: " + overallAuthorListAccuracy);
		
		LinkedList<String> sourcesOrderedByAffidabilityForAuthorList = 
				dm.getKeysOrderByValueWithinThreshold(sortedSourceAffidabilityForAuthorList, (float) 0.9);
		LinkedList<String> sourcesOrderedByAffidabilityForTitles = 
				dm.getKeysOrderByValueWithinThreshold(sortedSourceAffidabilityForTitles, (float) 0.9);
				
		ArrayList<Book> booksDeduplicated = d.computeDeduplication(booksGroupedByIsbn, 
				sourcesOrderedByAffidabilityForAuthorList ,sourcesOrderedByAffidabilityForTitles);
		
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
		
		float authorDeduplicatedCompleteness = m.computeAttributeCompleteness(authorsDeduplicated);
		System.out.println("Author deduplicated completeness: " + authorDeduplicatedCompleteness);
		float isbnDeduplicatedCompleteness = m.computeAttributeCompleteness(isbnDeduplicated);
		System.out.println("ISBN completeness: " + isbnDeduplicatedCompleteness);
		float sourceDeduplicatedCompleteness = m.computeAttributeCompleteness(sourcesDeduplicated);
		System.out.println("Source completeness: " + sourceDeduplicatedCompleteness);
		float titleDeduplicatedCompleteness = m.computeAttributeCompleteness(titlesDeduplicated);
		System.out.println("Title deduplicated completeness: " + titleDeduplicatedCompleteness);
		float tableDeduplicatedCompleteness = m.computeTableCompleteness(booksDeduplicated);
		System.out.println("Table deduplicated completeness: " + tableDeduplicatedCompleteness);
		
		HashMap<String, ArrayList<Book>> booksDeduplicatedGroupedByIsbn = dm.groupBookByIsbn(booksDeduplicated);
		ArrayList<Float> authorListAccuracyDeduplicated = m.computeSemanticAccuracy(
				booksDeduplicatedGroupedByIsbn, exactAuthorsList);

		float overallAuthorListAccuracyDeduplicated = m.computeMean(authorListAccuracyDeduplicated);
		System.out.println("Overall author list accuracy deduplicated: " + overallAuthorListAccuracyDeduplicated);
		
		ArrayList<Float> titleDeduplicatedAccuracies = new ArrayList<Float>();
		
		for(String _isbn: exactTitlesList.keySet()) {
			for(Book book: booksDeduplicatedGroupedByIsbn.get(_isbn)) {
				titleDeduplicatedAccuracies.add(m.computeSyntacticAccuracy(book.getTitle(), exactTitlesList.get(_isbn)));					
			}
		}
		
		float meanTitleAccuracyDeduplicated = m.computeMean(titleDeduplicatedAccuracies);
		System.out.println("Overall title accuracy Deduplicated: " + meanTitleAccuracyDeduplicated);
		
		dm.writeFile(booksDeduplicated, "finalDataset.txt");		
        
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
		
		
		/*while (!authors.isEmpty()) {
			int occurrences = Collections.frequency(authors, authors.get(0));
			sortedOccurrences.put(authors.get(0), occurrences);
			authors.removeAll(Collections.singleton(authors.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
		/*ArrayList<String> exactTitlesOccurrences = new ArrayList<String>();
		
		for (String _isbn: exactTitlesList.keySet()) {
			exactTitlesOccurrences.add(exactTitlesList.get(_isbn));
		}
		
		while (!exactTitlesOccurrences.isEmpty()) {
			int occurrences = Collections.frequency(exactTitlesOccurrences, exactTitlesOccurrences.get(0));
			if (occurrences >= 2)
				sortedOccurrences.put(exactTitlesOccurrences.get(0), occurrences);
			exactTitlesOccurrences.removeAll(Collections.singleton(exactTitlesOccurrences.get(0)));
		}
		
		dm.writeOccurrences(sortedOccurrences);*/
		
    }
	
}
