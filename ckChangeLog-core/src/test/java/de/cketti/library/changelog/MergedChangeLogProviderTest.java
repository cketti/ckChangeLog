package de.cketti.library.changelog;


import java.util.List;

import de.cketti.library.changelog.helper.ChangeLogBuilder;
import de.cketti.library.changelog.helper.ChangeLogProviderBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class MergedChangeLogProviderTest {
    @Test
    public void getChangeLog_shouldReturnMergedChangeLog() throws Exception {
        ChangeLogProvider masterChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", "First version")
                .addVersion(2, "1.1", "Some new feature", "Small bugfix")
                .addVersion(3, "1.2", "The latest change")
                .build();
        ChangeLogProvider localizedChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", "Erste Version")
                .addVersion(2, "1.1", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .build();
        MergedChangeLogProvider changeLogProvider = 
                new MergedChangeLogProvider(masterChangeLogProvider, localizedChangeLogProvider);

        List<ReleaseItem> changeLog = changeLogProvider.getChangeLog();
        
        assertEquals(changeLog, new ChangeLogBuilder()
                .addVersion(1, "1.0", "Erste Version")
                .addVersion(2, "1.1", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .addVersion(3, "1.2", "The latest change")
                .build());
    }

    @Test
    public void getChangeLogSince_shouldReturnMergedChangeLog() throws Exception {
        ChangeLogProvider masterChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", "First version")
                .addVersion(2, "1.1", "Some new feature", "Small bugfix")
                .addVersion(3, "1.2", "The latest change")
                .build();
        ChangeLogProvider localizedChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", "Erste Version")
                .addVersion(2, "1.1", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .build();
        MergedChangeLogProvider changeLogProvider = 
                new MergedChangeLogProvider(masterChangeLogProvider, localizedChangeLogProvider);

        List<ReleaseItem> changeLog = changeLogProvider.getChangeLogSince(1);
        
        assertEquals(changeLog, new ChangeLogBuilder()
                .addVersion(2, "1.1", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .addVersion(3, "1.2", "The latest change")
                .build());
    }

    @Test
    public void constructor_withFirstArgumentNull_shouldThrow() throws Exception {
        ChangeLogProvider changeLogProvider = mock(ChangeLogProvider.class);
        
        try {
            new MergedChangeLogProvider(null, changeLogProvider);
            fail();
        } catch (NullPointerException e) {
            assertEquals("masterChangeLogProvider == null", e.getMessage());
        }
    }

    @Test
    public void constructor_withSecondArgumentNull_shouldThrow() throws Exception {
        ChangeLogProvider changeLogProvider = mock(ChangeLogProvider.class);
        
        try {
            new MergedChangeLogProvider(changeLogProvider, null);
            fail();
        } catch (NullPointerException e) {
            assertEquals("localizedChangeLogProvider == null", e.getMessage());
        }
    }
}
