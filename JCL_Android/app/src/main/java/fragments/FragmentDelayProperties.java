package fragments;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
import com.hpc.jcl_android.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class FragmentDelayProperties extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_delay_properties, container,
				false);
		

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		EditText et = (EditText) getView().findViewById(R.id.edtDelayProperties);
		JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
		et.setText(jcl.recoverStringProperties("delay"));

	}



}
