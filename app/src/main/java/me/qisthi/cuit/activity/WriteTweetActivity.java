package me.qisthi.cuit.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.IntentHelper;
import me.qisthi.cuit.helper.TwitterHelper;

public class WriteTweetActivity extends ActionBarActivity {

    private Toolbar appToolbar;
    private EditText editTweet;
    private Button buttonTweet;
    private TextView textTweetLength;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tweet);


        appToolbar = (Toolbar) findViewById(R.id.appToolbar);
        editTweet = (EditText) findViewById(R.id.edit_tweet);
        buttonTweet = (Button) findViewById(R.id.button_tweet);
        textTweetLength = (TextView) findViewById(R.id.text_tweet_words);
        View editTweetLayout = findViewById(R.id.edit_tweet_root_layout);
        View buttonTweetLayout = findViewById(R.id.button_tweet_root_layout);

        //Start animation on start
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setStartOffset(1000);
            }
        });
        editTweetLayout.startAnimation(animation);
        buttonTweetLayout.startAnimation(animation);

        appToolbar.setTitle("Tweet");
        appToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tweetCount = 140-count;
                textTweetLength.setText(tweetCount+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTweet.length()==0 || editTweet.length()>140)
                {
                    String message = "Please enter your tweet";
                    Snackbar.with(WriteTweetActivity.this).dismiss();
                    Snackbar.with(WriteTweetActivity.this)
                            .text(message)
                            .show(WriteTweetActivity.this);
                }else{
                    new TwitterHelper.PostTweet(buttonTweet, editTweet,WriteTweetActivity.this).execute();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0,0);
        finish();
        IntentHelper.openTimelineActivity(this);
    }
}
