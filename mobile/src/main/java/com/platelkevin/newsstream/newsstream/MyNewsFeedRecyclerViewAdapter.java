package com.platelkevin.newsstream.newsstream;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.platelkevin.newsstream.core.NewsFeed;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NewsFeed} and makes a call to the
 * specified.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsFeedRecyclerViewAdapter.ViewHolder> {

    private final List<NewsFeed> mValues;
    private final NewsFeedFragment.OnListFragmentInteractionListener mListener;

    public MyNewsFeedRecyclerViewAdapter(List<NewsFeed> items, NewsFeedFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newsfeed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getUrl());
        holder.mMainColor.setBackgroundColor(holder.mView.getResources().getColor(mValues.get(position).getColor()));

        ImageButton btn = (ImageButton)holder.mView.findViewById(R.id.deleteBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteListFragmentInteraction(holder.mItem);
            }
        });

        btn = (ImageButton)holder.mView.findViewById(R.id.shareBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShareListFragmentInteraction(holder.mItem);
            }
        });

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mMainColor;
        public NewsFeed mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.url);
            mMainColor = (TextView) view.findViewById(R.id.newsFeedColor);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
