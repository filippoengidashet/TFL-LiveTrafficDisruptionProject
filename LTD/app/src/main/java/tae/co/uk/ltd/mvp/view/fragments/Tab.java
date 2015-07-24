package tae.co.uk.ltd.mvp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tae.co.uk.ltd.R;
import tae.co.uk.ltd.application.TrafficDisruptionApplication;
import tae.co.uk.ltd.mvp.model.adapter.DisruptionAdapter;
import tae.co.uk.ltd.mvp.model.constants.Constants;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.view.activity.MainActivity;

/**
 * Created by Filippo-TheAppExpert on 7/23/2015.
 */
public class Tab extends Fragment implements TrafficDisruptionApplication.DisruptionListener, MainActivity.FragmentListener {

    private static final String TAG = Tab.class.getSimpleName();
    private List<Disruption> mDisruptionList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DisruptionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTotal;
    private String mTabName;
    private int mTabPosition;
    private int mItemCount;

    public static Tab getTab(String tabName, int position) {
        Tab tab = new Tab();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TAB_NAME_EXTRA, tabName);
        bundle.putInt(Constants.TAB_POSITION_EXTRA, position);
        tab.setArguments(bundle);
        return tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_layout, container, false);

        Bundle arguments = getArguments();

        mTabName = arguments.getString(Constants.TAB_NAME_EXTRA);
        mTabPosition = arguments.getInt(Constants.TAB_POSITION_EXTRA);

        attachToParent();
        getMainActivity().setActiveTab(mTabName);
        configViews(view);
        setRetainInstance(true);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            getMainActivity().registerListener(Tab.this);
            mDisruptionList = ((TrafficDisruptionApplication) getActivity().getApplicationContext()).getDisruptionList(mTabName);
        }
    }

    private void configViews(View view) {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mTotal = (TextView) view.findViewById(R.id.total);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDisruptionList = ((TrafficDisruptionApplication) getActivity().getApplicationContext()).getDisruptionList(mTabName);

        mItemCount = mDisruptionList.size();

        getMainActivity().changeTabTitle("(" + mItemCount + ")", mTabPosition);
        mAdapter = new DisruptionAdapter(getActivity(), mDisruptionList);
        mRecyclerView.setAdapter(mAdapter);
        mTotal.setText(Integer.toString(mItemCount));
    }

    private void attachToParent() {
        ((TrafficDisruptionApplication) getActivity().getApplicationContext()).addListener(Tab.this);
    }

    @Override
    public void onDisruptionChanged(Disruption disruption) {
        Log.d(TAG, "New Disruption found! ID " + disruption.mId);

        if (mTabName.equals(disruption.mCategory)) {
            mItemCount++;
            mTotal.setText(Integer.toString(mItemCount));
            mDisruptionList.add(disruption);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStatusChanged(TrafficDisruptionApplication.FetchStatus status) {

    }

    @Override
    public void onFetchFailed(LiveTrafficDisruptionException exception) {

    }

    @Override
    public void onTextTyped(String text) {
        int itemCount = mAdapter.getItemCount();
        mAdapter.getFilter().filter(text);
        mTotal.setText(Integer.toString(itemCount));
        Log.d(TAG, "NOW SEARCH TEXT IS :: " + text + " Search Result is :: " + itemCount + " From :: " + mTabName);
    }

    @Override
    public void onRefreshList() {
        mDisruptionList.clear();
        mAdapter.notifyDataSetChanged();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}