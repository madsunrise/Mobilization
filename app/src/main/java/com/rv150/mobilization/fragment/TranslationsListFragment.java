package com.rv150.mobilization.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rv150.mobilization.R;
import com.rv150.mobilization.adapter.TranslationListAdapter;
import com.rv150.mobilization.dao.TranslationDAO;
import com.rv150.mobilization.model.Translation;
import com.rv150.mobilization.utils.UiThread;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ivan on 24.04.17.
 */

public class TranslationsListFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private State state;
    private TranslationDAO translationDAO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_translations_fragment, container, false);
        ButterKnife.bind(this, view);
        setUpRecyclerView();
        translationDAO = TranslationDAO.getInstance(getContext());
        updateData();
        return view;
    }

    private void setUpRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                llm.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }


    public void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Translation> translationList;
                switch (state) {
                    case HISTORY:
                        translationList = translationDAO.getAll();
                        break;
                    case FAVORITES:
                        translationList = translationDAO.getFavorites();
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown state value!");
                }
                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        setDataToRecyclerView(translationList);
                    }
                });
            }
        }).start();
    }


    private void setDataToRecyclerView(List<Translation> data) {
        TranslationListAdapter adapter = new TranslationListAdapter(data);
        recyclerView.swapAdapter(adapter, false);
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        HISTORY, FAVORITES
    }
}