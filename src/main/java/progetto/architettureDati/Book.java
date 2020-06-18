package progetto.architettureDati;

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
	
}