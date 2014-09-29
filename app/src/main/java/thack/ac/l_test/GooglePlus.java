/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package thack.ac.l_test;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.services.plus.model.Activity;

import java.io.IOException;
import java.util.List;

/**
 * Simple example that demonstrates how to use <a
 * href="code.google.com/p/google-http-java-client/">Google HTTP Client Library for Java</a> with
 * the <a href="https://developers.google.com/+/api/">Google+ API</a>.
 *
 * <p>
 * Note that in the case of the Google+ API, there is a much better custom library built on top of
 * this HTTP library that is much easier to use and hides most of these details for you. See <a
 * href="http://code.google.com/p/google-api-java-client/wiki/APIs#Google+_API">Google+ API for
 * Java</a>.
 * </p>
 *
 * @author Yaniv Inbar
 */

/**
 * Expanded by Zhu Liang to include more search and parse functions
 */
public class GooglePlus {

    private static final String API_KEY =
            "AIzaSyDcxcfG5-6X-ouovEBKUoeFJ9e19445CTM";

    private static final String USER_ID = "116899029375914044550";
    private static final int MAX_RESULTS = 10;

    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Feed of Google+ activities. */
    public static class ActivityFeed {

        /** List of Google+ activities. */
        @Key("items")
        private List<Activity> activities;

        public List<Activity> getActivities() {
            return activities;
        }
    }

    /** Google+ URL. */
    public static class PlusUrl extends GenericUrl {

        public PlusUrl(String encodedUrl) {
            super(encodedUrl);
        }

        @SuppressWarnings("unused")
        @Key
        private final String key = API_KEY;

        /** Maximum number of results. */
        @Key
        private int maxResults;

        public int getMaxResults() {
            return maxResults;
        }

        public PlusUrl setMaxResults(int maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        /** Lists the public activities for the given Google+ user ID. */
        public static PlusUrl listPublicActivities(String userId) {
            return new PlusUrl(
                    "https://www.googleapis.com/plus/v1/people/" + userId + "/activities/public");
        }

        /**
         * List the search result of a query in public activities
         * @param query
         * @return
         */
        public static PlusUrl listSearchResult(String query) {
            return new PlusUrl(
                    "https://www.googleapis.com/plus/v1/activities?query=" + query);
        }
    }

    private static List<Activity> parseResponse(HttpResponse response) throws IOException {
        ActivityFeed feed = response.parseAs(ActivityFeed.class);
        Log.d("Google+ response: ", feed.getActivities().toString());


        if (feed.getActivities().isEmpty()) {
            Log.d("Google+:","No activities found.");
        } else {
            //if (feed.getActivities().size() == MAX_RESULTS) {
            //    System.out.print("First ");
            //}
            Log.d("Google+:",feed.getActivities().size() + " activities found:");
            //for (Activity activity : feed.getActivities()) {
            //    Log.d("Google+:", "-----------------------------------------------");
            //    Log.d("Google+:", "HTML Content: " + activity.getObject().getContent());
            //    Log.d("Google+:", "+1's: " + activity.getObject().getPlusoners().getTotalItems());
            //    Log.d("Google+:", "URL: " + activity.getUrl());
            //    Log.d("Google+:", "ID: " + activity.get("id"));
            //}
        }
        return feed.getActivities();
    }

    public static List<Activity> run(String query) throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });
        PlusUrl url = PlusUrl.listSearchResult(query).setMaxResults(MAX_RESULTS);
        //url.put("fields", "items(id,url,object(content,plusoners/totalItems))");
        HttpRequest request = requestFactory.buildGetRequest(url);
        return parseResponse(request.execute());
    }

    //public static void main(String[] args) {
    //    if (API_KEY.startsWith("Enter ")) {
    //        System.err.println(API_KEY);
    //        System.exit(1);
    //    }
    //    try {
    //        try {
    //            newSearch();
    //            return;
    //        } catch (HttpResponseException e) {
    //            System.err.println(e.getMessage());
    //        }
    //    } catch (Throwable t) {
    //        t.printStackTrace();
    //    }
    //    System.exit(1);
    //}
}