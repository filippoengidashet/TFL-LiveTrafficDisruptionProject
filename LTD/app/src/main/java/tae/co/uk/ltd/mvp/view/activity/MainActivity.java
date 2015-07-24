package tae.co.uk.ltd.mvp.view.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tae.co.uk.ltd.R;
import tae.co.uk.ltd.application.TrafficDisruptionApplication;
import tae.co.uk.ltd.mvp.controller.Controller;
import tae.co.uk.ltd.mvp.model.adapter.DisruptionAdapter;
import tae.co.uk.ltd.mvp.model.adapter.ViewPagerAdapter;
import tae.co.uk.ltd.mvp.model.exception.LiveTrafficDisruptionException;
import tae.co.uk.ltd.mvp.model.pojo.Disruption;
import tae.co.uk.ltd.mvp.view.fragments.Tab;
import tae.co.uk.ltd.mvp.view.widget.SlidingTabLayout;


public class MainActivity extends AppCompatActivity implements TrafficDisruptionApplication.DisruptionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FloatingActionButton mFloatingActionButton;
    private Toolbar mToolbar;
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;

    private List<FragmentListener> mListeners = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private String mActiveTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configToolbar();
        configFloatingButton();
        configProgressDialog();

        configPager();
        configSlidingTabLayout();

        attachToParent();
    }

    private void configProgressDialog() {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Getting XML feed..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    private void configFloatingButton() {
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingBtn);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });
    }

    private void configSlidingTabLayout() {
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {

                int color;

                switch (position) {
                    case 0:
                        color = getResources().getColor(R.color.moderate);
                        break;
                    case 1:
                        color = getResources().getColor(R.color.low);
                        break;
                    case 2:
                        color = getResources().getColor(R.color.high);
                        break;
                    case 3:
                        color = getResources().getColor(R.color.colorAccent);
                        break;
                    case 4:
                        color = getResources().getColor(R.color.colorPrimary_two);
                        break;
                    default:
                        color = getResources().getColor(R.color.tabsScrollColor);
                }
                return color;
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.gray);
            }
        });
    }

    private void configPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void attachToParent() {
        ((TrafficDisruptionApplication) getApplicationContext()).addListener(MainActivity.this);
    }

    private void configToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mCollapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle("Disruption List");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                notifySearchTextTyped(newText.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                notifySearchTextTyped(query.toLowerCase());
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return true;
    }

    public void setActiveTab(String activeTab) {
        mActiveTab = activeTab;
    }

    private void notifySearchTextTyped(String newText) {
        for (FragmentListener listener : mListeners) {
            if (listener != null) {
                listener.onTextTyped(newText);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_map) {
            openMap();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openMap() {
        startActivity(new Intent(MainActivity.this, MapActivity.class));
    }

    private void refreshList() {
        for (FragmentListener listener : mListeners) {
            if (listener != null) {
                listener.onRefreshList();
            }
        }
        Toast.makeText(getApplicationContext(), "Refreshing List", Toast.LENGTH_SHORT).show();
        ((TrafficDisruptionApplication) getApplicationContext()).restartFetching();
    }

    public void changeTabTitle(String title, int position) {

//        mPagerAdapter.setPageTitle(title, position);
//        mPagerAdapter.notifyDataSetChanged();
//        mViewPager.invalidate();
        /**
         * It is possible to change the title here
         */
    }

    @Override
    public void onDisruptionChanged(Disruption disruption) {
//        mDisruptionList.add(disruption);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(TrafficDisruptionApplication.FetchStatus status) {
        switch (status) {
            case RUNNING:
                showProgressDialog();
                break;
            case STOPPED:
                hideProgressDialog();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Invalid Status!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onFetchFailed(LiveTrafficDisruptionException exception) {
        hideProgressDialog();
        Toast.makeText(getApplicationContext(), "" + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        mProgressDialog.show();
    }

    public void registerListener(FragmentListener listener) {
        mListeners.add(listener);
    }

    public interface FragmentListener {

        void onTextTyped(String text);

        void onRefreshList();
    }
}
