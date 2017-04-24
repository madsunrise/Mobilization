package com.rv150.mobilization.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.rv150.mobilization.R;
import com.rv150.mobilization.fragment.ListFragment;
import com.rv150.mobilization.fragment.TranslationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_toolbar)
    BottomNavigationView bottomToolbar;

    private Fragment translationFragment = new TranslationFragment();
    private Fragment listFragment = new ListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(R.string.translator);
        changeFragment(translationFragment, false);

        bottomToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_translate:
                        changeFragment(translationFragment, false);
                        setTitle(R.string.translator);
                        return true;
                    case R.id.action_history:
                        changeFragment(listFragment, true);
                        setTitle(R.string.history);
                        return true;
                    case R.id.action_favorites:
                        changeFragment(listFragment, true);
                        setTitle(R.string.favorites);
                        return true;
                }
                return false;
            }
        });
    }



    private void changeFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


    private final static String TAG = MainActivity.class.getSimpleName();
}
