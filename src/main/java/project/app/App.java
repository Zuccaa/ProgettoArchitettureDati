package project.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import project.dataQuality.Metrics;
import project.dataQuality.Deduplication;
import project.pojo.Book;
import project.utilities.DatasetMethods;

public class App {
    
	public static void main(String[] args) throws IOException {
		
		String DATASETPATH = System.getProperty("user.dir") + "\\book.txt";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authors.txt";
		String TITLESLISTPATH = System.getProperty("user.dir") + "\\titles.txt";
		
        DatasetMethods dm = new DatasetMethods();
        Metrics m = new Metrics();
        Deduplication d = new Deduplication();
        
        // Viene importato il dataset e assegnato ad una arrayList
		ArrayList<Book> books = dm.readDataset(DATASETPATH);
		
		/* Viene importata la lista degli autori di riferimento dal txt, inserendo i
		 * singoli autori dello stesso libro in una lista */
		HashMap<String, ArrayList<String>> exactAuthorsList = 
				dm.convertValuesIntoArrayListValues(dm.readList(AUTHORSLISTPATH));
		
		// Viene importata la lista dei titoli di riferimento dal txt
		HashMap<String, String> exactTitlesList = dm.readList(TITLESLISTPATH);
		
		/* ------------------------------
		 * FASE DI NORMALIZZAZIONE
		 * ------------------------------
		 */
		for (Book b: books) {
			// Viene normalizzato l'ISBN, convertendo tutti gli ISBN10 in ISBN13
			if (b.getIsbn().length() == 10)
				b.convertFromIsbn10ToIsbn13();
			// Viene normalizzato l'attributo author
			b.normalizeAuthor();
			// Viene normalizzato l'attributo title
			b.normalizeTitle();
		}
		
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> titles = new ArrayList<String>();
		
		ArrayList<Float> tupleCompleteness = new ArrayList<Float>();
		
		/* ------------------------------
		 * FASE DI CALCOLO DELLE METRICHE
		 * ------------------------------
		 */
		for (Book b: books) {
			// Rilevamento di ISBN incoerenti per la CONSISTENZA
			if (!b.checkControlDigit())
				System.out.println("ERRORE - La cifra di controllo per l'ISBN " + b.getIsbn() + " non è corretta! -");
			// Vengono inseriti in una lista i valori di COMPLETEZZA DI TUPLA
			tupleCompleteness.add(m.computeTupleCompleteness(b, false));
			// Vengono inseriti i vari attributi in liste apposite per i successivi calcoli
			authors.add(b.getAuthor());
			isbn.add(b.getIsbn());
			sources.add(b.getSource());
			titles.add(b.getTitle());
		}

		// Vengono contate le frequenze delle COMPLETEZZE DI TUPLA per una migliore rappresentazione
		HashMap<Float, Integer> tupleFrequencies = dm.countFrequencies(tupleCompleteness);
		
		// Per ogni attributo vengono calcolati i valori di COMPLETEZZA DI ATTRIBUTO
		float authorCompleteness = m.computeAttributeCompleteness(authors);
		float isbnCompleteness = m.computeAttributeCompleteness(isbn);
		float sourceCompleteness = m.computeAttributeCompleteness(sources);
		float titleCompleteness = m.computeAttributeCompleteness(titles);
		
		// Viene calcolata la COMPLETEZZA DI TABELLA
		float tableCompleteness = m.computeTableCompleteness(books);
		
		// Vengono raggruppati i libri in base all'ISBN per migliorare la computazione
		HashMap<String, ArrayList<Book>> booksGroupedByIsbn = dm.groupBookByIsbn(books);
		
		ArrayList<Float> titleAccuracies = new ArrayList<Float>();
		
		// Viene calcolata l'ACCURATEZZA SINTATTICA DEI TITOLI
		for(String _isbn: booksGroupedByIsbn.keySet())
			for(Book book: booksGroupedByIsbn.get(_isbn))
				titleAccuracies.add(m.computeSyntacticAccuracy(book.getTitle(), exactTitlesList.get(_isbn)));
		
		// Viene calcolato il valore medio dell'ACCURATEZZA SINTATTICA DEI TITOLI
		float overallTitleAccuracy = m.computeMean(titleAccuracies);
		
		// Viene calcolata l'ACCURATEZZA SEMANTICA DI AUTHOR LIST
		ArrayList<Float> authorListAccuracies = m.computeSemanticAccuracies(booksGroupedByIsbn, exactAuthorsList);
		
		// Viene calcolato il valore medio dell'ACCURATEZZA SEMANTICA DI AUTHOR LIST
		float overallAuthorListAccuracy = m.computeMean(authorListAccuracies);
		
		// Vengono restituiti su console i risultati delle metriche
		printMetrics(tupleFrequencies, authorCompleteness, isbnCompleteness, sourceCompleteness, titleCompleteness,
				tableCompleteness, titleAccuracies, overallTitleAccuracy, authorListAccuracies, 
				overallAuthorListAccuracy, "");
		
		/* ------------------------------
		 * FASE DI DEDUPLICAZIONE
		 * ------------------------------
		 */
		
		// Vengono calcolate le sources più affidabili per title e author list
		Map<String, Float> sortedSourceAffidabilityForTitles = dm.computeSortedSourceAffidability(
				booksGroupedByIsbn, titleAccuracies);
		Map<String, Float> sortedSourceAffidabilityForAuthorList = dm.computeSortedSourceAffidability(
				booksGroupedByIsbn, authorListAccuracies);
		
		// Vengono estrapolate solamente le sources affidabili, con indice maggiore di una certa soglia
		LinkedList<String> sourcesOrderedByAffidabilityForAuthorList = 
				dm.getKeysOrderedByValueWithinThreshold(sortedSourceAffidabilityForAuthorList, (float) 0.9);
		LinkedList<String> sourcesOrderedByAffidabilityForTitles = 
				dm.getKeysOrderedByValueWithinThreshold(sortedSourceAffidabilityForTitles, (float) 0.9);
		
		// Viene effettuata la deduplicazione
		ArrayList<Book> booksDeduplicated = d.computeDeduplication(booksGroupedByIsbn, 
				sourcesOrderedByAffidabilityForAuthorList ,sourcesOrderedByAffidabilityForTitles);
		
		// Viene stampato su file il nuovo dataset, ottenuto da questa fase
		dm.writeFile(booksDeduplicated, "finalDataset.txt");
		
		/* ---------------------------------
		 * FASE DI CONFRONTO DELLE METRICHE
		 * ---------------------------------
		 */
		
		/* In maniera analoga alla fase di calcolo delle metriche, esse vengono 
		 * calcolate con la nuova lista di libri deduplicati
		*/
		ArrayList<String> authorsDeduplicated = new ArrayList<String>();
		ArrayList<String> titlesDeduplicated = new ArrayList<String>();
		ArrayList<String> isbnDeduplicated = new ArrayList<String>();
		ArrayList<String> sourcesDeduplicated = new ArrayList<String>();
		
		ArrayList<Float> tupleCompletenessDeduplicated = new ArrayList<Float>();
		
		for (Book b: booksDeduplicated) {
			tupleCompletenessDeduplicated.add(m.computeTupleCompleteness(b, true));
			authorsDeduplicated.add(b.getAuthor());
			isbnDeduplicated.add(b.getIsbn());
			sourcesDeduplicated.add(b.getSource());
			titlesDeduplicated.add(b.getTitle());
		}
		
		HashMap<Float, Integer> tupleFrequenciesDeduplicated = dm.countFrequencies(tupleCompletenessDeduplicated);
		
		float authorDeduplicatedCompleteness = m.computeAttributeCompleteness(authorsDeduplicated);
		float isbnDeduplicatedCompleteness = m.computeAttributeCompleteness(isbnDeduplicated);
		float sourceDeduplicatedCompleteness = m.computeAttributeCompleteness(sourcesDeduplicated);
		float titleDeduplicatedCompleteness = m.computeAttributeCompleteness(titlesDeduplicated);
		float tableDeduplicatedCompleteness = m.computeTableCompleteness(booksDeduplicated);
		
		HashMap<String, ArrayList<Book>> booksDeduplicatedGroupedByIsbn = dm.groupBookByIsbn(booksDeduplicated);
		ArrayList<Float> authorListDeduplicatedAccuracy = m.computeSemanticAccuracies(
				booksDeduplicatedGroupedByIsbn, exactAuthorsList);
		
		float overallAuthorListDeduplicatedAccuracy = m.computeMean(authorListDeduplicatedAccuracy);
		
		ArrayList<Float> titleDeduplicatedAccuracies = new ArrayList<Float>();
		
		for(String _isbn: exactTitlesList.keySet())
			for(Book book: booksDeduplicatedGroupedByIsbn.get(_isbn))
				titleDeduplicatedAccuracies.add(m.computeSyntacticAccuracy(book.getTitle(), exactTitlesList.get(_isbn)));					
				
		float overallTitleDeduplicatedAccuracy = m.computeMean(titleDeduplicatedAccuracies);
		
		printMetrics(tupleFrequenciesDeduplicated, authorDeduplicatedCompleteness, isbnDeduplicatedCompleteness, 
				sourceDeduplicatedCompleteness, titleDeduplicatedCompleteness, tableDeduplicatedCompleteness, 
				titleDeduplicatedAccuracies, overallTitleDeduplicatedAccuracy, authorListDeduplicatedAccuracy, 
				overallAuthorListDeduplicatedAccuracy, "");
		
    }
	
	// Metodo per restituire su console i risultati delle metriche
	public static void printMetrics(HashMap<Float, Integer> tupleFrequencies, float authorCompleteness,
			float isbnCompleteness, float sourceCompleteness, float titleCompleteness,
			float tableCompleteness, ArrayList<Float> titleAccuracies, float overallTitleAccuracy, 
			ArrayList<Float> authorListAccuracies, float overallAuthorListAccuracy, String deduplicated) {
		
		System.out.println("COMPLETEZZA " + deduplicated);
		System.out.println("Completezze di tupla: " + tupleFrequencies.toString());
		System.out.println("Completezza su author list: " + authorCompleteness);
		System.out.println("Completezza su ISBN: " + isbnCompleteness);
		System.out.println("Completezza su source: " + sourceCompleteness);
		System.out.println("Completezza su title: " + titleCompleteness);
		System.out.println("Completezza di tabella: " + tableCompleteness);
		System.out.println("--------------------------------------");
		System.out.println("ACCURATEZZA " + deduplicated);
		new DatasetMethods().printFrequenciesOccurrences(titleAccuracies, "title");
		System.out.println("Valore medio dell'accuratezza di title: " + overallTitleAccuracy);
		new DatasetMethods().printFrequenciesOccurrences(authorListAccuracies, "author List");
		System.out.println("Valore medio dell'accuratezza di author list: " + overallAuthorListAccuracy);

	}
	
}
