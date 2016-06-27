/* 
 * Copyright (C) 2014 DarkKat
 * Copyright (C) 2016 Cyanide Android (rogersb11)
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
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.vrtoxin.SeekBarPreferenceCham;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedHeaderSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_SHOW_WEATHER = "expanded_header_show_weather";
    private static final String PREF_SHOW_LOCATION = "expanded_header_show_weather_location";
    private static final String STATUS_BAR_HEADER_FONT_STYLE = "status_bar_header_font_style";
    private static final String PREF_BG_COLOR = "expanded_header_background_color";
    private static final String PREF_RIPPLE_COLOR = "expanded_header_ripple_color";
    private static final String PREF_TEXT_COLOR = "expanded_header_text_color";
    private static final String PREF_CUSTOM_HEADER_DEFAULT = "status_bar_custom_header_default";
    private static final String POWER_MENU_BUTTON = "power_menu_button";
    private static final String ALARM_COLOR = "expanded_header_alarm_color";
    private static final String CLOCK_COLOR = "expanded_header_clock_color";
    private static final String DATE_COLOR = "expanded_header_date_color";
    private static final String POWER_MENU_COLOR = "expanded_header_power_menu_color";
    private static final String SETTINGS_COLOR = "expanded_header_settings_color";
    private static final String VRTOXIN_COLOR = "expanded_header_vrtoxin_color";
    private static final String WEATHER_COLOR = "expanded_header_weather_color";
    private static final String CUSTOM_HEADER_IMAGE_SHADOW = "status_bar_custom_header_shadow";
    private static final String STROKE_CATEGORY = "stroke_settings";
    private static final String STATUS_BAR_EXPANDED_HEADER_STROKE = "status_bar_expanded_header_stroke";
    private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR = "status_bar_expanded_header_stroke_color";
    private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS = "status_bar_expanded_header_stroke_thickness";
    private static final String STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS = "status_bar_expanded_header_corner_radius";
    private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP = "status_bar_expanded_header_stroke_dash_gap";
    private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH = "status_bar_expanded_header_stroke_dash_width";

    private static final int SYSTEMUI_SECONDARY = 0xff384248;
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    private static final int CYANIDE_BLUE = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private static final int DISABLED  = 0;
    private static final int ACCENT    = 1;
    private static final int CUSTOM    = 2;

    private SwitchPreference mShowWeather;
    private SwitchPreference mShowLocation;
    private ListPreference mStatusBarHeaderFontStyle;
    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mRippleColor;
    private ColorPickerPreference mTextColor;
    private ListPreference mCustomHeaderDefault;
    private ListPreference mPowerMenuButton;
    private ColorPickerPreference mAlarmColor;
    private ColorPickerPreference mClockColor;
    private ColorPickerPreference mDateColor;
    private ColorPickerPreference mPowerMenuColor;
    private ColorPickerPreference mSettingsColor;
    private ColorPickerPreference mVRToxinColor;
    private ColorPickerPreference mWeatherColor;
    private SeekBarPreferenceCham mHeaderShadow;
    private ListPreference mSBEHStroke;
    private ColorPickerPreference mSBEHStrokeColor;
    private SeekBarPreference mSBEHStrokeThickness;
    private SeekBarPreference mSBEHCornerRadius;
    private SeekBarPreference mSBEHStrokeDashGap;
    private SeekBarPreference mSBEHStrokeDashWidth;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_header_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        boolean showWeather = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER, 0) == 1;
        final int strokeMode = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, ACCENT);
        boolean notDisabled = strokeMode == ACCENT || strokeMode == CUSTOM;

        PreferenceCategory catStroke =
                (PreferenceCategory) findPreference(STROKE_CATEGORY);

        mShowWeather = (SwitchPreference) findPreference(PREF_SHOW_WEATHER);
        mShowWeather.setChecked(showWeather);
        mShowWeather.setOnPreferenceChangeListener(this);

        if (showWeather) {
            mShowLocation = (SwitchPreference) findPreference(PREF_SHOW_LOCATION);
            mShowLocation.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER_LOCATION, 1) == 1);
            mShowLocation.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_SHOW_LOCATION);
        }

        mStatusBarHeaderFontStyle = (ListPreference) findPreference(STATUS_BAR_HEADER_FONT_STYLE);
        mStatusBarHeaderFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarHeaderFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_HEADER_FONT_STYLE, 0)));
        mStatusBarHeaderFontStyle.setSummary(mStatusBarHeaderFontStyle.getEntry());

        mHeaderShadow = (SeekBarPreferenceCham) findPreference(CUSTOM_HEADER_IMAGE_SHADOW);
        final int headerShadow = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, 0);
        mHeaderShadow.setValue((int)(((double) headerShadow / 255) * 100));
        mHeaderShadow.setOnPreferenceChangeListener(this);

        mCustomHeaderDefault = (ListPreference) findPreference(PREF_CUSTOM_HEADER_DEFAULT);
        mCustomHeaderDefault.setOnPreferenceChangeListener(this);
        mCustomHeaderDefault.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_CUSTOM_HEADER_DEFAULT, 0)));
        mCustomHeaderDefault.setSummary(mCustomHeaderDefault.getEntry());

        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_BG_COLOR,
                SYSTEMUI_SECONDARY); 
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setDefaultColors(SYSTEMUI_SECONDARY, SYSTEMUI_SECONDARY);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mRippleColor =
                (ColorPickerPreference) findPreference(PREF_RIPPLE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_RIPPLE_COLOR,
                WHITE); 
        mRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRippleColor.setSummary(hexColor);
        mRippleColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mRippleColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_TEXT_COLOR,
                WHITE); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mTextColor.setOnPreferenceChangeListener(this);

        // Power Menu Button
        mPowerMenuButton = (ListPreference) findPreference(POWER_MENU_BUTTON);
        mPowerMenuButton.setOnPreferenceChangeListener(this);
        mPowerMenuButton.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_MENU_BUTTON, 2)));
        mPowerMenuButton.setSummary(mPowerMenuButton.getEntry());

        mAlarmColor =
                (ColorPickerPreference) findPreference(ALARM_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_ALARM_COLOR,
                WHITE); 
        mAlarmColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAlarmColor.setSummary(hexColor);
        mAlarmColor.setDefaultColors(WHITE, WHITE);
        mAlarmColor.setOnPreferenceChangeListener(this);

        mClockColor =
                (ColorPickerPreference) findPreference(CLOCK_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_CLOCK_COLOR,
                WHITE); 
        mClockColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClockColor.setSummary(hexColor);
        mClockColor.setDefaultColors(WHITE, WHITE);
        mClockColor.setOnPreferenceChangeListener(this);

        mDateColor =
                (ColorPickerPreference) findPreference(DATE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_DATE_COLOR,
                WHITE); 
        mDateColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mDateColor.setSummary(hexColor);
        mDateColor.setDefaultColors(WHITE, WHITE);
        mDateColor.setOnPreferenceChangeListener(this);

        mPowerMenuColor =
                (ColorPickerPreference) findPreference(POWER_MENU_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_POWER_MENU_COLOR,
                WHITE); 
        mPowerMenuColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mPowerMenuColor.setSummary(hexColor);
        mPowerMenuColor.setDefaultColors(WHITE, WHITE);
        mPowerMenuColor.setOnPreferenceChangeListener(this);

        mSettingsColor =
                (ColorPickerPreference) findPreference(SETTINGS_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_SETTINGS_COLOR,
                WHITE); 
        mSettingsColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mSettingsColor.setSummary(hexColor);
        mSettingsColor.setDefaultColors(WHITE, WHITE);
        mSettingsColor.setOnPreferenceChangeListener(this);

        mVRToxinColor =
                (ColorPickerPreference) findPreference(VRTOXIN_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_VRTOXIN_COLOR,
                WHITE); 
        mVRToxinColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mVRToxinColor.setSummary(hexColor);
        mVRToxinColor.setDefaultColors(WHITE, WHITE);
        mVRToxinColor.setOnPreferenceChangeListener(this);

        mWeatherColor =
                (ColorPickerPreference) findPreference(WEATHER_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_WEATHER_COLOR,
                WHITE); 
        mWeatherColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mWeatherColor.setSummary(hexColor);
        mWeatherColor.setDefaultColors(WHITE, WHITE);
        mWeatherColor.setOnPreferenceChangeListener(this);

        mSBEHStroke = (ListPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE);
        mSBEHStroke.setValue(String.valueOf(strokeMode));
        mSBEHStroke.setSummary(mSBEHStroke.getEntry());
        mSBEHStroke.setOnPreferenceChangeListener(this);

        mSBEHCornerRadius =
                (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS);
        int cornerRadius = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, 2);
        mSBEHCornerRadius.setValue(cornerRadius / 1);
        mSBEHCornerRadius.setOnPreferenceChangeListener(this);

        if (notDisabled) {
            mSBEHStrokeDashGap =
                    (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP);
            int strokeDashGap = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, 10);
            mSBEHStrokeDashGap.setValue(strokeDashGap / 1);
            mSBEHStrokeDashGap.setOnPreferenceChangeListener(this);

            mSBEHStrokeDashWidth =
                    (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH);
            int strokeDashWidth = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, 0);
            mSBEHStrokeDashWidth.setValue(strokeDashWidth / 1);
            mSBEHStrokeDashWidth.setOnPreferenceChangeListener(this);

            mSBEHStrokeThickness =
                    (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS);
            int strokeThickness = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, 4);
            mSBEHStrokeThickness.setValue(strokeThickness / 1);
            mSBEHStrokeThickness.setOnPreferenceChangeListener(this);

            if (strokeMode == CUSTOM) {
                mSBEHStrokeColor =
                        (ColorPickerPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, CYANIDE_BLUE); 
                mSBEHStrokeColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSBEHStrokeColor.setSummary(hexColor);
                mSBEHStrokeColor.setOnPreferenceChangeListener(this);
            } else {
                catStroke.removePreference(findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR));
            }
        } else if (strokeMode == DISABLED) {
            catStroke.removePreference(findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS));
            catStroke.removePreference(findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR));
            catStroke.removePreference(findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP));
            catStroke.removePreference(findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH));
        }

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
        boolean value;
        String hex;
        int intHex;

        if (preference == mShowWeather) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER,
                value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowLocation) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER_LOCATION,
                value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarHeaderFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarHeaderFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_HEADER_FONT_STYLE, val);
            mStatusBarHeaderFontStyle.setSummary(mStatusBarHeaderFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mCustomHeaderDefault) {
            int val = Integer.parseInt((String) newValue);
            int index = mCustomHeaderDefault.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CUSTOM_HEADER_DEFAULT, val);
            mCustomHeaderDefault.setSummary(mCustomHeaderDefault.getEntries()[index]);
            return true;
        } else if (preference == mBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mPowerMenuButton) {
            int val = Integer.parseInt((String) newValue);
            int index = mPowerMenuButton.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_MENU_BUTTON, val);
            mPowerMenuButton.setSummary(mPowerMenuButton.getEntries()[index]);
            return true;
        } else if (preference == mAlarmColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_ALARM_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClockColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_CLOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDateColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSettingsColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_SETTINGS_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mPowerMenuColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_POWER_MENU_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mVRToxinColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_VRTOXIN_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWeatherColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_WEATHER_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mHeaderShadow) {
         Integer headerShadow = (Integer) newValue;
         int realHeaderValue = (int) (((double) headerShadow / 100) * 255);
         Settings.System.putInt(getContentResolver(),
                 Settings.System.STATUS_BAR_CUSTOM_HEADER_SHADOW, realHeaderValue);
			return true;
        } else if (preference == mSBEHStroke) {
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE,
                    Integer.valueOf((String) newValue));
            mSBEHStroke.setValue(String.valueOf(newValue));
            mSBEHStroke.setSummary(mSBEHStroke.getEntry());
            refreshSettings();
            return true;
        } else if (preference == mSBEHStrokeColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSBEHStrokeThickness) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mSBEHCornerRadius) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, val * 1);
            return true;
        } else if (preference == mSBEHStrokeDashGap) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, val * 1);
            return true;
        } else if (preference == mSBEHStrokeDashWidth) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, val * 1);
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

        StatusBarExpandedHeaderSettings getOwner() {
            return (StatusBarExpandedHeaderSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER_LOCATION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_BG_COLOR,
                                    SYSTEMUI_SECONDARY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_RIPPLE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BUTTON, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_HEADER_FONT_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_ALARM_COLOR,
                                    SYSTEMUI_SECONDARY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CLOCK_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_DATE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_POWER_MENU_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SETTINGS_COLOR,
                                    SYSTEMUI_SECONDARY);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_VRTOXIN_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_WEATHER_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_cyanide,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SHOW_WEATHER_LOCATION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_BG_COLOR,
                                    BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_RIPPLE_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_ICON_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_BUTTON, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_HEADER_FONT_STYLE, 20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_ALARM_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CLOCK_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_DATE_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_POWER_MENU_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_SETTINGS_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_VRTOXIN_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_WEATHER_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, 20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, 0);
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
