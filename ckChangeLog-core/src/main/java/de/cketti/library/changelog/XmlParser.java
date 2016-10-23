/*
 * Copyright (C) 2012-2016 cketti and contributors
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public final class XmlParser {
    private static final int NO_VERSION = -1;
    private static final String TAG_CHANGELOG = "changelog";
    private static final String TAG_RELEASE = "release";
    private static final String ATTRIBUTE_VERSION = "version";
    private static final String ATTRIBUTE_VERSION_CODE = "versioncode";
    private static final String TAG_CHANGE = "change";


    private final XmlPullParser xmlPullParser;
    private final int lastVersionCode;
    private final List<ReleaseItem> result;
    
    
    public static List<ReleaseItem> parse(XmlPullParser xmlPullParser) {
        XmlParser xmlParser = new XmlParser(xmlPullParser, NO_VERSION);
        return xmlParser.readChangeLog();
    }

    public static List<ReleaseItem> parse(XmlPullParser xmlPullParser, int lastVersionCode) {
        XmlParser xmlParser = new XmlParser(xmlPullParser, lastVersionCode);
        return xmlParser.readChangeLog();
    }

    private XmlParser(XmlPullParser xmlPullParser, int lastVersionCode) {
        this.xmlPullParser = xmlPullParser;
        this.lastVersionCode = lastVersionCode;
        result = new ArrayList<>();
    }

    public List<ReleaseItem> readChangeLog() {
        try {
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals(TAG_RELEASE)) {
                    if (parseReleaseTag()) {
                        // Stop reading more elements if this entry is not newer than the last version.
                        break;
                    }
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException(e);
        }

        return result;
    }

    private boolean parseReleaseTag() throws XmlPullParserException, IOException {
        String version = xmlPullParser.getAttributeValue(null, ATTRIBUTE_VERSION);

        int versionCode;
        try {
            String versionCodeStr = xmlPullParser.getAttributeValue(null, ATTRIBUTE_VERSION_CODE);
            versionCode = Integer.parseInt(versionCodeStr);
        } catch (NumberFormatException e) {
            versionCode = NO_VERSION;
        }

        if (lastVersionCode != NO_VERSION && versionCode <= lastVersionCode) {
            return true;
        }

        int eventType = xmlPullParser.getEventType();
        List<String> changes = new ArrayList<>();
        while (eventType != XmlPullParser.END_TAG || xmlPullParser.getName().equals(TAG_CHANGE)) {
            if (eventType == XmlPullParser.START_TAG && xmlPullParser.getName().equals(TAG_CHANGE)) {
                eventType = xmlPullParser.next();
                String text = cleanText(xmlPullParser.getText());
                changes.add(text);
            }
            eventType = xmlPullParser.next();
        }

        ReleaseItem release = new ReleaseItem(versionCode, version, changes);
        result.add(release);

        return false;
    }

    private String cleanText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}
