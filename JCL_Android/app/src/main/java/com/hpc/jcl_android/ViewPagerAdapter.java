package com.hpc.jcl_android;

import fragments.FragmentCompatibility;
import fragments.FragmentHostConfiguration;
import fragments.FragmentParticipationProperties;
import fragments.FragmentDelayProperties;
import fragments.FragmentConfigurationProperties;
import fragments.FragmentSensorConfiguration;
import fragments.FragmentSizeProperties;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages

    final int PAGE_COUNT = 3;
    private String titles[] = new String[]{"Compatibility", "Sensors configuration", "Host Configuration"};
    //Context c;


    public ViewPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentCompatibility fragmenttab1 = new FragmentCompatibility();
                return fragmenttab1;
//            case 1:
//                FragmentParticipationProperties fragmenttab2 = new FragmentParticipationProperties();
//                return fragmenttab2;
//            case 2:
//                FragmentDelayProperties fragmenttab3 = new FragmentDelayProperties();
//                return fragmenttab3;
//            case 3:
//                FragmentConfigurationProperties fragmenttab4 = new FragmentConfigurationProperties();
//                return fragmenttab4;
            case 1:
                FragmentSensorConfiguration fragmenttab5 = new FragmentSensorConfiguration();
                return fragmenttab5;
//            case 2:
//                FragmentConfigurationProperties fragmenttab4 = new FragmentConfigurationProperties();
//                return fragmenttab4;
            case 2:
                FragmentHostConfiguration fragmenttab6 = new FragmentHostConfiguration();
                return fragmenttab6;
//            case 2:
//                FragmentSizeProperties fragmenttab6 = new FragmentSizeProperties();
//                return fragmenttab6;

        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}