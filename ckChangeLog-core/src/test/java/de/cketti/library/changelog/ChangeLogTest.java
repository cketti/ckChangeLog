package de.cketti.library.changelog;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
public class ChangeLogTest {
    private static final int APP_VERSION_CODE = 3;
    private static final String APP_VERSION_NAME = "1.2";
    private static final String APP_PACKAGE_NAME = "org.example.app";


    private Context context;
    private SharedPreferences preferences;
    private ChangeLogProvider changeLogProvider;


    @Before
    public void setUp() throws Exception {
        context = createContext();
        preferences = createSharedPreferences();
        changeLogProvider = createChangeLogProvider();
    }

    @Test
    public void getLastVersionCode() throws Exception {
        setLastVersionCode(2);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);

        int lastVersionCode = changeLog.getLastVersionCode();
        
        assertEquals(2, lastVersionCode);
    }

    @Test
    public void getCurrentVersionCode() throws Exception {
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);

        int currentVersionCode = changeLog.getCurrentVersionCode();
        
        assertEquals(APP_VERSION_CODE, currentVersionCode);
    }

    @Test
    public void getCurrentVersionName() throws Exception {
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);

        String currentVersionName = changeLog.getCurrentVersionName();
        
        assertEquals(APP_VERSION_NAME, currentVersionName);
    }

    @Test
    public void isFirstRun_withLastVersionCodeSmallerThanCurrentVersion_shouldReturnTrue() throws Exception {
        setLastVersionCode(1);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        
        boolean firstRun = changeLog.isFirstRun();

        assertTrue(firstRun);
    }

    @Test
    public void isFirstRun_withLastVersionCodeEqualToCurrentVersion_shouldReturnFalse() throws Exception {
        setLastVersionCode(APP_VERSION_CODE);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        
        boolean firstRun = changeLog.isFirstRun();

        assertFalse(firstRun);
    }

    @Test
    public void isFirstRunEver_withLastVersionCodeSet_shouldReturnFalse() throws Exception {
        setLastVersionCode(1);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        
        boolean firstRunEver = changeLog.isFirstRunEver();

        assertFalse(firstRunEver);
    }

    @Test
    public void isFirstRunEver_withLastVersionCodeUnset_shouldReturnTrue() throws Exception {
        setLastVersionCode(-1);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        
        boolean firstRunEver = changeLog.isFirstRunEver();

        assertTrue(firstRunEver);
    }

    @Test
    public void writeCurrentVersion() throws Exception {
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        
        changeLog.writeCurrentVersion();

        int lastVersionCode = preferences.getInt("ckChangeLog_last_version_code", -1);
        assertEquals(APP_VERSION_CODE, lastVersionCode);
    }

    @Test
    public void getChangeLog_shouldReturnDataFromChangeLogProvider() throws Exception {
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        List<ReleaseItem> releaseItemsToReturn = new ArrayList<>();
        when(changeLogProvider.getChangeLog()).thenReturn(releaseItemsToReturn);

        List<ReleaseItem> releaseItems = changeLog.getChangeLog();

        assertSame(releaseItemsToReturn, releaseItems);
    }

    @Test
    public void getRecentChanges_shouldReturnDataFromChangeLogProvider() throws Exception {
        setLastVersionCode(2);
        ChangeLog changeLog = ChangeLog.newInstance(context, preferences, changeLogProvider);
        List<ReleaseItem> releaseItemsToReturn = new ArrayList<>();
        when(changeLogProvider.getChangeLogSince(2)).thenReturn(releaseItemsToReturn);

        List<ReleaseItem> releaseItems = changeLog.getRecentChanges();

        assertSame(releaseItemsToReturn, releaseItems);
    }

    private void setLastVersionCode(int lastVersionCode) {
        preferences.edit().putInt("ckChangeLog_last_version_code", lastVersionCode).apply();
    }

    private Context createContext() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionCode = APP_VERSION_CODE;
        packageInfo.versionName = APP_VERSION_NAME;
        
        PackageManager packageManager = mock(PackageManager.class);
        //noinspection WrongConstant
        when(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(packageInfo);
        
        Context context = mock(Context.class);
        when(context.getPackageName()).thenReturn(APP_PACKAGE_NAME);
        when(context.getPackageManager()).thenReturn(packageManager);
        return context;
    }

    private SharedPreferences createSharedPreferences() {
        Context context = RuntimeEnvironment.application;
        SharedPreferences sharedPreferences = context.getSharedPreferences("test", Context.MODE_PRIVATE); 
        sharedPreferences.edit().clear().apply();
        return sharedPreferences;
    }

    private ChangeLogProvider createChangeLogProvider() {
        return mock(ChangeLogProvider.class);
    }
}
