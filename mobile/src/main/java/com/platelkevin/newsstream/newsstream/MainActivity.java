package com.platelkevin.newsstream.newsstream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.orm.SugarContext;
import com.platelkevin.newsstream.core.News;
import com.platelkevin.newsstream.core.NewsFeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * MainActivity of the application.
 * This activity will display the last news of every news feeds
 * with a drawer to add, delete, manage the news feeds
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String ARG_NEWS = "ARG_NEWS";
    private boolean mBound = false;
    private MainService mService;
    private final MyNewsRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    MainActivity() {
        super();

        mAdapter = new MyNewsRecyclerViewAdapter(new LinkedList<News>(), new NewsFragment.OnNewsListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(News item) {
                Intent intent = new Intent(MainActivity.this, OneNewsActivity.class);
                intent.putExtra(ARG_NEWS, item);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SugarContext.init(this);
        // Start fabric/crashlytics
        Fabric.with(this, new Crashlytics());

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createFabButton();
        createDrawer(toolbar);
        setUpListView();
    }

    protected void onTerminate() {
        SugarContext.terminate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MainService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Create the Fab button
     */
    private void createFabButton() {
        // Create fab-button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, NewsFeedActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Create the drawer menu
     * @param toolbar
     */
    private void createDrawer(Toolbar toolbar) {
        // Create drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpListView() {
        RecyclerView lv = (RecyclerView) findViewById(R.id.listView);

        if (getResources().getBoolean(R.bool.isTablet)) {
            GridLayoutManager g = new GridLayoutManager(this, 2);
            lv.setLayoutManager(g);
        } else {
            LinearLayoutManager g = new LinearLayoutManager(this);
            lv.setLayoutManager(g);
        }
        Log.d(TAG, "Setup list view...");

        updateRecycleView(mAdapter);
    }

    public void updateRecycleView(MyNewsRecyclerViewAdapter adapter) {
        RecyclerView lv = (RecyclerView) findViewById(R.id.listView);

        lv.setAdapter(adapter);
    }

    /**
     * On back, if the drawer is open then close the drawer,
     * otherwise do the normal back action
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_list) {

        } else if (id == R.id.nav_add_content) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNewsFeeds() {
        for (NewsFeed nf : mService.getNewsFeeds()) {
            new FeedNewsStream().execute(nf);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainService.MainServiceBinder binder = (MainService.MainServiceBinder)service;
            mService = binder.getService();
            mBound = true;
            initNewsFeeds();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    class FeedNewsStream extends AsyncTask<NewsFeed, Void, List<News>> {

        @Override
        protected List<News> doInBackground(NewsFeed... newsFeeds) {
            Log.d(MainActivity.TAG, "Starting background process....");
            List<News> news = new LinkedList<>();
            int count = newsFeeds.length;
            for (int i = 0; i < count; i++) {
                NewsFeed n = newsFeeds[i];
                List<News> ln = n.fetchAllNews(MainActivity.this);
                if (ln != null)
                    news.addAll(ln);
            }

            return news;
        }

        protected void onPostExecute(List<News> news) {
            Log.d(MainActivity.TAG, "ON POST EXECUTE");
            for (News n : news) {
                if (!MainActivity.this.mAdapter.getmValues().contains(n)) {
                    Log.d(TAG, "Is not containt : " + n.getTitle());
                    MainActivity.this.mAdapter.getmValues().add(n);
                }
            }
            Collections.sort(mAdapter.getmValues());
            MainActivity.this.updateRecycleView(mAdapter);
        }
    }
}
