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
import android.content.Intent;
import android.net.Uri;

import me.qisthi.cuit.activity.TimelineActivity;
import me.qisthi.cuit.activity.TweetDetailActivity;
import me.qisthi.cuit.activity.TwitterUserActivity;
import me.qisthi.cuit.activity.WriteTweetActivity;

public class IntentHelper {
    public static void openWriteTweetActivity(Activity rootActivity)
    {
        Intent detailIntent = new Intent(rootActivity, WriteTweetActivity.class);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rootActivity.startActivity(detailIntent);
    }

    public static void openReplyTweetActivity(Activity rootActivity, String username)
    {
        Intent detailIntent = new Intent(rootActivity, WriteTweetActivity.class);
        detailIntent.putExtra("replyUsername", username);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rootActivity.startActivity(detailIntent);
    }

    public static void openTimelineActivity(Activity rootActivity)
    {
        Intent timelineActivityIntent = new Intent(rootActivity.getApplicationContext(), TimelineActivity.class);
        timelineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        timelineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rootActivity.getApplicationContext().startActivity(timelineActivityIntent);
    }

    public static void openTweetDetailActivity(Activity rootActivity, String[] status)
    {
        Intent tweetDetailIntent = new Intent(rootActivity.getApplicationContext(), TweetDetailActivity.class);
        tweetDetailIntent.putExtra("statusInfo", status);
        tweetDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        tweetDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rootActivity.getApplicationContext().startActivity(tweetDetailIntent);

    }

    public static void openTwitterProfile(Activity rootActivity, String[] userProfile)
    {
        Intent twitterProfileIntent = new Intent(rootActivity.getApplicationContext(), TwitterUserActivity.class);
        twitterProfileIntent.putExtra("profileInfo", userProfile);
        twitterProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        twitterProfileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        rootActivity.getApplicationContext().startActivity(twitterProfileIntent);
    }

    public static void openPhone(Activity rootActivity,String phoneNum)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNum));
        if(intent.resolveActivity(rootActivity.getPackageManager())!=null)
        {
            rootActivity.startActivity(intent);
        }
    }

    public static void composeEmail(Activity rootActivity,String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("*/*");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (intent.resolveActivity(rootActivity.getPackageManager()) != null) {
            rootActivity.startActivity(intent);
        }
    }

    public static void openWebPage(Activity rootActivity, String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(rootActivity.getPackageManager()) != null) {
            rootActivity.startActivity(intent);
        }
    }

}
