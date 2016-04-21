package edu.imag.miage.lessemiscroustillants.data;


import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ArticleEntry;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.StockEntry;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract.ProductEntry;

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


        final String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StockEntry.COLUMN_STOCK_NAME + " TEXT NOT NULL);";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProductEntry.COLUMN_ARTICLE_KEY + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_STOCK_KEY + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_QUANTITE + " INTEGER NOT NULL," +
                ProductEntry.COLUMN_PRODUCT_DATE_LIMITE + " NUMERIC NOT NULL," +

                " FOREIGN KEY (" + ProductEntry.COLUMN_ARTICLE_KEY + ") REFERENCES " +
                ArticleEntry.TABLE_NAME + " (" + ArticleEntry._ID + "), " +

                " FOREIGN KEY (" + ProductEntry.COLUMN_STOCK_KEY + ") REFERENCES " +
               StockEntry.TABLE_NAME + " (" + StockEntry._ID + "));";

        db.execSQL(SQL_CREATE_ARTICLE_TABLE);
        db.execSQL(SQL_CREATE_STOCK_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);

        //db.execSQL("INSERT INTO " + StockEntry.TABLE_NAME + " (" + StockEntry.COLUMN_STOCK_NAME + ") VALUES (Frigo);");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXIST " + ProductEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXIST " + ArticleEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXIST " + StockEntry.TABLE_NAME);
            onCreate(db);
        }
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
