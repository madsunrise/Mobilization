package com.rv150.mobilization.activity;

import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rv150.mobilization.R;
import com.rv150.mobilization.network.TranslatorService;
import com.rv150.mobilization.utils.UiThread;

import java.util.List;

import static com.rv150.mobilization.network.TranslatorService.ERR_NETWORK;

public class MainActivity extends AppCompatActivity implements TranslatorService.ApiCallback {

    private final TranslatorService translatorService = TranslatorService.getInstance();

    private EditText userInput;
    private TextView translatedText;

    private Spinner spinnerFrom;
    private Spinner spinnerTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translatorService.setCallback(this);

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
                    translatorService.requestTranslate();
                }
                else {
                    translatedText.setText("");
                }
            }
        });

        translatedText = (TextView) findViewById(R.id.translated_text);

        spinnerFrom = (Spinner) findViewById(R.id.lang_from);
        spinnerTo = (Spinner) findViewById(R.id.lang_to);

        translatorService.getSupportedLanguages("ru");
        translatorService.setCallback(this);
    }



    @Override
    public void onDataLoaded(String data, boolean nextRequest) {
        if (data != null) {
            translatedText.setText(data);
        }
    }

    @Override
    public void supLanguagesLoaded(final List<String> langs) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapterFrom = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, langs);
                adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrom.setAdapter(adapterFrom);


                ArrayAdapter<String> adapterTo = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, langs);
                adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTo.setAdapter(adapterTo);
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

    
    public String getFreshData() {
        return userInput.getText().toString();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        translatorService.setCallback(null);
    }

    private final static String TAG = MainActivity.class.getSimpleName();
}
