package com.platelkevin.newsstream.newsstream;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.platelkevin.newsstream.core.News;
import com.platelkevin.newsstream.newsstream.NewsFragment.OnNewsListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link News} and makes a call to the
 * specified {@link OnNewsListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private List<News> mValues;
    private final OnNewsListFragmentInteractionListener mListener;

    public MyNewsRecyclerViewAdapter(List<News> items, OnNewsListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());


        holder.mContentView.setText(mValues.get(position).getContentPreview());
        holder.mDateView.setText(mValues.get(position).getDate().toString());
        if (mValues.get(position).getParent() != null) {
            holder.mFromView.setText(mValues.get(position).getParent().getTitle());
            holder.mColorBand.setBackgroundColor(holder.mView.getResources().getColor(mValues.get(position).getParent().getColor()));
        }
        Log.d(TAG, "Img url ? :" + holder.mItem.getImg());
        Picasso.with(holder.mContext).load(holder.mItem.getImg()).into(holder.mImg);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public List<News> getmValues() {
        return mValues;
    }

    public void setmValues(List<News> mValues) {
        this.mValues = mValues;
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDateView;
        public final ImageView mImg;
        public final Context mContext;
        public final TextView mFromView;
        public final TextView mColorBand;
        public News mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.title);
            mDateView = (TextView) view.findViewById(R.id.date);
            mFromView = (TextView) view.findViewById(R.id.from);
            mColorBand = (TextView) view.findViewById(R.id.from);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImg = (ImageView)view.findViewById(R.id.imgUrl);
            mContext = view.getContext();
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
