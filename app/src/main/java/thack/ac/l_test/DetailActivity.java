package thack.ac.l_test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
        String user = getIntent().getExtras().getString("user");
        String source = getIntent().getExtras().getString("source");
        //Parse the source
        if(source.equals(MainActivity.SOURCE_TWITTER)){
            source = getResources().getString(R.string.source_twitter);
        }
        if(source.equals(MainActivity.SOURCE_PLUS)){
            source = getResources().getString(R.string.source_plus);
        }
        if(source.equals(MainActivity.SOURCE_INSTA)){
            source = getResources().getString(R.string.source_insta);
        }

        String content = getIntent().getExtras().getString("content");
        //Parse the html elements
        Spanned content_spanned = Html.fromHtml(content);
        String time = getIntent().getExtras().getString("time");
        String url = getIntent().getExtras().getString("url");

        //Set up the CardViews and add the CardViews to the content view
        CardView sourceCard = ViewHelper.setupCardView(this, new String[]{source});
        detailView.addView(sourceCard);
        CardView userCard = ViewHelper.setupCardView(this, new String[]{user});
        detailView.addView(userCard);
        CardView contentCard = ViewHelper.setupCardView(this, new CharSequence[]{content_spanned});
        detailView.addView(contentCard);
        CardView timeCard = ViewHelper.setupCardView(this, new String[]{time});
        detailView.addView(timeCard);

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
            alert.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
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
