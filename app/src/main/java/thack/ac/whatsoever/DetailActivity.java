package thack.ac.whatsoever;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;


public class DetailActivity extends Activity {
    Activity self = this;
    public final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar bar = getActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
        }
        LinearLayout detailView = (LinearLayout) findViewById(R.id.detail_view);

        //Get the index from the previous activity
        int position = getIntent().getExtras().getInt("pos");
        //StatusItem item = (StatusItem) getIntent().getSerializableExtra("StatusItem");
        String user = getIntent().getExtras().getString("user");
        String source = getIntent().getExtras().getString("source");
        //String user = item.getUser();
        //String source = item.getSource();
        //Parse the source
        if(source.equals(Utils.SOURCE_TWITTER)){
            source = getResources().getString(R.string.source_twitter);
        }
        if(source.equals(Utils.SOURCE_PLUS)){
            source = getResources().getString(R.string.source_plus);
        }
        if(source.equals(Utils.SOURCE_INSTA)){
            source = getResources().getString(R.string.source_insta);
        }

        String content = getIntent().getExtras().getString("content");
        //String content = item.getContent();
        //Parse the html elements
        Spanned content_spanned = Html.fromHtml(content);

        String time = getIntent().getExtras().getString("time");
        String location = getIntent().getExtras().getString("location");
        //String url = getIntent().getExtras().getString("url");
        //String time = item.getExactTime();
        //String location = item.getLocation();

        //Set up the CardViews and add the CardViews to the content view
        //Add source
        CardView sourceCard = ViewHelper.setupCardView(this, new String[]{source});
        detailView.addView(sourceCard);
        //Add user
        CardView userCard = ViewHelper.setupCardView(this, new String[]{user});
        detailView.addView(userCard);
        //Add time
        CardView timeCard = ViewHelper.setupCardView(this, new String[]{time});
        detailView.addView(timeCard);
        //Add location
        if(location != null && !location.equals("")){
            CardView locationCard = ViewHelper.setupCardView(this, new String[]{location});
            detailView.addView(locationCard);
        }
        //Add content
        CardView contentCard = ViewHelper.setupCardView(this, new CharSequence[]{content_spanned});
        detailView.addView(contentCard);

        //Separate URL CardView is not needed as the links in content view is clickable
        //if(url != null){
        //    CardView urlCard = ViewHelper.setupURLCardView(this, new String[]{url});
        //    detailView.addView(urlCard);
        //}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

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
        }else if(id == R.id.action_settings){
            AlertDialog.Builder alert = new AlertDialog.Builder(self);
            alert.setTitle(getResources().getString(R.string.action_settings));
            String settings = getResources().getString(R.string.action_settings);
            ViewHelper.setDialogViewMessage(self, alert, settings);
            alert.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.setCancelable(true);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
