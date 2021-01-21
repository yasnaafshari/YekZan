package com.example.yekzan;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {
    private static Configuration configuration;

    public static void init(Activity activity) {
        activity.applyOverrideConfiguration(createConfiguration(App.context));
    }

    private static Configuration getConfiguration(Context context) {
        if (configuration == null) {
            configuration = createConfiguration(context);
        }
        return configuration;
    }

    private static Configuration createConfiguration(Context context) {
        Locale locale = new Locale("fa");
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        LocaleHelper.configuration = configuration;
        return configuration;
    }



}
