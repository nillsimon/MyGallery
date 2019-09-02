package com.nillsimon.mygallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlickFetcher {
    private static final String TAG = "FlickFetcher";
    private static final String API_KEY = "cfd200d92fe8e3b014af6ebabf65d2b0";
   // private static final String API_KEY = "a713e5f0aa7ff8eb04d486520c2ec933";


    public String getJSONString(String UrlSpec) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(UrlSpec)
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        return result;
    }
    public List<GalleryItem> fetchItems(){
        List<GalleryItem> galleryItems = new ArrayList<>();
        try{
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent" )
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getJSONString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(galleryItems, jsonBody);
        }catch (IOException ioe){
            Log.e(TAG, "Ошибка загрузки данных" , ioe);
        }catch (JSONException joe){
            Log.e(TAG, "Ошибка парсинга данных", joe);
        }
        return galleryItems;
    }
    private void parseItems(List<GalleryItem>items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setUrl(photosJsonObject.getString("url_s"));
            items.add(item);
        }

    }
}
