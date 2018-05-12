package de.cketti.changelog.helper;


import java.util.ArrayList;
import java.util.List;

import de.cketti.changelog.ReleaseItem;
import edu.emory.mathcs.backport.java.util.Collections;

import static java.util.Arrays.asList;


public class ChangeLogBuilder {
    private List<ReleaseItem> releaseItems = new ArrayList<>();


    public ChangeLogBuilder addVersion(int versionCode, String versionName, String date, String... changes) {
        ReleaseItem releaseItem = ReleaseItem.newInstance(versionCode, versionName, date, asList(changes));
        releaseItems.add(releaseItem);
        return this;
    }

    public List<ReleaseItem> build() {
        List<ReleaseItem> items = new ArrayList<>(releaseItems);
        Collections.reverse(items);
        return items;
    }
}
