package thack.ac.l_test;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DetailActivity extends Activity {
    LinearLayout.LayoutParams llp;
    public final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        LinearLayout detailView = (LinearLayout) findViewById(R.id.detail_view);

        //Get the index from the previous activity
        int position = getIntent().getExtras().getInt("pos");
        String user = getIntent().getExtras().getString("user");
        String content = getIntent().getExtras().getString("content");
        String url = getIntent().getExtras().getString("url");
        user = "@" + user;

        //Define the Layout Params
        llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int)getResources().getDimension(R.dimen.text_img_in_card_margin);
        //Resources r = getResources();
        //int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        //Log.e(TAG, "margin: " + margin);
        llp.setMargins(margin, margin, margin, margin); // llp.setMargins(left, top, right, bottom);

        //Set up the CardViews and add the CardViews to the content view
        CardView userCard = setupCardView(new String[]{user});
        detailView.addView(userCard);
        CardView contentCard = setupCardView(new String[]{content});
        detailView.addView(contentCard);
        if(url != null){
            CardView urlCard = setupCardView(new String[]{getString(R.string.url_contained_title), url});
            detailView.addView(urlCard);
        }
    }

    private CardView setupCardView(String[] s) {
        //Construct the CardView
        CardView cardView = new CardView(this);
        //Add LinearLayout to CardView
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(ll);

        //Add Children views into the LinearLayout
        for(String each_s : s){
            TextView textView = new TextView(this);
            textView.setLayoutParams(llp);
            textView.setText(each_s);
            ll.addView(textView);
        }
        return cardView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
