package me.qisthi.cuit.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.TwitterHelper;

public class WriteTweetActivity extends ActionBarActivity {

    private Toolbar appToolbar;
    private EditText editTweet;
    private Button buttonTweet;
    private TextView textTweetLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tweet);

        appToolbar = (Toolbar) findViewById(R.id.appToolbar);
        editTweet = (EditText) findViewById(R.id.edit_tweet);
        buttonTweet = (Button) findViewById(R.id.button_tweet);
        textTweetLength = (TextView) findViewById(R.id.text_tweet_words);

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
                    new TwitterHelper.PostTweet(WriteTweetActivity.this, editTweet).execute();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
