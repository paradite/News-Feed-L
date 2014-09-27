package thack.ac.l_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.text.util.Linkify;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class to provide helper methods for building alert dialogs
 * Created by paradite on 17/8/14.
 */
public class ViewHelper {
    //Normal LayoutParams
    public static LinearLayout.LayoutParams llp;
    //Title LayoutParams used with other TextViews
    public static LinearLayout.LayoutParams llp_title;
    public final String TAG = ((Object) this).getClass().getSimpleName();
    /**
     * Method to get a EditText with hint
     * @param context   Context
     * @param hint      Hint to be displayed
     * @param maxLength Max input allowed for the EditText
     * @return EditText
     */
    public static EditText getEditTextWithHint(Context context, String hint, int maxLength) {
        final EditText mEditText = new EditText(context);
        mEditText.setHint(hint);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        return mEditText;
    }

    /**
     * Method to get a EditText with text
     * @param context   Context
     * @param text      Text to be displayed initially
     * @param maxLength Max input allowed for the EditText
     * @return EditText
     */
    public static EditText getEditTextWithText(Context context, String text, int maxLength) {
        final EditText mEditText = new EditText(context);
        mEditText.setText(text);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        return mEditText;
    }

    /**
     * Set the view of dialog with two EditTexts
     * @param alert     AlertDialog
     * @param editText1 First EditText
     * @param editText2 First EditText
     */
    public static void setDialogView(Context context, AlertDialog.Builder alert, EditText editText1, EditText editText2) {
        LinearLayout ll=new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(editText1);
        ll.addView(editText2);
        alert.setView(ll);
    }

    /**
     * Set up the view with one message
     * @param context   Context
     * @param alert     AlertDialog
     * @param message   Message
     */
    public static void setDialogViewMessage(Context context, AlertDialog.Builder alert, String message){
        alert.setMessage(message);
    }

    /**
     * Set up the view with two messages
     * @param context   Context
     * @param alert     AlertDialog
     * @param message1  Message 1
     * @param message2  Message 2
     */
    public static void setDialogViewMessage(Context context, AlertDialog.Builder alert, String message1, String message2){
//        Log.e("setDialogViewMessage", "setDialogViewMessage");
        LinearLayout ll=new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(20, 10, 20, 10);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(layoutParams);
        TextView messageView1 = new TextView(context);
        TextView messageView2 = new TextView(context);
        TextView messageView3 = new TextView(context);
        messageView1.setLayoutParams(layoutParams);
        messageView2.setLayoutParams(layoutParams);
        messageView3.setLayoutParams(layoutParams);
        messageView1.setText(message1);
        messageView2.setText(message2);
        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        messageView3.setText("Card Safe Version " + version);
        ll.addView(messageView1);
        ll.addView(messageView2);
        ll.addView(messageView3);
        alert.setView(ll);

    }

    public static CardView setupCardView(Context c, String[] s) {

        //Set up the Layout Params
        setupCardViewLayoutParams(c, 1);

        //Construct the CardView
        CardView cardView = new CardView(c);
        //Add LinearLayout to CardView
        LinearLayout ll = new LinearLayout(c);
        ll.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(ll);

        //Add Children views into the LinearLayout
        for(String each_s : s){
            TextView textView = new TextView(c);
            textView.setLayoutParams(llp);
            textView.setTextIsSelectable(true);
            textView.setText(each_s);
            ll.addView(textView);
        }
        return cardView;
    }

    private static void setupCardViewLayoutParams(Context c, int noOfTextViews) {
        llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp_title = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int)c.getResources().getDimension(R.dimen.text_img_in_card_margin);
        //Resources r = getResources();
        //int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        //Log.e(TAG, "margin: " + margin);

        //Set different LayoutParams for different number of TextViews
        if(noOfTextViews == 1){
            ViewHelper.llp.setMargins(margin, margin, margin, margin); // llp.setMargins(left, top, right, bottom);
        }else{
            ViewHelper.llp.setMargins(margin, 0, margin, margin); // llp.setMargins(left, top, right, bottom);
        }
        ViewHelper.llp_title.setMargins(margin, margin, margin, 0); // llp.setMargins(left, top, right, bottom);
    }

    public static CardView setupURLCardView(Context c, String[] s) {

        //Set up the Layout Params
        setupCardViewLayoutParams(c, 2);

        //Construct the CardView
        CardView cardView = new CardView(c);
        //Add LinearLayout to CardView
        LinearLayout ll = new LinearLayout(c);
        ll.setOrientation(LinearLayout.VERTICAL);
        cardView.addView(ll);
        //Add URL Title
        String urlTitle = c.getString(R.string.url_contained_title);
        TextView textViewTitle = new TextView(c);
        textViewTitle.setLayoutParams(llp_title);
        textViewTitle.setTextIsSelectable(true);
        textViewTitle.setText(urlTitle);
        ll.addView(textViewTitle);

        //Add Children views into the LinearLayout
        for(String each_s : s){
            TextView textView = new TextView(c);
            textView.setLayoutParams(llp);
            //Allow links
            textView.setAutoLinkMask(Linkify.ALL);
            textView.setTextIsSelectable(true);
            textView.setText(each_s);
            ll.addView(textView);
        }
        return cardView;
    }
}
