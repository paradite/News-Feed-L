package thack.ac.l_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends Activity {
    private Activity self = this;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<StatusItem> dataset;
    public final String TAG = ((Object) this).getClass().getSimpleName();

    //Twitter related
    TwitterFactory tf;
    Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set up twitter4j
        setUpTwitter4j();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Create dataset
        dataset = new ArrayList<StatusItem>();

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(dataset);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View v , int position) {
                // do something with position
                Intent intent = new Intent();
                intent.putExtra("pos", position);
                intent.putExtra("user", dataset.get(position).getUser());
                intent.putExtra("content", dataset.get(position).getContent());
                intent.putExtra("time", dataset.get(position).getExactTime());
                if(dataset.get(position).getUrl_contained() != null && dataset.get(position).getUrl_contained().length > 0){
                    intent.putExtra("url", dataset.get(position).getUrl_contained()[0].getText());
                }
                intent.setClass(self, DetailActivity.class);
                startActivity(intent);
            }
        });

        // set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        new fetchFromTwitter().execute();

    }

    public void reFetch(){
        new fetchFromTwitter().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_refresh){
            new fetchFromTwitter().execute();

        }
        return super.onOptionsItemSelected(item);
    }

    public static int randInt(int min , int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public void setUpTwitter4j(){
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
     * Async Task to make http call and sync with server
     */
    private class fetchFromTwitter extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(self);
            this.dialog.setMessage("Getting tweets...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // gets Twitter instance with default credentials
                User user = twitter.verifyCredentials();
                List<twitter4j.Status> statuses = twitter.getHomeTimeline();
                //Reset the data set
                dataset.clear();
                Log.d(TAG, "Showing @" + user.getScreenName() + "'s home timeline.");
                for (twitter4j.Status s : statuses) {
                    //Log.d(TAG, "@" + s.getUser().getScreenName() + "\n" + s.getText());
                    StatusItem new_item = new StatusItem(s.getUser().getScreenName(), s.getText(), s.getCreatedAt(), s.getUser().getMiniProfileImageURL());
                    URLEntity urls[] = s.getURLEntities();
                    if(urls.length != 0){
                        new_item.setUrl_contained(urls);
                    }
                    MediaEntity m[] = s.getMediaEntities();
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
                                        reFetch();
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            //mAdapter.setmDataset(dataset);
            mAdapter.notifyDataSetChanged();
        }
    }

    public class DownloadImagesTask extends AsyncTask<StatusItem, Void, Void> {

        @Override
        protected Void doInBackground(StatusItem... statusItems) {
            String url = statusItems[0].getProfile_url();
            statusItems[0].setProfileDrawable(LoadImageFromWebOperations(url));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            //Log.d("Converter: ", url + d.toString());
            return d;
        } catch (Exception e) {
            Log.d("Exception: ", e.toString());
            return null;
        }
    }
}
