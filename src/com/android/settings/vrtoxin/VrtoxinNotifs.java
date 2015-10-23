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

import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.TwoStatePreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.DropDownPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.notification.NotificationAccessSettings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.List;

public class VrtoxinNotifs extends SettingsPreferenceFragment implements Indexable {
    private static final String TAG = "VRToxinNotificationSettings";
    
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_NOTIFICATION_PULSE = "notification_pulse";
    private static final String KEY_LOCK_SCREEN_NOTIFICATIONS = "lock_screen_notifications";
    private static final String KEY_NOTIFICATION_ACCESS = "manage_notification_access";
    private static final String KEY_ZEN_ACCESS = "manage_zen_access";
    private static final String KEY_ZEN_MODE = "zen_mode";

    private static final String[] RESTRICTED_KEYS = {
        KEY_ZEN_ACCESS,
        KEY_ZEN_MODE,
    };

    private final H mHandler = new H();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    private Context mContext;
    private boolean mSecure;
    private int mLockscreenSelectedValue;
    private PackageManager mPM;
    private TwoStatePreference mNotificationPulse;
    private DropDownPreference mLockscreen;
    private Preference mNotificationAccess;
    private Preference mZenAccess;

    private UserManager mUserManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mPM = mContext.getPackageManager();
        mUserManager = UserManager.get(getContext());

        addPreferencesFromResource(R.xml.vrtoxin_notifs);

        final PreferenceCategory notification = (PreferenceCategory)
                findPreference(KEY_NOTIFICATION);
        initPulse(notification);
        initLockscreenNotifications(notification);

        mNotificationAccess = findPreference(KEY_NOTIFICATION_ACCESS);
        refreshNotificationListeners();
        mZenAccess = findPreference(KEY_ZEN_ACCESS);
        refreshZenAccess();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotificationListeners();
        refreshZenAccess();
        mSettingsObserver.register(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingsObserver.register(false);
    }

    // === Pulse notification light ===

    private void initPulse(PreferenceCategory parent) {
        mNotificationPulse = (TwoStatePreference) parent.findPreference(KEY_NOTIFICATION_PULSE);
        if (mNotificationPulse == null) {
            Log.i(TAG, "Preference not found: " + KEY_NOTIFICATION_PULSE);
            return;
        }
        if (!getResources()
                .getBoolean(com.android.internal.R.bool.config_intrusiveNotificationLed)) {
            parent.removePreference(mNotificationPulse);
        } else {
            updatePulse();
            mNotificationPulse.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final boolean val = (Boolean)newValue;
                    return Settings.System.putInt(getContentResolver(),
                            Settings.System.NOTIFICATION_LIGHT_PULSE,
                            val ? 1 : 0);
                }
            });
        }
    }

    private void updatePulse() {
        if (mNotificationPulse == null) {
            return;
        }
        try {
            mNotificationPulse.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.NOTIFICATION_LIGHT_PULSE) == 1);
        } catch (Settings.SettingNotFoundException snfe) {
            Log.e(TAG, Settings.System.NOTIFICATION_LIGHT_PULSE + " not found");
        }
    }

    // === Lockscreen (public / private) notifications ===

    private void initLockscreenNotifications(PreferenceCategory parent) {
        mLockscreen = (DropDownPreference) parent.findPreference(KEY_LOCK_SCREEN_NOTIFICATIONS);
        if (mLockscreen == null) {
            Log.i(TAG, "Preference not found: " + KEY_LOCK_SCREEN_NOTIFICATIONS);
            return;
        }

        boolean isSecureNotificationsDisabled = isSecureNotificationsDisabled();
        boolean isUnredactedNotificationsDisabled = isUnredactedNotificationsDisabled();
        if (!isSecureNotificationsDisabled && !isUnredactedNotificationsDisabled) {
            mLockscreen.addItem(R.string.lock_screen_notifications_summary_show,
                    R.string.lock_screen_notifications_summary_show);
        }
        if (mSecure && !isSecureNotificationsDisabled) {
            mLockscreen.addItem(R.string.lock_screen_notifications_summary_hide,
                    R.string.lock_screen_notifications_summary_hide);
        }
        mLockscreen.addItem(R.string.lock_screen_notifications_summary_disable,
                R.string.lock_screen_notifications_summary_disable);
        updateLockscreenNotifications();
        if (mLockscreen.getItemCount() > 1) {
            mLockscreen.setCallback(new DropDownPreference.Callback() {
                @Override
                public boolean onItemSelected(int pos, Object value) {
                    final int val = (Integer) value;
                    if (val == mLockscreenSelectedValue) {
                        return true;
                    }
                    final boolean enabled =
                            val != R.string.lock_screen_notifications_summary_disable;
                    final boolean show = val == R.string.lock_screen_notifications_summary_show;
                    Settings.Secure.putInt(getContentResolver(),
                            Settings.Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS, show ? 1 : 0);
                    Settings.Secure.putInt(getContentResolver(),
                            Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, enabled ? 1 : 0);
                    mLockscreenSelectedValue = val;
                    return true;
                }
            });
        } else {
            // There is one or less option for the user, disable the drop down.
            mLockscreen.setEnabled(false);
        }
    }

    private boolean isSecureNotificationsDisabled() {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && (dpm.getKeyguardDisabledFeatures(null)
                & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_NOTIFICATIONS) != 0;
    }

    private boolean isUnredactedNotificationsDisabled() {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && (dpm.getKeyguardDisabledFeatures(null)
                & DevicePolicyManager.KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS) != 0;
    }

    private void updateLockscreenNotifications() {
        if (mLockscreen == null) {
            return;
        }
        final boolean enabled = getLockscreenNotificationsEnabled();
        final boolean allowPrivate = !mSecure || getLockscreenAllowPrivateNotifications();
        mLockscreenSelectedValue = !enabled ? R.string.lock_screen_notifications_summary_disable :
                allowPrivate ? R.string.lock_screen_notifications_summary_show :
                R.string.lock_screen_notifications_summary_hide;
        mLockscreen.setSelectedValue(mLockscreenSelectedValue);
    }

    private boolean getLockscreenNotificationsEnabled() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, 0) != 0;
    }

    private boolean getLockscreenAllowPrivateNotifications() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS, 0) != 0;
    }

    // === Notification listeners ===

    private void refreshNotificationListeners() {
        if (mNotificationAccess != null) {
            final int n = NotificationAccessSettings.getEnabledListenersCount(mContext);
            if (n == 0) {
                mNotificationAccess.setSummary(getResources().getString(
                        R.string.manage_notification_access_summary_zero));
            } else {
                mNotificationAccess.setSummary(String.format(getResources().getQuantityString(
                        R.plurals.manage_notification_access_summary_nonzero,
                        n, n)));
            }
        }
    }

    // === Zen access ===

    private void refreshZenAccess() {
        // noop for now
    }

    // === Callbacks ===

    private final class SettingsObserver extends ContentObserver {
        private final Uri NOTIFICATION_LIGHT_PULSE_URI =
                Settings.System.getUriFor(Settings.System.NOTIFICATION_LIGHT_PULSE);
        private final Uri LOCK_SCREEN_PRIVATE_URI =
                Settings.Secure.getUriFor(Settings.Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS);
        private final Uri LOCK_SCREEN_SHOW_URI =
                Settings.Secure.getUriFor(Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS);

        public SettingsObserver() {
            super(mHandler);
        }

        public void register(boolean register) {
            final ContentResolver cr = getContentResolver();
            if (register) {
                cr.registerContentObserver(NOTIFICATION_LIGHT_PULSE_URI, false, this);
                cr.registerContentObserver(LOCK_SCREEN_PRIVATE_URI, false, this);
                cr.registerContentObserver(LOCK_SCREEN_SHOW_URI, false, this);
            } else {
                cr.unregisterContentObserver(this);
            }
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (NOTIFICATION_LIGHT_PULSE_URI.equals(uri)) {
                updatePulse();
            }
            if (LOCK_SCREEN_PRIVATE_URI.equals(uri) || LOCK_SCREEN_SHOW_URI.equals(uri)) {
                updateLockscreenNotifications();
            }
        }
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        new BaseSearchIndexProvider() {
            @Override
            public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                        boolean enabled) {
                ArrayList<SearchIndexableResource> result =
                        new ArrayList<SearchIndexableResource>();

                SearchIndexableResource sir = new SearchIndexableResource(context);
                sir.xmlResId = R.xml.vrtoxin_notifs;
                result.add(sir);

                return result;
            }

            @Override
            public List<String> getNonIndexableKeys(Context context) {
                ArrayList<String> result = new ArrayList<String>();
                return result;
            }
        };

    private final class H extends Handler {

        private H() {
            super(Looper.getMainLooper());
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.VRTOXIN_SHIT;
    }
}
