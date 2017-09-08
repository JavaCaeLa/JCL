package implementations.util;

import android.app.Activity;
import android.content.Context;

/**
 * Created by estevao on 22/11/16.
 */
public final class JCL_ApplicationContext {
    private static Context context;


    public static Context getContext() {
        return context;
    }

    public static Activity getActivity(){
        return (Activity) context;
    }

    public static void setContext(Activity context) {
        JCL_ApplicationContext.context = context;
    }
}

