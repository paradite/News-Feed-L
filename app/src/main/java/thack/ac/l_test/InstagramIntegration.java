package thack.ac.l_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class to provide methods to integrate with Instagram
 * Created by paradite on 27/9/14.
 */
public class InstagramIntegration {
    public static final String TAG = "InstagramIntegration: ";
    public static final String APIURL = "https://api.instagram.com/v1";
    public static final String CLIENTID = "bc83eecbf2094edfa177eb9db64f0d9b";

    public static String buildURL(String query){
        return APIURL + "/tags/" + query + "/media/recent" + "?client_id=" + CLIENTID + "&count=" + Utils.MAX_COUNT;
    }

    /**
     * Main method to fetch data from Instagram
     * @param query
     * @return  ArrayList<StatusItem>
     */
    public static ArrayList<StatusItem> newSearch(String query) throws IOException, JSONException {
        String urlString = buildURL(query);
        URL url = new URL(urlString);
        InputStream inputStream = url.openConnection().getInputStream();
        String response = Utils.streamToString(inputStream);
        //Log.e(TAG, response);
        if(response == null){
            return null;
        }else{
            ArrayList<StatusItem> items = new ArrayList<StatusItem>();
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //Loop through and construct StatusItem
            for (int i = 0; i < jsonArray.length(); i++) {
                //Log.e(TAG, jsonArray.getJSONObject(i).getJSONObject("caption").toString());
                JSONObject imageJsonObject = jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");
                String imageUrlString = imageJsonObject.getString("url");
                JSONObject userJsonObject = jsonArray.getJSONObject(i).getJSONObject("user");
                String username = userJsonObject.getString("username");
                String profile_picture = userJsonObject.getString("profile_picture");
                String caption = "";
                if(jsonArray.getJSONObject(i).get("caption") != JSONObject.NULL){
                    JSONObject captionJsonObject = jsonArray.getJSONObject(i).getJSONObject("caption");
                    caption = captionJsonObject.getString("text");
                }
                int timeStamp = jsonArray.getJSONObject(i).getInt("created_time");
                Date time = new Date((long)timeStamp*1000);
                //Log.e(TAG, username);
                //Log.e(TAG, caption);
                //Log.e(TAG, time.toString());
                //Log.e(TAG, profile_picture);
                StatusItem new_item = new StatusItem(username, caption, time, profile_picture, Utils.SOURCE_INSTA);

                //Add image to the item, specific to Instagram
                new_item.setContent_pic_url(imageUrlString);

                items.add(new_item);
            }
            return items;
        }
    }

}
