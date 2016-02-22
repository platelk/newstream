package com.platelkevin.newsstream.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.platelkevin.newsstream.newsstream.R;
import com.squareup.picasso.Picasso;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by kevin on 09/02/2016.
 */
public class NewsFeed extends SugarRecord implements Parcelable {
    static final String TAG = "NewsFeed";
    static int randomColorProgress = 0;
    static Integer[] colors = {
            R.color.backgroundTitleDeepPurple500,
            R.color.backgroundTitleTeel500,
            R.color.backgroundTitleRed500,
            R.color.backgroundTitleGreen500,
            R.color.backgroundTitleDeepOrange500,
            R.color.backgroundTitleIndigo500,
            R.color.backgroundTitlePurple500,
            R.color.backgroundTitleTeel800,
            R.color.backgroundTitleRed800,
            R.color.backgroundTitleGreen800,
            R.color.backgroundTitleDeepOrange800,
            R.color.backgroundTitleDeepPurple800,
            R.color.backgroundTitleIndigo800,
            R.color.backgroundTitlePurple800
    };
    static Map<String, Integer> assignateColor = new HashMap<>();

    String  url;
    String  title;
    String  img;
    Time    lastUpdate;
    int     color;

    @Ignore
    List<News> news = new LinkedList<>();

    public NewsFeed() {
    }

    public List<News> fetchAllNews(final Context c) {
        InputStream inputStream = null;
        try {
            inputStream = (new URL(this.url)).openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null)
            return null;
        Feed feed = null;
        try {
            feed = EarlParser.parseOrThrow(inputStream, 0);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (DataFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (feed == null)
            return news;
        news.clear();
        Log.i(TAG, "Processing feed: " + feed.getTitle() + " - " + feed.getImageLink());
        this.title = feed.getTitle();
        this.img = feed.getImageLink();
        for (Item item : feed.getItems()) {
            final News n = new News();

            n.setTitle(item.getTitle());
            n.setImg(item.getImageLink());
            n.setUrl(item.getLink());
            n.setAuthor(item.getAuthor());
            n.setDate(item.getPublicationDate());
            n.setParent(this);

            Spanned h = Html.fromHtml(item.getDescription(), new Html.ImageGetter() {

                @Override
                public Drawable getDrawable(String source) {
                    Log.d(TAG, "Found img tag : " + source);
                    if (n.getImg() == null)
                        n.setImg(source);
                    Drawable d = null;
                    try {
                        d = new BitmapDrawable(Picasso.with(c).load(source).get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return d;
                }
            }, new Html.TagHandler() {

                @Override
                public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

                }
            });
            n.setContent(h.toString());


            String title = item.getTitle();
            Log.i(TAG, "Item title: " + (title == null ? "N/A" : title) + " - " + (n.getImg() == null ? "N/A" : n.getImg()));
            if (!news.contains(n))
                news.add(n);
        }
        return news;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Time getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Time lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        if (assignateColor.get(url) == null) {
            this.color = colors[randomColorProgress];
            randomColorProgress = (randomColorProgress + 1) % colors.length;
            assignateColor.put(this.url, this.color);
        } else {
            this.color = assignateColor.get(url);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(img);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<NewsFeed> CREATOR
            = new Parcelable.Creator<NewsFeed>() {
        public NewsFeed createFromParcel(Parcel in) {
            return new NewsFeed(in);
        }

        public NewsFeed[] newArray(int size) {
            return new NewsFeed[size];
        }
    };


    private NewsFeed(Parcel in) {
        url = in.readString();
        title = in.readString();
        img = in.readString();
        color = in.readInt();
    }
}
