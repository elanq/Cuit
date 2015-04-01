package me.qisthi.cuit.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import me.qisthi.cuit.R;
import me.qisthi.cuit.fragment.TimelineFragment;
import me.qisthi.cuit.helper.LayoutHelper;

public class TimelineActivity extends ActionBarActivity {
    public static final String TIMELINE_ACTION = "TimelineAction";
    public static final int HOME_TIMELINE_ACTION_VALUE = 0;
    public static final int MENTION_TIMELINE_ACTION_VALUE = 1;

    private Toolbar supportToolbar;
    private DrawerLayout drawerLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //Set navigation list adapter
        ArrayAdapter<String> navigationAdapter = new ArrayAdapter<>(this,R.layout.drawer_item_list, LayoutHelper.navigationMenu);

        //Find view
        supportToolbar = (Toolbar) findViewById(R.id.appToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.rootLayout);
        navigationList = (ListView) findViewById(R.id.drawer);

        supportToolbar.setTitle("Timeline");
        supportToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(supportToolbar);

        navigationList.setAdapter(navigationAdapter);
        navigationList.setClickable(false);
        navigationList.setOnItemClickListener(new DrawerItemClickListener());

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

        //Initiate timeline fragment first
        Bundle bundleFragment = new Bundle();
        bundleFragment.putInt(TIMELINE_ACTION, HOME_TIMELINE_ACTION_VALUE);
        Fragment homeFragment = new TimelineFragment();
        homeFragment.setArguments(bundleFragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentLayout, homeFragment)
                .commit();
    }

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

}
