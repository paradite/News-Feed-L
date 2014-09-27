package thack.ac.l_test;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by paradite on 21/9/14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<StatusItem> mDataset;
    OnItemClickListener mItemClickListener;

    //The term being queried, for highlighting purposes
    private static String queried_term;

    public static void setQueriedTerm(String queried_term) {
        MyAdapter.queried_term = queried_term;
    }

    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextViewTitle;
        public TextView mTextViewContent;
        public TextView mTextViewTime;
        public ImageView imgViewIcon;
        public ImageView imgViewRemoveIcon;
        public ViewHolder(View v) {
            super(v);
            mTextViewTitle = (TextView) v.findViewById(R.id.item_title);
            mTextViewContent = (TextView) v.findViewById(R.id.item_content);
            mTextViewTime = (TextView) v.findViewById(R.id.item_time);
            imgViewIcon = (ImageView) v.findViewById(R.id.item_icon);
            imgViewRemoveIcon = (ImageView) v.findViewById(R.id.remove_icon);

            mTextViewContent.setOnClickListener(this);
            imgViewRemoveIcon.setOnClickListener(this);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //Log.d("View: ", v.toString());
            //Toast.makeText(v.getContext(), mTextViewTitle.getText() + " position = " + getPosition(), Toast.LENGTH_SHORT).show();
            if(v.equals(imgViewRemoveIcon)){
                removeAt(getPosition());
            }else if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<StatusItem> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        StatusItem item = mDataset.get(position);
        // - replace the contents of the view with that element

        //Differentiate different sources

        holder.mTextViewTitle.setText(item.getSource() + item.getUser());
        //Add content and timing to the textview
        String content = item.getContent();
        //Parse the html elements
        Spanned content_spanned = Html.fromHtml(content);
        CharSequence timing = item.getDisplayTime();
        if(queried_term != null){
            //Format the queried term
            String clean_query = queried_term.replaceAll("[^\\w\\s]","");
            //Apply different styles to the term queried term
            final Pattern p = Pattern.compile(clean_query, Pattern.CASE_INSENSITIVE);
            final Matcher matcher = p.matcher(content_spanned);

            final SpannableStringBuilder spannable_content = new SpannableStringBuilder(content_spanned);
            final StyleSpan span = new StyleSpan(Typeface.BOLD);
            while (matcher.find()) {
                spannable_content.setSpan(
                        span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            holder.mTextViewContent.setText(spannable_content);
        }else {
            holder.mTextViewContent.setText(content);
        }

        holder.mTextViewTime.setText(timing);
        //Set the img
        holder.imgViewIcon.setImageDrawable(item.getProfileDrawable());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    //public void setmDataset(ArrayList<StatusItem> dataset){
    //    mDataset = dataset;
    //}

    public void add(StatusItem item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(StatusItem item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

}
