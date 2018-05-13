/*
 * Copyright (C) 2012-2018 cketti and contributors
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


import java.util.List;


/**
 * A Change Log provider that can return information about all versions or only ones newer than a given version code.
 */
public interface ChangeLogProvider {
    /**
     * Get all {@code ReleaseItem} entries of this Change Log.
     */
    List<ReleaseItem> getChangeLog();

    /**
     * Get only {@code ReleaseItem} entries newer than the given version code.
     *
     * @param lastVersionCode
     *         {@code ReleaseItem} entries with a version code lower than or equal to this value won't be returned.
     */
    List<ReleaseItem> getChangeLogSince(int lastVersionCode);
}
