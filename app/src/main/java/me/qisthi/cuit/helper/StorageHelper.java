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
import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

//TODO : Find way to cache statuses

public class StorageHelper {
    public static final String SINCE_TWEET_ID = "tweetSinceId";
    public static final String MAX_TWEET_ID ="tweetMaxId";
    public static final String SINCE_MENTION_TWEET_ID = "tweetMentionSinceId";
    public static final String MAX_MENTION_TWEET_ID = "tweetMentionMaxId";

    public static final String STORAGE_CACHE_HOME_TIMELINE = "cacheHomeTimeline";
    public static final String STORAGE_CACHE_MENTION_TIMELINE = "cacheMentionTimeline";

    public static String getPreferenceStringValue(Activity activity, String key)
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void writePreferenceStringValue(Activity activity, String key, String value)
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Long getPreferenceLongValue(Activity activity, String key)
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0);
    }

    public static void writePreferenceLongValue(Activity activity, String key, Long value)
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void writePreferenceStatusValue(Activity activity, String key,  List<String[]> statuses)
    {
        String rawStatuses = "";
        ObjectMapper mapper = new ObjectMapper();
//        for(Status  status : statuses)
//        {
//            String rawStatus = null;
//            try {
//                rawStatus = mapper.writeValueAsString(status);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            rawStatuses.add(rawStatus);
//        }
        try {
            rawStatuses = mapper.writeValueAsString(statuses);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, rawStatuses);
        editor.apply();
    }

    public static List<String[]> readPreferenceStatusValue(Activity activity, String key) throws Exception
    {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String jsonValue = sharedPreferences.getString(key, null);

        List<String[]> statuses;

        if(jsonValue!=null)
        {
            ObjectMapper mapper = new ObjectMapper();
            statuses = mapper.readValue(jsonValue, new TypeReference<List<String[]>>() {} );

            return statuses;
        }
        return null;
    }

}
