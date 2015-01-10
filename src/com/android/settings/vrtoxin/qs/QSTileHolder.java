/*
 * Copyright (C) 2015 The CyanogenMod Project
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
package com.android.settings.vrtoxin.qs;

import android.content.Context;

import com.android.internal.util.vrtoxin.QSConstants;
import com.android.internal.util.vrtoxin.QSUtils;
import com.android.settings.R;

/**
 * This class holds the icon, the name - or the string the user sees,
 * and the value which will be stored
 */
public class QSTileHolder {
    public static final String TILE_ADD_DELETE = "";

    public final String resourceName;
    public final String value;
    public final String name;

    public QSTileHolder(String resourceName, String value, String name) {
        this.resourceName = resourceName;
        this.value = value;
        this.name = name;
    }

    public static QSTileHolder from(Context context, String tileType) {
        String resourceName = null;
        int stringId = -1;

        if (!TILE_ADD_DELETE.equals(tileType) &&
                !QSUtils.getAvailableTiles(context).contains(tileType)) {
            return null;
        }

        switch (tileType) {
            case TILE_ADD_DELETE:
                break;
            case QSConstants.TILE_AMBIENT_DISPLAY:
                resourceName = "ic_qs_ambientdisplay_on";
                stringId = R.string.qs_tile_ambient_display;
                break;
            case QSConstants.TILE_AIRPLANE:
                resourceName = "ic_signal_airplane_enable_animation";
                stringId = R.string.airplane_mode;
                break;
            case QSConstants.TILE_BLUETOOTH:
                resourceName = "ic_qs_bluetooth_on";
                stringId = R.string.bluetooth_settings_title;
                break;
            case QSConstants.TILE_BRIGHTNESS:
                resourceName = "ic_qs_brightness_auto_on_alpha";
                stringId = R.string.qs_tile_brightness;
                break;
            case QSConstants.TILE_CAST:
                resourceName = "ic_qs_cast_on";
                stringId = R.string.cast_screen;
                break;
            case QSConstants.TILE_CELLULAR:
                resourceName = "ic_qs_signal_full_4";
                stringId = R.string.cellular_data_title;
                break;
            case QSConstants.TILE_INVERSION:
                resourceName = "ic_invert_colors_enable_animation";
                stringId = R.string.accessibility_display_inversion_preference_title;
                break;
            case QSConstants.TILE_EXPANDED_DESKTOP:
                resourceName = "ic_qs_expanded_desktop";
                stringId = R.string.qs_tile_expanded_desktop;
                break;
            case QSConstants.TILE_FLASHLIGHT:
                resourceName = "ic_signal_flashlight_enable_animation";
                stringId = R.string.power_flashlight;
                break;
            case QSConstants.TILE_HOTSPOT:
                resourceName = "ic_hotspot_enable_animation";
                stringId = R.string.hotspot;
                break;
            case QSConstants.TILE_LOCATION:
                resourceName = "ic_signal_location_enable_animation";
                stringId = R.string.location_title;
                break;
            case QSConstants.TILE_NFC:
                resourceName = "ic_qs_nfc_on";
                stringId = R.string.qs_tile_nfc;
                break;
            case QSConstants.TILE_ROTATION:
                resourceName = "ic_portrait_to_auto_rotate_animation";
                stringId = R.string.qs_tile_rotation;
                break;
            case QSConstants.TILE_SCREENOFF:
                resourceName = "ic_qs_power";
                stringId = R.string.qs_tile_screen_off;
                break;
            case QSConstants.TILE_SCREENSHOT:
                resourceName = "ic_qs_screenshot";
                stringId = R.string.qs_tile_screenshot;
                break;
            case QSConstants.TILE_SCREEN_TIMEOUT:
                resourceName = "ic_qs_screen_timeout_vector";
                stringId = R.string.qs_tile_screen_timeout;
                break;
            case QSConstants.TILE_USB_TETHERING:
                resourceName = "ic_qs_usb_tether_on";
                stringId = R.string.qs_tile_usb_tethering;
                break;
            case QSConstants.TILE_VOLUME:
                resourceName = "ic_qs_volume_panel";
                stringId = R.string.qs_tile_volume_panel;
                break;
            case QSConstants.TILE_VRTOXIN:
                resourceName = "ic_qs_cyanide_on";
                stringId = R.string.qs_tile_vrtoxin;
                break;
            case QSConstants.TILE_WIFI:
                resourceName = "ic_qs_wifi_full_4";
                stringId = R.string.wifi_quick_toggle_title;
                break;
            default:
                return null;
        }

        String name = stringId != -1 ? context.getString(stringId) : null;
        return new QSTileHolder(resourceName, tileType, name);
    }
}