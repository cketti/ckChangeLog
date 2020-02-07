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


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.res.Resources;
import androidx.annotation.IdRes;
import androidx.annotation.RawRes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * A {@link ChangeLogProvider} that reads data from an Android XML resource.
 */
class ResourceChangeLogProvider implements ChangeLogProvider {
    private final Resources resources;
    private final int resourceId;


    ResourceChangeLogProvider(Resources resources, @RawRes int resourceId) {
        this.resources = resources;
        this.resourceId = resourceId;
    }

    @Override
    public List<ReleaseItem> getChangeLog() {
        try {
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            InputStream inputStream = resources.openRawResource(resourceId);
            try {
                xmlPullParser.setInput(inputStream, null);
                return XmlParser.parse(xmlPullParser);
            } finally {
                inputStream.close();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new InvalidChangeLogException("Error parsing XML resource: " + getResourceName(resourceId), e);
        }
    }

    @Override
    public List<ReleaseItem> getChangeLogSince(int lastVersionCode) {
        try {
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            InputStream inputStream = resources.openRawResource(resourceId);
            try {
                xmlPullParser.setInput(inputStream, null);
                return XmlParser.parse(xmlPullParser, lastVersionCode);
            } finally {
                inputStream.close();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new InvalidChangeLogException("Error parsing XML resource: " + getResourceName(resourceId), e);
        }
    }

    private String getResourceName(@IdRes int resourceId) {
        try {
            return resources.getResourceName(resourceId);
        } catch (Resources.NotFoundException e) {
            return Integer.toString(resourceId);
        }
    }
}
