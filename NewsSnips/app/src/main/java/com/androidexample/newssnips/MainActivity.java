package com.androidexample.newssnips;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    public static final String LOG_TAG = MainActivity.class.getName();

    private static final int NEWS_LOADER_ID = 1;
    public String GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&page-size=10&order-by=newest&show-fields=standfirst,starRating,headline,thumbnail,short-url,lastModified,byline";
    public View coordinatorLayoutView;
    public ListView newsListView;
    public ProgressBar mProgressBar;
    public ImageView noNet;
    public ImageView noResult;
    public Snackbar snackbar;
    public View sbView;
    public ConnectivityManager connectivityManager;
    public NetworkInfo networkInfo;
    public TextView showMore;
    public int size = 10;
    public TextView dummy;
    private NewsAdapter mAdapter;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayoutView = findViewById(R.id.snackbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        newsListView = (ListView) findViewById(R.id.list);
        noNet = (ImageView) findViewById(R.id.no_net_image);
        noResult = (ImageView) findViewById(R.id.no_result);
        // input = (EditText) findViewById(R.id.editText);
        showMore = (TextView) findViewById(R.id.show_more);
        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        dummy = (TextView) findViewById(R.id.dummy);
        ColorStateList itemTextColor = null;

        mProgressBar.setVisibility(View.GONE);
        //    mProgressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(238, 238, 238), PorterDuff.Mode.MULTIPLY);
        noNet.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        showMore.setVisibility(View.VISIBLE);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.rgb(228, 80, 79));
        swipe.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                drawerOptionClick();
                swipe.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        drawerOptionClick();
                    }
                }, 1000);
            }
        });

        //When Runnung App for the first time or the home icon is clicked ,this function shows the combined news results
        drawerOptionClick();

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size < 50) {
                    size += 10;
                } else {
                    dummy.setVisibility(View.GONE);
                    showMore.setVisibility(View.GONE);
                }
                drawerOptionClick();
            }
        });
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        itemTextColor = navigationView.getItemTextColor();
        navigationView.setItemTextColor(itemTextColor);
        Menu menu = navigationView.getMenu();

        //For chnaging the textColor and textSize of
        // group's Name ie. Category
        MenuItem categoryName = menu.findItem(R.id.categoryGroup);
        SpannableString s = new SpannableString(categoryName.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance25), 0, s.length(), 0);

        //For changing the Text of action bar as the selected category
        categoryName.setTitle(s);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and open the Appopriate news accordingly
                switch (menuItem.getItemId()) {

                    case R.id.itemWorld:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=world";
                        setTitle("World News");

                        basicDefaults();
                        return true;
                    case R.id.itemFootball:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=football";
                        setTitle("Football News");
                        basicDefaults();
                        return true;
                    case R.id.itemFashion:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=fashion";
                        setTitle("Fashion News");
                        basicDefaults();
                        return true;
                    case R.id.itemSports:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=sport";
                        setTitle("Sports News");
                        basicDefaults();
                        return true;
                    case R.id.itemBusiness:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=business";
                        setTitle("Business News");
                        basicDefaults();
                        return true;
                    case R.id.itemTechnology:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=technology";
                        setTitle("Technology News");
                        basicDefaults();
                        return true;
                    case R.id.itemOpinion:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=commentisfree";
                        setTitle("Opinions");
                        basicDefaults();
                        return true;
                    case R.id.itemCulture:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=culture";
                        setTitle("Culture News");
                        basicDefaults();
                        return true;
                    case R.id.itemTravel:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=travel";
                        setTitle("Travel News");
                        basicDefaults();
                        return true;
                    case R.id.itemLifestyle:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=lifeandstyle";
                        setTitle("LifeStyle News");
                        basicDefaults();
                        return true;
                    case R.id.itemEnvironment:
                        GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&section=environment";
                        setTitle("Environment News");
                        basicDefaults();
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);

            }
        };

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void basicDefaults() {
        size = 10;
        dummy.setVisibility(View.VISIBLE);
        showMore.setVisibility(View.VISIBLE);
        drawerOptionClick();
    }

    private void drawerOptionClick() {

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.v("mainActivity", "Connectivity check");
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.i(LOG_TAG, "Net Is connected");
            performSearch();
        } else {
            showMore.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            noNet.setVisibility(View.VISIBLE);
            coordinatorLayoutView.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noInternet, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action_setting, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(0, 0, 0));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            TextView snackberText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            snackberText.setTextColor(Color.rgb(228, 80, 79));
            snackbar.show();
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();

        }
    }

    private void performSearch() {
        noNet.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        newsListView.setVisibility(View.VISIBLE);

        ContentFragment fragment = new ContentFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

        mAdapter = new NewsAdapter(MainActivity.this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the new URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.v("mainActivity", "Connectivity check");
        if (networkInfo != null && networkInfo.isConnected()) {

            Log.i(LOG_TAG, "If network is there");
            newsListView.setVisibility(View.VISIBLE);

            noNet.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            Log.i(LOG_TAG, "Calling init loader");

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
            loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            newsListView.setVisibility(View.GONE);
            showMore.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            noNet.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noInternet, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action_setting, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(0, 0, 0));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            TextView snackberText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            snackberText.setTextColor(Color.rgb(228, 80, 79));
            snackbar.show();
            Log.i(LOG_TAG, "No Internet");
        }


    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader called");
        noResult.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        Uri baseUri = Uri.parse(GAURDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("page-size", String.valueOf(size));
        uriBuilder.appendQueryParameter("show-fields", "standfirst,starRating,headline,thumbnail,short-url,bodyText,lastModified,byline");

        Log.v("uriBuilder", String.valueOf(uriBuilder));
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        Log.i(LOG_TAG, "onLoadFinished called");

        mAdapter.clear();
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text, Snackbar.LENGTH_SHORT).setAction(R.string.snackbar_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coordinatorLayoutView.setVisibility(View.GONE);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(0, 0, 0));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            TextView snackberText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            snackberText.setTextColor(Color.rgb(228, 80, 79));
            snackbar.show();

            coordinatorLayoutView.setVisibility(View.VISIBLE);

        } else {
            noResult.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noResult, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coordinatorLayoutView.setVisibility(View.GONE);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(0, 0, 0));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            TextView snackberText = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            snackberText.setTextColor(Color.rgb(228, 80, 79));
            snackbar.show();
        }
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i(LOG_TAG, "onLoadReset called");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            GAURDIAN_REQUEST_URL = "http://content.guardianapis.com/search?api-key=f51fa87d-8a55-4fc1-b552-8fa6eb98dee4&page-size=10&order-by=newest&show-fields=standfirst,starRating,headline,thumbnail,short-url,lastModified,byline";
            setTitle("News Snips");
            drawerOptionClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

} // Activity end
