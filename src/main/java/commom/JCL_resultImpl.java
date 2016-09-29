package commom;

import java.util.ArrayList;
import java.util.List;

import interfaces.kernel.JCL_result;

public class JCL_resultImpl implements JCL_result{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Long> time;
	private Object result;
	private Exception error;
		
	
	public JCL_resultImpl(){
		this.result = null;
		this.error = null;
		
	}
		
	public JCL_resultImpl(Object result, Exception error){
		this.result = result;
		this.error = error;	
	}

	@Override
	public Object getCorrectResult() {
		// TODO Auto-generated method stub
		return this.result;
	}

	@Override
	public Exception getErrorResult() {
		// TODO Auto-generated method stub
		return this.error;
	}

	@Override
	public void setCorrectResult(Object r) {
		this.result = r;		
	}

	@Override
	public void setErrorResult(Exception error) {
		this.error = error;
		
	}
	
	@Override
	public List<Long> getTime() {
		return time;
	}
	
	@Override
	public void setTime(List<Long> time) {
		this.time = time;
	}
	
	@Override
	public void addTime(Long time){
		
		this.time.add(time);	
	}
}
