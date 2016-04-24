package edu.imag.miage.lessemiscroustillants;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        final EditText quantite = (EditText) findViewById(R.id.add_nb_product);
        final EditText stock_name = (EditText) findViewById(R.id.add_name_stock);
        final DatePicker date_limite = (DatePicker) findViewById(R.id.add_date_limit_product);


        String year = String.valueOf(date_limite.getYear());
        String month = String.valueOf(date_limite.getMonth()+1);
        if(month.length() < 2){
            month = '0' + month;
        }
        String day = String.valueOf(date_limite.getDayOfMonth());
        if(day.length() < 2){
           day = '0' + day;
        }

        final String date_product = day + "/" + month + "/" + year;

        final Button button = (Button) findViewById(R.id.activity_add_product_button);

        Intent intent = getIntent();
        final String articleName = intent.getStringExtra("name_article");

        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            long idProductInserted;
            @Override
            public void onClick(View v) {
                if (quantite.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddProductActivity.this, R.string.mandatory_message, Toast.LENGTH_LONG).show();
                } else if (stock_name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddProductActivity.this, R.string.mandatory_message, Toast.LENGTH_LONG).show();
                } else if (date_product.toString().isEmpty()) {
                    Toast.makeText(AddProductActivity.this, R.string.mandatory_message, Toast.LENGTH_LONG).show();
                } else {
                    idProductInserted = addProduct(articleName, quantite.getText().toString(), date_product, stock_name.getText().toString());

                    if (idProductInserted != -1) {
                        Toast.makeText(MyApplication.getAppContext(), R.string.add_product_success, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MyApplication.getAppContext(), R.string.add_product_error, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private long addProduct(String articleName, String quantite, String date_limite, String stock_name) {

        long idProductUri = -1;

        Uri articleForNameUri = ArticleContract.ArticleEntry.buildArticleWithName(articleName);
        Log.d("Uri ArticleWithName", articleForNameUri.toString());
        Uri stockForNameUri = ArticleContract.StockEntry.buildStockWithName(stock_name);
        Log.d("Uri StockWithName", stockForNameUri.toString());

        Cursor idArticleCursor = this.getContentResolver().query(articleForNameUri,
                new String[]{ArticleContract.ArticleEntry._ID},
                null, null, null);

        String articleId = null;
        String stockId = null;
        long idProduct = -1;
        String dateLimiteProduct = null;
        int quantiteProduct = -1;

        if(idArticleCursor.moveToFirst()){
            int articleIdIndex = idArticleCursor.getColumnIndex(ArticleContract.ArticleEntry._ID);
            articleId = idArticleCursor.getString(articleIdIndex);
        }
        idArticleCursor.close();

        Cursor idStockCursor = this.getContentResolver().query(stockForNameUri,
                new String[]{ArticleContract.StockEntry._ID},
                null, null, null);

        if (idStockCursor.moveToFirst()){
            int stockIdIndex = idStockCursor.getColumnIndex(ArticleContract.StockEntry._ID);
            stockId = idStockCursor.getString(stockIdIndex);
            Log.d("AddProduct : ", "stockId --> " +stockId);
        } else { return -1; }
        idStockCursor.close();


        Uri productForStockIdAndArticleIdUri = ArticleContract.ProductEntry.buildProductWithStockIdAndArticleId(stockId, articleId);
        Cursor idProductCursor = this.getContentResolver().query(productForStockIdAndArticleIdUri,
                new String[]{
                        ArticleContract.ProductEntry.TABLE_NAME + "." + ArticleContract.ProductEntry._ID,
                        ArticleContract.ProductEntry.COLUMN_PRODUCT_DATE_LIMITE,
                        ArticleContract.ProductEntry.COLUMN_PRODUCT_QUANTITE},
                null, null, null);

        if (idProductCursor.moveToFirst()) {
            Log.d("AddProduct : ", "l\'article existe");

            int idProductIndex = idProductCursor.getColumnIndex(ArticleContract.ProductEntry._ID);
            idProduct = idProductCursor.getLong(idProductIndex);

            Log.d("AddProduct : ", "idProduct : " + idProduct);

            int dateProductIndex = idProductCursor.getColumnIndex(ArticleContract.ProductEntry.COLUMN_PRODUCT_DATE_LIMITE);
            dateLimiteProduct = idProductCursor.getString(dateProductIndex);

            int quantiteProductIndex = idProductCursor.getColumnIndex(ArticleContract.ProductEntry.COLUMN_PRODUCT_QUANTITE);
            quantiteProduct = idProductCursor.getInt(quantiteProductIndex);
        }
        idProductCursor.close();


            if (date_limite.equals(dateLimiteProduct)) {

                ContentValues contentValues = new ContentValues();

                contentValues.put(ArticleContract.ProductEntry.COLUMN_PRODUCT_QUANTITE, Integer.parseInt(quantite) + quantiteProduct);

                int updated = this.getContentResolver().update(
                        ArticleContract.ProductEntry.CONTENT_URI,
                        contentValues,
                        ArticleContract.ProductEntry.TABLE_NAME + "." + ArticleContract.ProductEntry._ID + " = ?",
                        new String[]{String.valueOf(idProduct)}
                );
                return updated;
            } else {
                ContentValues productValues = new ContentValues();

                productValues.put(ArticleContract.ProductEntry.COLUMN_ARTICLE_KEY, articleId);
                productValues.put(ArticleContract.ProductEntry.COLUMN_STOCK_KEY, stockId);
                productValues.put(ArticleContract.ProductEntry.COLUMN_PRODUCT_QUANTITE, Integer.parseInt(quantite));
                productValues.put(ArticleContract.ProductEntry.COLUMN_PRODUCT_DATE_LIMITE, date_limite);

                Uri insertedUri = this.getContentResolver().insert(
                        ArticleContract.ProductEntry.CONTENT_URI,
                        productValues);
                idProductUri = ContentUris.parseId(insertedUri);
                return idProductUri;
            }

    }
}
