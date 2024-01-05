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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;

import static com.razer.parts.Constants.*;
import com.razer.parts.ShellUtils;
import com.razer.parts.ShellUtils.CommandResult;
import com.razer.parts.SharedPreferenceUtil;
import com.razer.parts.ChromaManager;
import com.razer.parts.R;

public class ChromaFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private SwitchPreference mEnabledSwitch;

    private ListPreference mModePref;
    private EditTextPreference mColorPref;
    private EditTextPreference mBrightnessPref;

    private ChromaManager mManager;

    @Override
    public void onCreatePreferences(Bundle bundle, String key) {
        addPreferencesFromResource(R.xml.chroma_settings);
        findPreferences();
        bindListeners();
        updateSummary();
        mManager = new ChromaManager();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if(preference.getKey().equals(CHROMA_SWITCH)) {
            boolean enabled = (boolean) o;
            if(enabled) {
                mManager.systemReady();
                mManager.loadMcuWithParams(preference.getContext());
            } else {
                mManager.suspendThs();
            }
            return true;
        }
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
                    spfu.put(getContext(), preference.getKey(), (String) o);
        updateSummary();
        mManager.loadMcuWithParams(preference.getContext());
        return true;
    }

    private void findPreferences() {
        mEnabledSwitch = findPreference(CHROMA_SWITCH);
        mModePref = findPreference(CHROMA_MODE);
        mColorPref = findPreference(CHROMA_COLOR);
        mBrightnessPref = findPreference(CHROMA_BRIGHTNESS);
    }

    private void bindListeners() {
        mEnabledSwitch.setOnPreferenceChangeListener(this);
        mModePref.setOnPreferenceChangeListener(this);
        mColorPref.setOnPreferenceChangeListener(this);
        mBrightnessPref.setOnPreferenceChangeListener(this);
    }

    private void updateSummary() {
        updateModeSummary();
        updateEditTextSummary();
    }

    private void updateModeSummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String mode = (String) spfu.get(getContext(), CHROMA_MODE,
                "color");
        String[] entryvalue = getContext().getResources().getStringArray(R.array.chroma_mode_values);
        String[] entry = getContext().getResources().getStringArray(R.array.chroma_mode_entries);
        for (int i = 0; i < entryvalue.length; i++) {
            if (entryvalue[i].equals(mode)) {
                mModePref.setSummary(entry[i]);
            }
        }
    }

    private void updateEditTextSummary() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        String color = (String) spfu.get(getContext(), CHROMA_COLOR,
                "#00FF00");
        String brightness = (String) spfu.get(getContext(), CHROMA_BRIGHTNESS,
                "200");
        mColorPref.setSummary(color);
        mBrightnessPref.setSummary(brightness);
    }
}
