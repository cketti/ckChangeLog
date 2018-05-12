/*
 * Copyright (C) 2012-2017 cketti and contributors
 * https://github.com/cketti/ckChangeLog/graphs/contributors
 *
 * Portions Copyright (C) 2012 Martin van Zuilekom (http://martin.cubeactive.com)
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
 *
 *
 * Based on android-change-log:
 *
 * Copyright (C) 2011, Karsten Priegnitz
 *
 * Permission to use, copy, modify, and distribute this piece of software
 * for any purpose with or without fee is hereby granted, provided that
 * the above copyright notice and this permission notice appear in the
 * source code of all copies.
 *
 * It would be appreciated if you mention the author in your change log,
 * contributors list or the like.
 *
 * http://code.google.com/p/android-change-log/
 */
package de.cketti.changelog;


import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Generate a full or partial (What's New) Change Log.
 */
public final class ChangeLog {
    private static final String LOG_TAG = "ckChangeLog";
    private static final String VERSION_KEY = "ckChangeLog_last_version_code";
    private static final int NO_VERSION = -1;


    private final Context context;
    private final SharedPreferences preferences;
    private final ChangeLogProvider changeLogProvider;
    private int lastVersionCode;
    private int currentVersionCode;
    private String currentVersionName;


    /**
     * Create a {@code ChangeLog} instance using the default {@link SharedPreferences} file.
     *
     * @param context
     *         Context that is used to access resources.
     */
    public static ChangeLog newInstance(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return newInstance(context, preferences);
    }

    /**
     * Create a {@code ChangeLog} instance using the supplied {@code SharedPreferences} instance.
     *
     * @param context
     *         Context that is used to access resources.
     * @param preferences
     *         {@code SharedPreferences} instance that is used to persist the last version code.
     *
     */
    public static ChangeLog newInstance(Context context, SharedPreferences preferences) {
        Resources resources = context.getResources();
        ChangeLogProvider masterChangeLogProvider = new ResourceChangeLogProvider(resources, R.raw.changelog_master);
        ChangeLogProvider localizedChangeLogProvider = new ResourceChangeLogProvider(resources, R.raw.changelog);
        ChangeLogProvider changeLogProvider = new MergedChangeLogProvider(
                masterChangeLogProvider, localizedChangeLogProvider);

        return newInstance(context, preferences, changeLogProvider);
    }

    /**
     * Create a {@code ChangeLog} instance using the supplied {@code SharedPreferences} and {@code ChangeLogProvider}
     * instances.
     *
     * @param context
     *         Context that is used to access resources.
     * @param preferences
     *         {@code SharedPreferences} instance that is used to persist the last version code.
     * @param changeLogProvider
     *         {@code ChangeLogProvider} instance that is used to retrieve the Change Log.
     */
    public static ChangeLog newInstance(Context context, SharedPreferences preferences,
            ChangeLogProvider changeLogProvider) {
        ChangeLog changeLog = new ChangeLog(context, preferences, changeLogProvider);
        changeLog.init();

        return changeLog;
    }

    private ChangeLog(Context context, SharedPreferences preferences, ChangeLogProvider changeLogProvider) {
        this.context = context;
        this.preferences = preferences;
        this.changeLogProvider = changeLogProvider;
    }

    private void init() {
        lastVersionCode = preferences.getInt(VERSION_KEY, NO_VERSION);

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            currentVersionCode = packageInfo.versionCode;
            currentVersionName = packageInfo.versionName;
        } catch (NameNotFoundException e) {
            currentVersionCode = NO_VERSION;
            Log.e(LOG_TAG, "Could not get version information from manifest!", e);
        }
    }

    /**
     * Get version code of last installation.
     *
     * @return The version code of the last installation of this app (as described in the former
     *         manifest). This will be the same as returned by {@link #getCurrentVersionCode()} the
     *         second time this version of the app is launched (more precisely: the second time
     *         {@code ChangeLog} is instantiated).
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vcode">android:versionCode</a>
     */
    public int getLastVersionCode() {
        return lastVersionCode;
    }

    /**
     * Get version code of current installation.
     *
     * @return The version code of this app as described in the manifest.
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vcode">android:versionCode</a>
     */
    public int getCurrentVersionCode() {
        return currentVersionCode;
    }

    /**
     * Get version name of current installation.
     *
     * @return The version name of this app as described in the manifest.
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vname">android:versionName</a>
     */
    public String getCurrentVersionName() {
        return currentVersionName;
    }

    /**
     * Check if this is the first execution of this app version.
     *
     * @return {@code true} if this version of your app is started the first time.
     */
    public boolean isFirstRun() {
        return lastVersionCode < currentVersionCode;
    }

    /**
     * Check if this is a new installation.
     *
     * @return {@code true} if your app including {@code ChangeLog} is started the first time ever.
     *         Also {@code true} if your app was uninstalled and installed again.
     */
    public boolean isFirstRunEver() {
        return lastVersionCode == NO_VERSION;
    }

    /**
     * Write current version code to the preferences.
     *
     * <p>
     * Future calls to {@link #isFirstRun()} and {@link #isFirstRunEver()} will return {@code false}
     * for the current app version.
     * </p>
     */
    public void writeCurrentVersion() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(VERSION_KEY, currentVersionCode);
        editor.apply();
    }

    /**
     * Returns the full Change Log.
     *
     * @return A sorted {@code List} containing {@link ReleaseItem}s representing the full Change Log.
     */
    public List<ReleaseItem> getChangeLog() {
        return changeLogProvider.getChangeLog();
    }

    /**
     * Returns the list of changes for versions newer than the last version ("What's New").
     *
     * @return A sorted {@code List} containing {@link ReleaseItem}s representing the recent changes.
     */
    public List<ReleaseItem> getRecentChanges() {
        return changeLogProvider.getChangeLogSince(lastVersionCode);
    }
}
