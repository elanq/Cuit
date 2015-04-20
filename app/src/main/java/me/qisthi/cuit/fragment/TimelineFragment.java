package me.qisthi.cuit.fragment;/*
The MIT License (MIT)

Copyright (c) 2015 elan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.qisthi.cuit.R;
import me.qisthi.cuit.activity.TimelineActivity;
import me.qisthi.cuit.adapter.TweetAdapter;
import me.qisthi.cuit.helper.StorageHelper;
import me.qisthi.cuit.helper.TwitterHelper;
import twitter4j.Status;


public class TimelineFragment extends Fragment{

    private List<String[]> tweetsTimeline = new ArrayList<>();
    private TweetAdapter tweetAdapter;
    private SwipeRefreshLayout refreshLayout;

    private int timelineAction = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        timelineAction = getArguments().getInt(TimelineActivity.TIMELINE_ACTION);
        View v = inflater.inflate(R.layout.timeline_list_layout, container,false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.timelineListLayout);
        refreshLayout= (SwipeRefreshLayout) v.findViewById(R.id.refreshTweet);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //check cache stored in tweets timeline

        tweetAdapter = new TweetAdapter(getActivity(), tweetsTimeline);
        recyclerView.setAdapter(tweetAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (timelineAction)
                {
                    case TimelineActivity.HOME_TIMELINE_ACTION_VALUE:
                        new TwitterHelper.LoadHomeTimeline(getActivity(), tweetAdapter, tweetsTimeline, TwitterHelper.LOAD_MORE_HOME_TIMELINE, refreshLayout).execute();
                        break;
                    case TimelineActivity.MENTION_TIMELINE_ACTION_VALUE:
                        new TwitterHelper.LoadHomeTimeline(getActivity(), tweetAdapter, tweetsTimeline, TwitterHelper.LOAD_MORE_USER_MENTION, refreshLayout).execute();
                        break;
                    default:
                        break;
                }

            }
        });
        try {
            switch (timelineAction)
            {
                case TimelineActivity.HOME_TIMELINE_ACTION_VALUE:
                    new TwitterHelper.LoadHomeTimeline(getActivity(), tweetAdapter, tweetsTimeline, TwitterHelper.LOAD_HOME_TIMELINE, refreshLayout).execute();
                    break;
                case TimelineActivity.MENTION_TIMELINE_ACTION_VALUE:
                    new TwitterHelper.LoadHomeTimeline(getActivity(), tweetAdapter, tweetsTimeline, TwitterHelper.LOAD_USER_MENTION, refreshLayout).execute();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }



}
