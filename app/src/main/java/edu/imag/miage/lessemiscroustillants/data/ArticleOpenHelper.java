package edu.imag.miage.lessemiscroustillants.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ArticleEntry;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.StockEntry;

public class ArticleOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "article.db";
    public static final int DATABASE_VERSION = 1;


    public ArticleOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + ArticleEntry.TABLE_NAME + " (" +
                ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArticleEntry.COLUMN_ARTICLE_NAME + " TEXT NOT NULL," +
                ArticleEntry.COLUMN_GENERIC_NAME + " TEXT NOT NULL," +
                ArticleEntry.COLUMN_BRAND + " TEXT NOT NULL," +
                ArticleEntry.COLUMN_WEIGHT + " TEXT NOT NULL," +
                //ArticleEntry.COLUMN_INGREDIENTS + " TEXT NOT NULL," +
                ArticleEntry.COLUMN_BARCODE + " LONG NOT NULL," +

                " UNIQUE (" + ArticleEntry.COLUMN_BRAND + ", " +
                ArticleEntry.COLUMN_ARTICLE_NAME + ") ON CONFLICT REPLACE);";


       /* final String SQL_CREATE_STOCK_TABLE = "CREATE TABLE" + StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StockEntry.COLUMN_SOCK_NAME + "TEXT NOT NULL);";*/

        db.execSQL(SQL_CREATE_ARTICLE_TABLE);
        //db.execSQL(SQL_CREATE_STOCK_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXIST " + ArticleEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
