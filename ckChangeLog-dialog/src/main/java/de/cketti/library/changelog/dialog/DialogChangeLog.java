/*
 * Copyright (C) 2012-2017 cketti and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cketti.library.changelog.dialog;


import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.webkit.WebView;

import de.cketti.library.changelog.ChangeLog;
import de.cketti.library.changelog.ReleaseItem;


/**
 * Display a dialog showing a full or partial (What's New) Change Log.
 *
 * <p>
 * You can display a Change Log after app updates by putting the following code in your Activity's {@code onCreate()}
 * method:
 * </p>
 * <pre>{@code
 * DialogChangeLog changeLog = DialogChangeLog.newInstance(this);
 * if (changeLog.isFirstRun()) {
 *     changeLog.getLogDialog().show();
 * }
 * }</pre>
 */
public final class DialogChangeLog {
    /**
     * Default CSS styles used to format the Change Log.
     */
    public static final String DEFAULT_CSS = "" +
            "h1 { margin-left: 0px; font-size: 1.2em; }" + "\n" +
            "li { margin-left: 0px; }" + "\n" +
            "ul { padding-left: 2em; }";


    private final Context context;
    private final ChangeLog changeLog;
    private final HtmlFormatter formatter;


    /**
     * Create an instance using the default CSS to format the Change Log.
     */
    public static DialogChangeLog newInstance(Context context) {
        return DialogChangeLog.newInstance(context, DEFAULT_CSS);
    }

    /**
     * Create an instance using the supplied CSS to format the Change Log.
     */
    public static DialogChangeLog newInstance(Context context, String css) {
        ChangeLog changeLog = ChangeLog.newInstance(context);
        String versionFormat = context.getResources().getString(R.string.changelog_version_format);
        HtmlFormatter formatter = new HtmlFormatter(versionFormat, css);
        return new DialogChangeLog(context, changeLog, formatter);
    }

    private DialogChangeLog(Context context, ChangeLog changeLog, HtmlFormatter formatter) {
        this.context = context;
        this.changeLog = changeLog;
        this.formatter = formatter;
    }

    /**
     * Get the {@code ChangeLog} instance backing this {@code DialogChangeLog}.
     */
    public ChangeLog getChangeLog() {
        return changeLog;
    }

    /**
     * Get the "What's New" dialog.
     *
     * @return An AlertDialog displaying the changes since the previous installed version of your
     *         app (What's New). But when this is the first run of your app including
     *         {@code ChangeLog} then the full log dialog is show.
     */
    public AlertDialog getLogDialog() {
        return getDialog(changeLog.isFirstRunEver());
    }

    /**
     * Get a dialog with the full Change Log.
     *
     * @return An AlertDialog with a full Change Log displayed.
     */
    public AlertDialog getFullLogDialog() {
        return getDialog(true);
    }

    /**
     * Check if this is the first execution of this app version.
     *
     * @return {@code true} if this version of your app is started the first time.
     */
    public boolean isFirstRun() {
        return changeLog.isFirstRun();
    }

    private AlertDialog getDialog(boolean full) {
        WebView webView = new WebView(context);
        webView.loadDataWithBaseURL(null, getChangeLogHtml(full), "text/html", "UTF-8", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(full ? R.string.changelog_full_title : R.string.changelog_title)
                .setView(webView)
                .setCancelable(false)
                .setPositiveButton(R.string.changelog_ok_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changeLog.writeCurrentVersion();
                            }
                        });

        if (!full) {
            builder.setNegativeButton(R.string.changelog_show_full,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            getFullLogDialog().show();
                        }
                    });
        }

        return builder.create();
    }

    private String getChangeLogHtml(boolean full) {
        List<ReleaseItem> changelog = full ?
                changeLog.getChangeLog() :
                changeLog.getRecentChanges();

        return formatter.createHtmlChangeLog(changelog);
    }
}
