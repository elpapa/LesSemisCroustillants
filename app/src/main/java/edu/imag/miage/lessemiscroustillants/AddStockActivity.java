package edu.imag.miage.lessemiscroustillants;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.imag.miage.lessemiscroustillants.data.ArticleContract;

public class AddStockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        final EditText stock_name = (EditText) findViewById(R.id.add_stock);

        Button button = (Button) findViewById(R.id.activity_add_stock_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (stock_name.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddStockActivity.this, R.string.mandatory_message, Toast.LENGTH_LONG).show();
                } else {
                    addStock(stock_name.getText().toString());
                }
            }
        });
    }

    private long addStock(String stock_name){
        long stockId;

        Cursor stockCursor = this.getContentResolver().query(
                ArticleContract.StockEntry.CONTENT_URI,
                new String[]{ArticleContract.StockEntry._ID},
                ArticleContract.StockEntry.COLUMN_STOCK_NAME + " = ?",
                new String[]{stock_name},
                null);

        if (stockCursor.moveToFirst()) {
            int stockIdIndex = stockCursor.getColumnIndex(ArticleContract.StockEntry._ID);
            stockId = stockCursor.getLong(stockIdIndex);

            /* existe déjà */

        } else {
            ContentValues stockValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            stockValues.put(ArticleContract.StockEntry.COLUMN_STOCK_NAME, stock_name);

            // Finally, insert location data into the database.
            Uri insertedUri = this.getContentResolver().insert(
                    ArticleContract.StockEntry.CONTENT_URI,
                    stockValues
            );

            // The resulting URI contains the ID for the row.  Extract the stockId from the Uri.
            stockId = ContentUris.parseId(insertedUri);

            Toast.makeText(this, R.string.add_stock_success, Toast.LENGTH_LONG).show();

        }
        stockCursor.close();
        return stockId;
    }
}
