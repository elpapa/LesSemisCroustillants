package edu.imag.miage.lessemiscroustillants;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract;

/**
 * Created by boris on 15/04/2016.
 */
public class FetchArticleTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchArticleTask.class.getSimpleName();

    private final Context context;
    // private ArrayAdapter<String> articleAdapter;

    public FetchArticleTask(android.content.Context context) {
        this.context = context;
    }

    private boolean DEBUG = true;

    private String getArticleFromJson(String articleJsonStr) throws JSONException {

        final String OWN_G_NAME = "generic_name";
        final String OWN_A_NAME = "product_name";
        final String OWN_BRAND = "brands";
        final String OWN_WEIGHT = "quantity";
        final String OWN_BARCODE = "code";
        final String OWN_IMAGE = "image_thumb_url";

        final String OWN_PRODUCT = "product";
        final String OWN_STATUS = "status";



        JSONObject articleJson = new JSONObject(articleJsonStr);

        Log.d("JsonObject : ", articleJson.toString());

        if(articleJson.getInt(OWN_STATUS) == 0){
            return null;
        }
        JSONObject articleObject = articleJson.getJSONObject(OWN_PRODUCT);

        // Vector<ContentValues> cVVector = new Vector<>(articleObject.length());

        String articleName;
        String genericName;
        String brand;
        String weight;
        long barcode;
        String url_thumb;


        articleName = articleObject.getString(OWN_A_NAME);
        genericName = articleObject.getString(OWN_G_NAME);
        brand = articleObject.getString(OWN_BRAND);
        weight = articleObject.getString(OWN_WEIGHT);
        barcode = articleObject.getLong(OWN_BARCODE);
        url_thumb = articleObject.getString(OWN_IMAGE);

        addArticle(articleName, genericName, brand, barcode, weight, url_thumb);

        /*ContentValues articleValues = new ContentValues();

        articleValues.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME, articleName);
        articleValues.put(ArticleContract.ArticleEntry.COLUMN_GENERIC_NAME, genericName);
        articleValues.put(ArticleContract.ArticleEntry.COLUMN_BRAND, brand);
        articleValues.put(ArticleContract.ArticleEntry.COLUMN_WEIGHT, weight);
        articleValues.put(ArticleContract.ArticleEntry.COLUMN_BARCODE, barcode);

        cVVector.add(articleValues);

        if (cVVector.size() > 0){
            /*ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(ArticleContract.ArticleEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchArticleTask Complete. " + cVVector.size() + " Inserted");*/

        return articleName;
    }

    long addArticle(String article_name, String generic_name, String brand, long barcode, String weight, String url_thumb) {
        long articleId;

        Cursor articleCursor = context.getContentResolver().query(
                ArticleContract.ArticleEntry.CONTENT_URI,
                new String[]{ArticleContract.ArticleEntry._ID},
                ArticleContract.ArticleEntry.COLUMN_BARCODE + " = ?",
                new String[]{Long.toString(barcode)},
                null);

        if (articleCursor.moveToFirst()) {
            int articleIdIndex = articleCursor.getColumnIndex(ArticleContract.ArticleEntry._ID);
            articleId = articleCursor.getLong(articleIdIndex);

            //A modif : et retourner un message qui indique que l'article existe deja
            //
            //
            //
        } else {

            ContentValues articleValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME, article_name);
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_WEIGHT, weight);
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_GENERIC_NAME, generic_name);
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_BRAND, brand);
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_BARCODE, barcode);
            articleValues.put(ArticleContract.ArticleEntry.COLUMN_IMAGE, url_thumb);

            // Finally, insert location data into the database.
            Uri insertedUri = context.getContentResolver().insert(
                    ArticleContract.ArticleEntry.CONTENT_URI,
                    articleValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            articleId = ContentUris.parseId(insertedUri);
        }
        articleCursor.close();
        return articleId;
    }

    @Override
    protected String doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        String barcode = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String articleJsonStr = null;

        try {

            final String ARTICLE_BASE_URL = "http://fr.openfoodfacts.org/api/v0/produit/";

            Uri builtUri = Uri.parse(ARTICLE_BASE_URL).buildUpon().appendPath(barcode).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            articleJsonStr = buffer.toString();
            Log.d("JsonStr : ", articleJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getArticleFromJson(articleJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String articleStr) {
        if (articleStr != null) {
            Toast.makeText(context, R.string.add_success, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.no_article, Toast.LENGTH_LONG).show();
        }

    }
}

