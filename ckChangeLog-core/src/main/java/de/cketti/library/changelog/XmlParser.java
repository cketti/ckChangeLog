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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * The parser for ckChangeLog's XML file format.
 */
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

    private List<ReleaseItem> readChangeLog() {
        try {
            while (xmlPullParser.getEventType() != XmlPullParser.START_TAG) {
                xmlPullParser.next();
            }

            assertElementStart(TAG_CHANGELOG);

            int eventType;
            do {
                eventType = xmlPullParser.next(); 
                if (eventType == XmlPullParser.START_TAG) {
                    if (parseReleaseElement()) {
                        // Stop reading more elements if this entry is not newer than the last version.
                        break;
                    }
                }                
            } while (eventType != XmlPullParser.END_DOCUMENT);
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException(e);
        }

        return Collections.unmodifiableList(result);
    }

    private boolean parseReleaseElement() throws XmlPullParserException, IOException {
        assertElementStart(TAG_RELEASE);

        String version = parseVersionAttribute();
        int versionCode = parseVersionCodeAttribute();
        
        if (lastVersionCode != NO_VERSION && versionCode <= lastVersionCode && versionCode != NO_VERSION) {
            return true;
        }

        List<String> changes = new ArrayList<>();
        int eventType;
        do {
            eventType = xmlPullParser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String text = parseChangeElement();
                changes.add(text);
            }
        } while (eventType != XmlPullParser.END_TAG);

        if (changes.isEmpty()) {
            throw new InvalidChangeLogException("<release> tag must contain at least one <change> element");
        }
        
        ReleaseItem release = new ReleaseItem(versionCode, version, changes);
        result.add(release);

        return false;
    }

    private String parseVersionAttribute() {
        assertAttributePresent(ATTRIBUTE_VERSION);
        return xmlPullParser.getAttributeValue(null, ATTRIBUTE_VERSION);
    }

    private int parseVersionCodeAttribute() {
        assertAttributePresent(ATTRIBUTE_VERSION_CODE);

        String versionCodeStr = xmlPullParser.getAttributeValue(null, ATTRIBUTE_VERSION_CODE);
        try {
            return Integer.parseInt(versionCodeStr);
        } catch (NumberFormatException e) {
            return NO_VERSION;
        }
    }

    private String parseChangeElement() throws XmlPullParserException, IOException {
        assertElementStart(TAG_CHANGE);
        
        assertNextEventType(XmlPullParser.TEXT, "Expected text");
        String text = cleanText(xmlPullParser.getText());
        
        assertNextEventType(XmlPullParser.END_TAG, "Expected </change>");
        
        return text;
    }

    private String cleanText(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    private void assertNextEventType(int expectedEventType, String message) throws XmlPullParserException, IOException {
        xmlPullParser.next();

        int eventType = xmlPullParser.getEventType();
        if (eventType != expectedEventType) {
            throw new InvalidChangeLogException(message);
        }
    }

    private void assertElementStart(String expectedElementName) throws XmlPullParserException, IOException {
        String tagName = xmlPullParser.getName();
        if (!expectedElementName.equals(tagName)) {
            throw new InvalidChangeLogException(
                    "Unexpected tag: " + tagName + " (wanted: " + expectedElementName + ")");
        }
    }

    private void assertAttributePresent(String attributeName) {
        String attributeValue = xmlPullParser.getAttributeValue(null, attributeName);
        if (attributeValue == null) {
            throw new InvalidChangeLogException("Missing attribute: " + attributeName);
        }
    }
}
