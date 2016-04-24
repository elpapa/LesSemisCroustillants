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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import edu.imag.miage.lessemiscroustillants.com.google.zxing.integration.android.IntentIntegrator;
import edu.imag.miage.lessemiscroustillants.com.google.zxing.integration.android.IntentResult;
import edu.imag.miage.lessemiscroustillants.data.ArticleContract;

public class AddArticleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final EditText barcode = (EditText) findViewById(R.id.add_ref_codebarre);

        final Button button = (Button) findViewById(R.id.activity_add_button);
        final Button buttonScan = (Button) findViewById(R.id.scan_barcode);


        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArticle(barcode.getText().toString());
            }
        });

        assert buttonScan != null;
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(AddArticleActivity.this);
                scanIntegrator.initiateScan();
            }
        });
    }

    private void addArticle(String barcode) {
        FetchArticleTask articleTask = new FetchArticleTask(this);
        articleTask.execute(barcode);
        String articleName = null;
        try {
            articleName = articleTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(articleName != null){

        Intent intent = new Intent(AddArticleActivity.this, AddProductActivity.class)
                .putExtra("name_article", articleName);
        startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            String barcode = scanningResult.getContents();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "barcode : " + barcode, Toast.LENGTH_SHORT);
            toast.show();

            Cursor mCursor = MyApplication.getAppContext().getContentResolver().query(
                    ArticleContract.ArticleEntry.CONTENT_URI,
                    new String[]{ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME},
                    ArticleContract.ArticleEntry.COLUMN_BARCODE + " = ?",
                    new String[]{barcode},
                    null);

            if (mCursor.moveToFirst()) {
                int articleNameIndex = mCursor.getColumnIndex(ArticleContract.ArticleEntry.COLUMN_ARTICLE_NAME);
                String articleName = mCursor.getString(articleNameIndex);
                Intent addintent = new Intent(AddArticleActivity.this, AddProductActivity.class)
                        .putExtra("name_article", articleName);
                startActivity(addintent);
            } else {
                Log.d("Barcode : ", barcode);
                addArticle(barcode);
            }
        } else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
