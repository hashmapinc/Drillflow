package com.hashmapinc.tempus.witsml.valve.dot;

import org.junit.Test;

import java.util.HashSet;

public class XmlParserTest {
    @Test
    public void shouldGetChildrenKeyset() {
        String xml = "<simpleParent><childA/><childB/><childC/></simpleParent>";
        HashSet<String> keyset = XmlParser.getChildrenKeyset(xml);

        // assert
        assertEquals()
    }
}
