/*
 * Copyright (C) 2015-2016 The VRToxin Project
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

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.text.Html;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.util.vrtoxin.ScreenType;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.vrtoxin.PagerSlidingTabStrip;
import com.android.settings.vrtoxin.AndroidRecentsSettings;
import com.android.settings.vrtoxin.DashboardOptions;
import com.android.settings.vrtoxin.FloatingWindows;
import com.android.settings.vrtoxin.HwKeySettings;
import com.android.settings.vrtoxin.InterfaceSettings;
import com.android.settings.vrtoxin.LockS;
import com.android.settings.vrtoxin.MasterAnimationControl;
import com.android.settings.vrtoxin.PowerMenuSettings;
import com.android.settings.vrtoxin.QuickSettings;
import com.android.settings.vrtoxin.SlimSizer;
import com.android.settings.vrtoxin.StatusBarSettings;
import com.android.settings.vrtoxin.VrtoxinNotifs;
import com.android.settings.vrtoxin.WakelockBlocker;
import com.android.settings.vrtoxin.WeatherControl;
import com.android.settings.notification.OtherSoundSettings;

import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;

import java.util.List;

public class MainSettings extends SettingsPreferenceFragment {

    ViewPager mViewPager;
    String titleString[];
    ViewGroup mContainer;
    PagerSlidingTabStrip mTabs;
    ContentResolver mResolver;

    static Bundle mSavedState;

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        mResolver = getActivity().getContentResolver();
        final ActionBar actionBar = getActivity().getActionBar();
        int color = Settings.System.getInt(mResolver,
                Settings.System.SETTINGS_ICON_COLOR, 0xFFFFFFFF);
        Drawable d = getResources().getDrawable(R.drawable.ic_settings_vrtoxin).mutate();
        d.setColorFilter(color, Mode.SRC_IN);
        actionBar.setIcon(d);

        View view = inflater.inflate(R.layout.preference_vrtoxin_shit, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        StatusBarAdapter StatusBarAdapter = new StatusBarAdapter(getFragmentManager());
        mViewPager.setAdapter(StatusBarAdapter);
        mTabs.setViewPager(mViewPager);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     }

     @Override
     public void onResume() {
         super.onResume();


        if (!ScreenType.isTablet(getActivity())) {
            mContainer.setPadding(30, 30, 30, 30);
        }
    }

    class StatusBarAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public StatusBarAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new OtherSoundSettings();
            frags[1] = new MasterAnimationControl();
            frags[2] = new PowerUsageSummary();
            frags[3] = new HwKeySettings();
            frags[4] = new DashboardOptions();
            frags[5] = new FloatingWindows();
            frags[6] = new InterfaceSettings();
            frags[7] = new LockS();
            frags[8] = new VrtoxinNotifs();
            frags[9] = new PowerMenuSettings();
            frags[10] = new QuickSettings();
            frags[11] = new AndroidRecentsSettings();
            frags[12] = new StatusBarSettings();
            frags[13] = new SlimSizer();
            frags[14] = new WakelockBlocker();
            frags[15] = new WeatherControl();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    private String[] getTitles() {
        String titleString[];
        if (!ScreenType.isPhone(getActivity())) {
        titleString = new String[]{
                    getString(R.string.advanced_sound_title),
                    getString(R.string.vrtoxin_animations_settings),
                    getString(R.string.power_usage_summary_title),
                    getString(R.string.buttons_settings_title),
                    getString(R.string.settings_colors_title),
                    getString(R.string.floating_windows),
                    getString(R.string.interface_settings_title),
                    getString(R.string.lockscreen_settings),
                    getString(R.string.vrtoxin_notifications_title),
                    getString(R.string.power_menu_settings_title),
                    getString(R.string.quick_settings_title),
                    getString(R.string.recents_panel),
                    getString(R.string.status_bar_title),
                    getString(R.string.sizer_title),
                    getString(R.string.wakelock_blocker_title),
                    getString(R.string.weather_control_master_title)};
        } else {
        titleString = new String[]{
                    getString(R.string.advanced_sound_title),
                    getString(R.string.vrtoxin_animations_settings),
                    getString(R.string.power_usage_summary_title),
                    getString(R.string.buttons_settings_title),
                    getString(R.string.settings_colors_title),
                    getString(R.string.floating_windows),
                    getString(R.string.interface_settings_title),
                    getString(R.string.lockscreen_settings),
                    getString(R.string.vrtoxin_notifications_title),
                    getString(R.string.power_menu_settings_title),
                    getString(R.string.quick_settings_title),
                    getString(R.string.recents_panel),
                    getString(R.string.status_bar_title),
                    getString(R.string.sizer_title),
                    getString(R.string.wakelock_blocker_title),
                    getString(R.string.weather_control_master_title)};
        }
        return titleString;
    }
}
