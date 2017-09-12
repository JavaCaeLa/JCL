package br.com.ufop.jars;

public class Edge implements java.io.Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 5023403494588369426L;
	public final String target;
	    public final double weight;
	    public Edge(String argTarget, double argWeight)
	    { target = argTarget; weight = argWeight; }

}
