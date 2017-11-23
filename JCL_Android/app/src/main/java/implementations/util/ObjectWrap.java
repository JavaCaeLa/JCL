package implementations.util;

import io.protostuff.Tag;

public class ObjectWrap {
    @Tag(3)
    private Object obj;

    public ObjectWrap(){}
    public ObjectWrap(Object obj){
        this.obj = obj;
    }
    public Object getobj(){
        return obj;
    }
}