/* 
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.EditTextPreference;
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
import com.android.settings.vrtoxin.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ExpansionView extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_COLORS = "colors";
    private static final String EXPANSION_VIEW_TEXT_COLOR = "expansion_view_text_color";
    private static final String EXPANSION_VIEW_FONT_STYLE = "expansion_view_font_style";
    private static final String EXPANSION_VIEW_TEXT_SIZE = "expansion_view_text_size";
    private static final String EXPANSION_VIEW_TEXT_CUSTOM = "expansion_view_text_custom";
    private static final String EXPANSION_VIEW_ICON_COLOR = "expansion_view_icon_color";
    private static final String EXPANSION_VIEW_RIPPLE_COLOR = "expansion_view_ripple_color";
    private static final String EXPANSION_VIEW_WEATHER_SHOW_CURRENT = "expansion_view_weather_show_current";
    private static final String EXPANSION_VIEW_WEATHER_ICON_TYPE = "expansion_view_weather_icon_type";
    private static final String EXPANSION_VIEW_WEATHER_ICON_COLOR = "expansion_view_weather_icon_color";
    private static final String EXPANSION_VIEW_WEATHER_TEXT_COLOR = "expansion_view_weather_text_color";
    private static final String EXPANSION_VIEW_WEATHER_TEXT_SIZE = "expansion_view_weather_text_size";
    private static final String EXPANSION_VIEW_BACKGROUND_COLOR = "expansion_view_background_color";
    private static final String EXPANSION_VIEW_ANIMATION = "expansion_view_animation";
    private static final String EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE = "expansion_view_activity_panel_text_size";
    private static final String EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR = "expansion_view_activity_panel_text_color";
    private static final String EXPANSION_VIEW_CUSTOM_LOGO = "expansion_view_custom_logo";
    private static final String EXPANSION_VIEW_CUSTOM_RESET = "expansion_view_custom_reset";
    private static final String EXPANSION_VIEW_FORCE_SHOW = "expansion_view_force_show";
    private static final String EXPANSION_VIEW_VIBRATION = "expansion_view_vibration";
    private static final String EXPANSION_VIEW_PANEL_SHORTCUTS = "expansion_view_panel_shortcuts";
    private static final String STROKE_CATEGORY = "stroke_settings";
    private static final String EXPANSION_VIEW_STROKE = "expansion_view_stroke";
    private static final String EXPANSION_VIEW_STROKE_COLOR = "expansion_view_stroke_color";
    private static final String EXPANSION_VIEW_STROKE_THICKNESS = "expansion_view_stroke_thickness";
    private static final String EXPANSION_VIEW_CORNER_RADIUS = "expansion_view_corner_radius";
    private static final String EXPANSION_VIEW_STROKE_DASH_GAP = "expansion_view_stroke_dash_gap";
    private static final String EXPANSION_VIEW_STROKE_DASH_WIDTH = "expansion_view_stroke_dash_width";

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    private static final int CYANIDE_BLUE = 0xff1976D2;
    private static final int CYANIDE_GREEN = 0xff00ff00;
    private static final int DEFAULT_STROKE_COLOR = 0xffffffff;

    private static final int DISABLED  = 0;
    private static final int ACCENT    = 1;
    private static final int CUSTOM    = 2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    public final static int IMAGE_PICK = 1;

    private ColorPickerPreference mExpansionViewTextColor;
    private ListPreference mExpansionViewFontStyle;
    private SeekBarPreference mExpansionViewTextSize;
    private EditTextPreference mCustomText;
    private ColorPickerPreference mExpansionViewIconColor;
    private ColorPickerPreference mExpansionViewRippleColor;
    private SwitchPreference mShowCurrent;
    private ListPreference mIconType;
    private ColorPickerPreference mExpansionViewWeatherIconColor;
    private ColorPickerPreference mExpansionViewWeatherTextColor;
    private SeekBarPreference mExpansionViewWeatherTextSize;
    private ColorPickerPreference mExpansionViewBgColor;
    private SwitchPreference mShowBg;
    private ListPreference mExpansionViewAnimation;
    private SeekBarPreference mExpansionViewActivityPanelTextSize;
    private ColorPickerPreference mExpansionViewActivityPanelTextColor;
    private Preference mCustomLogo;
    private Preference mCustomLogoReset;
    private SwitchPreference mForceView;
    private SwitchPreference mVibrate;
    private SwitchPreference mShowShortcutBar;
    private ListPreference mStroke;
    private ColorPickerPreference mStrokeColor;
    private SeekBarPreference mStrokeThickness;
    private SeekBarPreference mCornerRadius;
    private SeekBarPreference mStrokeDashGap;
    private SeekBarPreference mStrokeDashWidth;

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

        addPreferencesFromResource(R.xml.expansion_view);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        final int strokeMode = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_STROKE, DISABLED);
        boolean notDisabled = strokeMode == ACCENT || strokeMode == CUSTOM;

        PreferenceCategory catStroke =
                (PreferenceCategory) findPreference(STROKE_CATEGORY);

        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mExpansionViewTextColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_TEXT_COLOR,
                WHITE); 
        mExpansionViewTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewTextColor.setSummary(hexColor);
        mExpansionViewTextColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewTextColor.setOnPreferenceChangeListener(this);

        mExpansionViewFontStyle = (ListPreference) findPreference(EXPANSION_VIEW_FONT_STYLE);
        mExpansionViewFontStyle.setOnPreferenceChangeListener(this);
        mExpansionViewFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.EXPANSION_VIEW_FONT_STYLE, 0)));
        mExpansionViewFontStyle.setSummary(mExpansionViewFontStyle.getEntry());

        mExpansionViewTextSize =
                (SeekBarPreference) findPreference(EXPANSION_VIEW_TEXT_SIZE);
        mExpansionViewTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.EXPANSION_VIEW_TEXT_SIZE, 14));
        mExpansionViewTextSize.setOnPreferenceChangeListener(this);

        mCustomText = (EditTextPreference) findPreference(EXPANSION_VIEW_TEXT_CUSTOM);
        mCustomText.getEditText().setHint(getResources().getString(
                com.android.internal.R.string.default_empty_shade_text));
        mCustomText.setOnPreferenceChangeListener(this);
        updateCustomTextPreference();

        mExpansionViewIconColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_ICON_COLOR,
                WHITE); 
        mExpansionViewIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewIconColor.setSummary(hexColor);
        mExpansionViewIconColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewIconColor.setOnPreferenceChangeListener(this);

        mExpansionViewRippleColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_RIPPLE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_RIPPLE_COLOR,
                WHITE); 
        mExpansionViewRippleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewRippleColor.setSummary(hexColor);
        mExpansionViewRippleColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewRippleColor.setOnPreferenceChangeListener(this);

        mShowCurrent =
                (SwitchPreference) findPreference(EXPANSION_VIEW_WEATHER_SHOW_CURRENT);
        mShowCurrent.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EXPANSION_VIEW_WEATHER_SHOW_CURRENT, 1) == 1);
        mShowCurrent.setOnPreferenceChangeListener(this);

        mIconType = (ListPreference) findPreference(EXPANSION_VIEW_WEATHER_ICON_TYPE);
        int iconType = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_WEATHER_ICON_TYPE, 0);
        mIconType.setValue(String.valueOf(iconType));
        mIconType.setSummary(mIconType.getEntry());
        mIconType.setOnPreferenceChangeListener(this);

        mExpansionViewWeatherIconColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_WEATHER_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_WEATHER_ICON_COLOR,
                WHITE); 
        mExpansionViewWeatherIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewWeatherIconColor.setSummary(hexColor);
        mExpansionViewWeatherIconColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewWeatherIconColor.setOnPreferenceChangeListener(this);

        mExpansionViewWeatherTextColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_WEATHER_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_WEATHER_TEXT_COLOR,
                WHITE); 
        mExpansionViewWeatherTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewWeatherTextColor.setSummary(hexColor);
        mExpansionViewWeatherTextColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewWeatherTextColor.setOnPreferenceChangeListener(this);

        mExpansionViewWeatherTextSize =
                (SeekBarPreference) findPreference(EXPANSION_VIEW_WEATHER_TEXT_SIZE);
        mExpansionViewWeatherTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.EXPANSION_VIEW_WEATHER_TEXT_SIZE, 14));
        mExpansionViewWeatherTextSize.setOnPreferenceChangeListener(this);

        mExpansionViewBgColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_BACKGROUND_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_BACKGROUND_COLOR, WHITE); 
        mExpansionViewBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewBgColor.setSummary(hexColor);
        mExpansionViewBgColor.setOnPreferenceChangeListener(this);

        mExpansionViewAnimation = (ListPreference) findPreference(EXPANSION_VIEW_ANIMATION);
        mExpansionViewAnimation.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.EXPANSION_VIEW_ANIMATION, 0)));
        mExpansionViewAnimation.setSummary(mExpansionViewAnimation.getEntry());
        mExpansionViewAnimation.setOnPreferenceChangeListener(this);

        mExpansionViewActivityPanelTextSize =
                (SeekBarPreference) findPreference(EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE);
        mExpansionViewActivityPanelTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE, 14));
        mExpansionViewActivityPanelTextSize.setOnPreferenceChangeListener(this);

        mExpansionViewActivityPanelTextColor =
                (ColorPickerPreference) findPreference(EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR,
                WHITE); 
        mExpansionViewActivityPanelTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mExpansionViewActivityPanelTextColor.setSummary(hexColor);
        mExpansionViewActivityPanelTextColor.setDefaultColors(WHITE, CYANIDE_BLUE);
        mExpansionViewActivityPanelTextColor.setOnPreferenceChangeListener(this);

        mCustomLogo = findPreference(EXPANSION_VIEW_CUSTOM_LOGO);
        mCustomLogoReset = findPreference(EXPANSION_VIEW_CUSTOM_RESET);

        mForceView =
                (SwitchPreference) findPreference(EXPANSION_VIEW_FORCE_SHOW);
        mForceView.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EXPANSION_VIEW_FORCE_SHOW, 0) == 1);
        mForceView.setOnPreferenceChangeListener(this);

        mVibrate =
                (SwitchPreference) findPreference(EXPANSION_VIEW_VIBRATION);
        mVibrate.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EXPANSION_VIEW_VIBRATION, 0) == 1);
        mVibrate.setOnPreferenceChangeListener(this);

        mShowShortcutBar =
                (SwitchPreference) findPreference(EXPANSION_VIEW_PANEL_SHORTCUTS);
        mShowShortcutBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.EXPANSION_VIEW_PANEL_SHORTCUTS, 1) == 1);
        mShowShortcutBar.setOnPreferenceChangeListener(this);

        mStroke = (ListPreference) findPreference(EXPANSION_VIEW_STROKE);
        mStroke.setValue(String.valueOf(strokeMode));
        mStroke.setSummary(mStroke.getEntry());
        mStroke.setOnPreferenceChangeListener(this);

        mCornerRadius =
                (SeekBarPreference) findPreference(EXPANSION_VIEW_CORNER_RADIUS);
        int cornerRadius = Settings.System.getInt(mResolver,
                Settings.System.EXPANSION_VIEW_CORNER_RADIUS, 2);
        mCornerRadius.setValue(cornerRadius / 1);
        mCornerRadius.setOnPreferenceChangeListener(this);

        if (notDisabled) {
            mStrokeDashGap =
                    (SeekBarPreference) findPreference(EXPANSION_VIEW_STROKE_DASH_GAP);
            int strokeDashGap = Settings.System.getInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_DASH_GAP, 10);
            mStrokeDashGap.setValue(strokeDashGap / 1);
            mStrokeDashGap.setOnPreferenceChangeListener(this);

            mStrokeDashWidth =
                    (SeekBarPreference) findPreference(EXPANSION_VIEW_STROKE_DASH_WIDTH);
            int strokeDashWidth = Settings.System.getInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_DASH_WIDTH, 0);
            mStrokeDashWidth.setValue(strokeDashWidth / 1);
            mStrokeDashWidth.setOnPreferenceChangeListener(this);

            mStrokeThickness =
                    (SeekBarPreference) findPreference(EXPANSION_VIEW_STROKE_THICKNESS);
            int strokeThickness = Settings.System.getInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_THICKNESS, 4);
            mStrokeThickness.setValue(strokeThickness / 1);
            mStrokeThickness.setOnPreferenceChangeListener(this);

            if (strokeMode == CUSTOM) {
                mStrokeColor =
                        (ColorPickerPreference) findPreference(EXPANSION_VIEW_STROKE_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.EXPANSION_VIEW_STROKE_COLOR, CYANIDE_BLUE); 
                mStrokeColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mStrokeColor.setSummary(hexColor);
                mStrokeColor.setOnPreferenceChangeListener(this);
            } else {
                catStroke.removePreference(findPreference(EXPANSION_VIEW_STROKE_COLOR));
            }
        } else if (strokeMode == DISABLED) {
            catStroke.removePreference(findPreference(EXPANSION_VIEW_STROKE_THICKNESS));
            catStroke.removePreference(findPreference(EXPANSION_VIEW_STROKE_COLOR));
            catStroke.removePreference(findPreference(EXPANSION_VIEW_STROKE_DASH_GAP));
            catStroke.removePreference(findPreference(EXPANSION_VIEW_STROKE_DASH_WIDTH));
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
        int intValue;
        int index;

        if (preference == mExpansionViewTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mExpansionViewFontStyle) {
            int val = Integer.parseInt((String) newValue);
            index = mExpansionViewFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_FONT_STYLE, val);
            mExpansionViewFontStyle.setSummary(mExpansionViewFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mCustomText) {
            String text = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.EXPANSION_VIEW_TEXT_CUSTOM, text);
            updateCustomTextPreference();
        } else if (preference == mExpansionViewTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_TEXT_SIZE, width);
            return true;
        } else if (preference == mExpansionViewIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_ICON_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mExpansionViewRippleColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_RIPPLE_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mShowCurrent) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_WEATHER_SHOW_CURRENT,
                    value ? 1 : 0);
            return true;
        } else if (preference == mIconType) {
            intValue = Integer.valueOf((String) newValue);
            index = mIconType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_WEATHER_ICON_TYPE,
                    intValue);
            mIconType.setSummary(mIconType.getEntries()[index]);
            return true;
        } else if (preference == mExpansionViewWeatherIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_WEATHER_ICON_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mExpansionViewWeatherTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_WEATHER_TEXT_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mExpansionViewWeatherTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_WEATHER_TEXT_SIZE, width);
            return true;
        } else if (preference == mExpansionViewIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_ICON_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mExpansionViewBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mExpansionViewAnimation) {
            Settings.System.putInt(mResolver, Settings.System.EXPANSION_VIEW_ANIMATION,
                    Integer.valueOf((String) newValue));
            mExpansionViewAnimation.setValue(String.valueOf(newValue));
            mExpansionViewAnimation.setSummary(mExpansionViewAnimation.getEntry());
            return true;
        } else if (preference == mExpansionViewActivityPanelTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE, width);
            return true;
        } else if (preference == mExpansionViewActivityPanelTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR, intHex);
            preference.setSummary(hex);
        } else if (preference == mForceView) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_FORCE_SHOW,
                    value ? 1 : 0);
            return true;
        } else if (preference == mVibrate) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_VIBRATION,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowShortcutBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_PANEL_SHORTCUTS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStroke) {
            Settings.System.putInt(mResolver, Settings.System.EXPANSION_VIEW_STROKE,
                    Integer.valueOf((String) newValue));
            mStroke.setValue(String.valueOf(newValue));
            mStroke.setSummary(mStroke.getEntry());
            refreshSettings();
            return true;
        } else if (preference == mStrokeColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStrokeThickness) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mCornerRadius) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_CORNER_RADIUS, val * 1);
            return true;
        } else if (preference == mStrokeDashGap) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_DASH_GAP, val * 1);
            return true;
        } else if (preference == mStrokeDashWidth) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANSION_VIEW_STROKE_DASH_WIDTH, val * 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCustomLogo) {
            setCustomLogo();
            return true;
        } else if (preference == mCustomLogoReset) {
            Settings.System.putString(mResolver,
            mCustomLogo.getKey(), "");
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Settings.System.putString(mResolver, EXPANSION_VIEW_CUSTOM_LOGO, selectedImage.toString());
        }
    }

    private void setCustomLogo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    private void updateCustomTextPreference() {
        String customText = Settings.System.getString(mResolver,
                Settings.System.EXPANSION_VIEW_TEXT_CUSTOM);
        if (customText == null) {
            customText = "";
        }
        mCustomText.setText(customText);
        mCustomText.setSummary(customText);
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

        ExpansionView getOwner() {
            return (ExpansionView) getTargetFragment();
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
                                    Settings.System.EXPANSION_VIEW_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_FONT_STYLE, 0);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_TEXT_CUSTOM,
                                    "Nothing to see here");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_TEXT_SIZE, 20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_RIPPLE_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_ICON_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_TEXT_SIZE, 12);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_SHOW_CURRENT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ANIMATION,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE,
                                    16);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_FORCE_SHOW, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_VIBRATION, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_SHORTCUTS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE,
                                    16);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_BACKGROUND_COLOR, 0xffffffff);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE,
                                    0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE_COLOR, WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE_THICKNESS, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_CORNER_RADIUS, 4);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_STROKE_DASH_GAP, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_STROKE_DASH_WIDTH, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_FONT_STYLE, 20);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_TEXT_CUSTOM,
                                    "Cyanide Is Fuckin Awesome!!!!");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_TEXT_SIZE, 25);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ICON_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_RIPPLE_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_ICON_COLOR,
                                    CYANIDE_GREEN);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_TEXT_SIZE, 18);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_WEATHER_SHOW_CURRENT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ANIMATION,
                                    2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_SIZE,
                                    20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_ACTIVITY_PANEL_TEXT_COLOR,
                                    CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_FORCE_SHOW, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_VIBRATION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_SHORTCUTS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_BACKGROUND_COLOR, BLACK);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE,
                                    2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE_COLOR, CYANIDE_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_STROKE_THICKNESS, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_CORNER_RADIUS, 20);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_STROKE_DASH_GAP, 10);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QS_STROKE_DASH_WIDTH, 0);
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
