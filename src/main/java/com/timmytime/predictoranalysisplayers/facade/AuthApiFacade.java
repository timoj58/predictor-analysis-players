package com.timmytime.predictoranalysisplayers.facade;

import com.timmytime.predictoranalysisplayers.util.rest.RestTemplateHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class AuthApiFacade extends RestTemplateHelper {

 /* @Value("${auth-host}")
    private String authHost;

    @Value("${auth-url}")
    private String authUrl;

    @Value("${auth.user}")
    private String authUser;

    @Value("${auth.password}")
    private String authPassword;
*/

    public HttpHeaders authenticate() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("username", "internal");
        headers.add("groups", "ROLE_AUTOMATION,");


     /*   AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(authUser);
        authRequest.setPassword(authPassword);

        HttpEntity<AuthRequest> entity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthToken> authToken = restTemplate.postForEntity(authHost+authUrl, entity, AuthToken.class);

        headers.add("application-token", authToken.getBody().getApplicationToken());
*/
        return headers;
    }

}
