package com.bskup.filmsy;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    /* Tag for log messages */
    public static String LOG_TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Theme change based on preference
        String themeName = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("theme", "AppThemeDark");
        Log.v(LOG_TAG, "value for theme String in settingsactivity oncreate: " + themeName);
        if (themeName.equals("AppThemeLight")) {
            setTheme(R.style.AppThemeLight);
        } else if (themeName.equals("AppThemeDark")) {
            setTheme(R.style.AppThemeDark);
        }

        setContentView(R.layout.activity_settings);
    }

    public static class FilmPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

        /* Boolean whether preferences have changed or not */
        public static Boolean mPreferencesChanged = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Log.v(LOG_TAG, "mPreferencesChanged in oncreate: " + mPreferencesChanged);

            addPreferencesFromResource(R.xml.settings_main);
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
                getActivity().recreate();
            }
            mPreferencesChanged = true;
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
}
