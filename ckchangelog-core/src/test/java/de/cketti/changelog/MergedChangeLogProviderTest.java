package de.cketti.changelog;


import java.util.List;

import de.cketti.changelog.helper.ChangeLogBuilder;
import de.cketti.changelog.helper.ChangeLogProviderBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;


@RunWith(RobolectricTestRunner.class)
public class MergedChangeLogProviderTest {
    @Test
    public void getChangeLog_shouldReturnMergedChangeLog() {
        ChangeLogProvider masterChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", null, "First version")
                .addVersion(2, "1.1", "2000-01-01", "Some new feature", "Small bugfix")
                .addVersion(3, "1.2", "2010-02-01", "The latest change")
                .build();
        ChangeLogProvider localizedChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", null, "Erste Version")
                .addVersion(2, "1.1", "2000-01-01", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .build();
        MergedChangeLogProvider changeLogProvider = 
                new MergedChangeLogProvider(masterChangeLogProvider, localizedChangeLogProvider);

        List<ReleaseItem> changeLog = changeLogProvider.getChangeLog();
        
        assertEquals(changeLog, new ChangeLogBuilder()
                .addVersion(1, "1.0", null, "Erste Version")
                .addVersion(2, "1.1", "2000-01-01", "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .addVersion(3, "1.2", "2010-02-01", "The latest change")
                .build());
    }

    @Test
    public void getChangeLogSince_shouldReturnMergedChangeLog() {
        ChangeLogProvider masterChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", null, "First version")
                .addVersion(2, "1.1", null, "Some new feature", "Small bugfix")
                .addVersion(3, "1.2", null, "The latest change")
                .build();
        ChangeLogProvider localizedChangeLogProvider = new ChangeLogProviderBuilder()
                .addVersion(1, "1.0", null, "Erste Version")
                .addVersion(2, "1.1", null, "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .build();
        MergedChangeLogProvider changeLogProvider = 
                new MergedChangeLogProvider(masterChangeLogProvider, localizedChangeLogProvider);

        List<ReleaseItem> changeLog = changeLogProvider.getChangeLogSince(1);
        
        assertEquals(changeLog, new ChangeLogBuilder()
                .addVersion(2, "1.1", null, "Neue Funktionalität", "Kleiner Fehlerbehebung")
                .addVersion(3, "1.2", null, "The latest change")
                .build());
    }

    @Test
    public void constructor_withFirstArgumentNull_shouldThrow() {
        ChangeLogProvider changeLogProvider = mock(ChangeLogProvider.class);
        
        try {
            new MergedChangeLogProvider(null, changeLogProvider);
            fail();
        } catch (NullPointerException e) {
            assertEquals("masterChangeLogProvider == null", e.getMessage());
        }
    }

    @Test
    public void constructor_withSecondArgumentNull_shouldThrow() {
        ChangeLogProvider changeLogProvider = mock(ChangeLogProvider.class);
        
        try {
            new MergedChangeLogProvider(changeLogProvider, null);
            fail();
        } catch (NullPointerException e) {
            assertEquals("localizedChangeLogProvider == null", e.getMessage());
        }
    }
}
