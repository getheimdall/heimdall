/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 *
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package br.com.heimdall.api.dto;

import br.com.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Data transference object class that represents a Heimdall LDAP.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LdapDTO implements Serializable {

    private Long id;

    @NotNull(message = "URL needs to be informed.")
    @Size(max = 200)
    private String url;

    @NotNull(message = "SearchBase needs to be informed.")
    private String searchBase;

    @NotNull(message = "UserDn needs to be informed.")
    @Size(max = 100)
    private String userDn;

    @NotNull(message = "Password needs to be informed.")
    private String password;

    @NotNull(message = "UserSearchFilter needs to be informed.")
    @Size(max = 120)
    private String userSearchFilter;

    @NotNull(message = "Status needs to be informed.")
    private Status status;
}
