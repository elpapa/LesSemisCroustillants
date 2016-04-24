package edu.imag.miage.lessemiscroustillants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by boris on 22/04/2016.
 */
public class FetchImageArticleTask extends AsyncTask<String, Void, Bitmap> {

    private final String LOG_TAG = FetchArticleTask.class.getSimpleName();


    @Override
    protected Bitmap doInBackground(String... params) {
        if (params.length == 0){
            return null;
        }

        String url_image = params[0];
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url_image).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
