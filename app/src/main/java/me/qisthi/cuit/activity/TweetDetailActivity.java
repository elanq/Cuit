package me.qisthi.cuit.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.ImageHelper;
import me.qisthi.cuit.helper.IntentHelper;
import twitter4j.Status;

public class TweetDetailActivity extends ActionBarActivity {
    private TextView statusUname;
    private TextView statusName;
    private TextView statusText;
    private TextView statusTime;
    private ImageView statusUserProfile;

    private Toolbar toolbar;

    private ImageButton buttonReply;
    private ImageButton buttonRetweet;
    private ImageButton buttonFave;
    private ImageButton buttonShare;

     Status status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        toolbar = (Toolbar) findViewById(R.id.appToolbar);

        statusName = (TextView)findViewById(R.id.textUserName);
        statusUname = (TextView)findViewById(R.id.textUname);
        statusText = (TextView)findViewById(R.id.textTweet);
        statusTime = (TextView)findViewById(R.id.textTime);
        statusUserProfile = (ImageView)findViewById(R.id.imageProfilePicture);

        buttonReply = (ImageButton) findViewById(R.id.btn_reply);

        toolbar.setTitle("Detail");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        final String[] statusInfo = getIntent().getStringArrayExtra("statusInfo");
        if(statusInfo!=null)
        {
            toolbar.setTitle(statusInfo[2]+"'s Tweet");

            statusName.setText(statusInfo[2]);
            statusUname.setText("@"+statusInfo[3]);
            statusText.setText(statusInfo[4]);

//                String dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH).format(status.getCreatedAt());
            statusTime.setText(statusInfo[0]);

            //Load image
            new ImageHelper.LoadImage(statusInfo[1], statusUserProfile, ImageHelper.LoadImage.LOAD_CIRCULAR_IMAGE).execute();

            buttonReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.openReplyTweetActivity(TweetDetailActivity.this, statusInfo[3]);
                }
            });
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
