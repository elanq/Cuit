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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nispok.snackbar.Snackbar;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.qisthi.cuit.R;
import me.qisthi.cuit.adapter.TweetAdapter;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
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


    public static String[] convertStatusToArray(Status status)
    {
        if(status==null)
        {
            return null;
        }
//        String dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(status.getCreatedAt());

        return new String[]{
                String.valueOf(status.getCreatedAt().getTime()),
                status.getUser().getBiggerProfileImageURL(), //1
                status.getUser().getName(), //2
                status.getUser().getScreenName(), //3
                status.getText(), //4
                String.valueOf(status.getId()) //5
        };
    }

    public static List<String[]> convertStatusesToArray(List<Status> statuses)
    {
        List<String[]> arrayStatuses = new ArrayList<>();
        if(statuses==null)
        {
            return null;
        }
        for(Status status:statuses) {
//            String dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(status.getCreatedAt());
            String[] statusArray = {
                    String.valueOf(status.getCreatedAt().getTime()),
                    status.getUser().getBiggerProfileImageURL(), //1
                    status.getUser().getName(), //2
                    status.getUser().getScreenName(), //3
                    status.getText(), //4
                    String.valueOf(status.getId()) //5
            };
            arrayStatuses.add(statusArray);
        }
        return arrayStatuses;
    }


    public static class LoadHomeTimeline extends AsyncTask<Void, Void, List<String[]>>
    {
        private ConfigurationBuilder confBuilder = new ConfigurationBuilder();
        private Activity activity;
        private TweetAdapter tweetAdapter;
        private List<String[]> tweetsTimeline;
        private SwipeRefreshLayout refreshLayout;

        private int action;

        public LoadHomeTimeline(Activity activity, TweetAdapter tweetAdapter, List<String[]> tweetsTimeline, int action) {
            this.activity = activity;
            this.tweetAdapter = tweetAdapter;
            this.tweetsTimeline = tweetsTimeline;
            this.action = action;
        }
        public LoadHomeTimeline(Activity activity, TweetAdapter tweetAdapter, List<String[]> tweetsTimeline, int action, SwipeRefreshLayout refreshLayout) {
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
        protected List<String[]> doInBackground(Void... params) {

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
                        //only loaded in first time/ when tweet cache has no value
                        List<String[]> homeStatusCache = StorageHelper.readPreferenceStatusValue(activity, StorageHelper.STORAGE_CACHE_HOME_TIMELINE);
                        if(homeStatusCache!=null)
                        {
                            return homeStatusCache;
                        }
                        return TwitterHelper.convertStatusesToArray(twitter.getHomeTimeline());
                    case LOAD_MORE_HOME_TIMELINE:
                        //get last id
                        Long sinceId = StorageHelper.getPreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID);
                        if(sinceId==0)
                        {
                            return null;
                        }
                        ResponseList<twitter4j.Status> responseTimeline = twitter.getHomeTimeline(new Paging(sinceId).count(100));
                        if(responseTimeline.size()>0)
                        {
                            StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID, responseTimeline.get(0).getId());
                        }
                        return TwitterHelper.convertStatusesToArray(responseTimeline);

                    case LOAD_USER_MENTION:
                        List<String[]> mentionStatusCache = StorageHelper.readPreferenceStatusValue(activity, StorageHelper.STORAGE_CACHE_MENTION_TIMELINE);
                        if(mentionStatusCache!=null)
                        {
                            return mentionStatusCache;
                        }
                        return TwitterHelper.convertStatusesToArray(twitter.getMentionsTimeline());
                    case LOAD_MORE_USER_MENTION:
                        Long mentionSinceId= StorageHelper.getPreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID);
                        if(mentionSinceId==0)
                        {
                            return null;
                        }
                        ResponseList<twitter4j.Status> responseMention = twitter.getMentionsTimeline(new Paging(mentionSinceId).count(100));
                        if(responseMention.size()>0)
                        {
                            StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID, responseMention.get(0).getId());
                        }
                        return TwitterHelper.convertStatusesToArray(responseMention);
                }
                return null;
            }catch (Exception ex)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String[]> response) {
            activity.setProgressBarIndeterminateVisibility(false);
            if(response!=null)
            {
                //save last id
                if(response.size()!=0)
                {
                    tweetsTimeline.addAll(0,response);
                    tweetAdapter.notifyDataSetChanged();
                    if(action == LOAD_HOME_TIMELINE || action == LOAD_MORE_HOME_TIMELINE)
                    {
                        StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_TWEET_ID, Long.parseLong(response.get(0)[5]));
                        StorageHelper.writePreferenceStatusValue(activity, StorageHelper.STORAGE_CACHE_HOME_TIMELINE, tweetsTimeline);
                    }else if(action == LOAD_USER_MENTION || action == LOAD_MORE_USER_MENTION)
                    {
                        StorageHelper.writePreferenceLongValue(activity, StorageHelper.SINCE_MENTION_TWEET_ID, Long.parseLong(response.get(0)[5]));
                        StorageHelper.writePreferenceStatusValue(activity, StorageHelper.STORAGE_CACHE_MENTION_TIMELINE, tweetsTimeline);
                    }

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

    public static class PostTweet extends AsyncTask<Void, Void, Boolean>
    {
        private Activity activity;
        private EditText editTweet;
        private Button btnTweet;

        private ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        public PostTweet(Button btnTweet, EditText editTweet, Activity activity) {
            this.btnTweet = btnTweet;
            this.editTweet = editTweet;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            btnTweet.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Configuration twitterConf = confBuilder.setOAuthConsumerKey(activity.getString(R.string.api_key))
                    .setOAuthConsumerSecret(activity.getString(R.string.api_secret))
                    .setOAuthAccessToken(activity.getString(R.string.access_token))
                    .setOAuthAccessTokenSecret(activity.getString(R.string.access_secret))
                    .setJSONStoreEnabled(true)
                    .build();
            Twitter twitter = new TwitterFactory(twitterConf).getInstance();
            try {
                String tweetString = editTweet.getText().toString();
                twitter.updateStatus(tweetString);
            } catch (TwitterException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String message = "Failed to update status";
            if(aBoolean)
            {
                message = "Status successfully updated";
            }
            editTweet.setText("");
            btnTweet.setEnabled(true);
            Snackbar.with(activity.getApplicationContext()).dismiss();
            Snackbar.with(activity.getApplicationContext())
                    .text(message)
                    .show(activity);
        }
    }

    public static class Retweet extends AsyncTask<Void, Void, Boolean>
    {
        private Activity activity;
        private ImageButton btnRetweet;
        private long tweetId;

        public Retweet(Activity activity, ImageButton btnRetweet, long tweetId) {
            this.activity = activity;
            this.btnRetweet = btnRetweet;
            this.tweetId = tweetId;
        }

        private ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnRetweet.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Configuration twitterConf = confBuilder.setOAuthConsumerKey(activity.getString(R.string.api_key))
                    .setOAuthConsumerSecret(activity.getString(R.string.api_secret))
                    .setOAuthAccessToken(activity.getString(R.string.access_token))
                    .setOAuthAccessTokenSecret(activity.getString(R.string.access_secret))
                    .setJSONStoreEnabled(true)
                    .build();
            Twitter twitter = new TwitterFactory(twitterConf).getInstance();
            try {
                twitter4j.Status status = twitter.retweetStatus(tweetId);
            } catch (TwitterException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String message = "Failed to retweet status";
            if(aBoolean)
            {
                message = "Status successfully retweeted";
            }
            btnRetweet.setEnabled(true);
            Snackbar.with(activity.getApplicationContext()).dismiss();
            Snackbar.with(activity.getApplicationContext())
                    .text(message)
                    .show(activity);
        }
    }

    public static class FavoriteTweet extends AsyncTask<Void, Void, Boolean>
    {
        private Activity activity;
        private ImageButton favoriteButton;
        private long tweetId;

        private ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        public FavoriteTweet(Activity activity, ImageButton favoriteButton, long tweetId) {
            this.activity = activity;
            this.favoriteButton = favoriteButton;
            this.tweetId = tweetId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            favoriteButton.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Configuration twitterConf = confBuilder.setOAuthConsumerKey(activity.getString(R.string.api_key))
                    .setOAuthConsumerSecret(activity.getString(R.string.api_secret))
                    .setOAuthAccessToken(activity.getString(R.string.access_token))
                    .setOAuthAccessTokenSecret(activity.getString(R.string.access_secret))
                    .setJSONStoreEnabled(true)
                    .build();
            Twitter twitter = new TwitterFactory(twitterConf).getInstance();
            try {
                twitter4j.Status status = twitter.createFavorite(tweetId);
            } catch (TwitterException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            String message = "Failed to favorite status";
            if(aBoolean)
            {
                message = "Status successfully favorited";
            }
            favoriteButton.setEnabled(true);
            Snackbar.with(activity.getApplicationContext()).dismiss();
            Snackbar.with(activity.getApplicationContext())
                    .text(message)
                    .show(activity);
        }
    }

}
