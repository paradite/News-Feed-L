package thack.ac.l_test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
        return APIURL + "/tags/" + query + "/media/recent" + "?client_id=" + CLIENTID;
    }

    /**
     * Main method to fetch data from Instagram
     * @param query
     * @return  ArrayList<StatusItem>
     */
    public static ArrayList<StatusItem> run(String query){
        String urlString = buildURL(query);
        try {
            URL url = new URL(urlString);
            InputStream inputStream = url.openConnection().getInputStream();
            String response = streamToString(inputStream);
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
                    JSONObject imageJsonObject = jsonArray.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");
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
                    StatusItem new_item = new StatusItem(username, caption, time, profile_picture, MainActivity.SOURCE_INSTA);

                    //Add image to the item, specific to Instagram
                    new_item.setContent_pic_url(imageUrlString);

                    items.add(new_item);
                }
                return items;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method that returns String from the InputStream given by p_is
     * @param p_is The given InputStream
     * @return The String from the InputStream
     */
    public static String streamToString(InputStream p_is)
    {
        try
        {
            BufferedReader m_br;
            StringBuffer m_outString = new StringBuffer();
            m_br = new BufferedReader(new InputStreamReader(p_is));
            String m_read = m_br.readLine();
            while(m_read != null)
            {
                m_outString.append(m_read);
                m_read =m_br.readLine();
            }
            return m_outString.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
