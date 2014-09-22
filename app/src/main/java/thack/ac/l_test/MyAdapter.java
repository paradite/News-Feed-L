package thack.ac.l_test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by paradite on 21/9/14.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mDataset;
    OnItemClickListener mItemClickListener;


    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        public ImageView imgViewIcon;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.item_title);
            imgViewIcon = (ImageView) v.findViewById(R.id.item_icon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), mTextView.getText() + " position = " + getPosition(), Toast.LENGTH_SHORT).show();
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
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
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);
        //Set the img
        holder.imgViewIcon.setImageResource(R.drawable.ic_launcher);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
