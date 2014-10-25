package thack.ac.whatsoever;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by paradite on 21/9/14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final int MAX_LENGTH_CONTENT = 200;
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
        public CardView       mCardView;
        public TextView       mTextViewTitle;
        public TextView       mTextViewContent;
        public ImageView      mImageViewContentPic;
        public TextView       mTextViewTime;
        public TextView       mTextViewLocation;
        public ImageView      imgViewIcon;
        public ImageView      imgViewRemoveIcon;
        public RelativeLayout mCardViewWrapper;
        public ProgressBar    mProfileProgress;
        public ProgressBar    mPictureProgress;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mCardViewWrapper = (RelativeLayout) v.findViewById(R.id.card_wrapper);
            mTextViewTitle = (TextView) v.findViewById(R.id.item_title);
            mTextViewContent = (TextView) v.findViewById(R.id.item_content);
            mImageViewContentPic = (ImageView) v.findViewById(R.id.item_content_pic);
            mTextViewTime = (TextView) v.findViewById(R.id.item_time);
            mTextViewLocation = (TextView) v.findViewById(R.id.item_location);
            imgViewIcon = (ImageView) v.findViewById(R.id.item_icon);
            imgViewRemoveIcon = (ImageView) v.findViewById(R.id.remove_icon);
            mProfileProgress = (ProgressBar) v.findViewById(R.id.progressBarProfilePic);
            mPictureProgress = (ProgressBar) v.findViewById(R.id.progressBarPicture);

            mTextViewContent.setOnClickListener(this);
            imgViewRemoveIcon.setOnClickListener(this);
            v.setOnClickListener(this);
            mTextViewContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, getPosition());
                    }
                    return false;
                }
            });
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
        Log.e("onBindViewHolder:", " pos: " + position + ", profile: " + item.getProfileDrawable());
        // - replace the contents of the view with that element

        //Differentiate different sources
        //Reference: http://colour.charlottedann.com/ and http://stackoverflow.com/questions/15852122/hex-transparency-in-colors

        //Set username and source
        holder.mTextViewTitle.setText(item.getSource() + item.getUser());
        //Apply colors for different sources
        if(item.getSource().equals(Utils.SOURCE_TWITTER)){
            holder.mTextViewTitle.setTextColor(Color.parseColor("#0084B4"));
            holder.mCardViewWrapper.setBackgroundColor(Color.parseColor("#330084B4"));
        }else if(item.getSource().equals(Utils.SOURCE_PLUS)){
            holder.mTextViewTitle.setTextColor(Color.parseColor("#DD4B39"));
            holder.mCardViewWrapper.setBackgroundColor(Color.parseColor("#33DD4B39"));
        }else if(item.getSource().equals(Utils.SOURCE_INSTA)){
            holder.mTextViewTitle.setTextColor(Color.parseColor("#675144"));
            holder.mCardViewWrapper.setBackgroundColor(Color.parseColor("#66675144"));
        }else{
            holder.mCardViewWrapper.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
        }

        //Set content
        String content = item.getContent();
        //Parse the html elements
        Spanned content_spanned = Html.fromHtml(content);
        //Get part of the string if too long
        if(content_spanned.length() > 200){
            content_spanned = (Spanned)TextUtils.concat(content_spanned.subSequence(0, MAX_LENGTH_CONTENT), "...\n(Click to read more)");
        }
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
        //Set the time
        holder.mTextViewTime.setText(item.getDisplayTime());
        //Set the location
        if(item.getLocation() != null){
            holder.mTextViewLocation.setText("at " + item.getLocation());
        }else{
            holder.mTextViewLocation.setText(null);
        }

        //Set the profile pic
        //Assume image is loading, show default loading progress
        //If it does not have image, hide the view
        if(item.getProfile_url() == null || item.getProfile_url().isEmpty()){
            holder.imgViewIcon.setVisibility(View.GONE);
            holder.mProfileProgress.setVisibility(View.GONE);
        }else if(item.getProfileDrawable() != null){
            //Loaded image when ready
            holder.imgViewIcon.setImageDrawable(item.getProfileDrawable());
            holder.mProfileProgress.setVisibility(View.GONE);
            holder.imgViewIcon.setVisibility(View.VISIBLE);
        }

        //Set content image (for Instagram)
        //Assume image is loading, show default loading progress
        //If it does not have image, hide both the view and the progress
        if(item.getContent_pic_url() == null || item.getContent_pic_url().isEmpty()){
            holder.mImageViewContentPic.setVisibility(View.GONE);
            holder.mPictureProgress.setVisibility(View.GONE);
        }else if(item.getContentDrawable() != null){
            //Loaded image when ready
            holder.mImageViewContentPic.setImageDrawable(item.getContentDrawable());
            holder.mPictureProgress.setVisibility(View.GONE);
            holder.mImageViewContentPic.setVisibility(View.VISIBLE);
        }
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
