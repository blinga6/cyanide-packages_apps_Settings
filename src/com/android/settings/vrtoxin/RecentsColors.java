/*
* Copyright (C) 2016 Cyanide Android (rogersb11)
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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.internal.logging.MetricsLogger;

public class RecentsColors extends SettingsPreferenceFragment implements
			Preference.OnPreferenceChangeListener {

    private static final String PREF_CLEAR_ALL_USE_ICON_COLOR = "recents_clear_all_use_icon_color";
    private static final String PREF_CLEAR_ALL_BG_COLOR = "recent_apps_clear_all_bg_color";
    private static final String PREF_CLEAR_ALL_ICON_COLOR = "recent_apps_clear_all_icon_color";
    private static final String MEM_TEXT_COLOR = "mem_text_color";
    private static final String MEMORY_BAR_COLOR = "memory_bar_color";
    private static final String MEMORY_BAR_USED_COLOR = "memory_bar_used_color";
    private static final String RECENTS_FULL_SCREEN_CLOCK_COLOR = "recents_full_screen_clock_color";
    private static final String RECENTS_FULL_SCREEN_DATE_COLOR = "recents_full_screen_date_color";
    private static final String RECENTS_APP_ICON_COLOR = "recents_app_icon_color";
    private static final String RECENTS_APP_ICON_COLOR_MODE = "recents_app_icon_color_mode";
    private static final String RECENTS_BG_COLOR = "recents_bg_color";
    private static final String RECENTS_ICON_COLOR = "recents_icon_color";
    private static final String RECENTS_TEXT_COLOR = "recents_text_color";
    private static final String RECENTS_DARK_MODE_BG_COLOR = "recents_dark_mode_bg_color";
    private static final String RECENTS_DARK_MODE_ICON_COLOR = "recents_dark_mode_icon_color";
    private static final String RECENTS_DARK_MODE_TEXT_COLOR = "recents_dark_mode_text_color";
    private static final String RECENTS_RIPPLE_COLOR = "recents_ripple_color";
    private static final String RECENTS_DARK_MODE_RIPPLE_COLOR = "recents_dark_mode_ripple_color";
    
    private static final int BLACK =0xff000000;
    private static final int WHITE = 0xffffffff;
    private static final int CYANIDE_BLUE = 0xff1976D2;
    private static final int CYANIDE_GREEN = 0xff00ff00;
    private static final int CYANIDE_RED = 0xffff0000;
    private static final int TRANSLUCENT_BLACK = 0x7a000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mClearAllUseIconColor;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;
    private ColorPickerPreference mMemTextColor;
    private ColorPickerPreference mMemBarColor;
    private ColorPickerPreference mMemBarUsedColor;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mDateColor;
    private ListPreference mCardAppIconColorMode;
    private ColorPickerPreference mCardAppIconColor;
    private ColorPickerPreference mCardBgColor;
    private ColorPickerPreference mCardIconColor;
    private ColorPickerPreference mCardTextColor;
    private ColorPickerPreference mCardDarkBgColor;
    private ColorPickerPreference mCardDarkIconColor;
    private ColorPickerPreference mCardDarkTextColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mRippleDarkColor;

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
        addPreferencesFromResource(R.xml.recents_colors);

        PreferenceScreen prefSet = getPreferenceScreen();
        mResolver = getActivity().getContentResolver();
        int intColor;
        String hexColor;
        
        mClearAllUseIconColor = (SwitchPreference) findPreference(PREF_CLEAR_ALL_USE_ICON_COLOR);
        boolean clearAllUseIconColor = Settings.System.getInt(mResolver,
               Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 0) == 1;
        mClearAllUseIconColor.setChecked(clearAllUseIconColor);
        mClearAllUseIconColor.setOnPreferenceChangeListener(this);

        mClearAllBgColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, CYANIDE_BLUE); 
        mClearAllBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllBgColor.setSummary(hexColor);
        mClearAllBgColor.setOnPreferenceChangeListener(this);

        mClearAllIconColor = (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE); 
        mClearAllIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClearAllIconColor.setSummary(hexColor);
        mClearAllIconColor.setOnPreferenceChangeListener(this);

        mMemTextColor =
                (ColorPickerPreference) findPreference(MEM_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MEM_TEXT_COLOR, WHITE); 
        mMemTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mMemTextColor.setSummary(hexColor);
        mMemTextColor.setOnPreferenceChangeListener(this);

        mMemBarColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MEMORY_BAR_COLOR, WHITE); 
        mMemBarColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mMemBarColor.setSummary(hexColor);
        mMemBarColor.setOnPreferenceChangeListener(this);

        mMemBarUsedColor =
                (ColorPickerPreference) findPreference(MEMORY_BAR_USED_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MEMORY_BAR_USED_COLOR, WHITE); 
        mMemBarUsedColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mMemBarUsedColor.setSummary(hexColor);
        mMemBarUsedColor.setOnPreferenceChangeListener(this);

        mClockColor =
                (ColorPickerPreference) findPreference(RECENTS_FULL_SCREEN_CLOCK_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR, WHITE);
        mClockColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClockColor.setSummary(hexColor);
        mClockColor.setOnPreferenceChangeListener(this);

        mDateColor =
                (ColorPickerPreference) findPreference(RECENTS_FULL_SCREEN_DATE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR, WHITE);
        mDateColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDateColor.setSummary(hexColor);
        mDateColor.setOnPreferenceChangeListener(this);

        mCardAppIconColorMode = (ListPreference) findPreference(RECENTS_APP_ICON_COLOR_MODE);
        int cardAppIconColorMode = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_APP_ICON_COLOR_MODE, 0);
        mCardAppIconColorMode.setValue(String.valueOf(cardAppIconColorMode));
        mCardAppIconColorMode.setSummary(mCardAppIconColorMode.getEntry());
        mCardAppIconColorMode.setOnPreferenceChangeListener(this);

        mCardAppIconColor =
                (ColorPickerPreference) findPreference(RECENTS_APP_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_APP_ICON_COLOR, WHITE); 
        mCardAppIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardAppIconColor.setSummary(hexColor);
        mCardAppIconColor.setOnPreferenceChangeListener(this);

        mCardBgColor =
                (ColorPickerPreference) findPreference(RECENTS_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_BG_COLOR, BLACK); 
        mCardBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardBgColor.setSummary(hexColor);
        mCardBgColor.setOnPreferenceChangeListener(this);

        mCardIconColor =
                (ColorPickerPreference) findPreference(RECENTS_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_ICON_COLOR, WHITE); 
        mCardIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardIconColor.setSummary(hexColor);
        mCardIconColor.setOnPreferenceChangeListener(this);

        mCardTextColor =
                (ColorPickerPreference) findPreference(RECENTS_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_TEXT_COLOR, WHITE); 
        mCardTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardTextColor.setSummary(hexColor);
        mCardTextColor.setOnPreferenceChangeListener(this);

        mCardDarkBgColor =
                (ColorPickerPreference) findPreference(RECENTS_DARK_MODE_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_DARK_MODE_BG_COLOR, WHITE); 
        mCardDarkBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardDarkBgColor.setSummary(hexColor);
        mCardDarkBgColor.setOnPreferenceChangeListener(this);

        mCardDarkIconColor =
                (ColorPickerPreference) findPreference(RECENTS_DARK_MODE_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_DARK_MODE_ICON_COLOR, BLACK); 
        mCardDarkIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardDarkIconColor.setSummary(hexColor);
        mCardDarkIconColor.setOnPreferenceChangeListener(this);

        mCardDarkTextColor =
                (ColorPickerPreference) findPreference(RECENTS_DARK_MODE_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_DARK_MODE_TEXT_COLOR, BLACK); 
        mCardDarkTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCardDarkTextColor.setSummary(hexColor);
        mCardDarkTextColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(RECENTS_RIPPLE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_RIPPLE_COLOR, WHITE); 
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setOnPreferenceChangeListener(this);

        mRippleDarkColor =
                (ColorPickerPreference) findPreference(RECENTS_DARK_MODE_RIPPLE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_DARK_MODE_RIPPLE_COLOR, TRANSLUCENT_BLACK); 
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleDarkColor.setSummary(hexColor);
        mRippleDarkColor.setOnPreferenceChangeListener(this);
        
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

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mClearAllUseIconColor) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR,
                    value ? 1 : 0);
            refreshSettings();
        } else if (preference == mClearAllBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEM_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mMemBarUsedColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MEMORY_BAR_USED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClockColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDateColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardAppIconColorMode) {
            int cardAppIconColorMode = Integer.valueOf((String) objValue);
            int index = mCardAppIconColorMode.findIndexOfValue((String) objValue);
            Settings.System.putInt(mResolver,
                Settings.System.RECENTS_APP_ICON_COLOR_MODE, cardAppIconColorMode);
            preference.setSummary(mCardAppIconColorMode.getEntries()[index]);
            return true;
        } else if (preference == mCardAppIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_APP_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardDarkTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_DARK_MODE_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardDarkBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_DARK_MODE_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCardDarkIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_DARK_MODE_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleDarkColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_DARK_MODE_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
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

        RecentsColors getOwner() {
            return (RecentsColors) getTargetFragment();
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
                                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEM_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_USED_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_APP_ICON_COLOR_MODE,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_APP_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_BG_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_ICON_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_TEXT_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_BG_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_RIPPLE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_RIPPLE_COLOR,
                                    TRANSLUCENT_BLACK);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_cyanide,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_CLEAR_ALL_USE_ICON_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    CYANIDE_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEM_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_COLOR,
                                    CYANIDE_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MEMORY_BAR_USED_COLOR,
                                    CYANIDE_RED);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_CLOCK_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_FULL_SCREEN_DATE_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_APP_ICON_COLOR_MODE,
                                    2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_APP_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_ICON_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_BG_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_ICON_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_BG_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_RIPPLE_COLOR,
                                    CYANIDE_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_DARK_MODE_RIPPLE_COLOR,
                                    CYANIDE_GREEN);
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
