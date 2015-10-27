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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.Utils;

public class RecentsPanelSettings extends SettingsPreferenceFragment implements Indexable,
        Preference.OnPreferenceChangeListener {

    private static final String PREF_USE_SLIM_RECENTS =
            "use_slim_recents";

    private SwitchPreference mUseSlimRecents;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.recents_panel_settings);
        mResolver = getActivity().getContentResolver();

        boolean useSlimRecents = Settings.System.getInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS, 0) == 1;
        mUseSlimRecents = (SwitchPreference) findPreference(PREF_USE_SLIM_RECENTS);
        mUseSlimRecents.setChecked(useSlimRecents);
        mUseSlimRecents.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseSlimRecents) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.USE_SLIM_RECENTS,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
 		return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}
