package edu.imag.miage.lessemiscroustillants.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.sql.Date;
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

    public static final int COL_PRODUCT_ID = 0;
    public static final int COL_ARTICLE_ID = 1;
    public static final int COL_STOCK_ID = 2;
    public static final int COL_QUANTITE = 3;
    public static final int COL_DATE_LIMITE = 4;
    public static final int COL_ARTICLE_NAME = 6;
    public static final int COL_GENERIC_NAME = 7;
    public static final int COL_BRAND = 8;
    public static final int COL_WEIGHT = 9;
    public static final int COL_BARCODE = 10;
    public static final int COL_STOCK_NAME = 12;

    public static final int I_COL_PRODUCT_ID = 0;
    public static final int I_COL_ARTICLE_ID = 1;
    public static final int I_COL_STOCK_ID = 2;
    public static final int I_COL_QUANTITE = 3;
    public static final int I_COL_DATE_LIMITE = 4;
    public static final int I_COL_ARTICLE_NAME = 5;
    public static final int I_COL_GENERIC_NAME = 6;
    public static final int I_COL_BRAND = 7;
    public static final int I_COL_WEIGHT = 8;
    public static final int I_COL_BARCODE = 9;
    public static final int I_COL_STOCK_NAME = 10;

    static {
        List<String> item = new ArrayList<>();
        Cursor mCursor = MyApplication.getAppContext().getContentResolver().query(
                ArticleContract.ProductEntry.CONTENT_URI,
                null,null,null,null,null);
int i=0;
        if (mCursor != null) {
            while(mCursor.moveToNext()){
                Log.d("iteration : ", "Itération " + i++);

                item.add(mCursor.getString(COL_PRODUCT_ID));
                item.add(mCursor.getString(COL_ARTICLE_ID));
                item.add(mCursor.getString(COL_STOCK_ID));
                item.add(mCursor.getString(COL_QUANTITE));
                item.add(mCursor.getString(COL_DATE_LIMITE));
                item.add(mCursor.getString(COL_ARTICLE_NAME));
                item.add(mCursor.getString(COL_GENERIC_NAME));
                item.add(mCursor.getString(COL_BRAND));
                item.add(mCursor.getString(COL_WEIGHT));
                item.add(mCursor.getString(COL_BARCODE));
                item.add(mCursor.getString(COL_STOCK_NAME));

                addItem(createArticleItem(
                        item.get(I_COL_PRODUCT_ID),
                        item.get(I_COL_ARTICLE_NAME),
                        item.get(I_COL_QUANTITE),
                        item
                ));
                item.clear();

            }
        mCursor.close();
        }
    }

    private static void addItem(ArticleItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ArticleItem createArticleItem(String id, String article_name, String quantite, List<String> details) {
        return new ArticleItem(id, article_name, quantite, makeDetails(article_name, quantite, details));
    }

    private static String makeDetails(String article_name, String quantite, List<String> details) {
        StringBuilder builder = new StringBuilder();
        builder.append("Détails pour ").append(article_name).append("\n");
        builder.append("\nNom générique : ").append(details.get(I_COL_GENERIC_NAME));
        builder.append("\nMarque : ").append(details.get(I_COL_BRAND));
        builder.append("\nPoids : ").append(details.get(I_COL_WEIGHT));
        builder.append("\nQuantité : ").append(details.get(I_COL_QUANTITE));

        Date date_limite = new Date(Long.parseLong(details.get(I_COL_DATE_LIMITE)));

        builder.append("\nDate de péremption : ").append(date_limite.toString());
        builder.append("\nCodeBarre : ").append(details.get(I_COL_BARCODE));
        builder.append("\n\nStocké dans : ").append(details.get(I_COL_STOCK_NAME));

        return builder.toString();
    }

    /**
     * A article item representing a piece of content.
     */
    public static class ArticleItem {
        public final String id;
        public final String content;
        public final String quantite;
        public final String details;

        public ArticleItem(String id, String article_name, String quantite, String details) {
            this.id = id;
            this.content = article_name;
            this.quantite = quantite;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
