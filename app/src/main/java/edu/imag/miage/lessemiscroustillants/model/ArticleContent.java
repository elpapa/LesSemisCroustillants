package edu.imag.miage.lessemiscroustillants.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.imag.miage.lessemiscroustillants.ArticleListActivity;
import edu.imag.miage.lessemiscroustillants.MyApplication;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ArticleEntry;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ArticleContent {

    /**
     * An array of article items.
     */
    public static final List<ArticleItem> ITEMS = new ArrayList<ArticleItem>();

    /**
     * A map article items, by ID.
     */

    public static final Map<String, ArticleItem> ITEM_MAP = new HashMap<String, ArticleItem>();

    public static final int COL_ARTICLE_ID = 0;
    public static final int COL_ARTICLE_NAME = 1;
    public static final int COL_GENERIC_NAME = 2;
    public static final int COL_BRAND = 3;
    public static final int COL_WEIGHT = 4;
    public static final int COL_BARCODE = 5;

    static {

        Cursor mCursor = MyApplication.getAppContext().getContentResolver().query(
                ArticleEntry.CONTENT_URI,
                null,null,null,null,null);

        while(mCursor.moveToNext()){
            addItem(createArticleItem(
                    mCursor.getString(COL_ARTICLE_ID),
                    mCursor.getString(COL_ARTICLE_NAME),
                    mCursor.getString(COL_GENERIC_NAME),
                    mCursor.getString(COL_BRAND),
                    mCursor.getString(COL_WEIGHT),
                    String.valueOf(mCursor.getLong(COL_BARCODE))
            ));
        }
        mCursor.close();
    }

    private static void addItem(ArticleItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ArticleItem createArticleItem(String id, String article_name, String generic_name, String brand, String weight, String barcode) {
        return new ArticleItem(id, article_name, makeDetails(article_name,generic_name,brand,weight,barcode));
    }

    private static String makeDetails(String article_name, String generic_name, String brand, String weight, String barcode) {
        StringBuilder builder = new StringBuilder();
        builder.append("Détails pour ").append(article_name).append("\n");
        builder.append("\nNom générique : ").append(generic_name);
        builder.append("\nMarque : ").append(brand);
        builder.append("\nPoids : ").append(weight);
        builder.append("\nCodeBarre : ").append(barcode);

        return builder.toString();
    }

    /**
     * A article item representing a piece of content.
     */
    public static class ArticleItem {
        public final String id;
        public final String content;
        public final String details;

        public ArticleItem(String id, String article_name, String details) {
            this.id = id;
            this.content = article_name;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
