package me.qisthi.cuit.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import me.qisthi.cuit.R;
import me.qisthi.cuit.helper.TwitterHelper;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button button = (Button) findViewById(R.id.btn_sign_in);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TwitterHelper.TwitterLogin(SplashActivity.this).execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.getIntent() != null && this.getIntent().getData() != null) {
            Uri uri = this.getIntent().getData();

            //handle returning from authenticating the user
            if (uri != null && uri.toString().startsWith(TwitterHelper.CALLBACK_URL)) {
                String token = uri.getQueryParameter("oauth_token");
                String verifier = uri.getQueryParameter("oauth_verifier");

                Toast.makeText(this, "Token : "+token+"\n verifier : "+verifier, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
