package br.com.ufop.jars;

import java.util.ArrayList;
import java.util.List;

public class Vertex implements Comparable<Vertex>,java.io.Serializable {
	   /**
	 * 
	 */
	private static final long serialVersionUID = 5086604618824479694L;
		public final String name;
	    public List<Edge> adjacencies = new ArrayList<Edge>();
	    public double minDistance = Double.POSITIVE_INFINITY;
	    public Vertex previous;
	    public Vertex(String argName) { name = argName; }
	    public String toString() { return name; }
	    public int compareTo(Vertex other)
	    {
	        return Double.compare(minDistance, other.minDistance);
	    }
}
