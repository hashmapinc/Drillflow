package com.hashmapinc.tempus.witsml.valve.dot;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Chris on 6/29/17.
 */
public class TestUtilities {
    public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));

        List<?> allDifferences = diff.getAllDifferences();
        Assert.assertEquals("Differences found: "+ diff.toString(), 0, allDifferences.size());
    }

    public static String getResourceAsString(String resourceName) throws IOException {
        String path = "src/test/resources/" + resourceName;
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
