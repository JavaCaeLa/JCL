package fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcl.jcl_examples.R;

import java.io.OutputStream;
import java.io.PrintStream;

public class FragmentRun extends Fragment {

    private static String terminal = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);
        PrintStream origOut = System.out;
        PrintStream origErr = System.err;
        PrintStream interceptor = new Interceptor(origOut);
        PrintStream interceptor2 = new Interceptor(origErr);
        System.setOut(interceptor);
        System.setErr(interceptor2);


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        TextView tx = (TextView) v.findViewById(R.id.txList);
        tx.setMovementMethod(new ScrollingMovementMethod());

    }

    public class Interceptor extends PrintStream {
        public Interceptor(OutputStream out) {
            super(out, true);
        }

        @Override
        public void print(final String s) {//do what ever you like
            super.print(s);
            terminal+=s;

                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TextView tx = (TextView) getView().findViewById(R.id.txList);
                                    tx.setText(terminal);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Error Print", e.getMessage());
                }
            }
    }

}
