package br.com.conductor.heimdall.api.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.api.entity.CredentialState;
import br.com.conductor.heimdall.api.enums.CredentialStateEnum;
import br.com.conductor.heimdall.api.repository.CredentialStateRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class CredentialStateService {

    @Autowired
    private CredentialStateRepository credentialStateRepository;

    public CredentialState findOne(String jti) {
        return credentialStateRepository.findByJti(jti);
    }

    public boolean verifyIfTokenIsRevokeOrLogout(String jti) {
        return  credentialStateRepository.findByJti(jti) == null ||
                credentialStateRepository.findByJtiAndStateEquals(jti, CredentialStateEnum.REVOKE) != null ||
                credentialStateRepository.findByJtiAndStateEquals(jti, CredentialStateEnum.LOGOUT) != null;
    }

    public CredentialState save(String jti, String username, CredentialStateEnum credentialStateEnum) {

        CredentialState credentialState = new CredentialState();

        CredentialState found = credentialStateRepository.findByJti(jti);

        if (found != null) {
            credentialState = found;
        } else {
            credentialState.setJti(jti);
        }

        credentialState.setState(credentialStateEnum);
        credentialState.setUsername(username);

        return credentialStateRepository.save(credentialState);
    }

    public void logout(String token) {
        String[] tokenSplit = token.split("\\.");
        String payloadAsString = tokenSplit[1];
        payloadAsString = new String(Base64.getDecoder().decode(payloadAsString));
        JSONObject payload = new JSONObject(payloadAsString);
        final String jti = payload.getString("jti");
        if (!verifyIfTokenIsRevokeOrLogout(jti)) {
            final String user = payload.getString("sub");
            save(jti, user, CredentialStateEnum.LOGOUT);
        }
    }
}
