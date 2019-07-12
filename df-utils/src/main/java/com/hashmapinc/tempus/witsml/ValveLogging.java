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
package com.hashmapinc.tempus.witsml;

import com.hashmapinc.tempus.WitsmlObjects.AbstractWitsmlObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.request.HttpRequest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class ValveLogging {
	public static String getLogMsg(
		String id,
		String message,
		AbstractWitsmlObject abstObject
	) {
		return 	System.lineSeparator() +
				"ExchangeId: " +
				id + System.lineSeparator() +
				"Message: " + message +
				System.lineSeparator() +
				"For Object: " + abstObject.toString() + System.lineSeparator();
	}


	public static String getLogMsg(
			String message
	) {
		return 	getLogMsg("n/a", message);
	}
	public static String getLogMsg(
			String id,
			String message
	) {
		return 	System.lineSeparator() +
				"ExchangeId: " +
				id + System.lineSeparator() +
				"Message: " + message + System.lineSeparator();
	}

	public static String getLogMsg(
			String id,
			String message,
			HttpRequest request
	) {
		try {
			String req = "";
			try {
				if (request != null && request.getBody() != null)
						req = convert(request.getBody().getEntity().getContent(), Charset.defaultCharset());
			} catch (IOException e) {
				req = "Could not parse request";
			}
			return System.lineSeparator() +
					"ExchangeId: " + id + System.lineSeparator() +
					"Message: " + message + System.lineSeparator() +
					"Address: " + request.getUrl() + System.lineSeparator() +
					"Headers: " + request.getHeaders() + System.lineSeparator() +
					"Method: " + request.getHttpMethod().name() + System.lineSeparator() +
					"Payload: " + req + System.lineSeparator();
		} catch (Exception ex){
			return "done";
		}
	}

	public static String getLogRespMsg(
			String id,
			String message,
			HttpResponse<String> response
	) {
		return 	System.lineSeparator() +
				"ExchangeId: " + id + System.lineSeparator() +
				"Message: " + message + System.lineSeparator() +
				"Headers: " + response.getHeaders() + System.lineSeparator() +
				"Status Code:" + response.getStatus() + System.lineSeparator() +
				"Status Text: " + response.getStatusText() + System.lineSeparator() +
				"Response Body: " + response.getBody() + System.lineSeparator();
	}

	private static String convert(InputStream inputStream, Charset charset) throws IOException {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception ex){
			return "Could not parse request";
		}
	}
}
