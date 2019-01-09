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

import java.util.Optional;

interface ValidationResult {
	static ValidationResult valid() {
		return ValidationSupport.valid();
	}

	static ValidationResult invalid(Short reason) {
		return new Invalid(reason);
	}

	boolean isValid();

	Optional<Short> getReason();
}

final class Invalid implements ValidationResult {

	private final Short reason;

	Invalid(Short reason) {
		this.reason = reason;
	}

	public boolean isValid() {
		return false;
	}

	public Optional<Short> getReason() {
		return Optional.of(reason);
	}

}

final class ValidationSupport {
	private static final ValidationResult valid = new ValidationResult() {
		public boolean isValid() {
			return true;
		}

		public Optional<Short> getReason() {
			return Optional.empty();
		}
	};

	public static ValidationResult valid() {
		return valid;
	}
}
