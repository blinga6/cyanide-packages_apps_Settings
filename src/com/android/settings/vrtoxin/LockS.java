/* 
 * Copyright (C) 2015 The Vrtoxin Project
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.OwnerInfoSettings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.vrtoxin.SeekBarPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.widget.LockPatternUtils;

public class LockS extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_OWNER_INFO_SETTINGS = "owner_info_settings";
    private static final String OWNER_INFO_FONT_SIZE  = "owner_info_font_size";
    private static final String LS_ALARM_DATE_FONT_SIZE  = "ls_alarm_date_font_size";
    private static final String LOCK_CLOCK_FONT_SIZE  = "lock_clock_font_size";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";
    private static final String PREF_LOCKSCREEN_ALPHA = "lockscreen_alpha";
    private static final String PREF_LOCKSCREEN_SECURITY_ALPHA = "lockscreen_security_alpha";

    private static final int MY_USER_ID = UserHandle.myUserId();
    private LockPatternUtils mLockPatternUtils;
    private Preference mOwnerInfoPref;
    private SeekBarPreference mOwnerSize;
    private SeekBarPreference mLCFontSize;
    private SeekBarPreference mLSAlarmDateFontSize;
    private ListPreference mLockClockFonts;
    private SeekBarPreference mLsAlpha;
    private SeekBarPreference mLsSecurityAlpha;

    private boolean mIsPrimary;
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

        addPreferencesFromResource(R.xml.vrtoxin_ls_shortcuts);
        mResolver = getActivity().getContentResolver();

        // Add options for device encryption
        mIsPrimary = MY_USER_ID == UserHandle.USER_OWNER;

        mOwnerInfoPref = findPreference(KEY_OWNER_INFO_SETTINGS);
        if (mOwnerInfoPref != null) {
            mOwnerInfoPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    OwnerInfoSettings.show(LockS.this);
                    return true;
                }
            });
        }

        mOwnerSize = (SeekBarPreference) findPreference(OWNER_INFO_FONT_SIZE);
        mOwnerSize.setValue(Settings.System.getInt(mResolver,
                Settings.System.OWNER_INFO_FONT_SIZE, 14));
        mOwnerSize.setOnPreferenceChangeListener(this);

        mLCFontSize = (SeekBarPreference) findPreference(LOCK_CLOCK_FONT_SIZE);
        mLCFontSize.setValue(Settings.System.getInt(mResolver,
                Settings.System.LOCK_CLOCK_FONT_SIZE, 88));
        mLCFontSize.setOnPreferenceChangeListener(this);

        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                mResolver, Settings.System.LOCK_CLOCK_FONTS, 0)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);

        mLSAlarmDateFontSize = (SeekBarPreference) findPreference(LS_ALARM_DATE_FONT_SIZE);
        mLSAlarmDateFontSize.setValue(Settings.System.getInt(mResolver,
                Settings.System.LS_ALARM_DATE_FONT_SIZE, 14));
        mLSAlarmDateFontSize.setOnPreferenceChangeListener(this);

        // LS alpha
        mLsAlpha =
                (SeekBarPreference) findPreference(PREF_LOCKSCREEN_ALPHA);
        float alpha = Settings.System.getFloat(mResolver,
                Settings.System.LOCKSCREEN_ALPHA, 0.45f);
        mLsAlpha.setValue((int)(100 * alpha));
        mLsAlpha.setOnPreferenceChangeListener(this);

        mLsSecurityAlpha =
                (SeekBarPreference) findPreference(PREF_LOCKSCREEN_SECURITY_ALPHA);
        float alpha2 = Settings.System.getFloat(mResolver,
                Settings.System.LOCKSCREEN_SECURITY_ALPHA, 0.75f);
        mLsSecurityAlpha.setValue((int)(100 * alpha2));
        mLsSecurityAlpha.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOwnerSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.OWNER_INFO_FONT_SIZE, width);
            return true;
        } else if (preference == mLCFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_CLOCK_FONT_SIZE, width);
            return true;
        } else if (preference == mLockClockFonts) {
            Settings.System.putInt(mResolver, Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
        } else if (preference == mLSAlarmDateFontSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(mResolver,
                    Settings.System.LS_ALARM_DATE_FONT_SIZE, width);
            return true;
        } else if (preference == mLsAlpha) {
            int alpha = (Integer) newValue;
            Settings.System.putFloat(mResolver,
                    Settings.System.LOCKSCREEN_ALPHA, alpha / 100.0f);
            return true;
        } else if (preference == mLsSecurityAlpha) {
            int alpha2 = (Integer) newValue;
            Settings.System.putFloat(mResolver,
                    Settings.System.LOCKSCREEN_SECURITY_ALPHA, alpha2 / 100.0f);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}
