package project.pojo;

import project.utilities.DatasetMethods;

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
		
		this.author = new DatasetMethods().convertHTMLSymbols(this.author);
		
		// Sostituisce i ; e / con ,
		this.author = this.author.replaceAll("[;/]", ",");
		// Sostituisce tutti i caratteri speciali esclusi , e ' con lo spazio
		this.author = this.author.replaceAll("[\\W&&[^,']]", " ");
		// Sostituisce tutte le lettere isolate da spazi con spazi
		this.author = this.author.replaceAll(" \\w ", "  ");
		// Separa le lettere minuscole seguite da maiuscole
		this.author = this.author.replaceAll("([a-z])_?([A-Z])", "$1 $2");
		// Sostituisce due o più spazi consecutivi con uno unico
		this.author = this.author.replaceAll(" {2,}", " ");
		// Rimuove lo spazio all'inizio ed alla fine della stringa
		this.author = this.author.replaceAll("(^\\s)|(\\s$)", "");
		// Rimuove la lettera isolata all'inizio della stringa
		this.author = this.author.replaceAll("\\A\\w\\s", "");
		
		this.author = this.author.toLowerCase();
		
	}
	
	public void normalizeTitle() {
		
		this.title = new DatasetMethods().convertHTMLSymbols(this.title);
		
		this.title += " ";
		
		// Sostituisce i number-th in number
		this.title = this.title.replaceAll("first edit", "1 edit");
		this.title = this.title.replaceAll("second edit", "2 edit");
		this.title = this.title.replaceAll("third edit", "3 edit");
		this.title = this.title.replaceAll("fourth edit", "4 edit");
		this.title = this.title.replaceAll("fifth edit", "5 edit");
		this.title = this.title.replaceAll("sixth edit", "6 edit");
		// Sostituisce i volumenumber in volume number e li inserisce alla fine della linea
		this.title = this.title.replaceAll("(volume)(\\d+)(.*)", "$3, $1 $2");
		// Sostituisce le seguenti possibilità in volume number:
		// v. number - v.number - vol.number - vol. number - vols.number - vols. number
		this.title = this.title.replaceAll("v(ol)?(s)?(\\.)?[\\s]?(\\d+)(.*)", ", volume$2 $4");
		// Sostituisce i pt. e pts. in part e parts
		this.title = this.title.replaceAll("(pt(s)?)\\.", "part$2");
		// Sostituisce gli ed. e eds. in edition
		this.title = this.title.replaceAll("(\\sed(s)?\\.)(\\s\\d)?", "$3 edition");
		// Sostituisce le occorrenze number edition in number-th Edition
		this.title = this.title.replaceAll("(1)[\\s]?(edition)(.*)", "$3, $1st edition");
		this.title = this.title.replaceAll("(2)[\\s]?(edition)(.*)", "$3, $1nd edition");
		this.title = this.title.replaceAll("(3)[\\s]?(edition)(.*)", "$3, $1rd edition");
		this.title = this.title.replaceAll("(\\d+)[\\s]?(edition)(.*)", "$3, $1th edition");
		// Sostituisce le seguenti possibilità in number-th Edition e le inserisce alla fine della linea:
		// number/e - numbere - number/ed - numbered / number ed
		this.title = this.title.replaceAll("(1)([\\s]?[/]?e[^a-ce-z])(.*)", "$3, $1st edition");
		this.title = this.title.replaceAll("(2)([\\s]?[/]?e[^a-ce-z])(.*)", "$3, $1nd edition");
		this.title = this.title.replaceAll("(3)([\\s]?[/]?e[^a-ce-z])(.*)", "$3, $1rd edition");
		this.title = this.title.replaceAll("(\\d+)([\\s]?[/]?e[^a-ce-z])(.*)", "$3, $1th edition");		
		// Sostituisce i spazio[:;,]spazio in [:;,]spazio
		this.title = this.title.replaceAll("\\s([:,;])\\s", "$1 ");
		// Sostituisce due o più spazi consecutivi con uno unico
		this.title = this.title.replaceAll(" {2,}", " ");
		// Rimuove lo spazio all'inizio ed alla fine della stringa
		this.title = this.title.replaceAll("(^\\s)|(\\s$)", "");
		// Rimuove tutte le parentesi e il loro contenuto
		this.title = this.title.replaceAll("\\([^\\(\\)]*\\)|[\\(\\)]", "");
		this.title = this.title.replaceAll("\\[[^\\[\\]]*\\]|[\\[\\]]", "");
		this.title = this.title.replaceAll("\\{[^\\{\\}]*\\}|[\\{\\}]", "");
		
	}
	
}