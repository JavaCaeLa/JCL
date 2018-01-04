package fragments;

import com.hpc.jcl_android.R;
import com.hpc.jcl_android.ViewPagerAdapter;

import tabs.SlidingTabLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment1 extends Fragment {

	public Fragment1() {
		super();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

		ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);

		viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(),
				getActivity()));
		
		viewPager.setCurrentItem(0);

		// Give the SlidingTabLayout the ViewPager
		SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view
				.findViewById(R.id.sliding_tabs);
		// Center the tabs in the layout
		slidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);

		slidingTabLayout.setDistributeEvenly(true);

		slidingTabLayout.setViewPager(viewPager);
		
		return view;
	}	
		
	

}
