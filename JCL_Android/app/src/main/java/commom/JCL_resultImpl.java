package commom;

import java.util.ArrayList;
import java.util.List;

import interfaces.kernel.JCL_result;
import io.protostuff.Tag;

public class JCL_resultImpl implements JCL_result {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Tag(1)
    private List<Long> time;
    @Tag(2)
    private Object result;
    @Tag(3)
    private Exception error;
    @Tag(4)
    private long memorysize;


    public JCL_resultImpl() {
        this.result = null;
        this.error = null;

    }

    public JCL_resultImpl(Object result, Exception error) {
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
    public void addTime(Long time) {

        this.time.add(time);
    }
    @Override
    public long getMemorysize() {
        return memorysize;
    }
    @Override
    public void setMemorysize(long memorysize) {
        this.memorysize = memorysize;
    }
}
