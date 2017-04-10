package com.rv150.mobilization.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rv150.mobilization.R;
import com.rv150.mobilization.network.TranslatorService;
import com.rv150.mobilization.utils.UiThread;

import static com.rv150.mobilization.network.TranslatorService.ERR_NETWORK;

public class MainActivity extends AppCompatActivity implements TranslatorService.ApiCallback {

    private final TranslatorService translatorService = TranslatorService.getInstance();

    private EditText userInput;
    private TextView translatedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText) findViewById(R.id.input_text);
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    translatorService.requestTranslate(s.toString());
                }
                else {
                    translatedText.setText("");
                }
            }
        });

        translatedText = (TextView) findViewById(R.id.translated_text);

        translatorService.setCallback(this);
    }



    @Override
    public void onDataLoaded(final String data) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                translatedText.setText(data);
            }
        });
    }

    @Override
    public void dataLoadingFailed(final int errCode) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                switch (errCode) {
                    case ERR_NETWORK: {
                        Toast.makeText(MainActivity.this, R.string.network_error_occured, Toast.LENGTH_SHORT).show();
                    }
                    default: {
                        Toast.makeText(MainActivity.this, R.string.internal_error_occured, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        translatorService.setCallback(null);
    }

    private final static String TAG = MainActivity.class.getSimpleName();
}
