package de.cketti.library.changelog;


import java.util.List;


/**
 * Container used to store information about a release/version.
 */
public class ReleaseItem {
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


    ReleaseItem(int versionCode, String versionName, List<String> changes) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.changes = changes;
    }
}
