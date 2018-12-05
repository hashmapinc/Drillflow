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
/**
* Copyright © 2018-2018 Hashmap, Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.hashmapinc.tempus.witsml.valve.dot;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hashmapinc.tempus.witsml.valve.model.UserJWTMapping;
import com.hashmapinc.tempus.witsml.valve.model.UserJWTMappings;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DotAuth {
    public final String URL;
    public final String API_KEY;

    private UserJWTMappings userJWTMappings = null;

    public DotAuth(String URL, String API_KEY) {
        this.URL = URL;
        this.API_KEY = API_KEY;
        userJWTMappings = new UserJWTMappings();
    }

    /**
     * This function gets a JWT from the auth endpoint of the DoT server
     * 
     * @param username
     *            - String username for basic auth
     * @param password
     *            - String password for basic auth
     * @return jwt - DecodedJWT obtained from the auth call
     * @throws UnirestException
     */
    public DecodedJWT getJWT(String username, String password) throws UnirestException {
        
        String tokenString = queryToken(username);
        
        if (tokenString == null) {
            tokenString = createNewToken(username, password);
        }

        // return the decoded tokenstring
        return JWT.decode(tokenString);
    }

    /**
     * Create new JWT token for given user
     * @param username
     * @return tokenString
     */
    private String createNewToken(String username, String password) throws UnirestException{
        UserJWTMapping userJWTMapping = new UserJWTMapping();
        // build the userinfo string
        String userinfo = "{\"account\":\"" + username + "\", \"password\":\"" + password + "\"}";

        // send the response
        HttpResponse<JsonNode> response = Unirest.post(URL).header("accept", "application/json")
                .header("Ocp-Apim-Subscription-Key", this.API_KEY).body(userinfo).asJson();

        // get the token string
        String tokenString = response.getBody().getObject().getString("jwt");

        userJWTMapping.setUsername(username);
        userJWTMapping.setToken(tokenString);

        userJWTMappings.addUserJWTMapping(userJWTMapping);
        return tokenString;
    }

    /**
     * Query token from UserJWTMapping cache
     * @param userName
     * @return tokenString if token exists else null
     */
    public String queryToken(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("Username is null");
        }

        if (userJWTMappings == null) {
            throw new IllegalArgumentException("No User JWT mapping found");
        }

        String tokenString = null;

        for (UserJWTMapping userJWTMapping : userJWTMappings.getUserJWTMappings()) {
            if (userName.equalsIgnoreCase(userJWTMapping.getUsername())) {
                tokenString = userJWTMapping.getToken();
            }
        }

        return tokenString;
    }
}
