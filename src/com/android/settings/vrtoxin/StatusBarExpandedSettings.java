/* 
 * Copyright (C) 2014 DarkKat
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

public class StatusBarExpandedSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String EMPTY_SHADE_TEXT_COLOR = "status_bar_empty_shade_text_color";
    private static final String EMPTY_SHADE_FONT_STYLE = "status_bar_empty_shade_font_style";
    private static final String STATUS_BAR_EMPTY_SHADE_TEXT_SIZE = "status_bar_empty_shade_text_size";
    private static final String STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM = "status_bar_empty_shade_text_custom";

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ColorPickerPreference mEmptyShadeTextColor;
    private ListPreference mStatusBarEmptyShadeFontStyle;
    private SeekBarPreference mEmptyShadeTextSize;
    private EditTextPreference mCustomText;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_settings);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mEmptyShadeTextColor =
                (ColorPickerPreference) findPreference(EMPTY_SHADE_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_COLOR,
                WHITE); 
        mEmptyShadeTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mEmptyShadeTextColor.setSummary(hexColor);
        mEmptyShadeTextColor.setDefaultColors(WHITE, VRTOXIN_BLUE);
        mEmptyShadeTextColor.setOnPreferenceChangeListener(this);

        mStatusBarEmptyShadeFontStyle = (ListPreference) findPreference(EMPTY_SHADE_FONT_STYLE);
        mStatusBarEmptyShadeFontStyle.setOnPreferenceChangeListener(this);
        mStatusBarEmptyShadeFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_EMPTY_SHADE_FONT_STYLE, 0)));
        mStatusBarEmptyShadeFontStyle.setSummary(mStatusBarEmptyShadeFontStyle.getEntry());

        mEmptyShadeTextSize =
                (SeekBarPreference) findPreference(STATUS_BAR_EMPTY_SHADE_TEXT_SIZE);
        mEmptyShadeTextSize.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_SIZE, 14));
        mEmptyShadeTextSize.setOnPreferenceChangeListener(this);

        mCustomText = (EditTextPreference) findPreference(STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM);
        mCustomText.getEditText().setHint(getResources().getString(
                com.android.internal.R.string.default_empty_shade_text));
        mCustomText.setOnPreferenceChangeListener(this);
        updateCustomTextPreference();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(com.android.internal.R.drawable.ic_settings_backup_restore)
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

        if (preference == mEmptyShadeTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatusBarEmptyShadeFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarEmptyShadeFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_EMPTY_SHADE_FONT_STYLE, val);
            mStatusBarEmptyShadeFontStyle.setSummary(mStatusBarEmptyShadeFontStyle.getEntries()[index]);
            return true;
        } else if (preference == mCustomText) {
            String text = (String) newValue;
            Settings.System.putString(mResolver,
                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM, text);
            updateCustomTextPreference();
        } else if (preference == mEmptyShadeTextSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_SIZE, width);
            return true;
        }
        return false;
    }

    private void updateCustomTextPreference() {
        String customText = Settings.System.getString(mResolver,
                Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM);
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

        StatusBarExpandedSettings getOwner() {
            return (StatusBarExpandedSettings) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_COLOR,
                                    WHITE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_FONT_STYLE, 0);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM,
                                    "Nothing to see here");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_SIZE, 20);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_FONT_STYLE, 20);
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_CUSTOM,
                                    "VRToxin Is Fuckin Awesome!!!!");
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_EMPTY_SHADE_TEXT_SIZE, 25);
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
