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
package com.hashmapinc.tempus.witsml.valve;

public class ValveAuthException extends Exception {
	
	/**
	 * Exception thrown if user is not authenticated
	 * 
	 * @param message
	 */
	public ValveAuthException(String message) {
		super(message);
	}
	
	/**
	 * Exception thrown if user is not authenticated
	 * @param message
	 * @param cause
	 */
	public ValveAuthException(String message, Throwable cause) {
		super(message, cause);
	}

}
