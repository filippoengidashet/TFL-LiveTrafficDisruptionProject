package tae.co.uk.ltd.mvp.model.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.view.fragments.Tab;

/**
 * Created by Filippo-TheAppExpert on 7/23/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_OF_TABS = 6;
    private String mTitles[] = {Constants.CategoryType.HAZARD, Constants.CategoryType.INFRASTRUCTURE, Constants.CategoryType.SPECIAL_EVENT
            , Constants.CategoryType.TRAFFIC_INCIDENT, Constants.CategoryType.TRAFFIC_VOLUME, Constants.CategoryType.WORK};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment view = null;

        switch (position) {
            case 0:
                view = Tab.getTab(Constants.CategoryType.HAZARD, position);
                break;
            case 1:
                view = Tab.getTab(Constants.CategoryType.INFRASTRUCTURE, position);
                break;
            case 2:
                view = Tab.getTab(Constants.CategoryType.SPECIAL_EVENT, position);
                break;
            case 3:
                view = Tab.getTab(Constants.CategoryType.TRAFFIC_INCIDENT, position);
                break;
            case 4:
                view = Tab.getTab(Constants.CategoryType.TRAFFIC_VOLUME, position);
                break;
            case 5:
                view = Tab.getTab(Constants.CategoryType.WORK, position);
                break;
            default:
                new LiveTrafficDisruptionException("Error position invalid!");
        }
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return NUM_OF_TABS;
    }

    public void setPageTitle(String newTitle, int position) {
        mTitles[position] = mTitles[position] + " " + newTitle;
        notifyDataSetChanged();
    }

    public String[] getTitles() {
        return mTitles;
    }
}

