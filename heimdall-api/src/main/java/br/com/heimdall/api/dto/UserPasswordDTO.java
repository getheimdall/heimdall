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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Data transference object class that represents updates in password to a Heimdall User.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
public class UserPasswordDTO implements Serializable {

    @NotNull
    @Size(min = 5, max = 16)
    @JsonProperty("current_password")
    private String currentPassword;

    @NotNull
    @Size(min = 5, max = 16)
    @JsonProperty("new_password")
    private String newPassword;

    @NotNull
    @Size(min = 5, max = 16)
    @JsonProperty("confirm_new_password")
    private String confirmNewPassword;
}
