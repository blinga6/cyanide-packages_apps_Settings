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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsLogger;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.vrtoxin.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class ExpansionViewPanels extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String EXPANSION_VIEW_PANEL_ONE = "expansion_view_panel_one";
    private static final String EXPANSION_VIEW_PANEL_TWO = "expansion_view_panel_two";
    private static final String EXPANSION_VIEW_PANEL_THREE = "expansion_view_panel_three";
    private static final String EXPANSION_VIEW_PANEL_FOUR = "expansion_view_panel_four";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET  = 0;

    private ListPreference mPanelOne;
    private ListPreference mPanelTwo;
    private ListPreference mPanelThree;
    private ListPreference mPanelFour;

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

        addPreferencesFromResource(R.xml.expansion_view_panels);
        mResolver = getActivity().getContentResolver();

        mPanelOne = (ListPreference) findPreference(EXPANSION_VIEW_PANEL_ONE);
        mPanelOne.setOnPreferenceChangeListener(this);
        mPanelOne.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.EXPANSION_VIEW_PANEL_ONE, 1)));
        mPanelOne.setSummary(mPanelOne.getEntry());

        mPanelTwo = (ListPreference) findPreference(EXPANSION_VIEW_PANEL_TWO);
        mPanelTwo.setOnPreferenceChangeListener(this);
        mPanelTwo.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.EXPANSION_VIEW_PANEL_TWO, 0)));
        mPanelTwo.setSummary(mPanelTwo.getEntry());
 
        mPanelThree = (ListPreference) findPreference(EXPANSION_VIEW_PANEL_THREE);
        mPanelThree.setOnPreferenceChangeListener(this);
        mPanelThree.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.EXPANSION_VIEW_PANEL_THREE, 3)));
        mPanelThree.setSummary(mPanelThree.getEntry());

        mPanelFour = (ListPreference) findPreference(EXPANSION_VIEW_PANEL_FOUR);
        mPanelFour.setOnPreferenceChangeListener(this);
        mPanelFour.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.EXPANSION_VIEW_PANEL_FOUR, 2)));
        mPanelFour.setSummary(mPanelFour.getEntry());

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
        int index;

        if (preference == mPanelOne) {
            int val = Integer.parseInt((String) newValue);
            index = mPanelOne.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_PANEL_ONE, val);
            mPanelOne.setSummary(mPanelOne.getEntries()[index]);
            return true;
        } else if (preference == mPanelTwo) {
            int val = Integer.parseInt((String) newValue);
            index = mPanelTwo.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_PANEL_TWO, val);
            mPanelTwo.setSummary(mPanelTwo.getEntries()[index]);
            return true;
        } else if (preference == mPanelThree) {
            int val = Integer.parseInt((String) newValue);
            index = mPanelThree.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_PANEL_THREE, val);
            mPanelThree.setSummary(mPanelThree.getEntries()[index]);
            return true;
        } else if (preference == mPanelFour) {
            int val = Integer.parseInt((String) newValue);
            index = mPanelFour.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.EXPANSION_VIEW_PANEL_FOUR, val);
            mPanelFour.setSummary(mPanelFour.getEntries()[index]);
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

        ExpansionViewPanels getOwner() {
            return (ExpansionViewPanels) getTargetFragment();
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
                                    Settings.System.EXPANSION_VIEW_PANEL_ONE,0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_TWO, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_THREE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_FOUR, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.reset_vrtoxin,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_ONE,1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_TWO, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_THREE, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.EXPANSION_VIEW_PANEL_FOUR, 2);
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
