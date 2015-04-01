package me.qisthi.cuit.helper;/*
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

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nispok.snackbar.Snackbar;

import java.util.List;

import me.qisthi.cuit.R;
import me.qisthi.cuit.adapter.TweetAdapter;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHelper {
    public static final int LOAD_HOME_TIMELINE = 0;
    public static final int LOAD_MORE_HOME_TIMELINE = 1;
    public static final int LOAD_USER_MENTION = 2;
    public static final int LOAD_MORE_USER_MENTION = 3;

    public static final int CACHE_HOME_TIMELINE = 4;
    public static final int CACHE_USER_MENTION = 5;


    public static class LoadHomeTimeline extends AsyncTask<Void, Void, ResponseList<Status>>
    {
        private ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        private Activity activity;
        private TweetAdapter tweetAdapter;
        private List<twitter4j.Status> tweetsTimeline;
        private SwipeRefreshLayout refreshLayout;

        private int action;

        public LoadHomeTimeline(Activity activity, TweetAdapter tweetAdapter, List<twitter4j.Status> tweetsTimeline, int action) {
            this.activity = activity;
            this.tweetAdapter = tweetAdapter;
            this.tweetsTimeline = tweetsTimeline;
            this.action = action;
        }
        public LoadHomeTimeline(Activity activity, TweetAdapter tweetAdapter, List<twitter4j.Status> tweetsTimeline, int action, SwipeRefreshLayout refreshLayout) {
            this.activity = activity;
            this.tweetAdapter = tweetAdapter;
            this.tweetsTimeline = tweetsTimeline;
            this.action = action;
            this.refreshLayout = refreshLayout;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... params) {

            try{
                Configuration twitterConf = confBuilder.setOAuthConsumerKey(activity.getString(R.string.api_key))
                        .setOAuthConsumerSecret(activity.getString(R.string.api_secret))
                        .setOAuthAccessToken(activity.getString(R.string.access_token))
                        .setOAuthAccessTokenSecret(activity.getString(R.string.access_secret))
                        .setJSONStoreEnabled(true)
                        .build();
                Twitter twitter = new TwitterFactory(twitterConf).getInstance();
                switch (action)
                {
                    case LOAD_HOME_TIMELINE:
                        return twitter.getHomeTimeline();
                    case LOAD_MORE_HOME_TIMELINE:
                        //get last id
                        Long sinceId = StorageHelper.getPreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID);
                        if(sinceId==0)
                        {
                            return null;
                        }
                        ResponseList<twitter4j.Status> responseTimeline = twitter.getHomeTimeline(new Paging(sinceId));
                        if(responseTimeline.size()>0)
                        {
                            StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID, responseTimeline.get(0).getId());
                        }
                        return responseTimeline;

                    case LOAD_USER_MENTION:
                        return twitter.getMentionsTimeline();
                    case LOAD_MORE_USER_MENTION:
                        Long mentionSinceId= StorageHelper.getPreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID);
                        if(mentionSinceId==0)
                        {
                            return null;
                        }
                        ResponseList<twitter4j.Status> responseMention = twitter.getMentionsTimeline(new Paging(mentionSinceId));
                        if(responseMention.size()>0)
                        {
                            StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID, responseMention.get(0).getId());
                        }
                        return responseMention;
                }
                return null;
            }catch (Exception ex)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> response) {
            activity.setProgressBarIndeterminateVisibility(false);
            if(response!=null)
            {
                //save last id
                if(response.size()!=0)
                {
                    if(action == LOAD_HOME_TIMELINE)
                    {
                        StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID, response.get(0).getId());
                    }else if(action == LOAD_USER_MENTION)
                    {
                        StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID, response.get(0).getId());
                    }
                    tweetsTimeline.addAll(0,response);
                    tweetAdapter.notifyDataSetChanged();
                }
            }else{
                Snackbar.with(activity.getApplicationContext()).dismiss();
                Snackbar.with(activity.getApplicationContext())
                        .text("Failed to load tweets data")
                        .show(activity);
            }

            if(refreshLayout.isRefreshing())
            {
                refreshLayout.setRefreshing(false);
            }

        }
    }
}
