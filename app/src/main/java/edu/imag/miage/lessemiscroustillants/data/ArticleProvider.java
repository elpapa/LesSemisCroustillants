package edu.imag.miage.lessemiscroustillants.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by boris on 14/04/2016.
 */
public class ArticleProvider extends ContentProvider {


    private static final UriMatcher uriMatcher = buildUriMatcher();
    private ArticleOpenHelper articleHelper;

    static final int ARTICLE = 100;
    static final int ARTICLE_WITH_BARCODE = 101;
    static final int STOCK = 200;

    private static final SQLiteQueryBuilder articleByBarcodeQueryBuilder;

    static{
        articleByBarcodeQueryBuilder = new SQLiteQueryBuilder();
        articleByBarcodeQueryBuilder.setTables(
                ArticleContract.ArticleEntry.TABLE_NAME +
                "." + ArticleContract.ArticleEntry.COLUMN_BARCODE);
    }

    private static final String articleSelection =
            ArticleContract.ArticleEntry.TABLE_NAME +
            "." + ArticleContract.ArticleEntry.COLUMN_BARCODE + " = ?";

    private Cursor getArticleByBarcode(Uri uri, String[] projection, String sortOrder){
        String barcode = ArticleContract.ArticleEntry.getBarcodeFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = articleSelection;
        selectionArgs = new String[]{barcode};
        return articleByBarcodeQueryBuilder.query(articleHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArticleContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ArticleContract.PATH_ARTICLE, ARTICLE);
        matcher.addURI(authority, ArticleContract.PATH_ARTICLE + "/*", ARTICLE_WITH_BARCODE);
        matcher.addURI(authority, ArticleContract.PATH_STOCK, STOCK);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        articleHelper = new ArticleOpenHelper(getContext());
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
            case ARTICLE:
                retCursor = articleHelper.getReadableDatabase().query(
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
                retCursor = articleHelper.getReadableDatabase().query(
                        ArticleContract.StockEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
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
            default:
                throw new UnsupportedOperationException("Unknow uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = articleHelper.getWritableDatabase();
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = articleHelper.getWritableDatabase();
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
            final SQLiteDatabase db = articleHelper.getWritableDatabase();
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
        final SQLiteDatabase db = articleHelper.getWritableDatabase();
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
