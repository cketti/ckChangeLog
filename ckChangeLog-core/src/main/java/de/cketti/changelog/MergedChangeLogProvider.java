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
package de.cketti.changelog;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.SparseArray;

import static de.cketti.changelog.Preconditions.checkNotNull;


/**
 * {@link ChangeLogProvider} that merges the data from two {@code ChangeLogProvider}s.
 *
 * <p>
 * Change Log entries from the provider with localized data are favored over the entries from the master provider.
 * However, when a specific entry is missing in the localized provider the entry from the master provider will be used.
 * </p>
 */
public final class MergedChangeLogProvider implements ChangeLogProvider {
    private final ChangeLogProvider masterChangeLogProvider;
    private final ChangeLogProvider localizedChangeLogProvider;

    
    public MergedChangeLogProvider(ChangeLogProvider masterChangeLogProvider,
            ChangeLogProvider localizedChangeLogProvider) {
        this.masterChangeLogProvider = checkNotNull(masterChangeLogProvider, "masterChangeLogProvider == null");
        this.localizedChangeLogProvider = checkNotNull(localizedChangeLogProvider, 
                "localizedChangeLogProvider == null");
    }

    @Override
    public List<ReleaseItem> getChangeLog() {
        List<ReleaseItem> masterChangeLog = masterChangeLogProvider.getChangeLog();
        List<ReleaseItem> localizedChangeLog = localizedChangeLogProvider.getChangeLog();
        return merge(masterChangeLog, localizedChangeLog);
    }

    @Override
    public List<ReleaseItem> getChangeLogSince(int lastVersionCode) {
        List<ReleaseItem> masterChangeLog = masterChangeLogProvider.getChangeLogSince(lastVersionCode);
        List<ReleaseItem> localizedChangeLog = localizedChangeLogProvider.getChangeLogSince(lastVersionCode);
        return merge(masterChangeLog, localizedChangeLog);
    }

    private List<ReleaseItem> merge(List<ReleaseItem> masterChangeLog, List<ReleaseItem> localizedChangeLog) {
        SparseArray<ReleaseItem> localizedChangeLogMap = new SparseArray<>();
        for (ReleaseItem releaseItem : localizedChangeLog) {
            localizedChangeLogMap.put(releaseItem.versionCode, releaseItem);
        }
        
        List<ReleaseItem> mergedChangeLog = new ArrayList<>(masterChangeLog.size());
        for (int i = 0, len = masterChangeLog.size(); i < len; i++) {
            ReleaseItem masterReleaseItem = masterChangeLog.get(i);
            int key = masterReleaseItem.versionCode;

            // Use release information from localized change log and fall back to the master file if necessary.
            ReleaseItem release = localizedChangeLogMap.get(key, masterReleaseItem);

            mergedChangeLog.add(release);
        }

        Collections.sort(mergedChangeLog, new VersionCodeComparator());

        return mergedChangeLog;
    }

    
    class VersionCodeComparator implements Comparator<ReleaseItem> {
        @Override
        public int compare(ReleaseItem lhs, ReleaseItem rhs) {
            if (lhs.versionCode < rhs.versionCode) {
                return 1;
            } else if (lhs.versionCode > rhs.versionCode) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
