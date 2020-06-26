package project.pojo;

public class Book {

	String source;
	String isbn;
	String title;
	String author;
	
	public Book(String[] infoBook) {
		super();
		this.source = infoBook[0].toLowerCase();
		this.isbn = infoBook[1];
		this.title = infoBook[2].toLowerCase();
		this.author = infoBook[3];
	}
	
	public Book(String isbn, String title, String author) {
		this(new String[]{"-", isbn, title, author}); 
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
	
	@Override
	public String toString() {
		return "Book [isbn=" + isbn + ", title=" + title + ", author=" + author + "]";
	}

	public void convertFromIsbn10ToIsbn13() {
		
		if (isbn.length() == 10) {
			
			this.isbn = "978" + isbn.substring(0, 9);
			
			int controlDigit = computeControlDigit(this.isbn);
			
			if (controlDigit != 10)
				this.isbn += String.valueOf(controlDigit);
			else
				this.isbn += "0";
				
		}
		
	}
	
	public boolean checkControlDigit() {
		
		if (this.isbn.length() == 13) {
			
			int controlDigit = Integer.parseInt(this.isbn.substring(12));
			int newControlDigit = computeControlDigit(this.isbn.substring(0, 12));
			
			return controlDigit == newControlDigit || (controlDigit == 0 && newControlDigit == 10);
			
		}

		return false;
	}
	
	public int computeControlDigit(String isbn12) {
		
		int sumResult = 0;
		int n = 0;
		
		for (int i = 0; i < isbn12.length(); i++) {
			
			n = Character.getNumericValue(isbn12.charAt(i));
		
			if (i % 2 == 0)
				sumResult += n;
			else
				sumResult += n * 3;
		
		}
		
		return 10 - (sumResult % 10);

	}

	public void normalizeAuthor() {
		this.author = this.author.replaceAll("[;/]", ",");
		this.author = this.author.replaceAll("[\\W&&[^,']]", " ");
		this.author = this.author.replaceAll(" \\w ", "  ");
		this.author = this.author.replaceAll("^\\s", "");
		this.author = this.author.replaceAll("([a-z])_?([A-Z])", "$1 $2");
		this.author = this.author.replaceAll(" {2,}", " ");
		this.author = this.author.replaceAll("\\A\\w\\s", "");
		this.author = this.author.toLowerCase();
	}
	
}