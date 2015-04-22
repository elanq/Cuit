package me.qisthi.cuit.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import me.qisthi.cuit.R;
import me.qisthi.cuit.fragment.TimelineFragment;
import me.qisthi.cuit.helper.AnimHelper;
import me.qisthi.cuit.helper.IntentHelper;
import me.qisthi.cuit.helper.LayoutHelper;

public class TimelineActivity extends ActionBarActivity {
    public static final String TIMELINE_ACTION = "TimelineAction";
    public static final int HOME_TIMELINE_ACTION_VALUE = 0;
    public static final int MENTION_TIMELINE_ACTION_VALUE = 1;

    private Toolbar supportToolbar;
    private DrawerLayout drawerLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageButton tweetButton;
    private ProgressBar appProgress;
    private SharedPreferences sharedPreferences;

    private View revealView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        Crashlytics.start(TimelineActivity.this);

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        if(!ImageLoader.getInstance().isInited())
        {
            ImageLoader.getInstance().init(configuration);
        }

        setContentView(R.layout.activity_timeline);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        //Set navigation list adapter
        ArrayAdapter<String> navigationAdapter = new ArrayAdapter<>(this,R.layout.drawer_item_list, LayoutHelper.navigationMenu);

        //Find view
        supportToolbar = (Toolbar) findViewById(R.id.appToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.rootLayout);
        navigationList = (ListView) findViewById(R.id.drawer);
        tweetButton = (ImageButton) findViewById(R.id.fab_button);
        appProgress = (ProgressBar) findViewById(R.id.appProgress);
        revealView = findViewById(R.id.reveal_view);

        //Set activity toolbar
        supportToolbar.setTitle("Timeline");
        supportToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(supportToolbar);

        //set navigation toolbar
        navigationList.setAdapter(navigationAdapter);
        navigationList.setClickable(false);
        navigationList.setOnItemClickListener(new DrawerItemClickListener());


        //set navigation icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, supportToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
        };

        //add listener to tweetbutton
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animator.AnimatorListener revealAnimationListener = new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        IntentHelper.openWriteTweetActivity(TimelineActivity.this);
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                };

                int [] location = new int[2];
                v.getLocationOnScreen(location);
                revealView.setBackgroundColor(getResources().getColor(android.R.color.secondary_text_light));
                int cx = (location[0] + (v.getWidth() / 2));
                int cy = location[1] + (AnimHelper.getStatusBarHeight(TimelineActivity.this) / 2);

                SharedPreferences.Editor ed = sharedPreferences.edit();
                ed.putInt("x", cx);
                ed.putInt("y", cy);
                ed.apply();

                AnimHelper.showRevealEFfect(revealView, cx, cy, revealAnimationListener);

            }
        });

        //Initiate timeline fragment first
        Bundle bundleFragment = new Bundle();
        bundleFragment.putInt(TIMELINE_ACTION, HOME_TIMELINE_ACTION_VALUE);
        Fragment homeFragment = new TimelineFragment();
        homeFragment.setArguments(bundleFragment);

        //load initial home timeline first
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentLayout, homeFragment)
                .commit();
    }

    public void startHideRevealEffect(final int cx, final int cy) {

        if (cx != 0 && cy != 0) {
            // Show the unReveal effect when the view is attached to the window
            revealView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                    // Get the accent color
                    TypedValue outValue = new TypedValue();
                    getTheme().resolveAttribute(android.R.attr.colorPrimary, outValue, true);
                    revealView.setBackgroundColor(outValue.data);
                    AnimHelper.hideRevealEffect(revealView, cx, cy, 1920);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {

                }
            });
        }
    }

    //this must be called after initiation navigation icon
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setLinkNavigation(position);
            drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            });
            drawerLayout.closeDrawer(navigationList);
        }

        //Set action handler when user clicked specific menu
        private void setLinkNavigation(int position)
        {

            switch (position)
            {
                //Home fragment
                case 0 :

                    Fragment homeFragment = new TimelineFragment();
                    Bundle bundleFragment = new Bundle();
                    bundleFragment.putInt(TIMELINE_ACTION, HOME_TIMELINE_ACTION_VALUE);

                    homeFragment.setArguments(bundleFragment);
                    selectItem(homeFragment);
                    getSupportActionBar().setTitle("Timeline");
                    break;
                //Mention fragment
                case 1 :

                    Fragment mentionFragment = new TimelineFragment();
                    Bundle mentionBundle = new Bundle();
                    mentionBundle.putInt(TIMELINE_ACTION, MENTION_TIMELINE_ACTION_VALUE);
                    mentionFragment.setArguments(mentionBundle);
                    selectItem(mentionFragment);
                    getSupportActionBar().setTitle("Mention");
                    break;
                //DM fragment
                case 2 :
                    break;
                //Lists fragment
                case 3 :
                    break;
                //Favorite fragment
                case 4 :
                    break;
                //Search fragment
                case 5 :
                    break;
                //Settings fragment
                case 6 :
                    break;
                default:break;
            }
        }

        private void selectItem(Fragment fragment)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentLayout, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show the unReveal effect
        final int cx = sharedPreferences.getInt("x", 0);
        final int cy = sharedPreferences.getInt("y", 0);

        startHideRevealEffect(cx, cy);

    }
}
