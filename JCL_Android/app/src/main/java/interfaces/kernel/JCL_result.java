package interfaces.kernel;

import java.io.Serializable;
import java.util.List;

/**
 * A result in JCL is composed by a success result or an error result. This way, 
 * the developer can easy get the result, verify if it is null. If it is null
 * there is an error result of the developer code. JCL must guarantee the
 * middleware robust execution.
 * @author Joubert Lima
 * @version 1.0
 */

public interface JCL_result extends Serializable{

	/**
	 * Returns the correct result from an execution of JCl.
	 * @return The correct result.
	 */
	public abstract Object getCorrectResult();

	/**
	 * Returns the error result from an execution of JCl as an Exception.
	 * @return The error Exception.
	 */
	public abstract Exception getErrorResult();

	/**
	 * Sets the correct result from an execution of JCl.
	 * @param r - The correct result to be set.
	 */
	public abstract void setCorrectResult(Object r);

	/**
	 * Sets the error result from an execution of JCl as an Exception.
	 * @param error - The error Exception to be set in the result.
	 */
	public abstract void setErrorResult(Exception error);

	public abstract List<Long> getTime();

	public abstract void setTime(List<Long> time);

	public abstract void addTime(Long time);

	public abstract long getMemorysize();

	public abstract void setMemorysize(long memorysize);

}
