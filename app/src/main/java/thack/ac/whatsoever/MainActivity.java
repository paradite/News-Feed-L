package thack.ac.whatsoever;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends Activity {
    private Activity self = this;

    //Views
    //Search Related Views
    private SearchView   searchView;
    private MenuItem     searchMenuItem;
    //RecyclerView
    private RecyclerView mRecyclerView;
    //Introduction CardView
    private CardView     mCardView;
    private ImageView    removeIconView;
    private TextView     titleView;
    private TextView     suggestionsView;
    private ProgressBar  bar;


    private MyAdapter                  mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<StatusItem> dataset;
    public final String TAG = ((Object) this).getClass().getSimpleName();
    public String query;
    //Twitter related
    TwitterFactory tf;
    Twitter        twitter;

    //Boolean to track if each async task has been executed
    private boolean PLUS_EXECUTED      = false;
    private boolean TWITTER_EXECUTED   = false;
    private boolean INSTA_EXECUTED     = false;
    //Track number of pictures still need to be downloaded
    private int     pic_download_tasks = 0;

    public String DEFAULT_QUERY = "Google Glass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up introduction card
        mCardView = (CardView) findViewById(R.id.card_view);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        removeIconView = (ImageView) mCardView.findViewById(R.id.remove_icon);
        titleView = (TextView) mCardView.findViewById(R.id.item_title);
        suggestionsView = (TextView) mCardView.findViewById(R.id.item_content);
        removeIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCardView.setVisibility(View.GONE);
            }
        });
        //Use CardView to toggle SearchView
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView != null) {
                    searchMenuItem.expandActionView();
                    //searchView.setIconified(false);
                }
            }
        });

        //Set up twitter4j
        setUpTwitter4j();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create dataset
        dataset = new ArrayList<StatusItem>();

        // Construct default query
        if (query == null) {
            query = DEFAULT_QUERY;
        }

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                // do something with position
                Intent intent = new Intent();
                intent.putExtra("pos", position);
                //intent.putExtra("StatusItem", dataset.get(position));
                intent.putExtra("source", dataset.get(position).getSource());
                intent.putExtra("user", dataset.get(position).getUser());
                intent.putExtra("content", dataset.get(position).getContent());
                intent.putExtra("time", dataset.get(position).getExactTime());
                if(dataset.get(position).getLocation() != null){
                    intent.putExtra("location", dataset.get(position).getLocation());
                }
                ////Url for twitter
                //if (dataset.get(position).getUrl_contained_twitter() != null && dataset.get(position).getUrl_contained_twitter().length > 0) {
                //    intent.putExtra("url", dataset.get(position).getUrl_contained_twitter()[0].getText());
                //}
                ////Url for Google+
                //if (dataset.get(position).getUrl_contained_plus() != null && dataset.get(position).getUrl_contained_plus().length > 0) {
                //    intent.putExtra("url", dataset.get(position).getUrl_contained_plus()[0]);
                //}
                intent.setClass(self, DetailActivity.class);
                startActivity(intent);
            }
        });

        // set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //new fetchMyTimelineFromTwitter().execute();
        handleIntent(getIntent());
    }

    public void newFetch() {
        //Update the queried term in adapter
        MyAdapter.setQueriedTerm(query);

        //Reset the data set
        dataset.clear();

        //Reset the async task trackers
        PLUS_EXECUTED = false;
        TWITTER_EXECUTED = false;
        INSTA_EXECUTED = false;
        //Recalculate the number of picture download tasks
        pic_download_tasks = Utils.MAX_RESULTS_TOTAL;

        //Start fetching from sources
        newTwitterFetch();
        newGooglePlusFetch();
        newInstaFetch();

        mCardView.setVisibility(View.VISIBLE);
        suggestionsView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        if (titleView != null) {
            titleView.setText("Searching for " + query + "...");
        }
    }

    private void newTwitterFetch() {
        //Execute twitter first, start others at the end of twitter
        if (!TWITTER_EXECUTED) {

            //Update async task tracker to prevent running task twice
            TWITTER_EXECUTED = true;
            new fetchSearchFromTwitter().execute(query);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //Validate the searchView input
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "Query word= " + s);
                s = Utils.getStringAlphanumeric(s);
                Log.d(TAG, "Validated word= " + s);
                if (s.length() < 2) {
                    Toast.makeText(getApplicationContext(), "At least 2 letter please.", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    searchMenuItem.collapseActionView();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        searchMenuItem.expandActionView();
        //searchView.setIconified(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog.Builder alert = new AlertDialog.Builder(self);
            alert.setTitle(getResources().getString(R.string.action_about));
            String credit = getResources().getString(R.string.credit);
            ViewHelper.setDialogViewMessage(self, alert, credit);
            alert.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.setPositiveButton("Go to Play Store", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            alert.setCancelable(true);
            alert.show();
            return true;
        } else if (id == R.id.action_settings) {
            AlertDialog.Builder alert = new AlertDialog.Builder(self);
            alert.setTitle(getResources().getString(R.string.action_settings));
            String settings = getResources().getString(R.string.settings);
            ViewHelper.setDialogViewMessage(self, alert, settings);
            alert.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.setCancelable(true);
            alert.show();
            return true;
        } else if (id == R.id.action_refresh) {
            newFetch();

        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpTwitter4j() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("vdKT4mCY9D2eK6y238veerGgj")
                .setOAuthConsumerSecret("SnrCTLQATvT8FC4jo0pDI5FtyzDrjjHJBssdGnH4qp6tWaPhW6")
                .setOAuthAccessToken("63657268-GW9QhBmRVEV2MNcBBAB4kulGrkQo3xU4CpYZgTy2e")
                .setOAuthAccessTokenSecret("HSnEWnW22xEv6M8Db5X0eko2pSQc22r1IK7K2k6n15R7m");
        tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    /**
     * Twitter Async Task
     * Async Task to make http call and sync with server
     * Twitter Async Task
     */
    private class fetchSearchFromTwitter extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                // gets Twitter instance with default credentials
                User user = twitter.verifyCredentials();
                String query_word = strings[0];
                try {
                    query_word = URLEncoder.encode(query_word, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Query query = new Query(query_word);
                query.setCount(Utils.MAX_RESULTS_TWITTER);
                QueryResult result = twitter.search(query);
                List<twitter4j.Status> statuses = result.getTweets();
                //publishProgress(statuses.size());
                Log.d(TAG, "Showing " + statuses.size() + " Twitter search results for " + strings[0] + ":");
                //Log.d(TAG, "Showing search results for " + strings[0] + ":");
                //Add in new data to the data set
                for (twitter4j.Status s : statuses) {
                    //Log.d(TAG, "@" + s.getUser().getScreenName() + "\n" + s.getText());
                    StatusItem new_item = new StatusItem(s.getUser().getScreenName(), s.getText(), s.getCreatedAt(), s.getUser().getProfileImageURL(), Utils.SOURCE_TWITTER);
                    //Add additional information
                    URLEntity urls[] = s.getURLEntities();
                    if (urls.length != 0) {
                        new_item.setUrl_contained_twitter(urls);
                    }
                    MediaEntity m[] = s.getMediaEntities();
                    //Add location
                    //Log.e(TAG, "Twitter: ");

                    //if(s.getPlace() != null)Log.e(TAG, "place:"+s.getPlace().toString());
                    //if (s.getGeoLocation()!=null)Log.e(TAG, "geo:"+s.getGeoLocation().toString());
                    if(s.getPlace() != null && s.getPlace().getName() != null){
                        new_item.setLocation(s.getPlace().getName());
                    }
                    dataset.add(new_item);
                    new DownloadImagesTask().execute(new_item);
                }

                //Sort by time
                Collections.sort(dataset);
            } catch (TwitterException te) {
                te.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(self)
                                .setMessage("Error occurred when getting the tweets")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        TWITTER_EXECUTED = false;
                                        newTwitterFetch();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .setCancelable(true)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Notify the adapter
            mAdapter.notifyDataSetChanged();
        }
    }

    private void newGooglePlusFetch() {
        //Parse query for Google+
        String clean_query = Utils.getStringSpaceAlphanumeric(query);
        if (!PLUS_EXECUTED) {
            //Update async task tracker to prevent running task twice
            PLUS_EXECUTED = true;
            new fetchSearchFromGooglePlus().execute(clean_query);
        }
    }

    /**
     * Google+ Async Task
     * Async Task to make http call and sync with server
     * Google+ Async Task
     */
    private class fetchSearchFromGooglePlus extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                //Get response from Google+
                List<com.google.api.services.plus.model.Activity> activities = GooglePlus.run(strings[0]);

                //Log.d(TAG, "Showing " + activities.size() + "Plus search results for " + strings[0] + ":");
                //Add in new data to the data set
                for (com.google.api.services.plus.model.Activity a : activities) {
                    //Log.d(TAG, "@" + a.getActor().getDisplayName());
                    //Log.d(TAG, "" + a.getPublished().getTimeZoneShift());
                    Date parsed_date = Utils.parseRFC3339Date(a.getPublished().toStringRfc3339());
                    //Log.d(TAG, "\n" + parsed_date.toString());

                    StatusItem new_item = new StatusItem(a.getActor().getDisplayName(), a.getObject().getContent(), parsed_date, a.getActor().getImage().getUrl(), Utils.SOURCE_PLUS);
                    //Additional information
                    String urls[] = {a.getUrl()};
                    if (urls.length != 0) {
                        new_item.setUrl_contained_plus(urls);
                    }
                    //Add location
                    //Log.e(TAG, "plus: ");
                    if(a.getLocation() != null){
                        String location = a.getLocation().getDisplayName();
                        //Log.e(TAG, "locat:"+location);
                        new_item.setLocation(location);
                    }
                    if(a.getPlaceName() != null){
                        String location = a.getPlaceName();
                        //Log.e(TAG, "place:"+location);
                        new_item.setLocation(location);
                    }
                    //Add picture
                    List<com.google.api.services.plus.model.Activity.PlusObject.Attachments> attachements = a.getObject().getAttachments();
                    if(attachements != null && attachements.size() > 0 && attachements.get(0).getFullImage() != null){
                        String picture_url = attachements.get(0).getFullImage().getUrl();
                        new_item.setContent_pic_url(picture_url);
                    }
                    dataset.add(new_item);
                    new DownloadImagesTask().execute(new_item);
                }

                //Sort by time
                Collections.sort(dataset);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(self)
                                .setMessage("Error occurred when getting the Google+ posts")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        PLUS_EXECUTED = false;
                                        newGooglePlusFetch();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .setCancelable(true)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Notify the adapter
            mAdapter.notifyDataSetChanged();
        }
    }

    private void newInstaFetch() {
        //Parse query more for Instagram (remove spaces)
        String cleaner_query = Utils.getStringAlphanumeric(query);
        if (!INSTA_EXECUTED) {
            //Update async task tracker to prevent running task twice
            INSTA_EXECUTED = true;
            new fetchSearchFromInstagram().execute(cleaner_query);
        }
    }

    /**
     * Instagram Async Task
     * Async Task to make http call and sync with server
     * Instagram Async Task
     */
    private class fetchSearchFromInstagram extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            //Get response from Instagram
            ArrayList<StatusItem> items = null;
            try {
                items = InstagramIntegration.newSearch(strings[0]);
                Log.d(TAG, "Showing Instagram search results for " + strings[0] + ":");
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(self)
                                .setMessage("Error occurred when getting the Instagram posts")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        INSTA_EXECUTED = false;
                                        newInstaFetch();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .setCancelable(true)
                                .show();
                    }
                });
            }
            if (items == null) return null;
            Log.d(TAG, "Showing " + items.size() + "Instagram search results for " + strings[0] + ":");
            //Add in new data to the data set
            for (StatusItem item : items) {
                //Log.d(TAG, "@" + item.getUser());
                //Log.d(TAG, "\n" + item.getDisplayTime());

                //String urls[] = {item.getUrl()};
                //if(urls.length != 0){
                //    new_item.setUrl_contained_plus(urls);
                //}
                //MediaEntity m[] = a.getMediaEntities();
                dataset.add(item);
                new DownloadImagesTask().execute(item);
            }

            //Sort by time
            Collections.sort(dataset);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tryDismissBar();

            //Notify the adapter
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Try to dismiss the progress bar after fetching all the data
     */
    private void tryDismissBar() {
        if(TWITTER_EXECUTED && PLUS_EXECUTED && INSTA_EXECUTED){
            if(pic_download_tasks > 0){
                if (titleView != null) {
                    titleView.setText("Loading pictures in " + pic_download_tasks + " posts...");
                }
            }else if(mCardView.getVisibility() != View.GONE){
                mCardView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Search completed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DownloadAllImagesTask extends AsyncTask<ArrayList<StatusItem>, Void, Void> {
        @Override
        protected Void doInBackground(ArrayList<StatusItem>... arrayLists) {
            for (StatusItem statusItem : arrayLists[0]) {
                String profile_url = statusItem.getProfile_url();
                String pic_url = statusItem.getContent_pic_url();
                statusItem.setProfileDrawable(Utils.LoadImageFromWebOperations(profile_url, 1));
                if (pic_url != null) {
                    //Log.e(TAG, statusItem.getSource() + statusItem.getUser() + ": " + pic_url);
                    statusItem.setContentDrawable(Utils.LoadImageFromWebOperations(pic_url, 2));
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

        }
    }

    public class DownloadImagesTask extends AsyncTask<StatusItem, Void, Void> {

        @Override
        protected Void doInBackground(StatusItem... statusItems) {
            Log.e(TAG, "DownloadImagesTask: ");
            String profile_url = statusItems[0].getProfile_url();
            String pic_url = statusItems[0].getContent_pic_url();
            statusItems[0].setProfileDrawable(Utils.LoadImageFromWebOperations(profile_url, 1));
            if (pic_url != null) {
                //Log.e(TAG, statusItems[0].getSource() + statusItems[0].getUser() + ": " + pic_url);
                statusItems[0].setContentDrawable(Utils.LoadImageFromWebOperations(pic_url, 2));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
            pic_download_tasks--;
            Log.e(TAG, "pic: " + pic_download_tasks);
            tryDismissBar();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //Trim the query
            query = query.trim();
            newFetch();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
