package br.com.ufop.exemplo;

public class Item implements java.io.Serializable{
	  /**
	 * 
	 */
	  private static final long serialVersionUID = -7377347334736346186L;
	  private int weight, value; // must be positive

	  // Constructor:
	  public Item(int w, int v) {
	    weight = w; value = v;
	  }


	  // Accessor methods:
	  public int weight() { return weight; }
	  public int value() { return value; }
	}