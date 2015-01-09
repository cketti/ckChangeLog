# ckChangeLog - An Android Library to display a Change Log

![Screenshot](screenshot_1.png)
![Screenshot](screenshot_2.png)

This library provides an easy way to display a change log in your app.

## Features

 * Displays changes since the last app update
 * Can display the complete change log history
 * Uses a simple XML file as source
 * Supports partial translations
 * Easily extendable to use something other than a dialog

Repository at <https://github.com/cketti/ckChangeLog>.


## Usage

1. Create the master change log in `res/xml/changelog_master.xml`. Formatted like this:

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <changelog>
      <release version="1.1" versioncode="11" >
          <change>Totally new and shiny version</change>
      </release>
      <release version="1.0" versioncode="10" >
          <change>Fixed: A bug fix</change>
          <change>Some other changes I can't quite remember</change>
      </release>
      <release version="0.1" versioncode="1">
          <change>First release</change>
      </release>
  </changelog>
  ```

2. Create translations of this `changelog_master.xml` file in files named `changelog.xml` under
language-specific versions of `res/xml/`, e.g. `res/xml-de/changelog.xml`.

3. Display the change log dialog by putting the following code in your activity's `onCreate()` method:

  ```java
  ChangeLog cl = new ChangeLog(this);
  if (cl.isFirstRun()) {
      cl.getLogDialog().show();
  }
  ```

## Include the library

The easiest way to add ckChangeLog to your project is via Gradle. Just add the following lines to your `build.gradle`:

```groovy
dependencies {
    compile 'de.cketti.library.changelog:ckchangelog:1.2.2'
}
```

To tell Gradle where to find the library, make sure `build.gradle` also contains this:

```groovy
repositories {
    mavenCentral()
}
```

## Customize labels

In order to change the labels of the dialog add the following items to your `strings.xml`:

```
  <string name="changelog_full_title">Change Log</string>
  <string name="changelog_title">What\'s New</string>
  <string name="changelog_ok_button">OK</string>
  <string name="changelog_show_full">More…</string>
```

## Changelog

### Version 1.2.2 (2015-01-09)
* Added Ukrainian translation

### Version 1.2.1
* Add support for [AboutLibraries](https://github.com/mikepenz/AboutLibraries)
* Fix build scripts so Javadoc JAR is properly created

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


## Acknowledgments

This library is based on:
* [android-change-log](http://code.google.com/p/android-change-log/) by Karsten Priegnitz
* [Inscription](https://github.com/MartinvanZ/Inscription/) by [Martin van Zuilekom](https://github.com/MartinvanZ/)

Other contributors:
* [See here](https://github.com/cketti/ckChangeLog/graphs/contributors)
* You? Please create pull requests against the [dev](https://github.com/cketti/ckChangeLog/tree/dev) branch


## License

    Copyright (C) 2012-2015 cketti and contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
