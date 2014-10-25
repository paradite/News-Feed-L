package thack.ac.whatsoever;

import android.os.AsyncTask;

/**
* Created by paradite on 25/10/14.
*/
public class DownloadImagesTask extends AsyncTask<StatusItem, Void, Void> {
    int position;
    private MyAdapter    mAdapterRef;
    private MainActivity mainActivity;

    //Record the position of item
    public DownloadImagesTask(int pos, MyAdapter adapter, MainActivity activity) {
        position = pos;
        mAdapterRef = adapter;
        mainActivity = activity;
    }

    @Override
    protected Void doInBackground(StatusItem... statusItems) {
        //Log.e(TAG, "DownloadImagesTask: ");
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
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mAdapterRef.notifyItemChanged(position);
            }
        });
        mainActivity.pic_download_tasks--;
        //Log.e(TAG, "pic: " + pic_download_tasks);
        mainActivity.tryDismissBar();
    }
}
