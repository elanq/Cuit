package me.qisthi.cuit.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.Autolink;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.ImageHelper;
import me.qisthi.cuit.helper.IntentHelper;
import me.qisthi.cuit.helper.TwitterHelper;

public class TweetDetailActivity extends ActionBarActivity {
    private TextView statusUname;
    private TextView statusName;
    private TextView statusText;
    private TextView statusTime;
    private ImageView statusUserProfile;

    private Toolbar toolbar;
    private RecyclerView recView;

    private ImageButton buttonReply;
    private ImageButton buttonRetweet;
    private ImageButton buttonFave;
    private ImageButton buttonShare;

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
        buttonRetweet = (ImageButton) findViewById(R.id.btn_retweet);
        buttonFave = (ImageButton) findViewById(R.id.btn_favorite);

        toolbar.setTitle("Detail");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));




        final String[] statusInfo = getIntent().getStringArrayExtra("statusInfo");
        if(statusInfo!=null)
        {
            Autolink autolink = new Autolink();
            String linked = autolink.autoLink(statusInfo[4]);

            Spanned spannedText= Html.fromHtml(linked);

            toolbar.setTitle(statusInfo[2]+"'s Tweet");

            statusName.setText(statusInfo[2]);
            statusUname.setText("@"+statusInfo[3]);
            statusText.setText(spannedText);

            String dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm", Locale.ENGLISH).format(Long.parseLong(statusInfo[0]));
            statusTime.setText(dateFormat);

            //Load image
//            new ImageHelper.LoadImage(statusInfo[1], statusUserProfile, ImageHelper.LoadImage.LOAD_CIRCULAR_IMAGE).execute();
            ImageLoader.getInstance().displayImage(statusInfo[1], statusUserProfile, ImageHelper.getDisplayOptions());




            buttonReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.openReplyTweetActivity(TweetDetailActivity.this, statusInfo[3]);
                }
            });

            buttonRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //Build alert dialog
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TweetDetailActivity.this);
                    alertBuilder.setMessage(R.string.retweet_message);
                    alertBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //retweet initiated
                                    new TwitterHelper.Retweet(TweetDetailActivity.this, (ImageButton)v, Long.parseLong(statusInfo[5])).execute();
                                }
                            });
                    alertBuilder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                }
            });

            buttonFave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TwitterHelper.FavoriteTweet(TweetDetailActivity.this, buttonFave, Long.parseLong(statusInfo[5])).execute();
                }
            });
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0,0);
        finish();
        IntentHelper.openTimelineActivity(this);
    }
}
