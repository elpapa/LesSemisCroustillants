package edu.imag.miage.lessemiscroustillants.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by boris on 14/04/2016.
 */
public class ArticleContract {

    public static final String CONTENT_AUTHORITY = "edu.imag.miage.lessemiscroustillants.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARTICLE = "article";


    public static final class ArticleEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";

        public static final String COLUMN_ARTICLE_NAME = "article_name";
        public static final String COLUMN_GENERIC_NAME = "generic_name";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_BARCODE = "barcode";
        //public static final String COLUMN_INGREDIENTS = "ingredients";

        public static Uri buildArticleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArticle(String barcode) {
            return CONTENT_URI.buildUpon().appendPath(barcode).build();
        }

        public static String getBarcodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class StockEntry implements BaseColumns{
        public static final String TABLE_NAME = "stock";

        public static final String COLUMN_STOCK_NAME = "stock_name";
    }

    public static final class ProductEntry implements BaseColumns{
        public static final String TABLE_NAME = "product";

        public static final String COLUMN_ARTICLE_KEY = "article_id";
        public static final String COLUMN_STOCK_KEY = "stock_id";
        public static final String COLUMN_PRODUCT_QUANTITE = "quantite";

    }



}
