package com.platelkevin.newsstream.newsstream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.platelkevin.newsstream.core.NewsFeed;

import java.io.FileDescriptor;
import java.util.LinkedList;
import java.util.List;

public class MainService extends Service {

    final MainServiceBinder mBinder = new MainServiceBinder();
    List<NewsFeed> newsFeeds;

    public MainService() {
        newsFeeds = new LinkedList<>();
    }

    @Override
    public void onCreate() {
        //newsFeeds = NewsFeed.listAll(NewsFeed.class);

        SugarContext.init(this);

        NewsFeed.findById(NewsFeed.class, (long) 1);
        if (NewsFeed.count(NewsFeed.class) == 0) {
            NewsFeed nf = new NewsFeed();
            nf.setUrl("http://feeds2.feedburner.com/LeJournalduGeek");
            newsFeeds.add(nf);
            nf.save();

            nf = new NewsFeed();
            nf.setUrl("http://www.developpez.com/index/rss");
            newsFeeds.add(nf);
            nf.save();
        } else {

            newsFeeds = NewsFeed.listAll(NewsFeed.class);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {

    }

    public void addNewsFeeds(NewsFeed nf) {
        newsFeeds.add(nf);
        nf.save();
    }


    public void removeNewsFeeds(NewsFeed nf) {
        newsFeeds.remove(nf);
        nf.delete();
    }


    public List<NewsFeed> getNewsFeeds() {
        return newsFeeds;
    }

    public void setNewsFeeds(List<NewsFeed> newsFeeds) {
        this.newsFeeds = newsFeeds;
    }


    public class MainServiceBinder extends Binder {
        MainService getService() {
            // Return this instance of MainService so clients can call public methods
            return MainService.this;
        }
    }
}
