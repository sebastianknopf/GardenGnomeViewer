package com.ggnome.viewer.common;

import android.content.Intent;
import android.os.Bundle;

import com.ggnome.viewer.InfoActivity;
import com.ggnome.viewer.R;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public final class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.setPreferencesFromResource(R.xml.preference_screen, null);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "prefInformation":
                Intent intent = new Intent(this.getActivity(), InfoActivity.class);
                this.getActivity().startActivity(intent);
                break;
        }

        return super.onPreferenceTreeClick(preference);
    }
}
