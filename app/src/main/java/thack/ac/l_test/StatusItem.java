package thack.ac.l_test;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import twitter4j.URLEntity;

/**
 * Created by paradite on 23/9/14.
 */
public class StatusItem {
    private String user;
    private String content;
    private Date created_at;
    private String profile_url;
    private Drawable profileDrawable;
    private URLEntity[] url_contained;

    public URLEntity[] getUrl_contained() {
        return url_contained;
    }

    public void setUrl_contained(URLEntity[] url_contained) {
        this.url_contained = url_contained;
    }

    public StatusItem(String user, String content, Date created_at, String profile_url) {
        this.user = user;
        this.content = content;
        this.created_at = created_at;
        this.profile_url = profile_url;
        //this.profileDrawable = LoadImageFromWebOperations(profile_url);
        //Leave the fetching of profile picture to the adapter to do
        this.url_contained = null;
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
}
