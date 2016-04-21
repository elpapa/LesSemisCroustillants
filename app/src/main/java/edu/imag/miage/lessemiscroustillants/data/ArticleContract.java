package edu.imag.miage.lessemiscroustillants.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by boris on 14/04/2016.
 */
public class ArticleContract {

    public static final String CONTENT_AUTHORITY = "edu.imag.miage.lessemiscroustillants.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARTICLE = "article";
    public static final String PATH_STOCK = "stock";
    public static final String PATH_PRODUCT = "product";

    public static final class ArticleEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";

        public static final String COLUMN_ARTICLE_ID = "article_id";
        public static final String COLUMN_ARTICLE_NAME = "article_name";
        public static final String COLUMN_GENERIC_NAME = "generic_name";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_BARCODE = "barcode";
        //public static final String COLUMN_INGREDIENTS = "ingredients";

        public static Uri buildArticleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildArticleWithName(String article_name){
            return CONTENT_URI.buildUpon().appendPath(article_name).build();
        }

        public static Uri buildArticle(String barcode) {
            return CONTENT_URI.buildUpon().appendPath(barcode).build();
        }

        public static String getBarcodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getArticleNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class StockEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        public static final String TABLE_NAME = "stock";

        public static final String COLUMN_STOCK_ID = "stock_id";

        public static final String COLUMN_STOCK_NAME = "stock_name";

        public static Uri buildStockUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildStockWithName(String stock_name){
            return CONTENT_URI.buildUpon().appendPath(stock_name).build();
        }
    }

    public static final class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "product";

        public static final String COLUMN_PRODUCT_ID = "product_id";

        public static final String COLUMN_ARTICLE_KEY = "article_id";
        public static final String COLUMN_STOCK_KEY = "stock_id";
        public static final String COLUMN_PRODUCT_QUANTITE = "quantite";
        public static final String COLUMN_PRODUCT_DATE_LIMITE = "date_limite";

        public static Uri buildProductsWithStockName(String stock_name){
            return CONTENT_URI.buildUpon().appendPath(stock_name).build();
        }

        public static Uri buildProductWithStockIdAndArticleId(String stockId, String articleId){
            return CONTENT_URI.buildUpon().appendPath(stockId).appendPath(articleId).build();
        }

        public static String getBarcodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getStockNameFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getStockIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getArticleIdFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

    }



}
