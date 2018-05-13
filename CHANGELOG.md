## Change Log

### Version 2.0.0 (unreleased)
**Breaking change!** Change log files are now located in `raw` resource directories rather than the `xml` ones
(see issue #48).

The library was split into a core library (`ckchangelog-core`) and a library for the UI part
(`ckchangelog-legacy-dialog`).

The core library provides the base functionality like parsing the XML file and remembering the version code of the last
app version. This allows users of the core library to provide their own visualization of the change log.

The `ckchangelog-legacy-dialog` library provides the simple dialog from ckChangeLog 1.x that renders the change log in a
`WebView` inside an `AlertDialog`. However, users are strongly encouraged to write their own UI to display the change
log. It is the author's belief that `ckchangelog-legacy-dialog` should be avoided because of the following issues:
* Displaying a dialog on app startup is quite obtrusive.
* WebView is a very resource-intensive UI widget.
* Creating a ChangeLog instance triggers a read from `SharedPreferences` and a query to `PackageManager`. Both
  operations that should be performed in a background thread, but aren't if you're using `ckchangelog-legacy-dialog`.
  Similarly, reading the change log resources are operations that should be performed in the background.

Support for a `date` attribute on the `release` element has been added to `ckchangelog-core`. It's an optional attribute
that accepts arbitrary strings. It's up the user of the library whether to parse the string as a date in a certain
format, output the string as-is, or ignore the value.

Example:
```xml
<release version="1.0" versioncode="1" date="2018-04-01">
    <change>First release</change>
</release>
```
**Note:** `ckchangelog-legacy-dialog` does not support this new attribute.

#### Update from ckChangeLog 1.x
 
1. Replace the old entry in the dependency block with this:

   ```groovy
   dependencies {
       compile 'de.cketti.library.changelog:ckchangelog-legacy-dialog:2.0.0'
   }
   ```

2. Move the change log files from `res/xml/` and `res/xml-*` to `res/raw/` and `res/raw-*` respectively.

3. Then replace the old ckChangeLog code in your Activity's `onCreate()` method with this:

   ```java
   DialogChangeLog changeLog = DialogChangeLog.newInstance(this);
   if (changeLog.isFirstRun()) {
       changeLog.getLogDialog().show();
   }
   ```

Advanced functionality like getting the last version code is available via the `ChangeLog` instance that can be
retrieved by using `DialogChangeLog#getChangeLog()`.  
Example: `dialogChangeLog.getChangeLog().isFirstRunEver()`


### Version 1.2.2 (2015-01-09)
* Added Ukrainian translation

### Version 1.2.1
* Added support for [AboutLibraries](https://github.com/mikepenz/AboutLibraries)
* Fixed build scripts so Javadoc JAR is properly created

### Version 1.2.0
* Made constant `DEFAULT_CSS` public
* Changed internals to make it easier to read the change log from different sources
* Added public method `getChangeLog(boolean)` that returns a list of `ReleaseItem`s
* Changed minSdkVersion to 4
* Switched to Gradle as build system
* Added Greek, Spanish, Polish, and Russian translation

### Version 1.1.0
* Added method `skipLogDialog()`
* Added Slovak and German translation

### Version 1.0.0
* **Breaking change!** Moved master translation from `res/raw/changelog.xml` to `res/xml/changelog_master.xml`
* Added German translation of the sample app

### Version 0.1
* Initial release
