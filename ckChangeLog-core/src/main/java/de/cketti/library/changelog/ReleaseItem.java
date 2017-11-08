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
package de.cketti.library.changelog;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.cketti.library.changelog.Preconditions.checkNotNull;


/**
 * Container used to store information about a release/version.
 */
public final class ReleaseItem {
    /**
     * Version code of the release.
     */
    public final int versionCode;

    /**
     * Version name of the release.
     */
    public final String versionName;

    /**
     * List of changes introduced with that release.
     */
    public final List<String> changes;


    public static ReleaseItem newInstance(int versionCode, String versionName, List<String> changes) {
        List<String> copiedChanges = new ArrayList<>(changes);
        return new ReleaseItem(versionCode, versionName, copiedChanges);
    }

    ReleaseItem(int versionCode, String versionName, List<String> changes) {
        this.versionCode = versionCode;
        this.versionName = checkNotNull(versionName, "versionName == null");
        this.changes = Collections.unmodifiableList(checkNotNull(changes, "changes == null"));
    }

    @Override
    public String toString() {
        return "ReleaseItem{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", changes=" + changes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReleaseItem that = (ReleaseItem) o;

        if (versionCode != that.versionCode) {
            return false;
        }
        if (!versionName.equals(that.versionName)) {
            return false;
        }
        return changes.equals(that.changes);

    }

    @Override
    public int hashCode() {
        int result = versionCode;
        result = 31 * result + versionName.hashCode();
        result = 31 * result + changes.hashCode();
        return result;
    }
}
