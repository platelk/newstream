package com.platelkevin.newsstream.newsstream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.platelkevin.newsstream.core.News;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class OneNewsActivity extends AppCompatActivity {

    private News mNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mNews = intent.getExtras().getParcelable(MainActivity.ARG_NEWS);
        }
        setContentView(R.layout.activity_one_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getText(R.string.share_action_title));
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_pre_text) + mNews.getUrl());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initOneNewsView();
        initButtonView();
    }

    private void initOneNewsView() {
        TextView from = (TextView)findViewById(R.id.from);
        TextView date = (TextView)findViewById(R.id.date);
        TextView author = (TextView)findViewById(R.id.author);
        ImageView img = (ImageView)findViewById(R.id.imgUrl);
        TextView content = (TextView)findViewById(R.id.content);
        TextView title = (TextView)findViewById(R.id.title);

        if (mNews == null)
            return;
        if (mNews.getParent() != null)
            from.setText(mNews.getParent().getTitle());
        date.setText(mNews.getDate().toString());
        author.setText(mNews.getAuthor());
        Picasso.with(this).load(mNews.getImg()).into(img);
        title.setText(mNews.getTitle());
        content.setText(mNews.getContent());
    }

    private void initButtonView() {
        Button seeMore = (Button)findViewById(R.id.see_more);
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNews.getUrl()));
                startActivity(browserIntent);
            }
        });
    }
}
