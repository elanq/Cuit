package me.qisthi.cuit.adapter;/*
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
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.Autolink;
import com.twitter.HitHighlighter;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.ImageHelper;
import me.qisthi.cuit.helper.IntentHelper;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder>{

    private List<String[]> statuses;
    private Activity activity;
    private int colorLayout = 1;
    private boolean colorBoolInc = true;
    private int lastPosition = -1;

    public TweetAdapter(Activity activity, List<String[]> statuses) {
        this.activity = activity;
        this.statuses = statuses;

//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(activity);
//        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(configuration);
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.timeline_row_layout, viewGroup, false);
        return new TweetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TweetViewHolder tweetViewHolder, int i) {
        final String[] status = statuses.get(i);
        String dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(Long.parseLong(status[0]));
        tweetViewHolder.textTime.setText(dateFormat);
//        new ImageHelper.LoadImage(status[1], tweetViewHolder.profilePicture, ImageHelper.LoadImage.LOAD_CIRCULAR_IMAGE).execute();
//        imageLoader.loadImage(status[1], new SimpleImageLoadingListener(){
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if(loadedImage!=null )
//                {
//                    Bitmap roundBitmap = ImageHelper.getRoundedCornerBitmap(loadedImage, 128);
//                    ImageView imageView = tweetViewHolder.profilePicture;
//                    imageView.setImageBitmap(roundBitmap);
//                }
//            }
//        });

//        imageLoader.displayImage("http://"+status[1], tweetViewHolder.profilePicture);
        ImageLoader.getInstance().displayImage(status[1], tweetViewHolder.profilePicture,ImageHelper.getDisplayOptions());
        tweetViewHolder.textName.setText(status[2]);
        tweetViewHolder.textUname.setText("@"+status[3]);

        //TODO : Set link clickable
        Autolink autolink = new Autolink();
        String linked = autolink.autoLink(status[4]);
        Spanned linkedText = Html.fromHtml(linked);
        tweetViewHolder.textTweet.setText(linkedText);
        //Set dynamic tweet color
        switch (colorLayout)
        {
            case 1 :
                tweetViewHolder.tweetLayout.setBackgroundColor(activity.getResources().getColor(R.color.tweet1));
                break;
            case 2 :
                tweetViewHolder.tweetLayout.setBackgroundColor(activity.getResources().getColor(R.color.tweet2));
                break;
            case 3 :
                tweetViewHolder.tweetLayout.setBackgroundColor(activity.getResources().getColor(R.color.tweet3));
                break;
            case 4 :
                tweetViewHolder.tweetLayout.setBackgroundColor(activity.getResources().getColor(R.color.tweet4));
                break;
            case 5 :
                tweetViewHolder.tweetLayout.setBackgroundColor(activity.getResources().getColor(R.color.tweet5));
                break;
            default:
                break;
        }

        if(colorBoolInc)
        {
            colorLayout++;
        }else{
            colorLayout--;
        }

        if(colorLayout==6 && colorBoolInc)
        {
            colorBoolInc = false;
            colorLayout =4;
        }else if(colorLayout == 0 && !colorBoolInc)
        {
            colorBoolInc = true;
            colorLayout = 2;
        }

        //set tweet click listener
        tweetViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.openTweetDetailActivity(activity,status);

            }
        });

//        setAnimation(tweetViewHolder.container, i);
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), android.R.anim.slide_in_left);
            animation.setDuration(500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    class TweetViewHolder extends RecyclerView.ViewHolder
    {
        protected ImageView profilePicture;
        protected TextView textUname;
        protected TextView textName;
        protected TextView textTweet;
        protected TextView textTime;
        protected RelativeLayout tweetLayout;
        protected  View container;

        public TweetViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            profilePicture = (ImageView) itemView.findViewById(R.id.imageProfilePicture);
            textUname = (TextView) itemView.findViewById(R.id.textUname);
            textName = (TextView) itemView.findViewById(R.id.textUserName);
            textTweet = (TextView) itemView.findViewById(R.id.textTweet);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            tweetLayout = (RelativeLayout) itemView.findViewById(R.id.timeline_row_layout_root);
        }
    }
}
