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

import java.util.function.Function;
import java.util.function.Predicate;

import com.hashmapinc.tempus.witsml.server.api.QueryValidation.ERRORCODE;

interface Validation extends Function<ValidateParam, ValidationResult> {
	static Validation error401() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_401.value());
	}

static Validation error402() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_402.value());
	}

static Validation error403() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_403.value());
	}

static Validation error404() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_404.value());
	}

static Validation error405() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_405.value());
	}

static Validation error406() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_406.value());
	}

static Validation error407() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_407.value());
	}

static Validation error408() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_408.value());
	}

static Validation error409() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_409.value());
	}

static Validation error410() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_410.value());
	}
	
static Validation error411() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_411.value());
	}

static Validation error412() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_412.value());
	}

static Validation error413() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_413.value());
	}

static Validation error414() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_414.value());
	}

static Validation error415() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_415.value());
	}

static Validation error416() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_416.value());
	}

static Validation error417() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_417.value());
	}

static Validation error418() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_418.value());
	}

static Validation error419() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_419.value());
	}

static Validation error420() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_420.value());
	}

static Validation error421() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_421.value());
	}

static Validation error422() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_422.value());
	}

static Validation error423() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_423.value());
	}

static Validation error424() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_424.value());
	}

static Validation error425() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_425.value());
	}

static Validation error426() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_426.value());
	}

static Validation error427() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_427.value());
	}

static Validation error428() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_428.value());
	}

static Validation error429() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_429.value());
	}

static Validation error430() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_430.value());
	}

static Validation error431() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_431.value());
	}

static Validation error432() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_432.value());
	}

static Validation error433() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_433.value());
	}

static Validation error434() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_434.value());
	}

static Validation error435() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_435.value());
	}

static Validation error436() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_436.value());
	}

static Validation error437() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_437.value());
	}

static Validation error438() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_438.value());
	}

static Validation error439() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_439.value());
	}

static Validation error440() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_440.value());
	}

static Validation error441() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_441.value());
	}

static Validation error442() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_442.value());
	}

static Validation error443() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_443.value());
	}

static Validation error444() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_444.value());
	}

static Validation error445() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_445.value());
	}

static Validation error446() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_446.value());
	}

static Validation error447() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_447.value());
	}

static Validation error448() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_448.value());
	}

static Validation error449() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_449.value());
	}

static Validation error450() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_450.value());
	}

static Validation error451() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_451.value());
	}

static Validation error452() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_452.value());
	}

static Validation error453() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_453.value());
	}

static Validation error454() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_454.value());
	}

static Validation error455() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_455.value());
	}

static Validation error456() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_456.value());
	}

static Validation error457() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_457.value());
	}

static Validation error458() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_458.value());
	}

static Validation error459() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_459.value());
	}

static Validation error460() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_460.value());
	}

static Validation error461() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_461.value());
	}

static Validation error462() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_462.value());
	}

static Validation error463() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_463.value());
	}

static Validation error464() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_464.value());
	}

static Validation error465() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_465.value());
	}

static Validation error466() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_466.value());
	}

static Validation error467() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_467.value());
	}

static Validation error468() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_468.value());
	}

static Validation error469() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_469.value());
	}

static Validation error470() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_470.value());
	}

static Validation error471() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_471.value());
	}

static Validation error472() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_472.value());
	}

static Validation error473() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_473.value());
	}

static Validation error474() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_474.value());
	}

static Validation error475() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_475.value());
	}

static Validation error476() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_476.value());
	}

static Validation error477() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_477.value());
	}

static Validation error478() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_478.value());
	}

static Validation error479() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_479.value());
	}

static Validation error480() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_480.value());
	}

static Validation error481() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_481.value());
	}

static Validation error482() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_482.value());
	}

static Validation error483() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_483.value());
	}

static Validation error484() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_484.value());
	}

static Validation error485() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_485.value());
	}

static Validation error486() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_486.value());
	}

static Validation error487() {
		return holds(param -> !param.getWMLtypeIn().trim().isEmpty(), ERRORCODE.ERROR_487.value());
	}
	
	static Validation error999() {
		return holds(param -> !param.getXMLin().trim().isEmpty(), ERRORCODE.ERROR_999.value());
	}

	static Validation holds(Predicate<ValidateParam> p, String message) {
		return param -> p.test(param) ? valid() : invalid(message);
	}

	static ValidationResult invalid(String message) {
		//return new Invalid(message);
		return null;
	}

	static ValidationResult valid() {
		return ValidationSupport.valid();
	}

	default Validation and(Validation other) {
		return user -> {
			final ValidationResult result = this.apply(user);
			return result.isValid() ? other.apply(user) : result;
		};
	}
}
