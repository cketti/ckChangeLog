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
package de.cketti.changelog.dialog;


import java.util.List;

import de.cketti.changelog.ReleaseItem;


class HtmlFormatter {
    private final String versionFormat;
    private final String css;


    public HtmlFormatter(String versionFormat, String css) {
        this.versionFormat = versionFormat;
        this.css = css;
    }

    public String createHtmlChangeLog(List<ReleaseItem> changelog) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head><style type=\"text/css\">");
        sb.append(css);
        sb.append("</style></head><body>");

        for (ReleaseItem release : changelog) {
            sb.append("<h1>");
            sb.append(String.format(versionFormat, release.versionName));
            sb.append("</h1><ul>");
            for (String change : release.changes) {
                sb.append("<li>");
                sb.append(change);
                sb.append("</li>");
            }
            sb.append("</ul>");
        }

        sb.append("</body></html>");

        return sb.toString();
    }
}
