/**
 * Copyright © 2018-2019 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hashmapinc.tempus.witsml.server.api;

import com.hashmapinc.tempus.witsml.valve.IValve;
import com.hashmapinc.tempus.witsml.valve.ValveFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreValidationTest {

    private IValve valve;
    private ValveConfig config;

    private String minWellQueryTemplate =
            "<wells xmlns=\"http://www.witsml.org/schemas/1series\" " +
                    "version=\"1.4.1.1\">" +
                    "<well/>" +
                    "</wells>";

    @Autowired
    private void setValveConfig(ValveConfig config){
        this.config = config;
    }


    @Before
    public void Setup() {
        try{
            valve = ValveFactory.buildValve("DoT", config.getConfiguration());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // *****************ADD TO STORE TESTS***************** //

    @Test
    public void testSuccess(){
        short resp = StoreValidator.validateAddToStore(
                "well",
                "<wells xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n" +
                        "<well  uid=\"uid12333\">\n" +
                        "\t\t<name>Well Test</name>\n" +
                        "</well>\n" +
                        "</wells>",
                null,
                valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void test408ShouldErrorEmptyMultiLine(){
        short resp = StoreValidator.validateAddToStore("well", "", null, valve);
        assertThat(resp).isEqualTo((short)-408);
    }

    @Test
    public void test486WithSubstring(){
        short resp = StoreValidator.validateAddToStore("well", "<wellbores xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n" +
                "<wellbore  uid=\"uid12333\">\n" +
                "</wellbore>\n" +
                "</wellbores>", null, valve);
        assertThat(resp).isEqualTo((short)-486);
    }

    @Test
    public void test486WithoutSimilarSubstring(){
        short resp = StoreValidator.validateAddToStore("well", "<logs xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n" +
                "<log  uid=\"uid12333\">\n" +
                "</log>\n" +
                "</logs>", null, valve);
        assertThat(resp).isEqualTo((short)-486);
    }

    @Test
    public void test487UnsupportedObjectShouldError(){ ;
        short resp = StoreValidator.validateAddToStore("iceCream", "<iceCreams xmlns=\"http://www.witsml.org/schemas/131\" version=\"1.3.1.1\">\n" +
                "<iceCream  uid=\"uid12333\">\n" +
                "</iceCream>\n" +
                "</iceCreams>", null, valve);
        assertThat(resp).isEqualTo((short)-487);
    }

    @Test
    public void test401DoesNotContainPluralRootElementError(){ ;
        short resp = StoreValidator.validateAddToStore("well", "<well uid=\"uid12333\" ><name>test</name></well>", null, valve);
        assertThat(resp).isEqualTo((short)-401);
    }

    @Test
    public void test468DoesNotContainVersionElement(){ ;
        short resp = StoreValidator.validateAddToStore("well", "<wells xmlns=\"http://www.witsml.org/schemas/131\">\n" +
                "<well  uid=\"uid12333\">\n" +
                "\t\t<name>Well Test</name>\n" +
                "</well>\n" +
                "</wells>", null, valve);
        assertThat(resp).isEqualTo((short)-468);
    }

    @Test
    public void test403DoesNotContainADefaultNamespace(){ ;
        short resp = StoreValidator.validateAddToStore("well", "<wells version=\"1.3.1.1\">\n" +
                "<well  uid=\"uid12333\">\n" +
                "\t\t<name>Well Test</name>\n" +
                "</well>\n" +
                "</wells>", null, valve);
        assertThat(resp).isEqualTo((short)-403);
    }

    // *****************GET CAP TESTS***************** //

    @Test
    public void test411InvalidOptionsIn(){
        short resp = StoreValidator.validateGetCap("dataVersion:123");
        assertThat(resp).isEqualTo((short)-411);
    }

    @Test
    public void test424CheckForDataVersionExist(){
        short resp = StoreValidator.validateGetCap("hello=123");
        assertThat(resp).isEqualTo((short)-424);
    }

    @Test
    public void getCapShouldSuceed(){
        short resp = StoreValidator.validateGetCap("dataVersion=1.3.1.1");
        assertThat(resp).isEqualTo((short)1);
    }

    // *****************GET FROM STORE TESTS***************** //

    @Test
    public void getFromStoreGoodKeyGoodValueShouldSuceed() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            put("requestObjectSelectionCapability", "true");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreNoKeyNoValueShouldSuceed() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreAnyOtherKeyAnyOtherValueShouldSucceed() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            put("anyOtherKey", "anyOtherValue");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreAnyOtherKeyGoodValueShouldSucceed() throws Exception {
        //String WMLtypeIn, String xmlIn, Map<String,String> optionsIn, IValve valve;
        Map<String, String> testMap = new HashMap<String, String>() {{
            put("anyOtherKey", "true");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                "<wells xmlns=\"http://www.witsml.org/schemas/1series\" version=\"1.4.1.1\"><well/></wells>",
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreGoodKeyAnyOtherValueExceptWhitespaceOrEmptyShouldSucceed() throws Exception {
        Map<String, String> testMap = new HashMap<String, String>() {{
            put("requestObjectSelectionCapability", "anyOtherValue");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreGoodKeyEmptyValueShouldFail() throws Exception {
        Map<String, String> testMap = new HashMap<String, String>() {{
            put("requestObjectSelectionCapability", "");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)-1001);
    }

    @Test
    public void getFromStoreGoodKeyNullValueShouldFail() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            // Will this get caught by invalid XML (can there be a null key value?)
            put("requestObjectSelectionCapability", null);
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)-1001);
    }

    @Test
    public void getFromStoreNullKeyNullValueShouldSucceed() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            put(null, null);
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)1);
    }

    @Test
    public void getFromStoreGoodKeyGoodValueMoreEntriesShouldFail() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            put("requestObjectSelectionCapability", "true");
            put("anotherKey", "anotherValue");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                minWellQueryTemplate,
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)-427);
    }

    @Test
    public void getFromStoreGoodKeyGoodValueButXMLInHasUIDShouldFail() throws Exception {
        Map<String, String> testMap = new HashMap<>() {{
            put("requestObjectSelectionCapability", "true");
        }};
        short resp = StoreValidator.validateGetFromStore(
                "well",
                "<wells xmlns=\"http://www.witsml.org/schemas/1series\" version=\"1.4.1.1\">" +
                      "<well uid=\"uid12333\"></well></wells>",
                testMap,
                this.valve);
        assertThat(resp).isEqualTo((short)-428);
    }

}
