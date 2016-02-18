package de.cketti.library.changelog.dialog;


import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.webkit.WebView;

import de.cketti.library.changelog.ChangeLog;
import de.cketti.library.changelog.ChangeLog.ReleaseItem;


public final class DialogChangeLog {
    /**
     * Default CSS styles used to format the change log.
     */
    public static final String DEFAULT_CSS = "" +
            "h1 { margin-left: 0px; font-size: 1.2em; }" + "\n" +
            "li { margin-left: 0px; }" + "\n" +
            "ul { padding-left: 2em; }";


    private final Context context;
    private final ChangeLog changeLog;
    private final String css;


    public static DialogChangeLog newInstance(Context context) {
        ChangeLog changeLog = new ChangeLog(context);
        return new DialogChangeLog(context, changeLog, DEFAULT_CSS);
    }

    public static DialogChangeLog newInstance(Context context, String css) {
        ChangeLog changeLog = new ChangeLog(context);
        return new DialogChangeLog(context, changeLog, css);
    }

    private DialogChangeLog(Context context, ChangeLog changeLog, String css) {
        this.context = context;
        this.changeLog = changeLog;
        this.css = css;
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
     * Get a dialog with the full change log.
     *
     * @return An AlertDialog with a full change log displayed.
     */
    public AlertDialog getFullLogDialog() {
        return getDialog(true);
    }

    private AlertDialog getDialog(boolean full) {
        WebView wv = new WebView(context);
        //wv.setBackgroundColor(0); // transparent
        wv.loadDataWithBaseURL(null, getLog(full), "text/html", "UTF-8", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(
                context.getResources().getString(
                        full ? R.string.changelog_full_title : R.string.changelog_title))
                .setView(wv)
                .setCancelable(false)
                // OK button
                .setPositiveButton(
                        context.getResources().getString(R.string.changelog_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // The user clicked "OK" so save the current version code as
                                // "last version code".
                                changeLog.updateVersionInPreferences();
                            }
                        });

        if (!full) {
            // Show "More…" button if we're only displaying a partial change log.
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

    /**
     * Get changes since last version as HTML string.
     *
     * @return HTML string containing the changes since the previous installed version of your app
     *         (What's New).
     */
    public String getLog() {
        return getLog(false);
    }

    /**
     * Get full change log as HTML string.
     *
     * @return HTML string containing the full change log.
     */
    public String getFullLog() {
        return getLog(true);
    }

    /**
     * Get (partial) change log as HTML string.
     *
     * @param full
     *         If this is {@code true} the full change log is returned. Otherwise only changes for
     *         versions newer than the last version are returned.
     *
     * @return The (partial) change log.
     */
    protected String getLog(boolean full) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head><style type=\"text/css\">");
        sb.append(css);
        sb.append("</style></head><body>");

        String versionFormat = context.getResources().getString(R.string.changelog_version_format);

        List<ReleaseItem> changelog = changeLog.getChangeLog(full);

        for (ReleaseItem release : changelog) {
            sb.append("<h1>");
            sb.append(String.format(versionFormat, release.versionName));
            sb.append("</h1><ul>");
            for (String change : release.changes) {
                sb.append("<li>");
                sb.append(change);
                sb.append("</li>");
            }
            sb.append("</ul>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }

    public boolean isFirstRun() {
        return changeLog.isFirstRun();
    }
}
