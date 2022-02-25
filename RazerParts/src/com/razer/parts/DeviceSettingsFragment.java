/*
 * Copyright (c) 2021, Alcatraz323 <alcatraz32323@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.razer.parts;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import static android.provider.Settings.System.MIN_REFRESH_RATE;
import static android.provider.Settings.System.PEAK_REFRESH_RATE;
import static android.provider.Settings.System.PREFERRED_REFRESH_RATE;

import static com.razer.parts.Constants.*;
import com.razer.parts.ShellUtils;
import com.razer.parts.ShellUtils.CommandResult;
import com.razer.parts.SharedPreferenceUtil;
import com.razer.parts.ChromaManager;
import com.razer.parts.R;

public class DeviceSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private ListPreference mResolutionPref;
    private ListPreference mRefreshRatePref;

    private Preference mChromaPref;
    private Preference mDolbyAtmosPref;
    private Preference mActiveWakeupPref;

    public String TAG = "RazerParts";

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.device_settings);
        findPreferences();
        bindListeners();
        updateSummary();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case SCREEN_RESOLUTION:
                String resolution = (String) o;
                if(o.equals("1440")) {
                    ShellUtils.execCommand("wm density 480", false);
                    ShellUtils.execCommand("wm size 1440x2560", false);
                } else {
                    ShellUtils.execCommand("wm density 360", false);
                    ShellUtils.execCommand("wm size 1080x1920", false);
                }
                break;
            case SCREEN_REFRESH_RATE:
                int parseInt = Integer.parseInt((String) o);
                Log.e(TAG, "To set rate: "+parseInt);
                IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
                if (windowManagerService == null) {
                    Log.e(TAG, "fps interface failure");
                    return false;
                }
                try {
                    windowManagerService.setDisplayRefreshRate(0, parseInt);
                } catch(RemoteException e) {
                    Log.e(TAG, "Error settings refresh rate", e);
                }
                Settings.System.putInt(getContext().getContentResolver(), MIN_REFRESH_RATE, parseInt);
	            Settings.System.putInt(getContext().getContentResolver(), PEAK_REFRESH_RATE, parseInt);
                Settings.System.putFloat(getContext().getContentResolver(), PREFERRED_REFRESH_RATE,
                Float.valueOf((String) o));
                break;
        }
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                    spfu.put(getContext(), preference.getKey(), (String) o);
        updateSummary();
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case CHROMA:
                preference.getContext().startActivity(new Intent(getActivity(), ChromaActivity.class));
                break;
            case DOLBY_ATMOS:

                break;
            case ACTIVE_WAKE:
                preference.getContext().startActivity(new Intent()
                    .setClassName("org.lineageos.settings",
                     "org.lineageos.settings.doze.DozeSettingsActivity"));
                break;
        }
        return true;
    }

    private void findPreferences() {
        mResolutionPref = findPreference(SCREEN_RESOLUTION);
        mRefreshRatePref = findPreference(SCREEN_REFRESH_RATE);
        mChromaPref = findPreference(CHROMA);
        mDolbyAtmosPref = findPreference(DOLBY_ATMOS);
        mActiveWakeupPref = findPreference(ACTIVE_WAKE);
    }

    private void bindListeners() {
        mResolutionPref.setOnPreferenceChangeListener(this);
        mRefreshRatePref.setOnPreferenceChangeListener(this);
        mChromaPref.setOnPreferenceClickListener(this);
        mDolbyAtmosPref.setOnPreferenceClickListener(this);
        mActiveWakeupPref.setOnPreferenceClickListener(this);
    }

    private void updateSummary() {
        updateResolutionSummary();
        updateRefreshRateSummary();
    }

    private void updateResolutionSummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String resolution = (String) spfu.get(getContext(), SCREEN_RESOLUTION,
                "1440");
        String[] entryvalue = getContext().getResources().getStringArray(R.array.resolution_values);
        String[] entry = getContext().getResources().getStringArray(R.array.resolution_entries);
        for (int i = 0; i < entryvalue.length; i++) {
            if (entryvalue[i].equals(resolution)) {
                mResolutionPref.setSummary(entry[i]);
            }
        }
    }

    private void updateRefreshRateSummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String refreshRate = (String) spfu.get(getContext(), SCREEN_REFRESH_RATE,
                "120");
        String[] entryvalue = getContext().getResources().getStringArray(R.array.refresh_rate_values);
        String[] entry = getContext().getResources().getStringArray(R.array.refresh_rate_entries);
        for (int i = 0; i < entryvalue.length; i++) {
            if (entryvalue[i].equals(refreshRate)) {
                mRefreshRatePref.setSummary(entry[i]);
            }
        }
    }
}
