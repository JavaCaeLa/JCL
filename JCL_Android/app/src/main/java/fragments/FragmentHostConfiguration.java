package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hpc.jcl_android.JCL_ANDROID_Facade;
import com.hpc.jcl_android.ListViewConfAdapter;
import com.hpc.jcl_android.ListViewSensorAdapter;
import com.hpc.jcl_android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import implementations.util.android.AndroidSensor;
import sensor.JCL_Sensor;


public class FragmentHostConfiguration extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_host_configuration, container,
				false);
		

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		Vector<String> vecConfiguration = new Vector<>();
		Map<Integer, String> mapConf = new HashMap<>();

		vecConfiguration.add("SERVERIP");
		vecConfiguration.add("SERVERPORT");
		vecConfiguration.add("PORT");
		vecConfiguration.add("BROKERAD");
		vecConfiguration.add("BROKERPORT");
		vecConfiguration.add("DEVICE_TYPE");
		vecConfiguration.add("DEVICE_ID");
		vecConfiguration.add("STANDBY");
		vecConfiguration.add("ENCRYPTION");
		vecConfiguration.add("DEVICE_OS");
		vecConfiguration.add("DEVICE_RAM");
		vecConfiguration.add("CORE(S)");
		vecConfiguration.add("DEVICE_STORAGE_CAPACITY");
		vecConfiguration.add("DEVICE_PLATFORM");


		JCL_ANDROID_Facade jcl = JCL_ANDROID_Facade.getInstance();
        Properties properties = jcl.getConfigurationProperties(getContext());
		List<String[]> conf = new ArrayList<>();
		int i=0;
		for (String c: vecConfiguration){
			conf.add(new String[]{c+" = "+ properties.getProperty(c).trim()});
			mapConf.put(i++, c);
		}
		ListView list = (ListView) getView().findViewById(R.id.lvConf);
		ListViewConfAdapter confAdapter = new ListViewConfAdapter(getContext(), conf,mapConf);
		//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_list, conf);
        //ListViewSensorAdapter adapter = new ListViewSensorAdapter(getContext(),sensors);
		list.setAdapter(confAdapter);
	}



}
