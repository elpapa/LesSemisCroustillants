package edu.imag.miage.lessemiscroustillants.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ArticleEntry;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.StockEntry;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ProductEntry;
/**
 * Created by boris on 14/04/2016.
 */
public class ArticleProvider extends ContentProvider {


    private static final UriMatcher uriMatcher = buildUriMatcher();
    private ArticleOpenHelper productHelper;

    static final int ARTICLE = 100;
    static final int ARTICLE_WITH_BARCODE = 101;
    static final int ARTICLE_WITH_NAME = 102;
    static final int STOCK = 200;
    static final int STOCK_WITH_NAME = 201;
    static final int PRODUCT = 300;
    static final int PRODUCT_WITH_BARCODE = 301;
    static final int PRODUCT_WITH_STOCK_NAME = 302;
    static final int PRODUCT_WITH_STOCK_ID_AND_ARTICLE_ID = 303;

    private static final SQLiteQueryBuilder productQueryBuilder;

    static{
        productQueryBuilder = new SQLiteQueryBuilder();

        productQueryBuilder.setTables(
                ProductEntry.TABLE_NAME + " JOIN " +
                        ArticleEntry.TABLE_NAME + " ON " +
                        ArticleEntry.TABLE_NAME + "." + ArticleEntry._ID + " = " +
                        ProductEntry.COLUMN_ARTICLE_KEY + " JOIN " +
                        StockEntry.TABLE_NAME + " ON " +
                        StockEntry.TABLE_NAME + "." + StockEntry._ID + " = " + ProductEntry.COLUMN_STOCK_KEY
        );

        Log.d("ArticleProvider", productQueryBuilder.getTables());
    }

    private static final String productBarcodeSelection =
            ArticleContract.ProductEntry.TABLE_NAME +
            "." + ArticleContract.ArticleEntry.COLUMN_BARCODE + " = ?";

    private static final String articleBarcodeSelection =
            ArticleContract.ArticleEntry.TABLE_NAME +
                    "." + ArticleContract.ArticleEntry.COLUMN_BARCODE + " = ?";

    private static final String productStockNameSelection =
            ArticleContract.StockEntry.TABLE_NAME +
                    "." + ArticleContract.StockEntry.COLUMN_STOCK_NAME + " = ?";

    private static final String productStockIdAndArticleIdSelection =
            ArticleContract.ProductEntry.TABLE_NAME +
                    "." + ArticleContract.ProductEntry.COLUMN_STOCK_KEY + " = ? AND " +
                    ArticleContract.ProductEntry.TABLE_NAME +
                    "." + ArticleContract.ProductEntry.COLUMN_ARTICLE_KEY + " = ?";

    private static final String articleNameSelection =
            ArticleContract.ArticleEntry.TABLE_NAME +
                    "." + ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME + " = ?";

    private Cursor getProductByBarcode(Uri uri, String[] projection, String sortOrder){
        String barcode = ArticleContract.ProductEntry.getBarcodeFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = productBarcodeSelection;
        selectionArgs = new String[]{barcode};
        return productQueryBuilder.query(productHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getArticleByBarcode(Uri uri, String[] projection, String sortOrder){
        String barcode = ArticleContract.ArticleEntry.getBarcodeFromUri(uri);

        String[] selectionArgs = new String[]{barcode};
        String selection = articleBarcodeSelection;

        return productQueryBuilder.query(productHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    private Cursor getArticleByName(Uri uri, String[] projection, String sortOrder){
        String articleName = ArticleContract.ArticleEntry.getArticleNameFromUri(uri);

        String[] selectionArgs = new String[]{articleName};
        String selection = articleNameSelection;

        return  this.query(ArticleContract.ArticleEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private Cursor getProductByStockName(Uri uri, String[] projection, String sortOrder){
        String stockName = ArticleContract.ProductEntry.getStockNameFromUri(uri);

        String[] selectionArgs = new String[]{stockName};
        String selection = productStockNameSelection;

        return this.query(ArticleContract.ProductEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private Cursor getProductByStockIdAndArticleId(Uri uri, String[] projection, String sortOrder){
        String stockId = ArticleContract.ProductEntry.getStockIdFromUri(uri);
        String articleId = ArticleContract.ProductEntry.getArticleIdFromUri(uri);


        String[] selectionArgs = new String[]{stockId,articleId};
        String selection = productStockIdAndArticleIdSelection;

        return productQueryBuilder.query(productHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null, null,
                sortOrder);
    }

    private Cursor getStockByName(Uri uri, String[] projection, String sortOrder){
        String stockName = ArticleContract.ProductEntry.getStockNameFromUri(uri);

        String[] selectionArgs = new String[]{stockName};
        String selection = productStockNameSelection;

        return this.query(StockEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArticleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ArticleContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority, ArticleContract.PATH_ARTICLE + "/*", ARTICLE_WITH_BARCODE);
        matcher.addURI(authority, ArticleContract.PATH_ARTICLE + "/*", ARTICLE_WITH_NAME);
        matcher.addURI(authority, ArticleContract.PATH_STOCK, STOCK);
        matcher.addURI(authority, ArticleContract.PATH_STOCK + "/*", STOCK_WITH_NAME);
        matcher.addURI(authority, ArticleContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, ArticleContract.PATH_PRODUCT + "/*", PRODUCT_WITH_BARCODE);
        matcher.addURI(authority, ArticleContract.PATH_PRODUCT + "/*", PRODUCT_WITH_STOCK_NAME);
        matcher.addURI(authority, ArticleContract.PATH_PRODUCT + "/*/*", PRODUCT_WITH_STOCK_ID_AND_ARTICLE_ID);



        return matcher;
    }


    @Override
    public boolean onCreate() {
        productHelper = new ArticleOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (uriMatcher.match(uri)){
            case ARTICLE_WITH_BARCODE:
                retCursor = getArticleByBarcode(uri, projection, sortOrder);
                break;
            case ARTICLE_WITH_NAME:
                retCursor = getArticleByName(uri, projection, sortOrder);
                break;
            case ARTICLE:
                retCursor = productHelper.getReadableDatabase().query(
                        ArticleContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STOCK:
                retCursor = productHelper.getReadableDatabase().query(
                        ArticleContract.StockEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STOCK_WITH_NAME:
                retCursor = getStockByName(uri, projection, sortOrder);
                break;
            case PRODUCT:
                retCursor = productQueryBuilder.query(
                        productHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_WITH_BARCODE:
                retCursor = getProductByBarcode(uri, projection, sortOrder);
                break;
            case PRODUCT_WITH_STOCK_NAME:
                retCursor = getProductByStockName(uri, projection, sortOrder);
                break;
            case PRODUCT_WITH_STOCK_ID_AND_ARTICLE_ID:
                retCursor = getProductByStockIdAndArticleId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case ARTICLE_WITH_BARCODE:
                return ArticleContract.ArticleEntry.CONTENT_ITEM_TYPE;
            case ARTICLE:
                return ArticleContract.ArticleEntry.CONTENT_TYPE;
            case STOCK:
                return ArticleContract.StockEntry.CONTENT_TYPE;
            case PRODUCT_WITH_BARCODE:
                return ArticleContract.ProductEntry.CONTENT_ITEM_TYPE;
            case PRODUCT:
                return ArticleContract.ProductEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = productHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARTICLE:
                long _id = db.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ArticleContract.ArticleEntry.buildArticleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case STOCK:
                long stock_id = db.insert(ArticleContract.StockEntry.TABLE_NAME, null, values);
                if ( stock_id > 0 )
                    returnUri = ArticleContract.StockEntry.buildStockUri(stock_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case PRODUCT:
                long product_id = db.insert(ArticleContract.ProductEntry.TABLE_NAME, null, values);
                Log.d("ArticleProvider : ", "product_id --> " + product_id);
                if ( product_id > 0 )
                    returnUri = ArticleContract.StockEntry.buildStockUri(product_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = productHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ARTICLE:
                rowsDeleted = db.delete(
                        ArticleContract.ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK:
                rowsDeleted = db.delete(
                        ArticleContract.StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT:
                rowsDeleted = db.delete(
                        ArticleContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = productHelper.getWritableDatabase();
            final int match = uriMatcher.match(uri);
            int rowsUpdated;

            switch (match) {
                case ARTICLE:
                    rowsUpdated = db.update(ArticleContract.ArticleEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case STOCK:
                    rowsUpdated = db.update(ArticleContract.StockEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                case PRODUCT:
                    rowsUpdated = db.update(ArticleContract.ProductEntry.TABLE_NAME, values, selection,
                            selectionArgs);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = productHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case ARTICLE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
