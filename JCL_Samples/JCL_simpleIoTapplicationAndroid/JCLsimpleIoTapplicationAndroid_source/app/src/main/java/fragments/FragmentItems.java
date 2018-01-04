package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jcl.jcl_androidapplication.ListViewItemsAdapter;
import com.jcl.jcl_androidapplication.R;

import java.util.List;
import java.util.Map;

import interfaces.kernel.JCL_Sensor;
import interfaces.kernel.datatype.Sensor;


public class FragmentItems extends Fragment {
	private List<Object> items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_items, container,
				false);
		

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		ListView list = (ListView) getView().findViewById(R.id.lvItems);
		ListViewItemsAdapter confAdapter = new ListViewItemsAdapter(getContext(), items);
		list.setAdapter(confAdapter);
	}


	public List<Object> getItems() {
		return items;
	}

	public void setItems(List<Object> items) {
		this.items = items;
	}

}
