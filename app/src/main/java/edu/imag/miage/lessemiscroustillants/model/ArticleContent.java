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

    private static final int COUNT = 25;

    /*static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createArticleItem(i));
        }
    }*/

    static {

        Cursor mCursor = MyApplication.getAppContext().getContentResolver().query(
                ArticleEntry.CONTENT_URI,
                new String[]{ArticleEntry._ID ,ArticleEntry.COLUMN_ARTICLE_NAME},
                null,null,null,null);

        while(mCursor.moveToNext()){
            addItem(createArticleItem(
                    mCursor.getString(0),
                    mCursor.getString(1)
            ));
        }
        mCursor.close();
    }

    private static void addItem(ArticleItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ArticleItem createArticleItem(String id, String article_name) {
        return new ArticleItem(id, article_name, makeDetails(Integer.parseInt(id)));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
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
