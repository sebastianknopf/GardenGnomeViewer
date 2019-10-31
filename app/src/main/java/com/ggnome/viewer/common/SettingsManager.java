package com.ggnome.viewer.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class SettingsManager {

    private static SettingsManager singleInstance = null;

    private Context context;
    private SharedPreferences sharedPreferences;

    public static SettingsManager getInstance(Context context) {
        if(singleInstance == null) {
            singleInstance = new SettingsManager(context);
        }

        return singleInstance;
    }

    public SettingsManager(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public boolean getFullscreenEnabled() {
        return this.sharedPreferences.getBoolean("prefEnableFullscreen", true);
    }

}
