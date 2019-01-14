package com.hashmapinc.tempus.witsml.valve.dot;

import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;

public class UtilTest {

    @Test
    public void shouldProperlyMerge1311Well() throws Exception {
        String destString = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311dest.json")));
        String srcString = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311src.json")));

        JSONObject dest = new JSONObject(destString);
        JSONObject src = new JSONObject(srcString);

        JSONObject merged = Util.merge(dest, src);

        String actual = merged.toString(2);
        String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/utilTest/well1311merged.json")));

        assertEquals(expected, actual);
    }
}

