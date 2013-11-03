package de.cketti.sample.changelog;

import de.cketti.library.changelog.ChangeLog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;


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
}
