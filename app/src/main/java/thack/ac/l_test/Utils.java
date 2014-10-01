package thack.ac.l_test;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Utility Class containing helper methods
 * Created by paradite on 30/9/14.
 */
public class Utils {
    public static final int    MAX_RESULTS_INSTA   = 10;
    static final        int    MAX_RESULTS_TWITTER = 10;
    static              int    MAX_RESULTS_PLUS    = 10;
    public static       String SOURCE_PLUS         = "+";
    public static       String SOURCE_TWITTER      = "@";
    public static       String SOURCE_INSTA        = "#";

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static Drawable LoadImageFromWebOperations(String url, int compression) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inSampleSize = compression;
            Bitmap myBitmap = BitmapFactory.decodeStream(is, null, options);
            Drawable d = new BitmapDrawable(Resources.getSystem(), myBitmap);
            //Drawable d = Drawable.createFromStream(is, "src name");
            //Log.d("Converter: ", url + d.toString());
            return d;
        } catch (Exception e) {
            Log.d("Exception: ", e.toString());
            return null;
        }
    }

    public static java.util.Date parseRFC3339Date(String datestring) throws java.text.ParseException, IndexOutOfBoundsException{
        Date d = new Date();

        //if there is no time zone, we don't need to do any special parsing.
        if(datestring.endsWith("Z")){
            try{
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//spec for RFC3339
                s.setTimeZone(TimeZone.getTimeZone("UTC"));
                d = s.parse(datestring);
            }
            catch(java.text.ParseException pe){//try again with optional decimals
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//spec for RFC3339 (with fractional seconds)
                s.setTimeZone(TimeZone.getTimeZone("UTC"));
                s.setLenient(true);
                d = s.parse(datestring);
            }
            return d;
        }

        //step one, split off the timezone.
        String firstpart = datestring.substring(0,datestring.lastIndexOf('-'));
        String secondpart = datestring.substring(datestring.lastIndexOf('-'));

        //step two, remove the colon from the timezone offset
        secondpart = secondpart.substring(0,secondpart.indexOf(':')) + secondpart.substring(secondpart.indexOf(':')+1);
        datestring  = firstpart + secondpart;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//spec for RFC3339
        s.setTimeZone(TimeZone.getTimeZone("UTC"));
        try{
            d = s.parse(datestring);
        }
        catch(java.text.ParseException pe){//try again with optional decimals
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");//spec for RFC3339 (with fractional seconds)
            s.setTimeZone(TimeZone.getTimeZone("UTC"));
            s.setLenient(true);
            d = s.parse(datestring);
        }
        return d;
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

    static String getStringSpaceAlphanumeric(String s) {
        return s.replaceAll("[^\\w\\s]", "");
    }

    static String getStringAlphanumeric(String s) {
        return s.replaceAll("[^\\w]", "");
    }
}
