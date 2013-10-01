package de.cketti.sample.changelog;

import de.cketti.library.changelog.ChangeLog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_whats_new: {
                new DarkThemeChangeLog(this).getLogDialog().show();
                break;
            }
            case R.id.menu_full_changelog: {
                new ChangeLog(this).getFullLogDialog().show();
                break;
            }
            case R.id.menu_fdroid: {
                new FdroidChangeLog(this, getChangelogFromServer()).getFullLogDialog().show();
                break;
            }
        }

        return true;
    }

    /**
     * Example that shows how to create a themed dialog.
     */
    public static class DarkThemeChangeLog extends ChangeLog {
        public static final String DARK_THEME_CSS =
                "body { color: #ffffff; background-color: #282828; }" + "\n" + DEFAULT_CSS;

        public DarkThemeChangeLog(Context context) {
            super(new ContextThemeWrapper(context, R.style.DarkTheme), DARK_THEME_CSS);
        }
    }

    /**
     * Simulate getting a merged change log from the server.
     */
    private InputStream getChangelogFromServer() {
        String dummyContent =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<changelog>\n" +
                "    <release version=\"1.0\" versioncode=\"100\" >\n" +
                "        <change>This is totally made up</change>\n" +
                "    </release>\n" +
                "</changelog>";

        return new ByteArrayInputStream(dummyContent.getBytes());
    }

    /**
     * Example showing how to handle a pre-merged changelog XML available as {@code InputStream}.
     */
    public static class FdroidChangeLog extends ChangeLog {
        private final InputStream mInputStream;

        public FdroidChangeLog(Context context, InputStream inputStream) {
            super(context);
            mInputStream = inputStream;
        }

        @Override
        protected SparseArray<ReleaseItem> getMergedChangeLog(boolean full) {
            SparseArray<ReleaseItem> changelog;
            try {
                XmlPullParser xml = XmlPullParserFactory.newInstance().newPullParser();
                InputStreamReader reader = new InputStreamReader(mInputStream);
                xml.setInput(reader);
                try {
                    changelog = readChangeLog(xml, full);
                } finally {
                    try { reader.close(); } catch (Exception e) { /* do nothing */ }
                }
            } catch (XmlPullParserException e) {
                throw new RuntimeException("Failed to parse the change log", e);
            }

            return changelog;
        }
    }
}
