package com.rv150.mobilization.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rv150.mobilization.R;
import com.rv150.mobilization.network.ApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ApiHelper.ApiCallback {

    private final ApiHelper apiHelper = ApiHelper.getInstance();

    private EditText userInput;
    private Button translateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText) findViewById(R.id.input_text);
        translateBtn = (Button) findViewById(R.id.translate_btn);
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateBtn.setEnabled(false);
                translateBtn.setText(getString(R.string.loading));
                String query = userInput.getText().toString();
                apiHelper.requestTranslate(query);
            }
        });

        apiHelper.setCallback(this);
    }



    @Override
    public void onDataLoaded(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("text");
            final String text = (String)array.get(0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    translateBtn.setEnabled(true);
                    translateBtn.setText(getString(R.string.translate));
                }
            });

        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiHelper.setCallback(null);
    }
}
