package me.qisthi.cuit.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.ImageHelper;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class TweetDetailActivity extends ActionBarActivity {
    private TextView statusUname;
    private TextView statusName;
    private TextView statusText;
    private TextView statusTime;
    private ImageView statusUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        statusName = (TextView)findViewById(R.id.textUserName);
        statusUname = (TextView)findViewById(R.id.textUname);
        statusText = (TextView)findViewById(R.id.textTweet);
        statusTime = (TextView)findViewById(R.id.textTime);
        statusUserProfile = (ImageView)findViewById(R.id.imageProfilePicture);

        String json = getIntent().getStringExtra("statusJSON");
        String statusCreated = getIntent().getStringExtra("statusCreated");
        String statusProfilePictureURL = getIntent().getStringExtra("statusProfileUrl");
        try {
            Status status = TwitterObjectFactory.createStatus(json);
            if(status!=null)
            {
                statusName.setText(status.getUser().getName());
                statusUname.setText("@"+status.getUser().getScreenName());
                statusText.setText(status.getText());

//                String dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(status.getCreatedAt());
                statusTime.setText(statusCreated);

                //Load image
                new ImageHelper.LoadImage(statusProfilePictureURL, statusUserProfile, ImageHelper.LoadImage.LOAD_CIRCULAR_IMAGE).execute();
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}
