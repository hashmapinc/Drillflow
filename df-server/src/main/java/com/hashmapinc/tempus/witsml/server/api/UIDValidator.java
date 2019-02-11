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

public class UIDValidator {

    private boolean isError401 = true;

    private boolean isError464 = false;

    public boolean isError401() {
        return isError401;
    }

    public void setError401(boolean isError401) {
        this.isError401 = isError401;
    }

    public boolean isError464() {
        return isError464;
    }

    public void setError464(boolean isError464) {
        this.isError464 = isError464;
    }

}
