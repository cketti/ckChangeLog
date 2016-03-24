/*
 * Copyright (C) 2012-2015 cketti and contributors
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
package de.cketti.library.changelog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;


/**
 * Generate a full or partial (What's New) change log.
 */
@SuppressWarnings("UnusedDeclaration")
public final class ChangeLog {
    private static final String LOG_TAG = "ckChangeLog";
    private static final String VERSION_KEY = "ckChangeLog_last_version_code";
    private static final int NO_VERSION = -1;


    private final Context context;
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
        ChangeLog changeLog = new ChangeLog(context);
        changeLog.init(preferences);

        return changeLog;
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
        ChangeLog changeLog = new ChangeLog(context);
        changeLog.init(preferences);

        return changeLog;
    }

    private ChangeLog(Context context) {
        this.context = context;
    }

    private void init(SharedPreferences preferences) {
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(VERSION_KEY, currentVersionCode);

        // TODO: Update preferences from a background thread
        editor.commit();
    }

    /**
     * Returns the merged change log.
     *
     * @param full
     *         If this is {@code true} the full change log is returned. Otherwise only changes for
     *         versions newer than the last version are returned.
     *
     * @return A sorted {@code List} containing {@link ReleaseItem}s representing the (partial)
     *         change log.
     *
     * @see #getChangeLogComparator()
     */
    public List<ReleaseItem> getChangeLog(boolean full) {
        SparseArray<ReleaseItem> masterChangelog = getMasterChangeLog(full);
        SparseArray<ReleaseItem> changelog = getLocalizedChangeLog(full);

        List<ReleaseItem> mergedChangeLog = new ArrayList<ReleaseItem>(masterChangelog.size());

        for (int i = 0, len = masterChangelog.size(); i < len; i++) {
            int key = masterChangelog.keyAt(i);

            // Use release information from localized change log and fall back to the master file
            // if necessary.
            ReleaseItem release = changelog.get(key, masterChangelog.get(key));

            mergedChangeLog.add(release);
        }

        Collections.sort(mergedChangeLog, getChangeLogComparator());

        return mergedChangeLog;
    }

    private SparseArray<ReleaseItem> getMasterChangeLog(boolean full) {
        return readChangeLogFromResource(R.xml.changelog_master, full);
    }

    private SparseArray<ReleaseItem> getLocalizedChangeLog(boolean full) {
        return readChangeLogFromResource(R.xml.changelog, full);
    }

    private SparseArray<ReleaseItem> readChangeLogFromResource(int resId, boolean full) {
        XmlResourceParser xml = context.getResources().getXml(resId);
        try {
            return readChangeLog(xml, full);
        } finally {
            xml.close();
        }
    }

    private SparseArray<ReleaseItem> readChangeLog(XmlPullParser xml, boolean full) {
        SparseArray<ReleaseItem> result = new SparseArray<ReleaseItem>();

        try {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xml.getName().equals(ReleaseTag.NAME)) {
                    if (parseReleaseTag(xml, full, result)) {
                        // Stop reading more elements if this entry is not newer than the last
                        // version.
                        break;
                    }
                }
                eventType = xml.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return result;
    }

    private boolean parseReleaseTag(XmlPullParser xml, boolean full,
            SparseArray<ReleaseItem> changelog) throws XmlPullParserException, IOException {

        String version = xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION);

        int versionCode;
        try {
            String versionCodeStr = xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION_CODE);
            versionCode = Integer.parseInt(versionCodeStr);
        } catch (NumberFormatException e) {
            versionCode = NO_VERSION;
        }

        if (!full && versionCode <= lastVersionCode) {
            return true;
        }

        int eventType = xml.getEventType();
        List<String> changes = new ArrayList<String>();
        while (eventType != XmlPullParser.END_TAG || xml.getName().equals(ChangeTag.NAME)) {
            if (eventType == XmlPullParser.START_TAG && xml.getName().equals(ChangeTag.NAME)) {
                eventType = xml.next();
                String text = cleanText(xml.getText());
                changes.add(text);
            }
            eventType = xml.next();
        }

        ReleaseItem release = new ReleaseItem(versionCode, version, changes);
        changelog.put(versionCode, release);

        return false;
    }

    private String cleanText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    private Comparator<ReleaseItem> getChangeLogComparator() {
        return new Comparator<ReleaseItem>() {
            @Override
            public int compare(ReleaseItem lhs, ReleaseItem rhs) {
                if (lhs.versionCode < rhs.versionCode) {
                    return 1;
                } else if (lhs.versionCode > rhs.versionCode) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }


    private interface ChangeLogTag {
        String NAME = "changelog";
    }

    private interface ReleaseTag {
        String NAME = "release";
        String ATTRIBUTE_VERSION = "version";
        String ATTRIBUTE_VERSION_CODE = "versioncode";
    }

    private interface ChangeTag {
        String NAME = "change";
    }
}
