package de.cketti.sample.changelog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;

import info.changelog.dialog.DialogChangeLog;


public class MainActivity extends FragmentActivity {
    private static final String DARK_THEME_CSS = "body { color: #ffffff; background-color: #282828; }" + "\n" +
            DialogChangeLog.DEFAULT_CSS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogChangeLog cl = DialogChangeLog.newInstance(this);
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
                createDarkThemeChangeLog(this).getLogDialog().show();
                break;
            }
            case R.id.menu_full_changelog: {
                DialogChangeLog.newInstance(this).getFullLogDialog().show();
                break;
            }
        }

        return true;
    }

    private static DialogChangeLog createDarkThemeChangeLog(Context context) {
        ContextThemeWrapper themedContext = new ContextThemeWrapper(context, R.style.DarkTheme);
        return DialogChangeLog.newInstance(themedContext, DARK_THEME_CSS);
    }
}
