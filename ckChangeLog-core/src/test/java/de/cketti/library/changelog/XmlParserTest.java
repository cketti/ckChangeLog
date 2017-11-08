package de.cketti.library.changelog;


import java.io.InputStream;
import java.util.List;

import de.cketti.library.changelog.helper.ChangeLogBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class XmlParserTest {
    @Test
    public void parse_withEmptyChangeLog() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/valid_empty_changelog.xml");
        
        List<ReleaseItem> releaseItems = XmlParser.parse(xmlPullParser);

        assertTrue(releaseItems.isEmpty());
    }

    @Test
    public void parse_withValidChangeLog() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/valid_sample_changelog.xml");
        
        List<ReleaseItem> releaseItems = XmlParser.parse(xmlPullParser);

        assertEquals(new ChangeLogBuilder()
                .addVersion(1, "1.0", "First release")
                .addVersion(10, "2.0", "Fixed: A bug fix", "Some other changes I can't quite remember")
                .addVersion(11, "2.1", "Totally new and shiny version")
                .build(),
                releaseItems);
    }

    @Test
    public void parse_withWrongRootElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_wrong_root_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Unexpected tag: random (wanted: changelog)", e.getMessage());
        }
    }

    @Test
    public void parse_withWrongReleaseElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_wrong_release_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Unexpected tag: random (wanted: release)", e.getMessage());
        }
    }

    @Test
    public void parse_withReleaseElementMissingVersionAttribute_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_release_element_with_missing_version_attribute.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Missing attribute: version", e.getMessage());
        }
    }

    @Test
    public void parse_withReleaseElementMissingVersioncodeAttribute_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile(
                "/invalid_release_element_with_missing_versioncode_attribute.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Missing attribute: version", e.getMessage());
        }
    }

    @Test
    public void parse_withEmptyReleaseElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_empty_release_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("<release> tag must contain at least one <change> element", e.getMessage());
        }
    }

    @Test
    public void parse_withEmptyChangeElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_empty_change_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Expected text", e.getMessage());
        }
    }

    @Test
    public void parse_withWrongChangeElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_wrong_change_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Unexpected tag: random (wanted: change)", e.getMessage());
        }
    }

    @Test
    public void parse_withChangeElementContainingElement_shouldThrow() throws Exception {
        XmlPullParser xmlPullParser = createParserForFile("/invalid_change_element_contains_element.xml");
        
        try {
            XmlParser.parse(xmlPullParser);
            fail();
        } catch (InvalidChangeLogException e) {
            assertEquals("Expected </change>", e.getMessage());
        }
    }

    private XmlPullParser createParserForFile(String resourceName) throws XmlPullParserException {
        InputStream inputStream = getClass().getResourceAsStream(resourceName);
        MXParser xmlPullParser = new MXParser();
        xmlPullParser.setInput(inputStream, "utf-8");
        return xmlPullParser;
    }
}
