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
package com.hashmapinc.tempus.witsml.server.api.model.cap;

public class DataObject {
    private String Name;
    private int maxDataNodes;
    private int maxDataPoints;

    /**
     * Returns the name of the singular data object (for example "well")
     * @return The name of the singular data object
     */
    public String getName() {
        return Name;
    }

    /**
     * Sets the name of the singular object (for example "well")
     * @param name The name of the singular object
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * Returns the max number of data nodes supported per query for the specified object
     * @return The max number of data nodes supported per query for the specified object
     */
    public int getMaxDataNodes() {
        return maxDataNodes;
    }

    /**
     * Sets the max number of data nodes supported per query for the specified object. Must be greater than 0.
     * @param maxDataNodes the max number of data nodes supported per query
     */
    public void setMaxDataNodes(int maxDataNodes) {
        this.maxDataNodes = maxDataNodes;
    }

    /**
     * Returns the max number of data point supported per query for the specified object
     * @return The max number of data points supported per query for the specified object
     */
    public int getMaxDataPoints() {
        return maxDataPoints;
    }

    /**
     * Sets the max number of data points supported per query for the specified object. Must be greater than 0.
     * @param maxDataPoints the max number of data points supported per query
     */
    public void setMaxDataPoints(int maxDataPoints) {
        this.maxDataPoints = maxDataPoints;
    }
}
