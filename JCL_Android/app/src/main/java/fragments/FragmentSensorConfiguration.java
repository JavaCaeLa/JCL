package fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
import com.hpc.jcl_android.ListViewSensorAdapter;
import com.hpc.jcl_android.MainActivity;
import com.hpc.jcl_android.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import implementations.util.android.AndroidSensor;
import sensor.JCL_Sensor;


public class FragmentSensorConfiguration extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sensors, container,
				false);
		

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        Properties properties = jcl.getParticipationProperties(getContext());
        Properties delayProperties = jcl.getDelayProperties(getContext());
        Properties sizeProperties = jcl.getSizeProperties(getContext());
		List<AndroidSensor> sensors = new ArrayList<>();
		Map<Integer, Integer> mapSensors = new HashMap<>();
		for (int i=0; i<jcl.getSensors().size(); i++){
			mapSensors.put(i, sensors.size());
			if (jcl.getCompatibleSensorVec().get(i)) {
				try {
					String pos = jcl.getSensors().get(i).name;
					String delay = delayProperties.getProperty(pos).trim();
					String size = sizeProperties.getProperty(pos).trim();
					String part = properties.getProperty(pos).trim();
					String timeAu = null;
					if (i == JCL_Sensor.TypeSensor.TYPE_AUDIO.id)
						timeAu = delayProperties.getProperty("TIME_AUDIO").trim();
					sensors.add(new AndroidSensor(i, pos, size, part, delay, timeAu));
				}catch (Exception e){
					e.printStackTrace();
					jcl.deleteRecursive(new File(Environment.getExternalStorageDirectory().toString() + "/jclAndroid/"));

					properties = jcl.getParticipationProperties(getContext());
					delayProperties = jcl.getDelayProperties(getContext());
					sizeProperties = jcl.getSizeProperties(getContext());

					i=0;
					sensors.clear();
					mapSensors.clear();
				}
            }
		}

		ListView list = (ListView) getView().findViewById(R.id.lvSensors);
        ListViewSensorAdapter adapter = new ListViewSensorAdapter(getContext(),sensors, mapSensors);
		list.setAdapter(adapter);

		//EditText et = (EditText) getView().findViewById(R.id.edtDelayProperties);
		//JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
		//et.setText(jcl.recoverStringProperties("delay"));

	}



}
