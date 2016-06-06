/*
 * Copyright (C) 2015 The VRToxin Project
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

import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.content.ContentResolver;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.vrtoxin.RRUtils;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatusBarRandom extends SettingsPreferenceFragment {


    private static final String SHOW_THREEG = "show_threeg";	

    private SwitchPreference mShowThreeG;	
    private boolean mCheckPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.vrtoxin_random);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver(); 
        Context context = getActivity();
    }

	mShowThreeG = (SwitchPreference) findPreference(SHOW_THREEG);
        if (RRUtils.isWifiOnly(getActivity())) {
            prefSet.removePreference(mShowThreeG);
        } else {
        mShowThreeG.setChecked((Settings.System.getInt(resolver,
                Settings.System.SHOW_THREEG, 0) == 1));
        }
        
        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefSet;
    }


    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
         if  (preference == mShowThreeG) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_THREEG, checked ? 1:0);
            return true;
    }
       return super.onPreferenceTreeClick(preferenceScreen, preference);
}
 
   @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}
