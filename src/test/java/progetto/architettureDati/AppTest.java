package progetto.architettureDati;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import project.dataQuality.Metrics;
import project.pojo.Book;
import project.utilities.Attributes;
import project.utilities.DatasetMethods;

public class AppTest extends TestCase {
    
    public void testComputeSemanticAccuracy() {
    	String DATASETPATH = System.getProperty("user.dir") + "\\bookTest.txt";
		String AUTHORSLISTPATH = System.getProperty("user.dir") + "\\authorsTest.txt";
		
        DatasetMethods dm = new DatasetMethods();
        Metrics c = new Metrics();
        
		boolean checkControlDigit = true;
        
		ArrayList<Book> books = dm.readDataset(DATASETPATH);
		HashMap<String, ArrayList<String>> exactAuthorsList = 
				dm.convertValuesIntoArrayListValues(dm.readList(AUTHORSLISTPATH));
		
		for (Book b: books) {
			if (b.getIsbn().length() == 10)
				b.convertFromIsbn10ToIsbn13();
			else {
				checkControlDigit = b.checkControlDigit();
				if (!checkControlDigit)
					System.out.println(b.getIsbn());
			}
		}
		
		HashMap<String, ArrayList<String>> authorsGroupedByIsbn = dm.groupAttributeByIsbn(books, Attributes.AUTHOR);
		
		System.out.println(exactAuthorsList.toString());
		System.out.println(authorsGroupedByIsbn.toString());
		
		ArrayList<Float> semanticAccuracy = c.computeSemanticAccuracy(authorsGroupedByIsbn, exactAuthorsList);

		System.out.println(semanticAccuracy.toString());
		
		float overallSemanticAccuracy = c.computeOverallSemanticAccuracy(semanticAccuracy);
		System.out.println("Overall semantic accuracy: " + overallSemanticAccuracy);
		
		assert true;
    }
    
}
