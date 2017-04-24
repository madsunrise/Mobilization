package com.rv150.mobilization.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.rv150.mobilization.R;
import com.rv150.mobilization.dao.TranslationDAO;
import com.rv150.mobilization.model.TranslateRequest;
import com.rv150.mobilization.model.Translation;
import com.rv150.mobilization.network.TranslatorService;
import com.rv150.mobilization.utils.UiThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rv150.mobilization.network.TranslatorService.ERR_NETWORK;

/**
 * Created by ivan on 24.04.17.
 */

public class MainFragment extends Fragment implements TranslatorService.TranslateCallback {
    private static final String TAG = MainFragment.class.getSimpleName();
    private final TranslatorService translatorService = TranslatorService.getInstance();

    @BindView(R.id.input_text)
    EditText userInput;
    @BindView(R.id.translated_text)
    TextView translatedText;

    @BindView(R.id.spinner_from)
    Spinner spinnerFrom;
    @BindView(R.id.spinner_to)
    Spinner spinnerTo;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.add_to_favorites_btn)
    Button favoritesButton;

    private TranslationDAO translationDAO;

    private BiMap<String, String> availableLanguages = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            userInput.addTextChangedListener(watcher);
        }

        translatorService.setCallback(this);
        if (availableLanguages == null) {
            translatorService.requestSupportedLanguages(getString(R.string.ui_lang));
            showProgressBar();
        }
        else {
            updateSpinners();
        }
        translationDAO = TranslationDAO.getInstance(getContext());
        return view;
    }

    @Override
    public void onDataLoaded(final Translation data, boolean nextRequest) {
        if (data != null) {
            translatedText.setText(data.getTo());
            favoritesButton.setVisibility(View.VISIBLE);
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    translationDAO.insertTranslation(data);
                }
            });
        }
    }

    @Override
    public void supLanguagesLoaded(Map<String, String> langs) {
        hideProgressBar();
        availableLanguages = HashBiMap.create(langs);
        updateSpinners();
    }


    @OnClick(R.id.reverse_button)
    public void reverseLangs() {
        int from = spinnerFrom.getSelectedItemPosition();
        int to = spinnerTo.getSelectedItemPosition();
        spinnerFrom.setSelection(to);
        spinnerTo.setSelection(from);
        translatorService.requestTranslate();
    }



    @Override
    public void dataLoadingFailed(final int errCode) {
        UiThread.run(new Runnable() {
            @Override
            public void run() {
                switch (errCode) {
                    case ERR_NETWORK: {
                        Toast.makeText(getContext(), R.string.network_error_occured, Toast.LENGTH_SHORT).show();
                    }
                    default: {
                        Toast.makeText(getContext(), R.string.internal_error_occured, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public TranslateRequest getFreshData() {
        String from = spinnerFrom.getSelectedItem().toString();
        String fromCode = availableLanguages.inverse().get(from);
        String to = spinnerTo.getSelectedItem().toString();
        String toCode = availableLanguages.inverse().get(to);
        String text = userInput.getText().toString();
        return new TranslateRequest(fromCode, toCode, text);
    }


    private void updateSpinners() {
        List<String> langList = new ArrayList<>(availableLanguages.values());
        Collections.sort(langList);

        ArrayAdapter<String> adapterFrom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, langList);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapterFrom);
        spinnerFrom.setSelection(adapterFrom.getPosition(getString(R.string.default_lang_from)));

        ArrayAdapter<String> adapterTo = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, langList);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapterTo);
        spinnerTo.setSelection(adapterTo.getPosition(getString(R.string.default_lang_to)));
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        translatorService.setCallback(null);
    }


    @OnClick(R.id.add_to_favorites_btn)
    public void addToFavorites() {
        String from = userInput.getText().toString();
        String to = translatedText.getText().toString();
        final Translation translation = new Translation(from, to, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                translationDAO.insertTranslation(translation);
            }
        }).start();
        Toast.makeText(getContext(), R.string.added, Toast.LENGTH_SHORT).show();
    }


    private TextWatcher watcher = new TextWatcher() {
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
            } else {
                favoritesButton.setVisibility(View.GONE);
                translatedText.setText("");
            }
        }
    };


}
