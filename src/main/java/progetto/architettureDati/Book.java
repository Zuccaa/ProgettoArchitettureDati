package progetto.architettureDati;

import java.util.Arrays;

public class Book {

	String source;
	String isbn;
	String title;
	String author;
	
	public Book(String[] infoBook) {
		super();
		this.source = infoBook[0];
		this.isbn = infoBook[1];
		this.title = infoBook[2];
		this.author = infoBook[3];
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void convertFromIsbn10ToIsbn13() {
		
		int controlDigit = 0;
		int sumResult = 0;
		int n = 0;
		
		if (isbn.length() == 10) {
			
			this.isbn = "978" + isbn.substring(0, 9);
			
			for (int i = 0; i < this.isbn.length(); i++) {
				
				n = Character.getNumericValue(this.isbn.charAt(i));
			
				if (i % 2 == 0)
					sumResult += n;
				else
					sumResult += n * 3;
			
			}
			
			controlDigit = 10 - (sumResult % 10);
			
			if (controlDigit != 10)
				this.isbn += String.valueOf(controlDigit);
			else
				this.isbn += "0";
				
		}
		
	}
	
}