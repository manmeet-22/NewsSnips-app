package com.androidexample.newssnips;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.androidexample.newssnips.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving news data from GPA.
 */
public final class QueryUtils {
    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */

    private static final String TAG_HEADLINE = "headline";
    private static final String TAG_CONTENT = "standfirst";
    private static final String TAG_PUB_DATE = "lastModified";
    private static final String TAG_URL = "shortUrl";
    private static final String TAG_IMAGE = "thumbnail";
    private static final String TAG_CONTRIBUTOR = "byline";
    private static final String TAG_STARS= "starRating";

    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject newsResponse = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "response",
            // which represents a list of features (or news).
            JSONArray newsArray = newsResponse.getJSONArray("results");

            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                String headline, content="No Content", date,url,imageUrl="https://placeholdit.imgix.net/~text?txtsize=26&txt=No+preview&w=350&h=215",stars="0",section,contributor="No info";

                JSONObject newsDetails = currentNews.getJSONObject("fields");

                headline = (newsDetails.getString(TAG_HEADLINE));
               if(newsDetails.has(TAG_CONTENT)){
                   content = newsDetails.getString(TAG_CONTENT);
                   content= Html.fromHtml(content).toString();
               }
                date = newsDetails.getString(TAG_PUB_DATE);
                date= date.replace("T"," ");
                date= date.replace("Z","");
                url = newsDetails.getString(TAG_URL);

                if(newsDetails.has(TAG_IMAGE)){
                        imageUrl = newsDetails.getString(TAG_IMAGE);
                }
                if(newsDetails.has(TAG_CONTRIBUTOR)) {
                    contributor = newsDetails.getString(TAG_CONTRIBUTOR);
                }

                if(newsDetails.has(TAG_STARS))
                {
                    stars= newsDetails.getString(TAG_STARS);
                }

                Log.v(TAG_HEADLINE, headline);
            //    Log.v(TAG_CONTENT, content);
                Log.v(TAG_PUB_DATE, date);
                Log.v(TAG_URL, url);
                Log.v(TAG_IMAGE, imageUrl);
               Log.v(TAG_CONTRIBUTOR,contributor);
                Log.v(TAG_STARS,stars);

//                News newsSend = new News(headline, content, date, url, imageUrl,contributor,section,stars);

                News newsSend = new News(headline, content, date, url, imageUrl,contributor);

                // Add the new {@link News} to the list of news.
                news.add(newsSend);
            }
        }catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);

            e.printStackTrace();
        }


        // Return the list of news
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                return jsonResponse;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<News> fetchBooksData(String requestURL) {

        Log.i(LOG_TAG,"fetchBookData called");
       /*try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        URL url= createUrl(requestURL);

        String jsonResponse =null;
        try{
            jsonResponse= makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem making HTTP request.",e);
        }

        List<News> news =extractFeatureFromJson(jsonResponse);
        return news;
    }
}