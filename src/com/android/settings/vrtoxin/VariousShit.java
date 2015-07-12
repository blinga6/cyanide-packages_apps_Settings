/*
 * Copyright (C) 2013 The ChameleonOS Open Source Project
 * Copyright (C) 2014 The Android Ice Cold Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.vrtoxin;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.internal.logging.MetricsLogger;

/**
 * LAB files borrowed from excellent ChameleonOS for AICP
 */
public class VariousShit extends SettingsPreferenceFragment
        implements OnSharedPreferenceChangeListener,
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "VariousShit";

    private static final String KEY_HIDDEN_SHIT = "hidden_shit";
    private static final String KEY_HIDDEN_SHIT_UNLOCKED = "hidden_shit_unlocked";
    private static final String KEY_HIDDEN_IMG = "hidden_img";
    private static final String KEY_HIDDEN_YOGA = "hidden_anim";

    // Package name of the yoga
    public static final String YOGA_PACKAGE_NAME = "com.android.settings";
    // Intent for launching the yoga actvity
    public static Intent INTENT_YOGA = new Intent(Intent.ACTION_MAIN)
            .setClassName(YOGA_PACKAGE_NAME, YOGA_PACKAGE_NAME + ".vrtoxin.HiddenAnimActivity");

    private PreferenceScreen mVariousShitScreen;

    private Preference mHiddenShit;
    private PreferenceScreen mHiddenImg;
    private CheckBoxPreference mHiddenShitUnlocked;
    long[] mHits = new long[3];

    private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
    private final ArrayList<CheckBoxPreference> mResetCbPrefs
            = new ArrayList<CheckBoxPreference>();

    private Context mContext;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.vrtoxin_various_shit);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
        PackageManager pm = getPackageManager();
        Resources res = getResources();
        mContext = getActivity();
        Activity activity = getActivity();

        mVariousShitScreen = (PreferenceScreen) findPreference("various_shit_screen");

        // Hidden shit
        mHiddenShit = (Preference) findPreference(KEY_HIDDEN_SHIT);
        mHiddenImg = (PreferenceScreen) findPreference(KEY_HIDDEN_IMG);
        mAllPrefs.add(mHiddenShit);
        mHiddenShitUnlocked =
                findAndInitCheckboxPref(KEY_HIDDEN_SHIT_UNLOCKED);
        mHiddenShitUnlocked.setOnPreferenceChangeListener(this);

        boolean hiddenShitOpened = Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.HIDDEN_SHIT, 0) == 1;
        mHiddenShitUnlocked.setChecked(hiddenShitOpened);

        if (hiddenShitOpened) {
            mVariousShitScreen.removePreference(mHiddenShit);
        } else {
            mVariousShitScreen.removePreference(mHiddenShitUnlocked);
            mVariousShitScreen.removePreference(mHiddenImg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHiddenShit) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
            mHits[mHits.length-1] = SystemClock.uptimeMillis();
            if ((Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.HIDDEN_SHIT, 0) == 0) &&
                    (mHits[0] >= (SystemClock.uptimeMillis()-500))) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.HIDDEN_SHIT, 1);
                Toast.makeText(getActivity(),
                        R.string.hidden_shit_toast,
                        Toast.LENGTH_LONG).show();
                getPreferenceScreen().removePreference(mHiddenShit);
                addPreference(mHiddenShitUnlocked);
                mHiddenShitUnlocked.setChecked(true);
                addPreference(mHiddenImg);
            }
        } else if (preference == mHiddenImg) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
            mHits[mHits.length-1] = SystemClock.uptimeMillis();
            if  (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
                startActivity(INTENT_YOGA);
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        final String key = preference.getKey();
        if (preference == mHiddenShitUnlocked) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIDDEN_SHIT,
                    (Boolean) objValue ? 1 : 0);
            return true;
        }
        return false;
    }

    private void addPreference(Preference preference) {
        getPreferenceScreen().addPreference(preference);
        preference.setOnPreferenceChangeListener(this);
        mAllPrefs.add(preference);
    }

    private CheckBoxPreference findAndInitCheckboxPref(String key) {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
        if (pref == null) {
            throw new IllegalArgumentException("Cannot find preference with key = " + key);
        }
        mAllPrefs.add(pref);
        mResetCbPrefs.add(pref);
        return pref;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}

