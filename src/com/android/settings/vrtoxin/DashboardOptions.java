/*
 * Copyright (C) 2015 CyanideL
 * Copyright (C) 2016 The VRToxin Project
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

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.vrtoxin.SeekBarPreference;
import com.android.internal.logging.MetricsLogger;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class DashboardOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_BG_COLOR = "settings_bg_color";
    private static final String PREF_ICON_COLOR = "settings_icon_color";
    private static final String PREF_TEXT_COLOR = "settings_title_text_color";
    private static final String PREF_CAT_TEXT_COLOR = "settings_category_text_color";
    private static final String DASHBOARD_FONT_STYLE = "dashboard_font_style";
    private static final String DASHBOARD_COLUMNS_COUNT = "dashboard_columns_count";
    private static final String SETTINGS_TITLE_TEXT_SIZE  = "settings_title_text_size";
    private static final String SETTINGS_CATEGORY_TEXT_SIZE  = "settings_category_text_size";
    private static final String SETTINGS_TOOLBAR_TEXT_COLOR = "settings_toolbar_text_color";
    private static final String MODS_UNDERLINE_COLOR = "mods_underline_color";
    private static final String MODS_DIVIDER_COLOR = "mods_divider_color";
    private static final String MODS_TAB_TEXT_COLOR = "mods_tab_text_color";

    private ColorPickerPreference mBgColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mCatTextColor;
    private ListPreference mDashFontStyle;
    private ListPreference mDashColumns;
    private SeekBarPreference mDashTitleTextSize;
    private SeekBarPreference mDashCategoryTextSize;
    private ColorPickerPreference mToolbarTextColor;
    private ColorPickerPreference mUnderlineColor;
    private ColorPickerPreference mDividerColor;
    private ColorPickerPreference mTabTextColor;

    private static final int TRANSLUCENT_BLACK = 0x80000000;
    private static final int CYANIDE_BLUE = 0xff1976D2;
    private static final int CYANIDE_GREEN = 0xff00ff00;
    private static final int WHITE = 0xffffffff;
    private static final int BLACK = 0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

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

        addPreferencesFromResource(R.xml.dashboard_options);
        mResolver = getActivity().getContentResolver();

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mBgColor =
                (ColorPickerPreference) findPreference(PREF_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_BG_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBgColor.setNewPreviewColor(intColor);
        mBgColor.setSummary(hexColor);
        mBgColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ICON_COLOR, CYANIDE_BLUE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setNewPreviewColor(intColor);
        mIconColor.setSummary(hexColor);
        mIconColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_TITLE_TEXT_COLOR, BLACK);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setNewPreviewColor(intColor);
        mTextColor.setSummary(hexColor);
        mTextColor.setOnPreferenceChangeListener(this);

        mCatTextColor =
                (ColorPickerPreference) findPreference(PREF_CAT_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_CATEGORY_TEXT_COLOR, CYANIDE_BLUE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCatTextColor.setNewPreviewColor(intColor);
        mCatTextColor.setSummary(hexColor);
        mCatTextColor.setOnPreferenceChangeListener(this);

        mDashFontStyle = (ListPreference) findPreference(DASHBOARD_FONT_STYLE);
        mDashFontStyle.setOnPreferenceChangeListener(this);
        mDashFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.DASHBOARD_FONT_STYLE, 0)));
        mDashFontStyle.setSummary(mDashFontStyle.getEntry());

        mDashColumns = (ListPreference) findPreference(DASHBOARD_COLUMNS_COUNT);
        mDashColumns.setOnPreferenceChangeListener(this);
        mDashColumns.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.DASHBOARD_COLUMNS_COUNT, 0)));
        mDashColumns.setSummary(mDashColumns.getEntry());

        mDashTitleTextSize =
                (SeekBarPreference) findPreference(SETTINGS_TITLE_TEXT_SIZE);
        mDashTitleTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SETTINGS_TITLE_TEXT_SIZE, 18));
        mDashTitleTextSize.setOnPreferenceChangeListener(this);

        mDashCategoryTextSize =
                (SeekBarPreference) findPreference(SETTINGS_CATEGORY_TEXT_SIZE);
        mDashCategoryTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SETTINGS_CATEGORY_TEXT_SIZE, 14));
        mDashCategoryTextSize.setOnPreferenceChangeListener(this);

        mToolbarTextColor =
                (ColorPickerPreference) findPreference(SETTINGS_TOOLBAR_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_TOOLBAR_TEXT_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mToolbarTextColor.setNewPreviewColor(intColor);
        mToolbarTextColor.setSummary(hexColor);
        mToolbarTextColor.setOnPreferenceChangeListener(this);

        mUnderlineColor =
                (ColorPickerPreference) findPreference(MODS_UNDERLINE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MODS_UNDERLINE_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mUnderlineColor.setNewPreviewColor(intColor);
        mUnderlineColor.setSummary(hexColor);
        mUnderlineColor.setOnPreferenceChangeListener(this);

        mDividerColor =
                (ColorPickerPreference) findPreference(MODS_DIVIDER_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MODS_DIVIDER_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDividerColor.setNewPreviewColor(intColor);
        mDividerColor.setSummary(hexColor);
        mDividerColor.setOnPreferenceChangeListener(this);

        mTabTextColor =
                (ColorPickerPreference) findPreference(MODS_TAB_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.MODS_TAB_TEXT_COLOR, WHITE);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTabTextColor.setNewPreviewColor(intColor);
        mTabTextColor.setSummary(hexColor);
        mTabTextColor.setOnPreferenceChangeListener(this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intHex;
        String hex;

        if (preference == mBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_TITLE_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCatTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_CATEGORY_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDashFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mDashFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.DASHBOARD_FONT_STYLE, val);
            mDashFontStyle.setSummary(mDashFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mDashColumns) {
            int val = Integer.parseInt((String) newValue);
            int index = mDashColumns.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.DASHBOARD_COLUMNS_COUNT, val);
            mDashColumns.setSummary(mDashColumns.getEntries()[index]);
            return true;
        } else if (preference == mDashTitleTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SETTINGS_TITLE_TEXT_SIZE, width);
            return true;
        } else if (preference == mDashCategoryTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SETTINGS_CATEGORY_TEXT_SIZE, width);
            return true;
        } else if (preference == mToolbarTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.SETTINGS_TOOLBAR_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mUnderlineColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MODS_UNDERLINE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDividerColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MODS_DIVIDER_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTabTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.MODS_TAB_TEXT_COLOR, intHex);
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

        DashboardOptions getOwner() {
            return (DashboardOptions) getTargetFragment();
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
                                    Settings.System.SETTINGS_BG_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ICON_COLOR,
                                    0xff009688);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_TITLE_TEXT_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_CATEGORY_TEXT_COLOR,
                                    0xff009688);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DASHBOARD_FONT_STYLE,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DASHBOARD_COLUMNS_COUNT,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_TITLE_TEXT_SIZE,
                                    18);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_CATEGORY_TEXT_SIZE,
                                    14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_UNDERLINE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_DIVIDER_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_TAB_TEXT_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_BG_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_ICON_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_TITLE_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_CATEGORY_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DASHBOARD_FONT_STYLE,
                                    20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.DASHBOARD_COLUMNS_COUNT,
                                    1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_TITLE_TEXT_SIZE,
                                    18);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.SETTINGS_CATEGORY_TEXT_SIZE,
                                    14);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_UNDERLINE_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_DIVIDER_COLOR,
                                    CYANIDE_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.MODS_TAB_TEXT_COLOR,
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
