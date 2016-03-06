/* 
 * Copyright (C) 2015 CyanideL
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

package com.android.settings.vrtoxin.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class PanelShortcutFragment extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PANEL_SHORTCUTS_PRESS_TYPE =
            "panel_shortcuts_press_type";
    private static final String PANEL_SHORTCUTS_ICON_COLOR_MODE =
            "panel_shortcuts_icon_color_mode";
    private static final String PANEL_SHORTCUTS_RIPPLE_COLOR_MODE =
            "panel_shortcuts_ripple_color_mode";
    private static final String PANEL_SHORTCUTS_ICON_COLOR = 
            "panel_shortcuts_icon_color";
    private static final String PANEL_SHORTCUTS_ICON_SIZE =
            "panel_shortcuts_icon_size";
    private static final String PANEL_SHORTCUTS_RIPPLE_COLOR =
            "panel_shortcuts_ripple_color";

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int VRTOXIN_BLUE = 0xff1976D2;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mPanelShortcutsClickType;
    private ListPreference mHeaderBarIconColorMode;
    private ListPreference mPanelShortcutsIconSize;
    private ListPreference mHeaderBarRippleColorMode;
    private ColorPickerPreference mHeaderBarIconColor;
    private ColorPickerPreference mHeaderBarRippleColor;

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

        addPreferencesFromResource(R.xml.panel_shortcut_fragment);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;
        int intValue;

        mPanelShortcutsClickType =
                (ListPreference) findPreference(PANEL_SHORTCUTS_PRESS_TYPE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.PANEL_SHORTCUTS_PRESS_TYPE, 0);
        mPanelShortcutsClickType.setValue(String.valueOf(intValue));
        mPanelShortcutsClickType.setSummary(mPanelShortcutsClickType.getEntry());
        mPanelShortcutsClickType.setOnPreferenceChangeListener(this);

        mHeaderBarIconColorMode =
                (ListPreference) findPreference(PANEL_SHORTCUTS_ICON_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.PANEL_SHORTCUTS_ICON_COLOR_MODE, 0);
        mHeaderBarIconColorMode.setValue(String.valueOf(intValue));
        mHeaderBarIconColorMode.setSummary(mHeaderBarIconColorMode.getEntry());
        mHeaderBarIconColorMode.setOnPreferenceChangeListener(this);

        mPanelShortcutsIconSize =
                (ListPreference) findPreference(PANEL_SHORTCUTS_ICON_SIZE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.PANEL_SHORTCUTS_ICON_SIZE, 36);
        mPanelShortcutsIconSize.setValue(String.valueOf(intValue));
        mPanelShortcutsIconSize.setSummary(mPanelShortcutsIconSize.getEntry());
        mPanelShortcutsIconSize.setOnPreferenceChangeListener(this);

        mHeaderBarRippleColorMode =
                (ListPreference) findPreference(PANEL_SHORTCUTS_RIPPLE_COLOR_MODE);
        intValue = Settings.System.getInt(mResolver,
                Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR_MODE, 2);
        mHeaderBarRippleColorMode.setValue(String.valueOf(intValue));
        mHeaderBarRippleColorMode.setSummary(mHeaderBarRippleColorMode.getEntry());
        mHeaderBarRippleColorMode.setOnPreferenceChangeListener(this);

        mHeaderBarIconColor =
                (ColorPickerPreference) findPreference(PANEL_SHORTCUTS_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.PANEL_SHORTCUTS_ICON_COLOR,
                DEFAULT_COLOR); 
        mHeaderBarIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderBarIconColor.setSummary(hexColor);
        mHeaderBarIconColor.setOnPreferenceChangeListener(this);
        mHeaderBarIconColor.setAlphaSliderEnabled(true);

        mHeaderBarRippleColor = (ColorPickerPreference) findPreference(PANEL_SHORTCUTS_RIPPLE_COLOR);
        mHeaderBarRippleColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR, DEFAULT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderBarRippleColor.setSummary(hexColor);
        mHeaderBarRippleColor.setNewPreviewColor(intColor);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup_restore)
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

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        final ListView list = (ListView) view.findViewById(android.R.id.list);
        // our container already takes care of the padding
        if (list != null) {
            int paddingTop = list.getPaddingTop();
            int paddingBottom = list.getPaddingBottom();
            list.setPadding(0, paddingTop, 0, paddingBottom);
        }
        return view;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;

        if (preference == mPanelShortcutsClickType) {
            intValue = Integer.valueOf((String) newValue);
            index = mPanelShortcutsClickType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_PRESS_TYPE, intValue);
            preference.setSummary(mPanelShortcutsClickType.getEntries()[index]);
            return true;
        } else if (preference == mHeaderBarIconColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mHeaderBarIconColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                  Settings.System.PANEL_SHORTCUTS_ICON_COLOR_MODE, intValue);
            preference.setSummary(mHeaderBarIconColorMode.getEntries()[index]);
            return true;
        } else if (preference == mPanelShortcutsIconSize) {
            intValue = Integer.valueOf((String) newValue);
            index = mPanelShortcutsIconSize.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_ICON_SIZE, intValue);
            preference.setSummary(mPanelShortcutsIconSize.getEntries()[index]);
            return true;
        } else if (preference == mHeaderBarRippleColorMode) {
            intValue = Integer.valueOf((String) newValue);
            index = mHeaderBarRippleColorMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR_MODE, intValue);
            preference.setSummary(mHeaderBarRippleColorMode.getEntries()[index]);
            return true;
        } else if (preference == mHeaderBarIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mHeaderBarRippleColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR, intHex);
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

        PanelShortcutFragment getOwner() {
            return (PanelShortcutFragment) getTargetFragment();
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
                                    Settings.System.PANEL_SHORTCUTS_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_ICON_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_ICON_SIZE,
                                    48);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR,
                                    DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_ICON_COLOR_MODE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR_MODE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_ICON_COLOR,
                                    VRTOXIN_BLUE);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_ICON_SIZE,
                                    36);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.PANEL_SHORTCUTS_RIPPLE_COLOR,
                                    0xff00ff00);
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
