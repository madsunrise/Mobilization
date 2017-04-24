package com.rv150.mobilization.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by ivan on 30.03.17.
 */

public class UiThread {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void run(Runnable runnable) {
        HANDLER.post(runnable);
    }
}