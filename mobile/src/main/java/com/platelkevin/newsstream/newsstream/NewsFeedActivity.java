package com.platelkevin.newsstream.newsstream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.platelkevin.newsstream.core.News;
import com.platelkevin.newsstream.core.NewsFeed;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class NewsFeedActivity extends AppCompatActivity implements NewsFeedFragment.OnListFragmentInteractionListener {
    private static final String TAG = "NewsFeedActivity";
    private boolean mBound = false;
    private MainService mService;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Fabric.with(this, new Answers(), new Crashlytics());

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initAddNewsFeeds();
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

    protected void initAddNewsFeeds() {
        Button addBtn = (Button)findViewById(R.id.addUrl);
        final EditText url = (EditText)findViewById(R.id.editText);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    NewsFeed nf = new NewsFeed();
                    nf.setUrl(url.getText().toString());
                    new AddNewsFeedStream().execute(nf);
                }
            }
        });
    }

    protected void initNewsFeeds() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.fragment);
        recyclerView.setAdapter(new MyNewsFeedRecyclerViewAdapter(mService.getNewsFeeds(), new NewsFeedFragment.OnListFragmentInteractionListener() {

            @Override
            public void onListFragmentInteraction(NewsFeed item) {
                NewsFeedActivity.this.onListFragmentInteraction(item);
            }

            @Override
            public void onDeleteListFragmentInteraction(NewsFeed item) {
                NewsFeedActivity.this.onDeleteListFragmentInteraction(item);
            }

            @Override
            public void onShareListFragmentInteraction(NewsFeed item) {
                NewsFeedActivity.this.onShareListFragmentInteraction(item);
            }
        }));
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

    @Override
    public void onListFragmentInteraction(NewsFeed item) {

    }

    @Override
    public void onDeleteListFragmentInteraction(final NewsFeed item) {
        if (mService == null)
            return;
        mService.removeNewsFeeds(item);

        Snackbar
                .make(findViewById(R.id.newsFeedContainer), R.string.snackbar_delete, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mService.addNewsFeeds(item);
                    }
                })
                .show(); // Don’t forget to show!
    }

    @Override
    public void onShareListFragmentInteraction(NewsFeed item) {

    }

    class AddNewsFeedStream extends AsyncTask<NewsFeed, Void, List<News>> {

        @Override
        protected List<News> doInBackground(NewsFeed... newsFeeds) {
            Log.d(NewsFeedActivity.TAG, "Starting background process....");
            NewsFeed nf = newsFeeds[0];
            List<News> r = nf.fetchAllNews(NewsFeedActivity.this);
            if (r != null)
                mService.addNewsFeeds(nf);
            return r;
        }

        protected void onPostExecute(List<News> r) {
            Log.d(NewsFeedActivity.TAG, "ON POST EXECUTE");
            if (r == null) {
                Toast t = Toast.makeText(NewsFeedActivity.this.getApplicationContext(),
                        NewsFeedActivity.this.getResources().getText(R.string.toast_news_feed_add_error).toString(),
                        Toast.LENGTH_SHORT);
                t.show();
            } else {
                Toast t = Toast.makeText(NewsFeedActivity.this.getApplicationContext(),
                        NewsFeedActivity.this.getResources().getText(R.string.toast_news_feed_added).toString(),
                        Toast.LENGTH_SHORT);
                t.show();
            }
            final EditText url = (EditText)findViewById(R.id.editText);
            url.setText("");
            initNewsFeeds();
        }
    }
}
