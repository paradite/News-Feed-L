package thack.ac.l_test;

import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.URLEntity;

/**
 * Created by paradite on 23/9/14.
 */
public class StatusItem implements Comparable<StatusItem>{
    private String user;
    private String content;
    private Date created_at;
    private String profile_url;
    private Drawable profileDrawable;
    private URLEntity[] url_contained_twitter;

    public String[] getUrl_contained_plus() {
        return url_contained_plus;
    }

    public void setUrl_contained_plus(String[] url_contained_plus) {
        this.url_contained_plus = url_contained_plus;
    }

    private String[] url_contained_plus;
    private String source;

    public String getSource() {
        return source;
    }

    public URLEntity[] getUrl_contained_twitter() {
        return url_contained_twitter;
    }

    public void setUrl_contained_twitter(URLEntity[] url_contained_twitter) {
        this.url_contained_twitter = url_contained_twitter;
    }

    public StatusItem(String user, String content, Date created_at, String profile_url, String source) {
        this.user = user;
        this.content = content;
        this.created_at = created_at;
        this.profile_url = profile_url;
        //this.profileDrawable = LoadImageFromWebOperations(profile_url);
        //Leave the fetching of profile picture to the adapter to do
        this.url_contained_twitter = null;
        this.source = source;
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

    public CharSequence getDisplayTime(){
        //Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //return formatter.format(this.created_at);
        return DateUtils.getRelativeTimeSpanString(this.created_at.getTime());
    }

    public String getExactTime(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ");
        return formatter.format(this.created_at);
        //return DateUtils.getRelativeTimeSpanString(this.created_at.getTime());
    }

    @Override
    public int compareTo(StatusItem o) {
        return -getCreated_at().compareTo(o.getCreated_at());
    }
}
