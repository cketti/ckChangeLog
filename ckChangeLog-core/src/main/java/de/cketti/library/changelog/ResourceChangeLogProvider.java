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


import java.util.List;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.XmlRes;


public class ResourceChangeLogProvider implements ChangeLogProvider {
    private final Context context;
    private final int resourceId;


    ResourceChangeLogProvider(Context context, @XmlRes int resourceId) {
        this.context = context;
        this.resourceId = resourceId;
    }

    @Override
    public List<ReleaseItem> getChangeLog() {
        XmlResourceParser xml = context.getResources().getXml(resourceId);
        try {
            return XmlParser.parse(xml);
        } finally {
            xml.close();
        }
    }

    @Override
    public List<ReleaseItem> getChangeLogSince(int lastVersionCode) {
        XmlResourceParser xml = context.getResources().getXml(resourceId);
        try {
            return XmlParser.parse(xml, lastVersionCode);
        } finally {
            xml.close();
        }
    }
}
