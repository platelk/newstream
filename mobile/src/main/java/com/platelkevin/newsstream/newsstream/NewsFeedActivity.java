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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.platelkevin.newsstream.core.News;
import com.platelkevin.newsstream.core.NewsFeed;

import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements NewsFeedFragment.OnListFragmentInteractionListener {
    private boolean mBound = false;
    private MainService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    List<News> r = nf.fetchAllNews(NewsFeedActivity.this);
                    if (r == null) {
                        mService.addNewsFeeds(nf);
                        Toast t = Toast.makeText(NewsFeedActivity.this.getApplicationContext(),
                                NewsFeedActivity.this.getResources().getText(R.string.toast_news_feed_add_error).toString(),
                                Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        mService.addNewsFeeds(nf);
                        Toast t = Toast.makeText(NewsFeedActivity.this.getApplicationContext(),
                                NewsFeedActivity.this.getResources().getText(R.string.toast_news_feed_added).toString(),
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                    url.setText("");
                    initNewsFeeds();
                }
            }
        });
    }

    protected void initNewsFeeds() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.fragment);
        recyclerView.setAdapter(new MyNewsFeedRecyclerViewAdapter(mService.getNewsFeeds(), new NewsFeedFragment.OnListFragmentInteractionListener() {

            @Override
            public void onListFragmentInteraction(NewsFeed item) {

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
}