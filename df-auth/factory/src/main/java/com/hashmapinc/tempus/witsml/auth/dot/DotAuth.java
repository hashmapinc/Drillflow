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
package com.hashmapinc.tempus.witsml.auth.dot;

import com.hashmapinc.tempus.witsml.auth.AbstractAuth;

public class DotAuth extends AbstractAuth {
    final String NAME = "DoT";
    final String DESCRIPTION = "Auth for interacting with a DoT valve.";

    /**
     * constructor
     */
    public DotAuth() {
    }

    /**
     * Retrieve the name of the auth
     * @return The name of the auth
     */
    @override
    public String getName() {
        return this.NAME;
    }

    /**
     * Retrieve the description of the auth
     * @return The description of the auth
     */
    @override
    public String getDescription() {
        return this.DESCRIPTION;
    }
}