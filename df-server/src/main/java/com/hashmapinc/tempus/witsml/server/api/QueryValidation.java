/**
 * Copyright © 2018-2018 Hashmap, Inc
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Query Validator
 * 
 * @author
 *
 */
public class QueryValidation {

    private static final Logger LOG = Logger.getLogger(QueryValidation.class.getName());

    /**
     * This method validates input parameters for addToStore in StoreImpl, if
     * the input is not according to the specs it will return an error code.
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @param version
     * @return an error code according to the specs after checking the
     *         parameters.
     * @throws IOException
     */
    public static Short validateAddToStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
            String version) throws IOException {
        LOG.info("Validating addToStore");

        return validateStoreFunction(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn, version, STOREFUNCTION.ADD);
    }

    /**
     * This method validates input parameters for getFromStore in StoreImpl, if
     * the input is not according to the specs it will return an error code.
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @param version
     * @return an error code according to the specs after checking the
     *         parameters.
     * @throws IOException
     */
    public static Short validateGetFromStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
            String version) throws IOException {
        LOG.info("validating input for getFromStore");
        return validateStoreFunction(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn, version, STOREFUNCTION.GET);
    }

    /**
     * This method validates input parameters for updateInStore in StoreImpl, if
     * the input is not according to the specs it will return an error code.
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @param version
     * @return an error code according to the specs after checking the
     *         parameters.
     * @throws IOException
     */
    public static Short validateUpdateInStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
            String version) throws IOException {
        LOG.info("validating input for updateInStore");
        return validateStoreFunction(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn, version, STOREFUNCTION.UPDATE);
    }

    /**
     * This method validates input parameters for deleteFromStore in StoreImpl,
     * if the input is not according to the specs it will return an error code.
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @param version
     * @return an error code according to the specs after checking the
     *         parameters.
     * @throws IOException
     */
    public static Short validateDeleteFromStore(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
            String version) throws IOException {
        LOG.info("validating input for deleteInStore");
        return validateStoreFunction(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn, version, STOREFUNCTION.DELETE);
    }

    /**
     * Validate store function wrapper for CRUD operations.
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @param version
     * @param storeFunction
     *            - Add, Get, Update or Delete
     * @return
     * @throws IOException
     */
    private static short validateStoreFunction(String WMLtypeIn, String XMLin, String OptionsIn, String CapabilitiesIn,
            String version, STOREFUNCTION storeFunction) throws IOException {
        short result = 0;
        WITSMLVERSION witsmlVersion = WITSMLVERSION.getType(version);

        switch (witsmlVersion) {
        case v1311:
            LOG.info("Going to validate witsml version 1.3.1.1");
            switch (storeFunction) {
            case ADD:
                return validateAddtoStoreVersion1311(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case GET:
                return validateGetFromStoreVersion1311(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case UPDATE:
                return validateUpdateInStoreVersion1311(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case DELETE:
                return validateDeleteInStoreVersion1311(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            default:
                LOG.info("invalid Store Function");
                break;
            }
        case v1411:
            LOG.info("Going to validate witsml version 1.4.1.1");
            switch (storeFunction) {
            case ADD:
                return validateAddtoStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case GET:
                return validateGetFromStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case UPDATE:
                return validateUpdateInStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            case DELETE:
                return validateDeleteInStoreVersion1411(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
            default:
                LOG.info("invalid Store Function");
                break;
            }
        default:
            LOG.info("invalid witsml version");
            break;
        }
        return result;
    }

    /**
     * Validate Add to Store for version 1.4.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateAddtoStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForAddtoStoreVersion1411();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Add to Store for version 1.3.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateAddtoStoreVersion1311(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.3.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForAddtoStoreVersion1311();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Get from Store for version 1.4.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateGetFromStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForGetFromStoreVersion1411();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Get From Store for version 1.3.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateGetFromStoreVersion1311(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForGetFromStoreVersion1311();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Update in store version for 1.4.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateUpdateInStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForUpdateInStoreVersion1411();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Update in store for version 1.3.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateUpdateInStoreVersion1311(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForUpdateInStoreVersion1311();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Delte in store for version 1.4.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateDeleteInStoreVersion1411(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForDeleteInStoreVersion1411();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * Validate Delete in Store for version 1.3.1.1
     * 
     * @param WMLtypeIn
     * @param XMLin
     * @param OptionsIn
     * @param CapabilitiesIn
     * @return
     * @throws IOException
     */
    private static Short validateDeleteInStoreVersion1311(String WMLtypeIn, String XMLin, String OptionsIn,
            String CapabilitiesIn) throws IOException {
        Optional<Short> errorCode = null;
        // error checking for input parameters for version 1.4.1.1
        final ValidateParam validateParam = new ValidateParam(WMLtypeIn, XMLin, OptionsIn, CapabilitiesIn);
        Validation validation = Validation.checkErrorForDeleteInStoreVersion1311();
        ValidationResult result = validation.apply(validateParam);

        errorCode = result.getReason();
        if (errorCode != null && errorCode.isPresent()) {
            return errorCode.get();
        }

        return null;
    }

    /**
     * This method gets the errorMessage from the basemessages.properties file
     * based on the errorCode
     * 
     * @param errorCode
     * @return
     * @throws IOException
     */
    public static String getErrorMessage(Short errorCode) throws IOException {
        LOG.info("Checking the basemessage.properties file for errorMessages");
        Properties prop = new Properties();
        InputStream input = null;
        String basemessages = "resources/basemessages.properties";
        input = QueryValidation.class.getResourceAsStream(basemessages);
        if (input == null) {
            LOG.info("Error loading the basemessages.properties file");
        }
        prop.load(input);
        String errorM = "basemessages." + errorCode;
        LOG.info("The error Code is :" + errorM);
        return prop.getProperty(errorM);
    }

    /**
     * WitSml XML version
     *
     */
    enum WITSMLVERSION {
        v1311("1.3.1.1"), v1411("1.4.1.1");

        private String value;

        public static Map<String, WITSMLVERSION> typeMapping = new HashMap<String, QueryValidation.WITSMLVERSION>();
        static {
            typeMapping.put(v1311.name(), v1311);
            typeMapping.put(v1411.name(), v1411);
        }

        public static WITSMLVERSION getType(String typeName) {
            if (typeMapping.get(typeName) == null) {
                throw new IllegalArgumentException(String.format("There is no Type mapping with name (%s)"));
            }
            return typeMapping.get(typeName);
        }

        private WITSMLVERSION(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    /**
     * Error code for validations
     *
     */
    enum ERRORCODE {
        ERROR_401("-401"), ERROR_402("-402"), ERROR_403("-403"), ERROR_404("404"), ERROR_405("405"), ERROR_406(
                "406"), ERROR_407("-407"), ERROR_408("-408"), ERROR_409("-409"), ERROR_410("-410"), ERROR_411(
                        "-411"), ERROR_412("-412"), ERROR_413("-413"), ERROR_414("-414"), ERROR_415("-415"), ERROR_416(
                                "-416"), ERROR_417("-417"), ERROR_418("-418"), ERROR_419("-419"), ERROR_420(
                                        "-420"), ERROR_421("-421"), ERROR_422("-422"), ERROR_423("-423"), ERROR_424(
                                                "-424"), ERROR_425("-425"), ERROR_426("-426"), ERROR_427(
                                                        "-427"), ERROR_428("-428"), ERROR_429("-429"), ERROR_430(
                                                                "-430"), ERROR_431("-431"), ERROR_432(
                                                                        "-432"), ERROR_433("-433"), ERROR_434(
                                                                                "-434"), ERROR_435("-435"), ERROR_436(
                                                                                        "-436"), ERROR_437(
                                                                                                "-437"), ERROR_438(
                                                                                                        "-438"), ERROR_439(
                                                                                                                "-439"), ERROR_440(
                                                                                                                        "-440"), ERROR_441(
                                                                                                                                "-441"), ERROR_442(
                                                                                                                                        "-442"), ERROR_443(
                                                                                                                                                "-443"), ERROR_444(
                                                                                                                                                        "-444"), ERROR_445(
                                                                                                                                                                "-445"), ERROR_446(
                                                                                                                                                                        "-446"), ERROR_447(
                                                                                                                                                                                "-447"), ERROR_448(
                                                                                                                                                                                        "-448"), ERROR_449(
                                                                                                                                                                                                "-449"), ERROR_450(
                                                                                                                                                                                                        "-450"), ERROR_451(
                                                                                                                                                                                                                "-451"), ERROR_452(
                                                                                                                                                                                                                        "-452"), ERROR_453(
                                                                                                                                                                                                                                "-453"), ERROR_454(
                                                                                                                                                                                                                                        "-454"), ERROR_455(
                                                                                                                                                                                                                                                "-455"), ERROR_456(
                                                                                                                                                                                                                                                        "-456"), ERROR_457(
                                                                                                                                                                                                                                                                "-457"), ERROR_458(
                                                                                                                                                                                                                                                                        "-458"), ERROR_459(
                                                                                                                                                                                                                                                                                "-459"), ERROR_460(
                                                                                                                                                                                                                                                                                        "-460"), ERROR_461(
                                                                                                                                                                                                                                                                                                "-461"), ERROR_462(
                                                                                                                                                                                                                                                                                                        "-462"), ERROR_463(
                                                                                                                                                                                                                                                                                                                "-463"), ERROR_464(
                                                                                                                                                                                                                                                                                                                        "-464"), ERROR_465(
                                                                                                                                                                                                                                                                                                                                "-465"), ERROR_466(
                                                                                                                                                                                                                                                                                                                                        "-466"), ERROR_467(
                                                                                                                                                                                                                                                                                                                                                "-467"), ERROR_468(
                                                                                                                                                                                                                                                                                                                                                        "-468"), ERROR_469(
                                                                                                                                                                                                                                                                                                                                                                "-469"), ERROR_470(
                                                                                                                                                                                                                                                                                                                                                                        "-470"), ERROR_471(
                                                                                                                                                                                                                                                                                                                                                                                "-471"), ERROR_472(
                                                                                                                                                                                                                                                                                                                                                                                        "-472"), ERROR_473(
                                                                                                                                                                                                                                                                                                                                                                                                "-473"), ERROR_474(
                                                                                                                                                                                                                                                                                                                                                                                                        "-474"), ERROR_475(
                                                                                                                                                                                                                                                                                                                                                                                                                "-475"), ERROR_476(
                                                                                                                                                                                                                                                                                                                                                                                                                        "-476"), ERROR_477(
                                                                                                                                                                                                                                                                                                                                                                                                                                "-477"), ERROR_478(
                                                                                                                                                                                                                                                                                                                                                                                                                                        "-478"), ERROR_479(
                                                                                                                                                                                                                                                                                                                                                                                                                                                "-479"), ERROR_480(
                                                                                                                                                                                                                                                                                                                                                                                                                                                        "-480"), ERROR_481(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                "-481"), ERROR_482(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        "-482"), ERROR_483(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                "-483"), ERROR_484(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        "-484"), ERROR_485(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                "-485"), ERROR_486(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        "-486"), ERROR_487(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                "-487"), ERROR_999(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        "-999");

        private String value;

        private ERRORCODE(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    /**
     * Store functions for query
     * 
     */
    enum STOREFUNCTION {
        ADD("add"), GET("get"), UPDATE("update"), DELETE("delete");

        private String value;

        private STOREFUNCTION(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }
}
