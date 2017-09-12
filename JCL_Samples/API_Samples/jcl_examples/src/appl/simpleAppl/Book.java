package appl.simpleAppl;


import java.io.Serializable;

public class Book implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4833392148597759074L;
		String author;
		String editor;
		Integer pages;
		Integer year;
				
		
		public Book(String author, String editor, Integer pages, Integer year){
			this.author = author;
			this.editor = editor;
			this.pages = pages;
			this.year = year;			
			
		}
		
		public void print(){
			System.err.println(this.author);
			System.err.println(this.editor);
			System.err.println(pages);
			System.err.println(year);
			
		}
	

}
