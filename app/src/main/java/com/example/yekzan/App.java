package com.example.yekzan;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;
    }
}
