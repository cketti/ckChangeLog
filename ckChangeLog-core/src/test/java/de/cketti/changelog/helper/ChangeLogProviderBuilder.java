package de.cketti.changelog.helper;


import java.util.ArrayList;
import java.util.List;

import de.cketti.changelog.ChangeLogProvider;
import de.cketti.changelog.ReleaseItem;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;


public class ChangeLogProviderBuilder {
    private List<ReleaseItem> releaseItems = new ArrayList<>();

    
    public ChangeLogProviderBuilder addVersion(int versionCode, String versionName, String date,
            String... changes) {
        ReleaseItem releaseItem = ReleaseItem.newInstance(versionCode, versionName, date, asList(changes));
        releaseItems.add(releaseItem);
        return this;
    }

    public ChangeLogProvider build() {
        final List<ReleaseItem> safeReleaseItems = new ArrayList<>(releaseItems);
        return new ChangeLogProvider() {
            @Override
            public List<ReleaseItem> getChangeLog() {
                return unmodifiableList(safeReleaseItems);
            }

            @Override
            public List<ReleaseItem> getChangeLogSince(int lastVersionCode) {
                List<ReleaseItem> result = new ArrayList<>(); 
                for (ReleaseItem releaseItem : releaseItems) {
                    if (releaseItem.versionCode > lastVersionCode) {
                        result.add(releaseItem);
                    }
                }
                
                return result;
            }
        };
    }
}
