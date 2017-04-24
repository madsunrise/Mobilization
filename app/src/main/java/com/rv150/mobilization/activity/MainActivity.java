package com.rv150.mobilization.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.rv150.mobilization.R;
import com.rv150.mobilization.fragment.MainFragment;
import com.rv150.mobilization.fragment.TranslationsListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_toolbar)
    BottomNavigationView bottomToolbar;

    private MainFragment mainFragment = new MainFragment();
    private TranslationsListFragment translationsListFragment = new TranslationsListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(R.string.translator);
        changeFragment(mainFragment);

        bottomToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_translate:
                        changeFragment(mainFragment);
                        setTitle(R.string.translator);
                        return true;
                    case R.id.action_history:
                        translationsListFragment.setState(TranslationsListFragment.State.HISTORY);
                        changeFragment(translationsListFragment);
                        setTitle(R.string.history);
                        return true;
                    case R.id.action_favorites:
                        translationsListFragment.setState(TranslationsListFragment.State.FAVORITES);
                        changeFragment(translationsListFragment);
                        setTitle(R.string.favorites);
                        return true;
                }
                return false;
            }
        });
    }



    private void changeFragment(Fragment fragment) {
        if (fragment instanceof TranslationsListFragment && translationsListFragment.isAdded()) {
            translationsListFragment.updateData();
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commitAllowingStateLoss();
    }
}
