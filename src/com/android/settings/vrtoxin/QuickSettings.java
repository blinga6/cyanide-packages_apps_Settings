/*
 * Copyright (C) 2015 VRToxin Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.vrtoxin.qs.DraggableGridView;
import com.android.settings.vrtoxin.qs.QSTiles;
import com.android.internal.logging.MetricsLogger;

import com.android.internal.widget.LockPatternUtils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.List;

public class QuickSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_QS_TYPE = "qs_type";
    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    private static final String PREF_NUM_OF_COLUMNS = "sysui_qs_num_columns";
    private static final String PREF_BLOCK_ON_SECURE_KEYGUARD = "block_on_secure_keyguard";
    private static final String PREF_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String PREF_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String PREF_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    private static final String PREF_QS_STROKE = "qs_stroke";
    private static final String PREF_QS_STROKE_COLOR = "qs_stroke_color";
    private static final String PREF_QS_STROKE_THICKNESS = "qs_stroke_thickness";
    private static final String PREF_QS_CORNER_RADIUS = "qs_corner_radius";

    private static final int QS_TYPE_PANEL  = 0;
    private static final int QS_TYPE_BAR    = 1;
    private static final int QS_TYPE_HIDDEN = 2;
    static final int DEFAULT_QS_STROKE_COLOR = 0xFF80CBC4;

    private ListPreference mQSType;
    private ListPreference mQuickPulldown;
    private ListPreference mSmartPulldown;
    private ListPreference mNumColumns;
    private SwitchPreference mBlockOnSecureKeyguard;
    private ListPreference mTileAnimationStyle;
    private ListPreference mTileAnimationDuration;
    private ListPreference mTileAnimationInterpolator;
    private ListPreference mQSStroke;
    private ColorPickerPreference mQSStrokeColor;
    private SeekBarPreference mQSStrokeThickness;
    private SeekBarPreference mQSCornerRadius;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.quick_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        final int qsType = Settings.System.getInt(mResolver,
               Settings.System.QS_TYPE, QS_TYPE_PANEL);

        mQSType = (ListPreference) findPreference(PREF_QS_TYPE);
        mQSType.setValue(String.valueOf(qsType));
        mQSType.setSummary(mQSType.getEntry());
        mQSType.setOnPreferenceChangeListener(this);

        if (qsType == QS_TYPE_PANEL) {
            mNumColumns = (ListPreference) findPreference("sysui_qs_num_columns");
            int numColumns = Settings.System.getIntForUser(mResolver,
                    Settings.System.QS_NUM_TILE_COLUMNS, getDefaultNumColums(),
                    UserHandle.USER_CURRENT);
            mNumColumns.setValue(String.valueOf(numColumns));
            updateNumColumnsSummary(numColumns);
            mNumColumns.setOnPreferenceChangeListener(this);
            DraggableGridView.setColumnCount(numColumns);

            mTileAnimationStyle = (ListPreference) findPreference(PREF_TILE_ANIM_STYLE);
            int tileAnimationStyle = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.ANIM_TILE_STYLE, 0,
                    UserHandle.USER_CURRENT);
            mTileAnimationStyle.setValue(String.valueOf(tileAnimationStyle));
            updateTileAnimationStyleSummary(tileAnimationStyle);
            updateAnimTileDuration(tileAnimationStyle);
            mTileAnimationStyle.setOnPreferenceChangeListener(this);

            mTileAnimationDuration = (ListPreference) findPreference(PREF_TILE_ANIM_DURATION);
            int tileAnimationDuration = Settings.System.getIntForUser(getContentResolver(),
                    Settings.System.ANIM_TILE_DURATION, 2000,
                    UserHandle.USER_CURRENT);
            mTileAnimationDuration.setValue(String.valueOf(tileAnimationDuration));
            updateTileAnimationDurationSummary(tileAnimationDuration);
            mTileAnimationDuration.setOnPreferenceChangeListener(this);

            mTileAnimationInterpolator = (ListPreference) findPreference(PREF_TILE_ANIM_INTERPOLATOR);
                int tileAnimationInterpolator = Settings.System.getIntForUser(getContentResolver(),
                        Settings.System.ANIM_TILE_INTERPOLATOR, 0,
                        UserHandle.USER_CURRENT);
            mTileAnimationInterpolator.setValue(String.valueOf(tileAnimationInterpolator));
            updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
            mTileAnimationInterpolator.setOnPreferenceChangeListener(this);

        } else {
            removePreference("sysui_qs_num_columns");
            removePreference("qs_tile_animation_style");
            removePreference("qs_tile_animation_duration");
        }

        if (qsType == QS_TYPE_BAR || qsType == QS_TYPE_HIDDEN) {
            removePreference("qs_panel_tiles");
            removePreference("sysui_qs_num_columns");
            removePreference("sysui_qs_main_tiles");
            removePreference("qs_location_advanced");
            removePreference("quick_settings_collapse_panel");
            removePreference("qs_wifi_detail");
            removePreference("quick_settings_vibrate");
            removePreference("qs_tile_animation_style");
            removePreference("qs_tile_animation_duration");
            removePreference("qs_tile_animation_interpolator");
        }

        if (qsType == QS_TYPE_PANEL || qsType == QS_TYPE_HIDDEN) {
            removePreference("qs_bar_buttons");
        }

        // Volume dialog stroke
        mQSStroke =
                (ListPreference) findPreference(PREF_QS_STROKE);
        int qSStroke = Settings.System.getIntForUser(mResolver,
                        Settings.System.QS_STROKE, 1,
                        UserHandle.USER_CURRENT);
        mQSStroke.setValue(String.valueOf(qSStroke));
        mQSStroke.setSummary(mQSStroke.getEntry());
        mQSStroke.setOnPreferenceChangeListener(this);

        // Volume dialog stroke color
        mQSStrokeColor =
                (ColorPickerPreference) findPreference(PREF_QS_STROKE_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.VOLUME_DIALOG_STROKE_COLOR, DEFAULT_QS_STROKE_COLOR); 
        mQSStrokeColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQSStrokeColor.setSummary(hexColor);
        mQSStrokeColor.setOnPreferenceChangeListener(this);

        // Volume dialog stroke thickness
        mQSStrokeThickness =
                (SeekBarPreference) findPreference(PREF_QS_STROKE_THICKNESS);
        int qsStrokeThickness = Settings.System.getInt(mResolver,
                Settings.System.QS_STROKE_THICKNESS, 4);
        mQSStrokeThickness.setValue(qsStrokeThickness / 1);
        mQSStrokeThickness.setOnPreferenceChangeListener(this);

        // Volume dialog corner radius
        mQSCornerRadius =
                (SeekBarPreference) findPreference(PREF_QS_CORNER_RADIUS);
        int qSCornerRadius = Settings.System.getInt(mResolver,
                Settings.System.QS_CORNER_RADIUS, 2);
        mQSCornerRadius.setValue(qSCornerRadius / 1);
        mQSCornerRadius.setOnPreferenceChangeListener(this);

        QSSettingsDisabler(qSStroke);

        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getIntForUser(mResolver,
                Settings.System.QS_QUICK_PULLDOWN, 1, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);

        // Smart Pulldown
        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this);
        int smartPulldown = Settings.System.getInt(mResolver,
                Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);

        final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
        mBlockOnSecureKeyguard = (SwitchPreference) findPreference(PREF_BLOCK_ON_SECURE_KEYGUARD);
        if (lockPatternUtils.isSecure(UserHandle.myUserId())) {
            mBlockOnSecureKeyguard.setChecked(Settings.Secure.getInt(mResolver,
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD, 1) == 1);
            mBlockOnSecureKeyguard.setOnPreferenceChangeListener(this);
        } else {
            removePreference("block_on_secure_keyguard");
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }*/

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;
        if (preference == mQSType) {
            int intValue = Integer.valueOf((String) newValue);
            int index = mQSType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.QS_TYPE, intValue);
            preference.setSummary(mQSType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mNumColumns) {
            int numColumns = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_NUM_TILE_COLUMNS,
                    numColumns, UserHandle.USER_CURRENT);
            updateNumColumnsSummary(numColumns);
            DraggableGridView.setColumnCount(numColumns);
            return true;
		} else if (preference == mQuickPulldown) {
            int quickPulldownValue = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_QUICK_PULLDOWN,
                    quickPulldownValue, UserHandle.USER_CURRENT);
            updatePulldownSummary(quickPulldownValue);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.QS_SMART_PULLDOWN,
                    smartPulldown, UserHandle.USER_CURRENT);
            updateSmartPulldownSummary(smartPulldown);
            return true;
        } else if (preference == mBlockOnSecureKeyguard) {
            Settings.Secure.putInt(mResolver,
                    Settings.Secure.STATUS_BAR_LOCKED_ON_SECURE_KEYGUARD,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mTileAnimationStyle) {
            int tileAnimationStyle = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_STYLE,
                    tileAnimationStyle, UserHandle.USER_CURRENT);
            updateTileAnimationStyleSummary(tileAnimationStyle);
            updateAnimTileDuration(tileAnimationStyle);
            return true;
        } else if (preference == mTileAnimationDuration) {
            int tileAnimationDuration = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_DURATION,
                    tileAnimationDuration, UserHandle.USER_CURRENT);
            updateTileAnimationDurationSummary(tileAnimationDuration);
            return true;
        } else if (preference == mTileAnimationInterpolator) {
            int tileAnimationInterpolator = Integer.valueOf((String) newValue);
            Settings.System.putIntForUser(getContentResolver(), Settings.System.ANIM_TILE_INTERPOLATOR,
                    tileAnimationInterpolator, UserHandle.USER_CURRENT);
            updateTileAnimationInterpolatorSummary(tileAnimationInterpolator);
            return true;
        } else if (preference == mQSStroke) {
            int qSStroke = Integer.parseInt((String) newValue);
            int index = mQSStroke.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(mResolver, Settings.System.
                    QS_STROKE, qSStroke, UserHandle.USER_CURRENT);
            mQSStroke.setSummary(mQSStroke.getEntries()[index]);
            QSSettingsDisabler(qSStroke);
            return true;
        } else if (preference == mQSStrokeColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.QS_STROKE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mQSStrokeThickness) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mQSCornerRadius) {
            int val = (Integer) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.QS_CORNER_RADIUS, val * 1);
            return true;
        }
        return false;
    }

    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_summary_left
                    : R.string.quick_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }
    }

    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else {
            String type = null;
            switch (value) {
                case 1:
                    type = res.getString(R.string.smart_pulldown_dismissable);
                    break;
                case 2:
                    type = res.getString(R.string.smart_pulldown_persistent);
                    break;
                default:
                    type = res.getString(R.string.smart_pulldown_all);
                    break;
            }
            // Remove title capitalized formatting
            type = type.toLowerCase();
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
    }

    private void updateNumColumnsSummary(int numColumns) {
        String prefix = (String) mNumColumns.getEntries()[mNumColumns.findIndexOfValue(String
                .valueOf(numColumns))];
        mNumColumns.setSummary(getResources().getString(R.string.qs_num_columns_showing, prefix));
    }

    private int getDefaultNumColums() {
        try {
            Resources res = getPackageManager()
                    .getResourcesForApplication("com.android.systemui");
            int val = res.getInteger(res.getIdentifier("quick_settings_num_columns", "integer",
                    "com.android.systemui")); // better not be larger than 5, that's as high as the
                                              // list goes atm
            return Math.max(1, val);
        } catch (Exception e) {
            return 3;
        }
    }

    private void updateTileAnimationStyleSummary(int tileAnimationStyle) {
        String prefix = (String) mTileAnimationStyle.getEntries()[mTileAnimationStyle.findIndexOfValue(String
                .valueOf(tileAnimationStyle))];
        mTileAnimationStyle.setSummary(getResources().getString(R.string.qs_set_animation_style, prefix));
    }

    private void updateTileAnimationInterpolatorSummary(int tileAnimationInterpolator) {
        String prefix = (String) mTileAnimationInterpolator.getEntries()[mTileAnimationInterpolator.findIndexOfValue(String
                .valueOf(tileAnimationInterpolator))];
        mTileAnimationInterpolator.setSummary(getResources().getString(R.string.qs_set_animation_interpolator, prefix));
    }

    private void updateTileAnimationDurationSummary(int tileAnimationDuration) {
        String prefix = (String) mTileAnimationDuration.getEntries()[mTileAnimationDuration.findIndexOfValue(String
                .valueOf(tileAnimationDuration))];
        mTileAnimationDuration.setSummary(getResources().getString(R.string.qs_set_animation_duration, prefix));
    }

    private void updateAnimTileDuration(int tileAnimationStyle) {
        if (mTileAnimationDuration != null) {
            if (tileAnimationStyle == 0) {
                mTileAnimationDuration.setSelectable(false);
                mTileAnimationInterpolator.setSelectable(false);
            } else {
                mTileAnimationDuration.setSelectable(true);
                mTileAnimationInterpolator.setSelectable(true);
            }
        }
    }

    private void QSSettingsDisabler(int qSStroke) {
        if (qSStroke == 0) {
            mQSStrokeColor.setEnabled(false);
            mQSStrokeThickness.setEnabled(false);
        } else if (qSStroke == 1) {
            mQSStrokeColor.setEnabled(false);
            mQSStrokeThickness.setEnabled(true);
        } else {
            mQSStrokeColor.setEnabled(true);
            mQSStrokeThickness.setEnabled(true);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}
