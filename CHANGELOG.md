## Change Log

### Version 2.0.0 (unreleased)
**Breaking change!** The library was split into a core library (`ckchangelog-core`) and a library for the UI part
(`ckchangelog-dialog`).

The core library provides the base functionality like parsing the XML file and remembering the version code of the last
app version. This allows users of the core library to easily provide their own visualization of the Change Log.  

The `ckchangelog-dialog` library provides the simple dialog from ckChangeLog 1.x that renders the Change Log in a
`WebView` inside an `AlertDialog`. 

#### Update from ckChangeLog 1.x
 
Replace the old entry in the dependency block with this:

```groovy
dependencies {
    compile 'de.cketti.library.changelog:ckchangelog-dialog:2.0.0'
}
```

Then replace the old ckChangeLog code in your Activity's `onCreate()` method with this:

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
