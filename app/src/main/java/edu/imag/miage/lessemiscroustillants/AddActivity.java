package edu.imag.miage.lessemiscroustillants;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final EditText barcode = (EditText) findViewById(R.id.add_ref_codebarre);

        Button button = (Button) findViewById(R.id.activity_add_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (barcode.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddActivity.this, R.string.mandatory_message, Toast.LENGTH_LONG).show();
                } else {
                    addArticle(barcode.getText().toString());
                }
            }
        });
    }

    private void addArticle(String barcode) {
        FetchArticleTask articleTask = new FetchArticleTask(this);
        articleTask.execute(barcode);
    }


}
