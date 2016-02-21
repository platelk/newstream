package com.platelkevin.newsstream.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.orm.SugarRecord;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by kevin on 09/02/2016.
 */
public class News extends SugarRecord implements Parcelable, Comparable<News> {
    public final int PREVIEW_SIZE = 200;
    String      url;
    String      title;
    String      content;
    String      img;
    String      author = "";
    NewsFeed    parent;

    Date date;

    public News() {}

    public News(String title, String content, String img, String url) {
        this.title = title;
        this.content = content;
        this.img = img;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public String getContentPreview() {
        int length = content.length();
        String preview = content;
        if (length > PREVIEW_SIZE)
            preview = content.substring(0, PREVIEW_SIZE);
        if (preview.length() < length)
            preview += " [...]";

        return preview;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public NewsFeed getParent() {
        return parent;
    }

    public void setParent(NewsFeed parent) {
        this.parent = parent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(java.util.Date publicationDate) {
        date = new Date(publicationDate.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(img);
        dest.writeLong(date.getTime());
        dest.writeString(author);
        dest.writeParcelable(parent, flags);
    }

    public static final Parcelable.Creator<News> CREATOR
            = new Parcelable.Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };

    private News(Parcel in) {
        url = in.readString();
        title = in.readString();
        content = in.readString();
        img = in.readString();
        date = new Date(in.readLong());
        author = in.readString();
        parent = in.readParcelable(NewsFeed.class.getClassLoader());
    }

    public int hashCode() {
        return (int)this.date.getTime();
    }

    @Override
    public int compareTo(News another) {
        int r = (int)(another.date.getTime() - this.date.getTime());
        if (r == 0)
            return this.title.compareTo(another.title);
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;

        News news = (News) o;

        if (!title.equals(news.title)) return false;
        if (author != null && !author.equals(news.author)) return false;
        return date.equals(news.date);

    }
}
