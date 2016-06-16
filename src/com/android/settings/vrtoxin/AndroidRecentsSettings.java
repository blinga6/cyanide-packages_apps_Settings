/*
* Copyright (C) 2015 The VRToxin Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.vrtoxin.SeekBarPreference;

import com.android.settings.vrtoxin.util.Helpers;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.internal.logging.MetricsLogger;

public class AndroidRecentsSettings extends SettingsPreferenceFragment implements
			Preference.OnPreferenceChangeListener {

    private static final String SYSTEMUI_RECENTS_MEM_DISPLAY = "systemui_recents_mem_display";
    private static final String PREF_CAT_CLEAR_ALL = "recents_panel";
    private static final String SHOW_CLEAR_ALL_RECENTS = "show_clear_all_recents";
    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String RECENTS_USE_OMNISWITCH = "recents_use_omniswitch";
    private static final String OMNISWITCH_START_SETTINGS = "omniswitch_start_settings";
    public static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    public static Intent INTENT_OMNISWITCH_SETTINGS = new Intent(Intent.ACTION_MAIN)
            .setClassName(OMNISWITCH_PACKAGE_NAME, OMNISWITCH_PACKAGE_NAME + ".SettingsActivity");
    private static final String CATEGORY_OMNI_RECENTS = "omni_recents";
    private static final String RECENTS_EMPTY_VRTOXIN_LOGO = "recents_empty_vrtoxin_logo";
    private static final String KEY_SCREEN_PINNING = "screen_pinning_settings";
    private static final String IMMERSIVE_RECENTS_CAT_OPTIONS = "immersive_recents_cat_options";
    private static final String IMMERSIVE_RECENTS = "immersive_recents";
    private static final String RECENTS_FONT_STYLE = "recents_font_style";
    private static final String RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE = "recents_full_screen_clock_date_size";
    private static final String RECENTS_SEARCH_BAR = "recents_search_bar";
    private static final String SHAKE_TO_CLEAN_RECENTS = "shake_to_clean_recents";
    private static final String RECENTS_FULL_SCREEN_CLOCK = "recents_full_screen_clock";
    private static final String RECENTS_FULL_SCREEN_DATE = "recents_full_screen_date";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mMemoryBar;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsClearAllLocation;
    private PreferenceScreen mScreenPinning;
    private SwitchPreference mRecentsUseOmniSwitch;
    private Preference mOmniSwitchSettings;
    private boolean mOmniSwitchInitCalled;
    private PreferenceCategory mOmniSwitch;
    private SwitchPreference mRecentsStyle;
    private ListPreference mImmersiveRecents;
    private ListPreference mRecentsFontStyle;
    private SeekBarPreference mRecentsFontSize;
    private SwitchPreference mSearchBar;
    private SwitchPreference mShakeClean;
    private SwitchPreference mShowClock;
    private SwitchPreference mShowDate;

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
        addPreferencesFromResource(R.xml.recents_options);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();
        int intColor;
        String hexColor;
        PackageManager pm = getPackageManager();

        PreferenceCategory catClearAll =
                (PreferenceCategory) findPreference(PREF_CAT_CLEAR_ALL);

        mRecentsStyle = (SwitchPreference) prefSet.findPreference(RECENTS_EMPTY_VRTOXIN_LOGO);
        mRecentsStyle.setChecked(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 0) == 1);
        mRecentsStyle.setOnPreferenceChangeListener(this);

        boolean enableMemoryBar = Settings.System.getInt(mResolver,
                Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0) == 1;
        mMemoryBar = (SwitchPreference) findPreference(SYSTEMUI_RECENTS_MEM_DISPLAY);
        mMemoryBar.setChecked(enableMemoryBar);
        mMemoryBar.setOnPreferenceChangeListener(this);

        mRecentsClearAll = (SwitchPreference) prefSet.findPreference(SHOW_CLEAR_ALL_RECENTS);
        mRecentsClearAll.setChecked(Settings.System.getIntForUser(mResolver,
            Settings.System.SHOW_CLEAR_ALL_RECENTS, 1, UserHandle.USER_CURRENT) == 1);
        mRecentsClearAll.setOnPreferenceChangeListener(this);

        mRecentsClearAllLocation = (ListPreference) prefSet.findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(mResolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
        updateRecentsLocation(location);

        final boolean screenPinning = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCK_TO_APP_ENABLED, 0) == 1;
        if (mScreenPinning != null) {
            mScreenPinning.setSummary(screenPinning ?
                getResources().getString(R.string.switch_on_text) :
                getResources().getString(R.string.switch_off_text));
        }

        mRecentsUseOmniSwitch = (SwitchPreference)
                findPreference(RECENTS_USE_OMNISWITCH);

        try {
            mRecentsUseOmniSwitch.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_USE_OMNISWITCH) == 1);
            mOmniSwitchInitCalled = true;
        } catch(SettingNotFoundException e){
            // if the settings value is unset
        }
        mRecentsUseOmniSwitch.setOnPreferenceChangeListener(this);

        mOmniSwitchSettings = (Preference)
                findPreference(OMNISWITCH_START_SETTINGS);
        mOmniSwitchSettings.setEnabled(mRecentsUseOmniSwitch.isChecked());

        mOmniSwitch = (PreferenceCategory) findPreference(CATEGORY_OMNI_RECENTS);
        if (!Utils.isPackageInstalled(getActivity(), OMNISWITCH_PACKAGE_NAME)) {
            prefSet.removePreference(mOmniSwitch);
        }

        int immersiveRecents = Settings.System.getInt(mResolver,
                Settings.System.IMMERSIVE_RECENTS, 0);
        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                mResolver, Settings.System.IMMERSIVE_RECENTS, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());

        mRecentsFontStyle = (ListPreference) findPreference(RECENTS_FONT_STYLE);
        mRecentsFontStyle.setOnPreferenceChangeListener(this);
        mRecentsFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.RECENTS_FONT_STYLE, 0)));
        mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntry());

        mRecentsFontSize =
                (SeekBarPreference) findPreference(RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE);
        mRecentsFontSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, 14));
        mRecentsFontSize.setOnPreferenceChangeListener(this);

        mSearchBar = (SwitchPreference) prefSet.findPreference(RECENTS_SEARCH_BAR);
        mSearchBar.setChecked(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_SEARCH_BAR, 0) == 1);
        mSearchBar.setOnPreferenceChangeListener(this);

        mShakeClean = (SwitchPreference) prefSet.findPreference(SHAKE_TO_CLEAN_RECENTS);
        mShakeClean.setChecked(Settings.System.getInt(mResolver,
            Settings.System.SHAKE_TO_CLEAN_RECENTS, 0) == 1);
        mShakeClean.setOnPreferenceChangeListener(this);

        mShowClock = (SwitchPreference) prefSet.findPreference(RECENTS_FULL_SCREEN_CLOCK);
        mShowClock.setChecked(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_FULL_SCREEN_CLOCK, 0) == 1);
        mShowClock.setOnPreferenceChangeListener(this);

        mShowDate = (SwitchPreference) prefSet.findPreference(RECENTS_FULL_SCREEN_DATE);
        mShowDate.setChecked(Settings.System.getInt(mResolver,
            Settings.System.RECENTS_FULL_SCREEN_DATE, 0) == 1);
        mShowDate.setOnPreferenceChangeListener(this);
        
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int color = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ICON_COLOR, 0xFFFFFFFF);
        Drawable d = getResources().getDrawable(com.android.internal.R.drawable.ic_settings_backup_restore).mutate();
        d.setColorFilter(color, Mode.SRC_IN);
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(d)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mOmniSwitchSettings){
            startActivity(INTENT_OMNISWITCH_SETTINGS);
            return true;
         }
         return super.onPreferenceTreeClick(preferenceScreen, preference);
     }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mMemoryBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY,
                    value ? 1 : 0);
            return true;
        } else if (preference == mRecentsClearAll) {
            boolean show = (Boolean) objValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.SHOW_CLEAR_ALL_RECENTS, show ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            updateRecentsLocation(location);
            refreshSettings();
            return true;
        } else if (preference == mScreenPinning) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_TO_APP_ENABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mRecentsUseOmniSwitch) {
            boolean value = (Boolean) objValue;
            if (value && !mOmniSwitchInitCalled){
                openOmniSwitchFirstTimeWarning();
                mOmniSwitchInitCalled = true;
            }
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_USE_OMNISWITCH,
                    value ? 1 : 0);
            mOmniSwitchSettings.setEnabled(value);
            return true;
        } else if (preference == mRecentsStyle) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, show ? 1 : 0);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mImmersiveRecents) {
            Settings.System.putInt(getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.valueOf((String) objValue));
            mImmersiveRecents.setValue(String.valueOf(objValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference == mRecentsFontStyle) {
            int val = Integer.parseInt((String) objValue);
            int index = mRecentsFontStyle.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FONT_STYLE, val);
            mRecentsFontStyle.setSummary(mRecentsFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mRecentsFontSize) {
            int width = ((Integer)objValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_DATE_SIZE, width);
            return true;
        } else if (preference == mSearchBar) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_SEARCH_BAR, show ? 1 : 0);
            return true;
        } else if (preference == mShakeClean) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHAKE_TO_CLEAN_RECENTS, show ? 1 : 0);
            return true;
        } else if (preference == mShowClock) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK, show ? 1 : 0);
            return true;
        } else if (preference == mShowDate) {
            boolean show = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_FULL_SCREEN_DATE, show ? 1 : 0);
            return true;
        }
        return false;
    }

    private void updateRecentsLocation(int value) {
        ContentResolver resolver = getContentResolver();
        Resources res = getResources();
        int summary = -1;

        Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, value);

        if (value == 0) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 0);
            summary = R.string.recents_clear_all_location_top_right;
        } else if (value == 1) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 1);
            summary = R.string.recents_clear_all_location_top_left;
        } else if (value == 2) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 2);
            summary = R.string.recents_clear_all_location_top_center;
        } else if (value == 3) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
            summary = R.string.recents_clear_all_location_bottom_right;
        } else if (value == 4) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 4);
            summary = R.string.recents_clear_all_location_bottom_left;
        } else if (value == 5) {
            Settings.System.putInt(resolver, Settings.System.RECENTS_CLEAR_ALL_LOCATION, 5);
            summary = R.string.recents_clear_all_location_bottom_center;
        }
        if (mRecentsClearAllLocation != null && summary != -1) {
            mRecentsClearAllLocation.setSummary(res.getString(summary));
        }
    }

    private void openOmniSwitchFirstTimeWarning() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.omniswitch_first_time_title))
                .setMessage(getResources().getString(R.string.omniswitch_first_time_message))
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                }).show();
    }
    
    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        AndroidRecentsSettings getOwner() {
            return (AndroidRecentsSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHOW_CLEAR_ALL_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 0);
                                    Helpers.restartSystemUI();
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.IMMERSIVE_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FONT_STYLE,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SEARCH_BAR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHAKE_TO_CLEAN_RECENTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHOW_CLEAR_ALL_RECENTS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SYSTEMUI_RECENTS_MEM_DISPLAY, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_EMPTY_VRTOXIN_LOGO, 1);
                                    Helpers.restartSystemUI();
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.IMMERSIVE_RECENTS, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FONT_STYLE,
                                    20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SEARCH_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SHAKE_TO_CLEAN_RECENTS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE, 1);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }

}
