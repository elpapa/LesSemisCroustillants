package edu.imag.miage.lessemiscroustillants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract;

public class StockListProductActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(StockListProductActivity.this, AddProductActivity.class);
                startActivity(addIntent);
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.stock_list_item);
        ListView listView = (ListView) findViewById(R.id.list_product_stock);
        Intent intent = getIntent();
        String stock_name;
        if (intent != null && intent.hasExtra("stock_name")) {
            stock_name = intent.getStringExtra("stock_name");



            List<String> items = new ArrayList<>();
            Uri productForStockNameUri = ArticleContract.ProductEntry.buildProductsWithStockName(stock_name);
            Cursor mCursor = this.getContentResolver().query(productForStockNameUri,
                    null, null, null, null);

            String item;

            while(mCursor.moveToNext()){
                item = mCursor.getString(COL_ARTICLE_NAME) + " " + mCursor.getString(COL_QUANTITE);
                items.add(item);
            }

            mCursor.close();
            adapter.addAll(items);
            assert listView != null;
            listView.setAdapter(adapter);

        }
    }

}
