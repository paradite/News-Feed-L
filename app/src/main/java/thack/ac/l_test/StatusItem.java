package thack.ac.l_test;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by paradite on 23/9/14.
 */
public class StatusItem {
    private String user;
    private String content;
    private Date created_at;
    private String profile_url;
    private Drawable profileDrawable;

    public StatusItem(String user, String content, Date created_at, String profile_url) {
        this.user = user;
        this.content = content;
        this.created_at = created_at;
        this.profile_url = profile_url;
        this.profileDrawable = LoadImageFromWebOperations(profile_url);
    }

    public String getProfile_url() {
        return profile_url;
    }

    public Drawable getProfileDrawable() {
        return profileDrawable;
    }

    public void setProfileDrawable(Drawable profileDrawable) {
        this.profileDrawable = profileDrawable;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            Log.d("Converter: ", url + d.toString());
            return d;
        } catch (Exception e) {
            Log.d("Exception: ", e.toString());
            return null;
        }
    }
}
