package br.com.conductor.heimdall.api.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

import br.com.conductor.heimdall.api.dto.GoogleCaptchaDTO;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.INVALID_CAPTCHA_VALIDATION;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.UNAVAILABLE_CAPTCHA_VALIDATION;

/**
 * This class provides methods to validade the {@link Developer} resource.
 *
 * @author Leticia Campelo
 *
 */

@Service
public class ReCaptchaService {

     @Autowired
     private RestTemplate restTemplate;


     public GoogleCaptchaDTO validateCaptcha(String response, String secret){

          String url =  "https://www.google.com/recaptcha/api/siteverify?response=" + response + "&secret=" + secret;
          GoogleCaptchaDTO googleCaptchaValidation =  restTemplate.getForObject(url, GoogleCaptchaDTO.class);

          HeimdallException.checkThrow(googleCaptchaValidation == null, UNAVAILABLE_CAPTCHA_VALIDATION);
          HeimdallException.checkThrow(!googleCaptchaValidation.isSuccess(), INVALID_CAPTCHA_VALIDATION);

         return googleCaptchaValidation;
     }

}
