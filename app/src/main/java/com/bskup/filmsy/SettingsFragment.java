package com.bskup.filmsy;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created on 7/6/2017.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /* Boolean whether preferences have changed or not */
    public static Boolean mPreferencesChanged = false;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "mPreferencesChanged in oncreate: " + mPreferencesChanged);

        addPreferencesFromResource(R.xml.fragment_settings);
        /* Find the shared preferences and set listener on them */
        /* Listen for change of the theme preference */
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Preference theme = findPreference(getString(R.string.settings_theme_key));
        bindPreferenceSummaryToValue(theme);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("theme")) {
            /* If theme preference changed, recreate the activity */
            // TODO recreate at settings screen
            mPreferencesChanged = true;
            getActivity().recreate();
        }
        Log.v(LOG_TAG, "onSharedPreferenceChanged called");
        Log.v(LOG_TAG, "mPreferencesChanged: " + mPreferencesChanged);
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Find the shared preferences and set listener on them */
        /* Listen for change of the theme preference */
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

    /* Set listener implemented in other methods in this class onto preference */
    /* Read current value of the preference stored in SharedPreferences on the device */
    /* Create String to set as new preference summary, we just use the key name for now */
    /* Call onPreferenceChange passing in our preference and new summary String */
    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        String preferenceSummaryString = preferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceSummaryString);
    }
}
